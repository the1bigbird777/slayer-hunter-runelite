package com.slayerhunter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import net.runelite.client.ui.PluginPanel;

public class SlayerHunterPanel extends PluginPanel
{
    private static final String BUILT_IN_RULES = "SLAYER + HUNTER MAIN \u2014 CHALLENGE RULES\n"
            + "Updated: 2026-10-11\n"
            + "Banking limits and Thieving streak bonuses remain undecided.\n"
            + "\n"
            + "1. GENERAL RULES\n"
            + "\u2022 Slayer may be trained through assigned tasks; Hunter is unrestricted.\n"
            + "\u2022 Combat training and bosses are Slayer-task only, except approved activities: Pest Control, Barbarian Assault, quest-required fights and eligible off-task Combat Achievements.\n"
            + "\u2022 A Combat Achievement that can be completed on a Slayer task must be completed on-task.\n"
            + "\u2022 XP from quests, lamps, random events and achievement diaries is allowed in any skill.\n"
            + "\u2022 No direct player trading. Selling personally obtained items on the Grand Exchange is allowed.\n"
            + "\n"
            + "2. SKILLS AND RESOURCES\n"
            + "\u2022 Slayer: fight monsters on assigned Slayer tasks.\n"
            + "\u2022 Hunter: completely unrestricted, at any time.\n"
            + "\u2022 Cooking: use food obtained through Hunter, Slayer or permitted purchases.\n"
            + "\u2022 Prayer: use bones obtained through Hunter or Slayer; don't buy bones.\n"
            + "\u2022 Farming: use Slayer-obtained seeds.\n"
            + "\u2022 Herblore: train using permitted Slayer/Farming ingredients.\n"
            + "\u2022 Runecrafting: train using pure essence from Slayer. Buying combat runes is permitted.\n"
            + "\u2022 Crafting and Smithing: use materials permitted by the challenge, primarily Slayer resources.\n"
            + "\u2022 Construction: use materials personally obtained from any permitted source.\n"
            + "\u2022 Woodcutting: use legitimately obtained axes with available durability charges.\n"
            + "\u2022 Mining: use legitimately obtained pickaxes with available durability charges.\n"
            + "\u2022 Firemaking: use creature-dropped logs or logs cut with permitted axes.\n"
            + "\u2022 Fletching: use permitted logs and other permitted resources.\n"
            + "\u2022 Fishing: acquire a permitted fishing tool before training; charge limits apply.\n"
            + "\u2022 Agility: earn five course laps per Slayer task; unused laps carry over, any unlocked course permitted. Travel shortcuts do not consume laps.\n"
            + "\u2022 Thieving: spend earned attempts; failures count as attempts.\n"
            + "\n"
            + "3. TOOL DURABILITY \u2014 WOODCUTTING\n"
            + "One use = one entire tree chopped down (not one log).\n"
            + "Bronze axe .......... 10 trees\n"
            + "Iron axe ............ 15 trees\n"
            + "Steel axe ........... 25 trees\n"
            + "Black axe ........... 30 trees\n"
            + "Mithril axe ......... 40 trees\n"
            + "Adamant axe ......... 60 trees\n"
            + "Rune axe ............ 100 trees\n"
            + "Dragon axe .......... 2,500 trees\n"
            + "Crystal axe ......... 5,000 trees\n"
            + "3rd age axe ......... Unlimited\n"
            + "\n"
            + "4. TOOL DURABILITY \u2014 MINING\n"
            + "One use = one successfully mined ore.\n"
            + "Bronze pickaxe ...... 50 ores\n"
            + "Iron pickaxe ........ 75 ores\n"
            + "Steel pickaxe ....... 125 ores\n"
            + "Black pickaxe ....... 200 ores\n"
            + "Mithril pickaxe ..... 275 ores\n"
            + "Adamant pickaxe ..... 400 ores\n"
            + "Rune pickaxe ........ 550 ores\n"
            + "Dragon pickaxe ...... 2,500 ores\n"
            + "Crystal pickaxe ..... 5,000 ores\n"
            + "3rd age pickaxe ..... Unlimited\n"
            + "\n"
            + "5. TOOL DURABILITY \u2014 FISHING\n"
            + "One use = one successful catch; failed attempts don't count.\n"
            + "Small fishing net ... 100 catches\n"
            + "Big fishing net ..... 200 catches\n"
            + "Fishing rod ......... 150 catches\n"
            + "Fly fishing rod ..... 250 catches\n"
            + "Lobster pot ......... 400 catches\n"
            + "Regular harpoon ..... 500 catches\n"
            + "Dragon tools ........ 2,500 uses\n"
            + "Crystal tools ....... 5,000 uses\n"
            + "Barbarian rod ....... Unlimited\n"
            + "Karambwan vessel .... Unlimited\n"
            + "Unlisted fishing tools still need specific charge rules.\n"
            + "\n"
            + "6. AGILITY\n"
            + "\u2022 Completing a Slayer task earns five Agility laps.\n"
            + "\u2022 Unused laps carry forward.\n"
            + "\u2022 Laps may be spent at any unlocked Agility course.\n"
            + "\u2022 Shortcuts used for travel don't spend laps.\n"
            + "\n"
            + "7. THIEVING\n"
            + "\u2022 Earn one attempt per 1,000 GP earned directly from Slayer monster drops.\n"
            + "\u2022 Earn one attempt for completing a Slayer task.\n"
            + "\u2022 GP gained through High Alchemy or sales does not count.\n"
            + "\u2022 Failed attempts consume an attempt; unused attempts carry forward.\n"
            + "\u2022 STREAK BONUS DECISION PENDING: Previously +10 at streak 10, +50 at 50, +100 at 100 (non-stacking). User may remove these bonuses. CURRENT PLUGIN LOGIC STILL AWARDS THEM until changed.\n"
            + "\n"
            + "8. FOOD AND POTIONS\n"
            + "\u2022 Food and potions MAY be purchased.\n"
            + "\u2022 Food can alternatively come from Hunter and Slayer; potions can be made through permitted Herblore.\n"
            + "\n"
            + "9. PURCHASES AND ITEM SOURCING\n"
            + "\u2022 Buying weapon/armour upgrades, ammunition and runes is allowed.\n"
            + "\u2022 For restricted tools and items available as Slayer creature drops, obtain them from those creatures rather than buying them.\n"
            + "\u2022 Do not trade directly with other players.\n"
            + "\u2022 You may sell personally obtained items freely through the GE.\n"
            + "\u2022 Ground spawns are permitted only where no creature/Hunter/other permitted source can supply the item.\n"
            + "\n"
            + "10. QUESTS, DIARIES AND CLUES\n"
            + "\u2022 All quests are allowed. Get required items from creature drops when possible; otherwise an alternate source may be used. Quest-only items are permitted.\n"
            + "\u2022 Quest XP may be awarded to any skill.\n"
            + "\u2022 Achievement diaries of any tier may be completed when their requirements can be met under the normal restrictions. Diary rewards and XP lamps are permitted; diary activities do NOT grant a blanket exemption to training restrictions.\n"
            + "\u2022 All clue tiers may be completed at any time; rewards may be used or sold subject to existing combat/storage rules.\n"
            + "\n"
            + "11. MINIGAMES AND COMBAT EXCEPTIONS\n"
            + "\u2022 Pest Control and Barbarian Assault are allowed for combat training and rewards.\n"
            + "\u2022 Quest-required fights are allowed.\n"
            + "\u2022 Off-task Combat Achievements are allowed only when the achievement cannot be done during a Slayer task.\n"
            + "\u2022 Skilling minigames, including Wintertodt, Tempoross and Guardians of the Rift, are not allowed for XP/rewards.\n"
            + "\n"
            + "12. DEATH AND BANKING\n"
            + "\u2022 You may recover your own items after death by any method.\n"
            + "\u2022 Banking is limited; the exact whitelist is not yet final.\n"
            + "\n"
            + "";

