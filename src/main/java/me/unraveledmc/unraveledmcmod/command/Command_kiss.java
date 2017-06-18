package me.unraveledmc.unraveledmcmod.command;

import me.unraveledmc.unraveledmcmod.rank.Rank;
import me.unraveledmc.unraveledmcmod.util.FUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Kiss someone on the cheek", usage = "/<command> <player>")
public class Command_kiss extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0)
        {
            return false;
        }
        Player player = getPlayer(args[0]);
        if (player == null)
        {
            msg(FreedomCommand.PLAYER_NOT_FOUND);
            return true;
        }
        FUtil.bcastMsg(ChatColor.AQUA + plugin.esb.getDisplayName(playerSender.getName()) + ChatColor.AQUA + " gave " + plugin.esb.getDisplayName(player.getName()) + ChatColor.AQUA + " a kiss on the cheek");
        return true;
    }
}
