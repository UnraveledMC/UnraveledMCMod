
package me.unraveledmc.unraveledmcmod.command;

import me.unraveledmc.unraveledmcmod.rank.Rank;
import me.unraveledmc.unraveledmcmod.util.FUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@CommandPermissions(level = Rank.TELNET_ADMIN, source = SourceType.ONLY_CONSOLE)
@CommandParameters(description = "Wipes the CoreProtect data for the flatlands", usage = "/<command>")
public class Command_wipecoreprotectdata extends FreedomCommand
{

    @Override
    public boolean run(final CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        FUtil.adminAction(sender.getName(), "Wiping CoreProtect data for the flatlands", true);
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                plugin.cpb.clearDatabase(plugin.wm.flatlands.getWorld());
            }
        }.runTaskAsynchronously(plugin);
        return true;
    }
}
