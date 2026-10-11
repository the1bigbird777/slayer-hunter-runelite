package com.slayerhunter;

import javax.inject.Inject;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.BasicStroke;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.api.Point;
import net.runelite.api.events.NpcSpawned;
import java.util.Arrays;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.GameObject;
import net.runelite.api.Skill;
import net.runelite.api.events.StatChanged;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

import net.runelite.client.events.NpcLootReceived;
import net.runelite.client.game.ItemStack;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.Actor;
import net.runelite.api.Player;
import java.util.ArrayDeque;
import java.util.Deque;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.MenuAction;
import net.runelite.api.NPC;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.GameState;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.game.ItemManager;
import java.util.Locale;
import net.runelite.api.ChatMessageType;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.eventbus.Subscribe;
import java.awt.image.BufferedImage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

@PluginDescriptor(name = "Slayer Hunter Tracker", description = "Slayer task and agility lap tracking for a Slayer + Hunter challenge", tags = {"slayer", "hunter", "challenge"}, internalName = "slayer-hunter-tracker")
public class SlayerHunterPlugin extends Plugin
{
    static final String GROUP = "slayerhuntertracker";
    @Inject private ClientToolbar toolbar;
    @Inject private OverlayManager overlayManager;
    private SlayerTargetOverlay targetOverlay;
    @Inject private Client client;
    @Inject private ItemManager itemManager;
    @Inject private net.runelite.client.game.SkillIconManager skillIconManager;
    @Inject private ConfigManager configManager;
    private NavigationButton navigationButton;
    private boolean questException;
    private boolean highlightTargets;
    private boolean bankingAdvisory;
    private int lastThreshold = -1;
    private static final int[] CHARGE_ALERTS = {100, 50, 10, 0};
    private static final Pattern TASK_ASSIGNMENT = Pattern.compile("(?:your new task is to kill|you are to bring balance to|your task is to kill)\\s+(?:\\d+\\s+)?(.+?)(?:[.!,]|$)", Pattern.CASE_INSENSITIVE);
    // Each loot record is a one-time permission to count qualifying coin pickups.
    // Do not infer Slayer GP from inventory growth alone (alching, banks, shops, etc.).
    private static final class CoinLoot
    {
        final long expiresAt;
        int coins;
        boolean ringFallback;
        CoinLoot(long expiresAt, int coins, boolean ringFallback)
        {
            this.expiresAt = expiresAt;
            this.coins = coins;
            this.ringFallback = ringFallback;
        }
    }
    private final Deque<CoinLoot> coinLoot = new ArrayDeque<>();
    private int lastInventoryCoins = -1;
    private final Map<Integer, Integer> lastCarriedTools = new HashMap<>();
    private final Map<Integer, Integer> pendingToolLoot = new HashMap<>();
    private final Map<Integer, Long> pendingToolUntil = new HashMap<>();
    private boolean toolBaselineReady = false;
    private long lastWarningAt = 0L;
    private String lastWarning = "";
    private static final DateTimeFormatter HISTORY_DATE = DateTimeFormatter.ofPattern("MM-dd HH:mm");
    private long lastAlchemyAt = 0L;
    private long lastBlockedAt = 0;
    private String lastBlockedTarget = "";
    private SlayerHunterPanel panel;
    // Conservative activity detectors. User can correct any false positives in the sidebar.
    private final Map<Skill, Integer> previousXp = new HashMap<>();
    private long lastWoodcutXp = 0L;
    private long lastOreCreditAt = 0L;
    private long lastTreeDebitAt = 0L;
    private long lastFishCreditAt = 0L;
    private long lastThiefClickAt = 0L;
    private String lastThiefClick = "";
    private final Set<Integer> knownTreeObjects = new HashSet<>();
    // Only suppress identical successive notifications within a very short interval.
    private String lastTrackedMessage = "";
    private long lastTrackedAt = 0L;

