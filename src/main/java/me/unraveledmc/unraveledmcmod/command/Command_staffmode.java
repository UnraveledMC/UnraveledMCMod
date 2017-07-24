package me.unraveledmc.unraveledmcmod.command;

import me.unraveledmc.unraveledmcmod.config.ConfigEntry;
import me.unraveledmc.unraveledmcmod.rank.Rank;
import me.unraveledmc.unraveledmcmod.util.FUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Close server to non-staff.", usage = "/<command> [on | off]")
public class Command_staffmode extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length != 1)
        {
            return false;
        }

        if (args[0].equalsIgnoreCase("off"))
        {
            ConfigEntry.STAFF_ONLY_MODE.setBoolean(false);
            FUtil.adminAction(sender.getName(), "Opening the server to all players.", true);
            return true;
        }
        else if (args[0].equalsIgnoreCase("on"))
        {
            ConfigEntry.STAFF_ONLY_MODE.setBoolean(true);
            FUtil.adminAction(sender.getName(), "Closing the server to non-staff.", true);
            for (Player player : server.getOnlinePlayers())
            {
                if (!isStaffMember(player))
                {
                    player.kickPlayer("Server is now closed to non-staff.");
                }
            }
            return true;
        }

        return false;
    }
}
