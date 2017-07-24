package me.unraveledmc.unraveledmcmod.command;

import me.unraveledmc.unraveledmcmod.rank.Rank;
import me.unraveledmc.unraveledmcmod.config.ConfigEntry;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.NON_OP, source = SourceType.BOTH)
@CommandParameters(description = "Shows how to apply for staff", usage = "/<command>", aliases = "si")
public class Command_staffinfo extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        msg("How to apply for staff on the UnraveledMC server:", ChatColor.AQUA);
        msg(" - Do not ask for staff in game,", ChatColor.DARK_GREEN);
        msg(" - Be helpful within the server,", ChatColor.GOLD);
        msg(" - Report those breaking the rules,", ChatColor.DARK_GREEN);
        msg(" - And apply on our forums at this link:", ChatColor.GOLD);
        msg(" - " + ConfigEntry.STAFF_APPLICATION_URL.getString(), ChatColor.DARK_AQUA);
        msg(" - Do not apply for staff if you cannot be active!", ChatColor.RED);
        return true;
    }
}