    @Override
    protected void startUp()
    {
        lastInventoryCoins = -1;
        coinLoot.clear();
        previousXp.clear();
        lastCarriedTools.clear();
        pendingToolLoot.clear();
        pendingToolUntil.clear();
        toolBaselineReady = false;
        questException = get("questException") != 0;
        highlightTargets = get("highlightTargets") != 0;
        bankingAdvisory = get("bankingAdvisory") != 0;
        targetOverlay = new SlayerTargetOverlay();
        overlayManager.add(targetOverlay);
        panel = new SlayerHunterPanel(this);
        BufferedImage icon = ImageUtil.loadImageResource(getClass(), "/tracker.png");
        navigationButton = NavigationButton.builder()
            .tooltip("Slayer Hunter Tracker")
            .priority(6)
            .icon(icon)
            .panel(panel)
            .build();
        toolbar.addNavigation(navigationButton);
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event)
    {
        int id = event.getContainerId();
        if (id != InventoryID.INVENTORY.getId() && id != InventoryID.EQUIPMENT.getId()) return;
        ItemContainer container = event.getItemContainer();
        if (container == null) return;
        if (id == InventoryID.INVENTORY.getId())
        {
            int coins = 0;
            for (Item it : container.getItems())
            {
                if (it != null && it.getId() == 995)
                {
                    coins += it.getQuantity();
                }
            }
            onCoinStackChanged(coins);
        }
        // Combine inventory AND equipment. Equipping or unequipping is not an acquisition.
        // Initial observation is a baseline, not a free refill.
        Map<Integer, Integer> carried = new HashMap<>();
        for (InventoryID inventory : new InventoryID[]{InventoryID.INVENTORY, InventoryID.EQUIPMENT})
        {
            ItemContainer items = client.getItemContainer(inventory);
            if (items == null) continue;
            for (Item item : items.getItems())
            {
                if (item == null || item.getId() <= 0 || item.getQuantity() <= 0) continue;
                String name = itemManager.getItemComposition(item.getId()).getName();
                if (ToolKind.fromName(name) == null) continue;
                carried.merge(item.getId(), item.getQuantity(), Integer::sum);
            }
        }
        if (!toolBaselineReady)
        {
            lastCarriedTools.putAll(carried);
            toolBaselineReady = true;
        }
        else
        {
            for (Map.Entry<Integer, Integer> entry : carried.entrySet())
            {
                int itemId = entry.getKey();
                int gained = Math.max(0, entry.getValue() - lastCarriedTools.getOrDefault(itemId, 0));
                if (gained == 0) continue;
                int pending = pendingToolLoot.getOrDefault(itemId, 0);
                if (System.currentTimeMillis() > pendingToolUntil.getOrDefault(itemId, 0L)) pending = 0;
                int confirmed = Math.min(gained, pending);
                if (confirmed > 0)
                {
                    String name = itemManager.getItemComposition(itemId).getName();
                    int uses = ToolKind.initialUses(name);
                    String key = "tool.charge." + itemId;
                    if (uses == ToolKind.UNLIMITED) set(key, ToolKind.UNLIMITED);
                    else if (uses > 0) set(key, (int)Math.min((long)Integer.MAX_VALUE - 1, (long)get(key) + (long)uses * confirmed));
                    pendingToolLoot.put(itemId, pending - confirmed);
                    history("Obtained " + confirmed + " x " + name + ": +" + (uses == ToolKind.UNLIMITED ? "unlimited" : (uses * confirmed)) + " charges");
                }
            }
        }
        lastCarriedTools.clear();
        lastCarriedTools.putAll(carried);
        for (Integer itemId : carried.keySet())
        {
            ToolKind kind = ToolKind.fromName(itemManager.getItemComposition(itemId).getName());
            if (kind != null && toolId(kind) != itemId) set("tool." + kind.key + ".id", itemId);
        }
    }

    private boolean wearingWealthRing()
    {
        ItemContainer eq = client.getItemContainer(InventoryID.EQUIPMENT);
        if (eq == null) return false;
        for (Item item : eq.getItems())
        {
            if (item == null || item.getId() <= 0) continue;
            String name = itemManager.getItemComposition(item.getId()).getName().toLowerCase(Locale.ROOT);
            if (name.startsWith("ring of wealth")) return true;
        }
        return false;
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged event)
    {
        Actor actor = event.getActor();
        Player local = client.getLocalPlayer();
        if (actor != local || local == null) return;
        // Standard high/low alchemy cast animations. Never count the ensuing coin increase.
        if (local.getAnimation() == 713 || local.getAnimation() == 712)
        {
            lastAlchemyAt = System.currentTimeMillis();
        }
    }