    private static final int PANEL_WIDTH = 190;
    private static final Color BG = new Color(35, 37, 40);
    private static final Color CARD = new Color(47, 49, 54);
    private static final Color CARD_ALT = new Color(42, 44, 47);
    private static final Color BORDER = new Color(69, 73, 79);
    private static final Color TEXT = new Color(225, 227, 230);
    private static final Color MUTED = new Color(170, 174, 180);
    private static final Color ACCENT = new Color(140, 220, 130);

    private final SlayerHunterPlugin plugin;
    private final JPanel content = new JPanel();

    SlayerHunterPanel(SlayerHunterPlugin plugin)
    {
        super(false);
        this.plugin = plugin;
        setLayout(new BorderLayout());
        setBackground(BG);

        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(BG);
        content.setBorder(new EmptyBorder(8, 8, 8, 8));

        JScrollPane scroll = new JScrollPane(content,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
        refresh();
    }

    void refresh()
    {
        SwingUtilities.invokeLater(() -> {
            content.removeAll();
            content.add(buildHeader());
            content.add(Box.createVerticalStrut(8));
            addCard(buildSlayerCard());
            content.add(Box.createVerticalStrut(8));
            addCard(buildAgilityCard());
            content.add(Box.createVerticalStrut(8));
            addCard(buildThievingCard());
            content.add(Box.createVerticalStrut(8));
            addCard(buildToolsCard());
            content.add(Box.createVerticalStrut(8));
            addCard(buildActionCard());
            content.add(Box.createVerticalGlue());
            content.revalidate();
            content.repaint();
        });
    }

    // Prevent BoxLayout from stretching each card to fill unused sidebar height.
    private void addCard(JPanel panel)
    {
        Dimension preferred = panel.getPreferredSize();
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, preferred.height));
        content.add(panel);
    }

    private JPanel buildHeader()
    {
        JPanel panel = transparentPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("SLAYER + HUNTER");
        title.setAlignmentX(LEFT_ALIGNMENT);
        title.setForeground(ACCENT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));

        JLabel subtitle = new JLabel("v2.0  |  saved locally");
        subtitle.setAlignmentX(LEFT_ALIGNMENT);
        subtitle.setForeground(MUTED);
        subtitle.setFont(subtitle.getFont().deriveFont(Font.PLAIN, 13f));
        subtitle.setBorder(new EmptyBorder(2, 0, 0, 0));

        panel.add(title);
        panel.add(subtitle);
        return panel;
    }

    private JPanel buildSlayerCard()
    {
        JPanel card = card();
        card.add(sectionHeader("Slayer", net.runelite.api.Skill.SLAYER));
        card.add(infoLine("Active task", plugin.taskName().isEmpty() ? "UNKNOWN" : plugin.taskName()));
        if (plugin.taskName().isEmpty())
        {
            card.add(note("Unknown task = attacks blocked"));
        }
        card.add(toggleButton(plugin.protectionEnabled() ? "Attack guard: ON" : "Attack guard: OFF", e -> {
            if (plugin.protectionEnabled())
            {
                if (JOptionPane.showConfirmDialog(this,
                    "Disable off-task Attack protection? This permits all attack clicks.",
                    "Disable guard", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)
                {
                    return;
                }
            }
            plugin.setProtection(!plugin.protectionEnabled());
        }));
        card.add(actionButton("Set task / streak", e -> {
            String result = JOptionPane.showInputDialog(this,
                "Assigned Slayer NPC name (leave blank to clear):", plugin.taskName());
            if (result == null) return;
            String current = result.trim();
            String input = JOptionPane.showInputDialog(this,
                "Current Slayer task streak (before next completion):", "" + plugin.get("streak"));
            if (input == null) return;
            try
            {
                int number = Integer.parseInt(input.trim());
                if (number < 0) throw new NumberFormatException();
                plugin.taskName(current);
                plugin.set("streak", number);
            }
            catch (NumberFormatException ex)
            {
                error("Enter a nonnegative streak number.");
            }
        }));
        card.add(counterRow("Completed tasks", "tasks", 1));
        card.add(infoLine("Current streak", String.valueOf(plugin.get("streak"))));
        card.add(infoLine("Kills left (estimated)", String.valueOf(plugin.get("taskRemaining"))));
        javax.swing.JProgressBar progress = new javax.swing.JProgressBar();
        progress.setAlignmentX(LEFT_ALIGNMENT);
        progress.setMaximum(Math.max(1, plugin.get("taskTotal")));
        progress.setValue(Math.max(0, plugin.get("taskTotal") - plugin.get("taskRemaining")));
        progress.setStringPainted(true);
        progress.setString("Slayer task progress");
        card.add(progress);
        card.add(actionButton("Set kills left / total", e -> {
            String value = JOptionPane.showInputDialog(this, "Remaining kills, then total kills (example: 35,100):",
                plugin.get("taskRemaining") + "," + plugin.get("taskTotal"));
            if (value == null) return;
            try {
                String[] parts = value.split(",");
                int left = Integer.parseInt(parts[0].trim());
                int total = Integer.parseInt(parts[1].trim());
                if (left < 0 || total < left || total == 0) throw new NumberFormatException();
                plugin.set("taskTotal", total); plugin.set("taskRemaining", left);
            } catch (RuntimeException ex) { error("Enter valid numbers: remaining,total"); }
        }));
        card.add(toggleButton(plugin.questException() ? "Quest exception: ON" : "Quest exception: OFF",
            e -> plugin.toggleQuestException()));
        card.add(toggleButton(plugin.highlightTargets() ? "Task highlights: ON" : "Task highlights: OFF",
            e -> plugin.toggleHighlight()));
        card.add(actionButton("Complete Slayer task", e -> {
            String s = JOptionPane.showInputDialog(this,
                "Slayer streak number AFTER task completion (0 if unknown):", "0");
            if (s == null) return;
            try
            {
                int n = Math.max(0, Integer.parseInt(s.trim()));
                plugin.set("streak", n);
                plugin.completedTask(n);
            }
            catch (NumberFormatException ex)
            {
                error("Enter a whole number.");
            }
        }));
        return card;
    }

    private JPanel buildAgilityCard()
    {
        JPanel card = card();
        card.add(sectionHeader("Agility", net.runelite.api.Skill.AGILITY));
        card.add(counterRow("Laps earned", "lapCredits", 5));
        card.add(counterRow("Laps used", "lapsUsed", 1));
        card.add(infoLine("Laps available", String.valueOf(Math.max(0, plugin.get("lapCredits") - plugin.get("lapsUsed")))));
        return card;
    }

    private JPanel buildThievingCard()
    {
        JPanel card = card();
        card.add(sectionHeader("Thieving", net.runelite.api.Skill.THIEVING));
        card.add(counterRow("Attempts earned", "thiefEarned", 1));
        card.add(counterRow("Attempts spent", "thiefUsed", 1));

        JPanel gpRow = rowPanel();
        gpRow.add(leftLabel("Slayer GP"), BorderLayout.CENTER);
        JButton gp = smallIconButton();
        java.awt.image.BufferedImage coins = plugin.itemManager().getImage(995);
        if (coins != null)
        {
            gp.setIcon(new ImageIcon(coins.getScaledInstance(26, 26, Image.SCALE_SMOOTH)));
        }
        else
        {
            gp.setText("GP");
        }
        gp.setToolTipText("Add GP earned through Slayer toward Thieving attempts");
        gp.addActionListener(e -> {
            String s = JOptionPane.showInputDialog(this,
                "GP EARNED through Slayer (not total cash):", "1000");
            if (s == null) return;
            try
            {
                int value = Integer.parseInt(s.replace(",", "").trim());
                if (value < 0) throw new NumberFormatException();
                plugin.slayerGp(value);
            }
            catch (NumberFormatException ex)
            {
                error("Enter a nonnegative whole number up to 2,147,483,647.");
            }
        });
        gpRow.add(gp, BorderLayout.EAST);
        card.add(gpRow);
        card.add(infoLine("Next GP attempt", plugin.get("gpRemainder") + " / 1000"));
        return card;
    }

    private JPanel buildToolsCard()
    {
        JPanel card = card();
        card.add(sectionTextHeader("Tools"));
        card.add(toolCard(ToolKind.AXE, "Woodcutting"));
        card.add(Box.createVerticalStrut(6));
        card.add(toolCard(ToolKind.PICKAXE, "Mining"));
        card.add(Box.createVerticalStrut(6));
        card.add(toolCard(ToolKind.FISHING, "Fishing"));
        return card;
    }

    private JPanel buildActionCard()
    {
        JPanel card = card();
        JPanel row = transparentPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        JButton rules = compactButton("Rules");
        rules.addActionListener(e -> showRules());
        JButton reset = compactButton("Reset");
        reset.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "Erase ALL challenge counters?", "Confirm",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)
            {
                return;
            }
            for (String k : new String[]{"tasks", "lapCredits", "lapsUsed", "thiefEarned", "thiefUsed", "gpRemainder", "streak"})
            {
                plugin.set(k, 0);
            }
        });
        row.add(rules);
        row.add(Box.createHorizontalStrut(6));
        row.add(reset);
        card.add(row);
        card.add(Box.createVerticalStrut(6));
        card.add(actionButton("Refresh icons", e -> refresh()));
        card.add(Box.createVerticalStrut(6));
        card.add(actionButton("Activity history", e -> showHistory()));
        card.add(Box.createVerticalStrut(6));
        card.add(actionButton("Statistics", e -> showText("Challenge statistics", plugin.statisticsText())));
        card.add(Box.createVerticalStrut(6));
        card.add(actionButton("Backup / export", e -> exportBackup()));
        card.add(Box.createVerticalStrut(6));
        card.add(actionButton("Banking review", e -> bankingReview()));
        return card;
    }

    private JPanel toolCard(ToolKind kind, String title)
    {
        JPanel box = new JPanel(new BorderLayout(8, 0));
        box.setAlignmentX(LEFT_ALIGNMENT);
        box.setBackground(CARD_ALT);
        box.setBorder(compound(new EmptyBorder(8, 8, 8, 8), new LineBorder(BORDER)));
        box.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JLabel iconLabel = new JLabel();
        iconLabel.setBorder(new EmptyBorder(2, 0, 0, 0));
        int itemId = plugin.toolId(kind);
        if (itemId == 0)
        {
            itemId = kind == ToolKind.AXE ? net.runelite.api.ItemID.DRAGON_AXE
                : kind == ToolKind.PICKAXE ? net.runelite.api.ItemID.DRAGON_PICKAXE
                : net.runelite.api.ItemID.DRAGON_HARPOON;
        }
        java.awt.image.BufferedImage picture = plugin.itemManager().getImage(itemId);
        if (picture != null)
        {
            iconLabel.setIcon(new ImageIcon(picture.getScaledInstance(36, 36, Image.SCALE_SMOOTH)));
        }
        box.add(iconLabel, BorderLayout.WEST);

        JPanel details = transparentPanel();
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
        JLabel name = plainLabel(title, TEXT, 15f, Font.BOLD);
        int charges = plugin.toolCharges(kind);
        String remaining = charges == ToolKind.UNLIMITED ? "Unlimited" : String.valueOf(charges);
        JLabel value = plainLabel("Remaining: " + remaining, MUTED, 13f, Font.PLAIN);
        details.add(name);
        details.add(Box.createVerticalStrut(2));
        details.add(value);
        box.add(details, BorderLayout.CENTER);

        JButton use = compactButton("Use 1");
        use.addActionListener(e -> plugin.useTool(kind, 1));
        JPanel right = transparentPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.add(Box.createVerticalGlue());
        right.add(use);
        right.add(Box.createVerticalGlue());
        box.add(right, BorderLayout.EAST);
        return box;
    }

    private JPanel sectionHeader(String title, net.runelite.api.Skill skill)
    {
        JPanel panel = transparentPanel();
        panel.setLayout(new BorderLayout(6, 0));
        panel.setAlignmentX(LEFT_ALIGNMENT);
        panel.setBorder(new EmptyBorder(0, 0, 6, 0));

        JPanel left = transparentPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.X_AXIS));
        java.awt.image.BufferedImage icon = plugin.skillIconManager().getSkillImage(skill);
        if (icon != null)
        {
            left.add(new JLabel(new ImageIcon(icon.getScaledInstance(24, 24, Image.SCALE_SMOOTH))));
            left.add(Box.createHorizontalStrut(6));
        }
        JLabel text = plainLabel(title, TEXT, 16f, Font.BOLD);
        left.add(text);
        panel.add(left, BorderLayout.WEST);
        return panel;
    }

    private JPanel sectionTextHeader(String title)
    {
        JPanel panel = transparentPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(0, 0, 6, 0));
        panel.add(plainLabel(title, TEXT, 16f, Font.BOLD), BorderLayout.WEST);
        return panel;
    }

    private JPanel counterRow(String label, String key, int step)
    {
        JPanel outer = transparentPanel();
        outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));
        outer.setAlignmentX(LEFT_ALIGNMENT);
        outer.setBorder(new EmptyBorder(0, 0, 6, 0));

        JPanel top = rowPanel();
        top.add(leftLabel(label), BorderLayout.WEST);
        top.add(valueBadge(String.valueOf(plugin.get(key))), BorderLayout.EAST);
        outer.add(top);

        JPanel controls = transparentPanel();
        controls.setLayout(new FlowLayout(FlowLayout.LEFT, 4, 0));
        controls.setAlignmentX(LEFT_ALIGNMENT);
        JButton minus = compactButton("-");
        minus.setPreferredSize(new Dimension(40, 26));
        JButton plus = compactButton("+");
        plus.setPreferredSize(new Dimension(40, 26));
        minus.addActionListener(e -> plugin.adjust(key, -step));
        plus.addActionListener(e -> plugin.adjust(key, step));
        controls.add(minus);
        controls.add(plus);
        outer.add(controls);
        return outer;
    }

    private JPanel infoLine(String label, String value)
    {
        JPanel row = rowPanel();
        row.add(leftLabel(label), BorderLayout.WEST);
        row.add(rightValue(value), BorderLayout.EAST);
        row.setBorder(new EmptyBorder(0, 0, 6, 0));
        return row;
    }

    private JLabel note(String text)
    {
        JLabel label = plainLabel(text, MUTED, 13f, Font.PLAIN);
        label.setBorder(new EmptyBorder(0, 0, 6, 0));
        return label;
    }

    private JPanel card()
    {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(LEFT_ALIGNMENT);
        panel.setBackground(CARD);
        panel.setBorder(compound(new EmptyBorder(10, 10, 10, 10), new LineBorder(BORDER)));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        return panel;
    }

    private Border compound(Border inner, Border outer)
    {
        return new CompoundBorder(outer, inner);
    }

    private JPanel rowPanel()
    {
        JPanel panel = transparentPanel();
        panel.setLayout(new BorderLayout(6, 0));
        panel.setAlignmentX(LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        return panel;
    }

    private JPanel transparentPanel()
    {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setAlignmentX(LEFT_ALIGNMENT);
        return panel;
    }

    private JLabel plainLabel(String text, Color color, float size, int style)
    {
        JLabel label = new JLabel(text);
        label.setForeground(color);
        label.setFont(label.getFont().deriveFont(style, size));
        label.setAlignmentX(LEFT_ALIGNMENT);
        return label;
    }

    private JLabel leftLabel(String text)
    {
        return plainLabel(text, TEXT, 14f, Font.PLAIN);
    }

    private JLabel rightValue(String text)
    {
        JLabel label = plainLabel(text, TEXT, 16f, Font.BOLD);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        return label;
    }

    private JLabel valueBadge(String text)
    {
        JLabel label = plainLabel(" " + text + " ", TEXT, 18f, Font.BOLD);
        label.setOpaque(true);
        label.setBackground(new Color(58, 61, 66));
        label.setBorder(BorderFactory.createLineBorder(BORDER));
        return label;
    }

    private JButton actionButton(String text, java.awt.event.ActionListener listener)
    {
        JButton button = new JButton(text);
        styleButton(button, false);
        button.addActionListener(listener);
        button.setAlignmentX(LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        button.setBorder(new EmptyBorder(6, 8, 6, 8));
        return button;
    }

    private JButton toggleButton(String text, java.awt.event.ActionListener listener)
    {
        JButton button = actionButton(text, listener);
        button.setBackground(new Color(54, 58, 63));
        return button;
    }

    private JButton compactButton(String text)
    {
        JButton button = new JButton(text);
        styleButton(button, true);
        return button;
    }

    private JButton smallIconButton()
    {
        JButton button = compactButton("");
        button.setPreferredSize(new Dimension(38, 30));
        return button;
    }

    private void styleButton(JButton button, boolean compact)
    {
        button.setFont(button.getFont().deriveFont(Font.PLAIN, 13f));
        button.setFocusPainted(false);
        button.setForeground(TEXT);
        button.setBackground(new Color(60, 63, 68));
        button.setOpaque(true);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            BorderFactory.createEmptyBorder(compact ? 4 : 6, compact ? 8 : 10, compact ? 4 : 6, compact ? 8 : 10)));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(compact ? new Dimension(999, 30) : new Dimension(999, 32));
    }


    private void showText(String title, String value)
    {
        JTextArea text = new JTextArea(value);
        text.setEditable(false);
        text.setFont(new Font("Monospaced", Font.PLAIN, 12));
        text.setLineWrap(true); text.setWrapStyleWord(true);
        text.setCaretPosition(0);
        JScrollPane pane = new JScrollPane(text);
        pane.setPreferredSize(new Dimension(480, 450));
        JOptionPane.showMessageDialog(this, pane, title, JOptionPane.PLAIN_MESSAGE);
    }

    private void exportBackup()
    {
        javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
        chooser.setSelectedFile(new java.io.File("slayer-hunter-backup.txt"));
        if (chooser.showSaveDialog(this) != javax.swing.JFileChooser.APPROVE_OPTION) return;
        try
        {
            java.nio.file.Files.write(chooser.getSelectedFile().toPath(),
                plugin.backupText().getBytes(java.nio.charset.StandardCharsets.UTF_8));
            JOptionPane.showMessageDialog(this, "Backup exported.");
        }
        catch (java.io.IOException ex) { error("Could not export: " + ex.getMessage()); }
    }

    private void bankingReview()
    {
        String list = JOptionPane.showInputDialog(this,
            "Allowed bank item names, separated by commas (not yet finalized):", plugin.bankAllowedNames());
        if (list == null) return;
        plugin.bankAllowedNames(list);
        showText("Banking review — advisory only", plugin.bankReviewText());
    }

    private void showHistory()
    {
        JTextArea text = new JTextArea(plugin.historyText());
        text.setEditable(false);
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        text.setCaretPosition(0);
        JScrollPane pane = new JScrollPane(text);
        pane.setPreferredSize(new Dimension(430, 450));
        JOptionPane.showMessageDialog(this, pane, "Challenge activity history", JOptionPane.PLAIN_MESSAGE);
    }

    private void showRules()
    {
        JTextArea text = new JTextArea();
        text.setEditable(false);
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        text.setFont(new Font("SansSerif", Font.PLAIN, 12));
        try (java.io.InputStream in = getClass().getResourceAsStream("/CHALLENGE_RULES.txt"))
        {
            if (in == null)
            {
                text.setText(BUILT_IN_RULES);
            }
            else
            {
            byte[] bytes = new byte[8192];
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            int n;
            while ((n = in.read(bytes)) != -1) out.write(bytes, 0, n);
            text.setText(new String(out.toByteArray(), java.nio.charset.StandardCharsets.UTF_8));
            }
        }
        catch (java.io.IOException ex)
        {
            text.setText(BUILT_IN_RULES);
        }
        text.setCaretPosition(0);
        JScrollPane pane = new JScrollPane(text);
        pane.setPreferredSize(new Dimension(450, 540));
        JOptionPane.showMessageDialog(this, pane, "Slayer + Hunter Challenge Rules", JOptionPane.PLAIN_MESSAGE);
    }

    private void error(String s)
    {
        JOptionPane.showMessageDialog(this, s, "Invalid entry", JOptionPane.WARNING_MESSAGE);
    }
}
