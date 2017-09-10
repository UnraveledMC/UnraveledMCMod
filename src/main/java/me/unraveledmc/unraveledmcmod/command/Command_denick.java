package me.unraveledmc.unraveledmcmod.command;

import me.unraveledmc.unraveledmcmod.rank.Rank;
import me.unraveledmc.unraveledmcmod.util.FUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.MOD, source = SourceType.BOTH)
@CommandParameters(description = "Essentials Interface Command - Remove the nickname of all non-staff or all players on the server.", usage = "/<command> [-a]")
public class Command_denick extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        Boolean includeStaff = false;
        if (args.length > 0 && args[0].equals("-a"))
        {
            includeStaff = true;
        }
    
        FUtil.staffAction(sender.getName(), "Removing nicknames for all " + (includeStaff ? "players" : "non-staff"), false);

        for (Player player : server.getOnlinePlayers())
        {
            plugin.esb.setNickname(player.getName(), null);
        }

        return true;
    }
}
