package me.unraveledmc.unraveledmcmod.command;

import me.unraveledmc.unraveledmcmod.rank.Rank;
import me.unraveledmc.unraveledmcmod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@CommandPermissions(level = Rank.ADMIN, source = SourceType.ONLY_CONSOLE)
@CommandParameters(description = "Wipe the flatlands map. Requires manual restart after command is used.", usage = "/<command>")
public class Command_wipeflatlands extends FreedomCommand
{

    @Override
    public boolean run(final CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        plugin.sf.setSavedFlag("do_wipe_flatlands", true);

        FUtil.bcastMsg("Server is going offline for flatlands wipe.", ChatColor.GRAY);

        for (Player player : server.getOnlinePlayers())
        {
            player.kickPlayer("Server is going offline for flatlands wipe, come back in a few minutes.");
        }

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                plugin.cpb.clearDatabase(plugin.wm.flatlands.getWorld(), true);
            }
        }.runTaskAsynchronously(plugin);

        return true;
    }
}
