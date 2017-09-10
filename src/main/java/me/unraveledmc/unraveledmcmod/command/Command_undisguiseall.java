package me.unraveledmc.unraveledmcmod.command;

import me.unraveledmc.unraveledmcmod.rank.Rank;
import me.unraveledmc.unraveledmcmod.util.FUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.MOD, source = SourceType.BOTH)
@CommandParameters(description = "Undisguise all players on the server", usage = "/<command> [-a]", aliases = "uall")
public class Command_undisguiseall extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        Boolean includeStaff = false;
        if (args.length > 0 && args[0].equals("-a"))
        {
            includeStaff = true;
        }
        FUtil.staffAction(sender.getName(), "Undisguising all " + (includeStaff ? "players" : "non-staff"), true);
        plugin.ldb.undisguiseAll(includeStaff);
        return true;
    }
}