    @Subscribe
    public void onNpcLootReceived(NpcLootReceived event)
    {
        if (event.getNpc() == null || !allowedTarget(event.getNpc().getName())) return;
        if (get("taskRemaining") > 0) adjust("taskRemaining", -1);
        int coins = 0;
        for (ItemStack item : event.getItems())
        {
            if (item.getId() > 0 && item.getQuantity() > 0
                && ToolKind.fromName(itemManager.getItemComposition(item.getId()).getName()) != null)
            {
                pendingToolLoot.merge(item.getId(), item.getQuantity(), Integer::sum);
                pendingToolUntil.put(item.getId(), System.currentTimeMillis() + 60000L);
            }
            if (item.getId() == 995 && item.getQuantity() > 0)
            {
                coins += item.getQuantity();
            }
        }
        // RuneLite loot tracking may omit Ring of Wealth auto-collected currency.
        // Fallback ONLY for a nearby recognized Slayer kill while a wealth ring is worn.
        boolean ringFallback = wearingWealthRing();
        if (coins > 0 || ringFallback)
        {
            coinLoot.addLast(new CoinLoot(System.currentTimeMillis() + 30000L, coins, ringFallback));
        }
    }

    private void onCoinStackChanged(int current)
    {
        if (lastInventoryCoins < 0)
        {
            lastInventoryCoins = current;
            return; // baseline; never count an existing stack at login
        }
        int difference = current - lastInventoryCoins;
        lastInventoryCoins = current;
        if (difference <= 0 || taskName().isEmpty()) return;
        long now = System.currentTimeMillis();
        if (now - lastAlchemyAt < 2500L) return;
        coinLoot.removeIf(loot -> loot.expiresAt < now);
        int eligible = 0;
        for (CoinLoot loot : coinLoot)
        {
            if (difference <= 0) break;
            if (loot.coins > 0)
            {
                int take = Math.min(difference, loot.coins);
                eligible += take;
                difference -= take;
                loot.coins -= take;
                if (take > 0) loot.ringFallback = false;
            }
            else if (loot.ringFallback && loot.expiresAt - now >= 27000L)
            {
                // Auto-looted coins should arrive within a few seconds of the kill.
                eligible += difference;
                difference = 0;
                loot.coins = 0;
                loot.ringFallback = false;
            }
        }
        coinLoot.removeIf(loot -> loot.coins == 0 && !loot.ringFallback);
        if (eligible > 0)
        {
            slayerGp(eligible);
            history("Slayer coin loot: " + eligible + " GP");
        }
    }

    ItemManager itemManager() { return itemManager; }
    net.runelite.client.game.SkillIconManager skillIconManager() { return skillIconManager; }
    String toolName(ToolKind kind)
    {
        int id = get("tool." + kind.key + ".id");
        return id == 0 ? kind.placeholder : itemManager.getItemComposition(id).getName();
    }
    int toolId(ToolKind kind) { return get("tool." + kind.key + ".id"); }
    int toolCharges(ToolKind kind)
    {
        int id = toolId(kind);
        return id == 0 ? 0 : get("tool.charge." + id);
    }
    void useTool(ToolKind kind, int amount)
    {
        int id = toolId(kind);
        if (id == 0) return;
        if (toolCharges(kind) == ToolKind.UNLIMITED) return;
        adjust("tool.charge." + id, -amount);
        int remaining = toolCharges(kind);
        if (remaining != ToolKind.UNLIMITED && remaining <= 100
            && (remaining == 100 || remaining == 50 || remaining == 10 || remaining == 0))
            warn("Your " + kind.key + " has " + remaining + " charges remaining.");
        if (kind == ToolKind.AXE) adjust("treesCut", amount);
        if (kind == ToolKind.PICKAXE) adjust("oresMined", amount);
        if (kind == ToolKind.FISHING) adjust("fishCaught", amount);
    }

    @Override
    protected void shutDown()
    {
        if (targetOverlay != null) overlayManager.remove(targetOverlay);
        targetOverlay = null;
        if (navigationButton != null) toolbar.removeNavigation(navigationButton);
        navigationButton = null;
        coinLoot.clear();
        pendingToolLoot.clear();
        pendingToolUntil.clear();
        lastCarriedTools.clear();
        toolBaselineReady = false;
        lastInventoryCoins = -1;
        panel = null;
    }

