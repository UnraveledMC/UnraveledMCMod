package me.unraveledmc.unraveledmcmod.rank;

import lombok.Getter;
import org.bukkit.ChatColor;

public enum Rank implements Displayable
{

    IMPOSTOR("an", "Impostor", Type.PLAYER, "IMP", ChatColor.YELLOW),
    NON_OP("a", "Non-OP", Type.PLAYER, "", ChatColor.WHITE),
    OP("an", "OP", Type.PLAYER, "OP", ChatColor.RED),
    MOD("a", "Moderator", Type.STAFF, "Mod", ChatColor.GREEN),
    ADMIN("an", "Administrator", Type.STAFF, "Admin", ChatColor.BLUE),
    SENIOR_ADMIN("a", "Senior Administrator", Type.STAFF, "SrA", ChatColor.GOLD),
    ADMIN_CONSOLE("the", "Console", Type.STAFF_CONSOLE, "Console", ChatColor.DARK_PURPLE),
    SENIOR_CONSOLE("the", "Console", Type.STAFF_CONSOLE, "Console", ChatColor.DARK_PURPLE);
    @Getter
    private final Type type;
    @Getter
    private final String name;
    @Getter
    private final String determiner;
    @Getter
    private final String tag;
    @Getter
    private final String coloredTag;
    @Getter
    private final ChatColor color;

    private Rank(String determiner, String name, Type type, String abbr, ChatColor color)
    {
        this.type = type;
        this.name = name;
        this.determiner = determiner;
        this.tag = abbr.isEmpty() ? "" : "[" + abbr + "]";
        this.coloredTag = abbr.isEmpty() ? "" : ChatColor.DARK_GRAY + "[" + color + abbr + ChatColor.DARK_GRAY + "]" + color;
        this.color = color;
    }

    @Override
    public String getColoredName()
    {
        return color + name;
    }

    @Override
    public String getColoredLoginMessage()
    {
        return determiner + " " + color + ChatColor.ITALIC + name;
    }
    
    @Override
    public String getDeterminer()
    {
        return determiner;
    }
    
    @Override
    public String getItalicColoredName()
    {
        return color.toString() + ChatColor.ITALIC + name;
    }

    public boolean isConsole()
    {
        return getType() == Type.STAFF_CONSOLE;
    }

    public int getLevel()
    {
        return ordinal();
    }

    public boolean isAtLeast(Rank rank)
    {
        if (getLevel() < rank.getLevel())
        {
            return false;
        }

        if (!hasConsoleVariant() || !rank.hasConsoleVariant())
        {
            return true;
        }

        return getConsoleVariant().getLevel() >= rank.getConsoleVariant().getLevel();
    }

    public boolean isStaff()
    {
        return getType() == Type.STAFF || getType() == Type.STAFF_CONSOLE;
    }

    public boolean hasConsoleVariant()
    {
        return getConsoleVariant() != null;
    }

    public Rank getConsoleVariant()
    {
        switch (this)
        {
            case ADMIN:
            case ADMIN_CONSOLE:
                return ADMIN_CONSOLE;
            case SENIOR_ADMIN:
            case SENIOR_CONSOLE:
                return SENIOR_CONSOLE;
            default:
                return null;
        }
    }

    public Rank getPlayerVariant()
    {
        switch (this)
        {
            case ADMIN:
            case ADMIN_CONSOLE:
                return ADMIN;
            case SENIOR_ADMIN:
            case SENIOR_CONSOLE:
                return SENIOR_ADMIN;
            default:
                return null;
        }
    }

    public static Rank findRank(String string)
    {
        try
        {
            return Rank.valueOf(string.toUpperCase());
        }
        catch (Exception ignored)
        {
        }

        return Rank.NON_OP;
    }

    public static enum Type
    {

        PLAYER,
        STAFF,
        STAFF_CONSOLE;

        public boolean isStaff()
        {
            return this != PLAYER;
        }
    }

}
