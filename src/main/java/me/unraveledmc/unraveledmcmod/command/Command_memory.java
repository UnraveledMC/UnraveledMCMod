package me.unraveledmc.unraveledmcmod.command;

import me.unraveledmc.unraveledmcmod.rank.Rank;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import java.text.DecimalFormat;

@CommandPermissions(level = Rank.OP, source = SourceType.BOTH)
@CommandParameters(description = "Gets the server's memory usage", usage = "/<command>")
public class Command_memory extends FreedomCommand
{
    private static final int BYTES_PER_MB = 1024 * 1024;

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        Runtime runtime = Runtime.getRuntime();
        long usedMem = runtime.totalMemory() - runtime.freeMemory();
        String used_memory = new DecimalFormat("#").format((double) usedMem / (double) BYTES_PER_MB) + "MB";
        String max_memory = ((double) runtime.maxMemory() / (double) BYTES_PER_MB) + "MB";
        String percent_used = "(" + new DecimalFormat("#").format(((double) usedMem / (double) runtime.totalMemory()) * 100.0) + "%)";
        msg(used_memory + "/" + max_memory + " " + percent_used, ChatColor.AQUA);
        return true;
    }
}