    int get(String key)
    {
        String value = configManager.getConfiguration(GROUP, key);
        try { return value == null ? 0 : Math.max(0, Integer.parseInt(value)); }
        catch (NumberFormatException ignored) { return 0; }
    }

    void set(String key, int value)
    {
        configManager.setConfiguration(GROUP, key, Math.max(0, value));
        if (panel != null) panel.refresh();
    }

    void adjust(String key, int delta)
    {
        long value = (long) get(key) + delta;
        set(key, (int) Math.min(Integer.MAX_VALUE, Math.max(0L, value)));
    }

    String taskName()
    {
        String value = configManager.getConfiguration(GROUP, "activeTask");
        return value == null ? "" : value.trim();
    }

    void taskName(String name)
    {
        configManager.setConfiguration(GROUP, "activeTask", name == null ? "" : name.trim());
        if (panel != null) panel.refresh();
    }

    boolean protectionEnabled()
    {
        return !"false".equals(configManager.getConfiguration(GROUP, "protectCombat"));
    }

    void setProtection(boolean enabled)
    {
        configManager.setConfiguration(GROUP, "protectCombat", Boolean.toString(enabled));
        if (panel != null) panel.refresh();
    }

    private static String clean(String text)
    {
        return text == null ? "" : text.replaceAll("<[^>]*>", "")
            .replace('\u00a0', ' ').trim().toLowerCase(Locale.ROOT);
    }

    // This conservative first version only permits exact matches and plural/singular names.
    // Task variants and exceptions must be added individually after validating them.
    private boolean allowedTarget(String targetName)
    {
        String expected = clean(taskName());
        String target = clean(targetName);
        if (expected.isEmpty() || target.isEmpty()) return false;
        if (expected.equals(target)) return true;
        // Known variant names where a creature normally qualifies for its base task.
        if ((expected.equals("bloodvelds") || expected.equals("bloodveld")) && (target.equals("mutated bloodveld") || target.equals("bloodveld"))) return true;
        if ((expected.equals("abyssal demons") || expected.equals("abyssal demon")) && target.equals("abyssal sire")) return true;
        if ((expected.equals("hellhounds") || expected.equals("hellhound")) && target.equals("cerberus")) return true;
        if ((expected.equals("greater demons") || expected.equals("greater demon")) && (target.equals("k'ril tsutsaroth") || target.equals("skotizo"))) return true;
        if ((expected.equals("blue dragons") || expected.equals("blue dragon")) && target.equals("vorkath")) return true;
        if ((expected.equals("dagannoths") || expected.equals("dagannoth")) && target.startsWith("dagannoth ")) return true;
        if (expected.endsWith("s") && expected.substring(0, expected.length() - 1).equals(target)) return true;
        return target.endsWith("s") && target.substring(0, target.length() - 1).equals(expected);
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event)
    {
        String clickedOption = clean(event.getMenuOption());
        if (thievingAction(clickedOption))
        {
            int available = get("thiefEarned") - get("thiefUsed");
            if (available <= 0)
            {
                event.consume();
                warn("No Thieving attempts available.");
                return;
            }
            recordThievingAction(event, clickedOption);
        }
        if (!protectionEnabled() || questException) return;
        MenuAction action = event.getMenuAction();
        if (action != MenuAction.NPC_FIRST_OPTION && action != MenuAction.NPC_SECOND_OPTION
            && action != MenuAction.NPC_THIRD_OPTION && action != MenuAction.NPC_FOURTH_OPTION
            && action != MenuAction.NPC_FIFTH_OPTION && action != MenuAction.WIDGET_TARGET_ON_NPC)
        {
            return;
        }
        String option = clean(event.getMenuOption());
        // Only block attack and hostile spell actions. Do not block Talk-to, Examine, etc.
        boolean attack = option.equals("attack") || (action == MenuAction.WIDGET_TARGET_ON_NPC && (option.startsWith("cast") || option.startsWith("use")));
        if (!attack) return;
        NPC npc = event.getMenuEntry() == null ? null : event.getMenuEntry().getNpc();
        String npcName = npc != null ? npc.getName() : clean(event.getMenuTarget());
        if (allowedTarget(npcName)) return;
        event.consume();
        history("Blocked off-task attack: " + npcName);
        adjust("violations", 1);
        long now = System.currentTimeMillis();
        if (!npcName.equals(lastBlockedTarget) || now - lastBlockedAt > 2000)
        {
            lastBlockedTarget = npcName;
            lastBlockedAt = now;
            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Slayer Hunter: blocked attack on " + npcName + " (task: " + (taskName().isEmpty() ? "unknown" : taskName()) + ").", null);
        }
    }

