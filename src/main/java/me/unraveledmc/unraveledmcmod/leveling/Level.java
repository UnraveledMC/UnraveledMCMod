package me.unraveledmc.unraveledmcmod.leveling;

import lombok.Getter;
import org.bukkit.ChatColor;

public enum Level implements Displayable
{

    PEASANT("Peasant", ChatColor.WHITE, 0),
    SERVANT("Servant", ChatColor.YELLOW, 50),
    YEOMAN("Yeoman", ChatColor.AQUA, 100),
    KNIGHT("Knight", ChatColor.LIGHT_PURPLE, 150),
    NOBLE("Noble", ChatColor.GREEN, 200),
    FREEMAN("Freeman", ChatColor.RED, 250),
    KING("King", ChatColor.GOLD, 300),
    POPE("Pope", ChatColor.DARK_PURPLE, 350);
    @Getter
    private final String name;
    @Getter
    private final String tag;
    @Getter
    private final ChatColor color;
    @Getter
    private final int rankupPrice;

    private Level(String name, ChatColor color, int rankupPrice)
    {
        this.name = name;
        this.tag = ChatColor.DARK_GRAY + "[" + color + name + ChatColor.DARK_GRAY + "]" + color;
        this.color = color;
        this.rankupPrice = rankupPrice;
    }

    @Override
    public String getColoredName()
    {
        return color + name;
    }
    
    public int getLevel()
    {
        return ordinal();
    }
    
    public Level getNextLevel()
    {
        Level[] levels = values();
        try
        {
            return levels[(getLevel() + 1)];
        }
        catch (IndexOutOfBoundsException e)
        {
            return null;
        }
    }

    public boolean isAtLeast(Level level)
    {
        if (getLevel() < level.getLevel())
        {
            return false;
        }
        return getLevel() >= level.getLevel();
    }
    
    public static Level findLevel(String string)
    {
        try
        {
            return Level.valueOf(string.toUpperCase());
        }
        catch (Exception ignored)
        {
        }

        return null;
    }

}
