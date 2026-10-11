package com.slayerhunter;

import java.util.Locale;

/** The three individual tracked tool slots. */
enum ToolKind
{
    AXE("axe", "Dragon axe"),
    PICKAXE("pickaxe", "Dragon pickaxe"),
    FISHING("fishing", "Dragon harpoon");

    static final int UNLIMITED = Integer.MAX_VALUE;
    final String key;
    final String placeholder;

    ToolKind(String key, String placeholder)
    {
        this.key = key;
        this.placeholder = placeholder;
    }

    static ToolKind fromName(String raw)
    {
        if (raw == null) return null;
        String name = raw.toLowerCase(Locale.ROOT).trim();
        if (name.equals("null")) return null;
        if (name.contains("pickaxe")) return PICKAXE;
        if (name.endsWith(" axe") || name.equals("axe") || name.contains("hatchet")) return AXE;
        if (name.contains("harpoon") || name.contains("fishing rod")
            || name.contains("barbarian rod") || name.contains("barb-tail harpoon")
            || name.contains("net") && (name.contains("fishing") || name.equals("small net") || name.equals("big net"))
            || name.contains("lobster pot") || name.contains("karambwan vessel")
            || name.contains("fishing spear")) return FISHING;
        return null;
    }

    /** Starting charges assigned when a specific item ID is first observed. */
    static int initialUses(String raw)
    {
        if (raw == null) return 0;
        String name = raw.toLowerCase(Locale.ROOT).trim();

        if (name.contains("3rd age") || name.contains("third age")
            || name.contains("barbarian rod") || name.contains("barbarian fishing rod")
            || name.contains("karambwan vessel")) return UNLIMITED;

        // The special fishing items take priority over general material tiers.
        if (name.contains("small fishing net") || name.equals("small net")) return 100;
        if (name.contains("big fishing net") || name.equals("big net")) return 200;
        if (name.contains("fly fishing rod")) return 250;
        if (name.contains("fishing rod")) return 150;
        if (name.contains("lobster pot")) return 400;
        if (name.contains("harpoon") && !name.contains("dragon") && !name.contains("crystal")) return 500;

        if (name.contains("crystal")) return 5000;
        if (name.contains("dragon")) return 2500;

        if (fromName(name) == AXE || fromName(name) == PICKAXE)
        {
            if (name.contains("bronze")) return fromName(name) == AXE ? 10 : 50;
            if (name.contains("iron")) return fromName(name) == AXE ? 15 : 75;
            if (name.contains("steel")) return fromName(name) == AXE ? 25 : 125;
            if (name.contains("black")) return fromName(name) == AXE ? 30 : 200;
            if (name.contains("mithril")) return fromName(name) == AXE ? 40 : 275;
            if (name.contains("adamant")) return fromName(name) == AXE ? 60 : 400;
            if (name.contains("rune")) return fromName(name) == AXE ? 100 : 550;
        }

        return 0; // Unspecified tool type: do not guess.
    }
}