    // A successful ore or fish normally awards skill XP; no inventory quantity assumption.
    // Explicitly avoid charging unconfigured lower-tier tools.
    private void debitIfReady(ToolKind kind)
    {
        if (toolId(kind) == 0) return;
        int n = toolCharges(kind);
        if (n <= 0)
        {
            warn("No charges available for " + kind.key + ".");
            return;
        }
        if (n == ToolKind.UNLIMITED) return;
        useTool(kind, 1);
    }

    @Subscribe
    public void onStatChanged(StatChanged event)
    {
        Skill skill = event.getSkill();
        if (skill != Skill.WOODCUTTING && skill != Skill.MINING && skill != Skill.FISHING) return;
        int xp = event.getXp();
        Integer old = previousXp.put(skill, xp);
        if (old == null || xp <= old || client.getGameState() != GameState.LOGGED_IN) return;
        long now = System.currentTimeMillis();
        if (skill == Skill.WOODCUTTING)
        {
            // A chop XP event alone is not a full tree: wait for its despawn.
            lastWoodcutXp = now;
        }
        else if (skill == Skill.MINING && now - lastOreCreditAt >= 450)
        {
            // Approximation: mining XP usually accompanies an ore; special mining
            // activities can award XP differently and need manual correction.
            debitIfReady(ToolKind.PICKAXE);
            lastOreCreditAt = now;
        }
        else if (skill == Skill.FISHING && now - lastFishCreditAt >= 450)
        {
            // Tentative: one fishing catch/XP award; some catches yield multiple fish.
            debitIfReady(ToolKind.FISHING);
            lastFishCreditAt = now;
        }
    }

    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned event)
    {
        GameObject object = event.getGameObject();
        if (object == null || client.getLocalPlayer() == null) return;
        // Tree depletion: only a recognized tree disappearing shortly after a
        // woodcutting XP award, within short range of the player.
        String name = client.getObjectDefinition(object.getId()) == null ? ""
            : client.getObjectDefinition(object.getId()).getName();
        if (name == null || !name.toLowerCase(Locale.ROOT).matches(".*(tree|oak|willow|maple|yew|magic|redwood|teak|mahogany|arctic pine).*")) return;
        long now = System.currentTimeMillis();
        if (now - lastWoodcutXp > 4500 || now - lastTreeDebitAt < 800) return;
        if (object.getWorldLocation() == null || client.getLocalPlayer().getWorldLocation().distanceTo(object.getWorldLocation()) > 3) return;
        debitIfReady(ToolKind.AXE);
        lastTreeDebitAt = now;
        lastWoodcutXp = 0;
    }

    private static boolean thievingAction(String option)
    {
        return option.equals("pickpocket") || option.equals("steal-from")
            || option.equals("steal") || option.equals("lockpick")
            || option.equals("pick-lock") || option.equals("crack")
            || option.equals("loot") || option.equals("search for traps");
    }

    private void recordThievingAction(MenuOptionClicked event, String option)
    {
        // Counting the attempt at click time handles failures as well as successes.
        // Some unrelated Loot/Search interactions have the same menu label;
        // avoid ambiguous options rather than mischarging them.
        if (option.equals("loot") || option.equals("crack") || option.equals("search for traps")) return;
        String target = clean(event.getMenuTarget());
        String signature = option + ":" + target;
        long now = System.currentTimeMillis();
        if (signature.equals(lastThiefClick) && now - lastThiefClickAt < 500) return;
        lastThiefClick = signature;
        lastThiefClickAt = now;
        adjust("thiefUsed", 1);
        history("Thieving attempt: " + option);
    }

    @Subscribe
    public void onChatMessage(ChatMessage event)
    {
        ChatMessageType type = event.getType();
        if (type != ChatMessageType.GAMEMESSAGE && type != ChatMessageType.SPAM)
        {
            return;
        }
        String message = event.getMessage().replaceAll("<[^>]*>", "")
            .replace('\u00a0', ' ').trim().toLowerCase(Locale.ROOT);
        Matcher assignment = TASK_ASSIGNMENT.matcher(message);
        if (assignment.find()) taskName(assignment.group(1));
        // Slayer task completion is communicated via game chat.
        boolean taskComplete = message.contains("you have completed your task")
            || message.contains("you've completed your task")
            || message.contains("you have completed your slayer task");
        // Agility course lap messages differ slightly across courses.
        // Avoid counting partial obstacles and generic Agility XP as laps.
        boolean lapComplete = message.contains("you have completed a lap")
            || message.contains("you've completed a lap")
            || (message.contains("lap time:") && message.contains("course"));
        if (!taskComplete && !lapComplete)
        {
            return;
        }
        long now = System.currentTimeMillis();
        if (message.equals(lastTrackedMessage) && now - lastTrackedAt < 2500)
        {
            return;
        }
        lastTrackedMessage = message;
        lastTrackedAt = now;
        if (taskComplete)
        {
            taskName("");
            set("taskRemaining", 0);
            // Start with the streak before the NEXT completion, editable in the panel.
            int nextStreak = get("streak") + 1;
            set("streak", nextStreak);
            completedTask(nextStreak);
        }
        else
        {
            adjust("lapsUsed", 1);
            history("Agility lap completed");
        }
    }

    private void warn(String message)
    {
        long now = System.currentTimeMillis();
        if (message.equals(lastWarning) && now - lastWarningAt < 4000L) return;
        lastWarning = message;
        lastWarningAt = now;
        history("WARNING: " + message);
        adjust("warnings", 1);
        if (client.getGameState() == GameState.LOGGED_IN)
        {
            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Slayer Hunter: " + message, null);
        }
    }

    void history(String message)
    {
        String event = LocalDateTime.now().format(HISTORY_DATE) + "  "
            + message.replace("\n", " ").replace("\r", " ");
        String existing = configManager.getConfiguration(GROUP, "activityHistory");
        String value = event + (existing == null || existing.isEmpty() ? "" : "\n" + existing);
        String[] lines = value.split("\n");
        if (lines.length > 100) value = String.join("\n", java.util.Arrays.copyOf(lines, 100));
        configManager.setConfiguration(GROUP, "activityHistory", value);
    }

    String historyText()
    {
        String stored = configManager.getConfiguration(GROUP, "activityHistory");
        return stored == null || stored.isEmpty() ? "No activity recorded yet." : stored;
    }

    void completedTask(int streak)
    {
        adjust("tasks", 1);
        history("Slayer task completed; +5 Agility laps");
        adjust("lapCredits", 5);
        adjust("thiefEarned", 1 + (streak > 0 && streak % 100 == 0 ? 100 : streak > 0 && streak % 50 == 0 ? 50 : streak > 0 && streak % 10 == 0 ? 10 : 0));
    }


    boolean questException() { return questException; }
    void toggleQuestException()
    {
        questException = !questException;
        set("questException", questException ? 1 : 0);
        history("Quest combat exception " + (questException ? "enabled" : "disabled"));
    }
    boolean highlightTargets() { return highlightTargets; }
    void toggleHighlight()
    {
        highlightTargets = !highlightTargets;
        set("highlightTargets", highlightTargets ? 1 : 0);
    }
    boolean bankingAdvisory() { return bankingAdvisory; }
    void toggleBankingAdvisory()
    {
        bankingAdvisory = !bankingAdvisory;
        set("bankingAdvisory", bankingAdvisory ? 1 : 0);
    }
    String statisticsText()
    {
        return "SLAYER + HUNTER CHALLENGE STATISTICS\n\n"
            + "Slayer tasks: " + get("tasks") + "\n"
            + "Current streak: " + get("streak") + "\n"
            + "Agility laps earned: " + get("lapCredits") + "\n"
            + "Agility laps used: " + get("lapsUsed") + "\n"
            + "Thieving attempts earned: " + get("thiefEarned") + "\n"
            + "Thieving attempts used: " + get("thiefUsed") + "\n"
            + "Trees recorded: " + get("treesCut") + "\n"
            + "Ores recorded: " + get("oresMined") + "\n"
            + "Fish catches recorded: " + get("fishCaught") + "\n"
            + "Blocked attacks: " + get("violations") + "\n"
            + "Warnings issued: " + get("warnings") + "\n\n"
            + "Activity counters may include unverified automatic detections.";
    }
    String backupText()
    {
        StringBuilder output = new StringBuilder("# Slayer Hunter Tracker settings backup\n");
        String[] keys = {"tasks", "streak", "lapCredits", "lapsUsed", "thiefEarned",
            "thiefUsed", "gpRemainder", "treesCut", "oresMined", "fishCaught",
            "violations", "warnings", "taskRemaining", "taskTotal", "questException",
            "highlightTargets", "bankingAdvisory"};
        for (String key : keys) output.append(key).append('=').append(get(key)).append('\n');
        output.append("activeTask=").append(taskName()).append('\n');
        for (ToolKind kind : ToolKind.values())
        {
            int id = toolId(kind);
            output.append("tool.").append(kind.key).append(".id=").append(id).append('\n');
            if (id > 0) output.append("tool.charge.").append(id).append('=')
                .append(toolCharges(kind)).append('\n');
        }
        output.append("\n# ACTIVITY LOG\n").append(historyText());
        return output.toString();
    }
    String bankReviewText()
    {
        ItemContainer bank = client.getItemContainer(InventoryID.BANK);
        if (bank == null) return "Bank not open. Open your bank and try again.\n"
            + "The banking whitelist is not final, so this is advisory only.";
        String allow = configManager.getConfiguration(GROUP, "bankAllowedNames");
        if (allow == null || allow.trim().isEmpty())
            return "No permitted bank items configured.\nSet a comma-separated whitelist first."
                + "\nThe plugin will warn, never transfer or destroy items.";
        java.util.Set<String> allowed = new java.util.HashSet<>();
        for (String item : allow.split(",")) allowed.add(clean(item));
        StringBuilder result = new StringBuilder("REVIEW ITEMS NOT ON WHITELIST\n\n");
        int found = 0;
        for (Item item : bank.getItems())
        {
            if (item == null || item.getId() <= 0 || item.getQuantity() <= 0) continue;
            String name = itemManager.getItemComposition(item.getId()).getName();
            if (!allowed.contains(clean(name)))
            {
                result.append(name).append(" x").append(item.getQuantity()).append('\n');
                found++;
                if (found >= 80) { result.append("...more items omitted\n"); break; }
            }
        }
        if (found == 0) result.append("No unlisted items found.");
        return result.toString();
    }
    void bankAllowedNames(String names)
    {
        configManager.setConfiguration(GROUP, "bankAllowedNames", names);
    }
    String bankAllowedNames()
    {
        String value = configManager.getConfiguration(GROUP, "bankAllowedNames");
        return value == null ? "" : value;
    }
    private class SlayerTargetOverlay extends Overlay
    {
        SlayerTargetOverlay()
        {
            setPosition(OverlayPosition.DYNAMIC);
            setLayer(OverlayLayer.ABOVE_SCENE);
        }
        @Override public java.awt.Dimension render(Graphics2D graphics)
        {
            if (!highlightTargets || client.getGameState() != GameState.LOGGED_IN || taskName().isEmpty()) return null;
            for (NPC npc : client.getNpcs())
            {
                if (npc == null || !allowedTarget(npc.getName())) continue;
                java.awt.Shape hull = npc.getConvexHull();
                if (hull == null) continue;
                graphics.setColor(new Color(60, 215, 90, 50));
                graphics.fill(hull);
                graphics.setColor(new Color(60, 215, 90, 170));
                graphics.setStroke(new BasicStroke(2));
                graphics.draw(hull);
            }
            return null;
        }
    }

    void slayerGp(int gp)
    {
        long pending = (long) get("gpRemainder") + gp;
        int earned = (int) (pending / 1000L);
        set("gpRemainder", (int) (pending % 1000L));
        adjust("thiefEarned", earned);
    }
}
