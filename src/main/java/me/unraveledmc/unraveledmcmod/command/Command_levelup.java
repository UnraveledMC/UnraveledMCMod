package me.unraveledmc.unraveledmcmod.command;

import me.unraveledmc.unraveledmcmod.leveling.Level;
import me.unraveledmc.unraveledmcmod.rank.Rank;
import me.unraveledmc.unraveledmcmod.shop.ShopData;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Levels you up", usage = "/<command>")
public class Command_levelup extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        ShopData sd = plugin.sh.getData(playerSender);
        Level currentLevel = plugin.lvm.getLevel(playerSender);
        Level nextLevel = currentLevel.getNextLevel();
        int coins = sd.getCoins();
        if (nextLevel == null)
        {
            msg("You don't have any more levels to rank up to", ChatColor.RED);
            return true;
        }
        int levelupPrice = nextLevel.getRankupPrice();
        if (!plugin.sl.canAfford(levelupPrice, coins))
        {
            msg("You need " + (levelupPrice - coins) + " more coins to rankup to " + nextLevel.getColoredName(), ChatColor.RED);
            return true;
        }
        sd.setCoins(coins - levelupPrice);
        sd.setLevel(nextLevel);
        plugin.sh.save(sd);
        msg("You have successfully ranked up to " + nextLevel.getColoredName(), ChatColor.GREEN);
        return true;
    }
}
