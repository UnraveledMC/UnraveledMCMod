package me.unraveledmc.unraveledmcmod.command;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.ArrayList;
import me.unraveledmc.unraveledmcmod.rank.Rank;
import me.unraveledmc.unraveledmcmod.util.FLog;
import me.unraveledmc.unraveledmcmod.util.FUtil;
import me.unraveledmc.unraveledmcmod.util.FSync;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@CommandPermissions(level = Rank.OP, source = SourceType.BOTH)
@CommandParameters(description = "Gets the name history of a user", usage = "/<command> <username>", aliases = "nh")
public class Command_namehistory extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0)
        {
            return false;
        }

        final Player player = getPlayer(args[0]);
        final String name;

        if (player != null)
        {
            name = player.getName();
        }
        else
        {
            name = args[0];
        }

        final CommandSender commandSender = sender;
        
        msg("Fetching name history for " + name, ChatColor.GREEN);
        
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                try
                {
                    final URL getUrl = new URL("https://creeperseth.com/api/minecraft/namehistory/index.php?username=" + name);
                    final URLConnection urlConnection = getUrl.openConnection();
                    urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.2.13) Gecko/20101203 Firefox/3.6.13 (.NET CLR 3.5.30729)");
                    final BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    final List<String> lines = new ArrayList();
                    String line;
                    while ((line = br.readLine()) != null)
                    {
                        lines.add(line);
                    }
                    br.close();

                    if (!plugin.isEnabled())
                    {
                        return;
                    }

                    switch (lines.get(0))
                    {
                        case "false":
                            FSync.playerMsg(commandSender, ChatColor.RED + "There is no username history for \"" + name + "\"");
                            break;
                        case "invalid":
                            FSync.playerMsg(commandSender, ChatColor.RED + "That name does not comply with minecraft username standards");
                            break;
                        default:
                            for (String name : lines)
                            {
                                FSync.playerMsg(commandSender, FUtil.colorize(name));
                            }   break;
                    }

                }
                catch (Exception ex)
                {
                    FLog.severe(ex);
                    FSync.playerMsg(commandSender, ChatColor.RED + "Failed to connect to the API");
                }
            }
        }.runTaskAsynchronously(plugin);

        return true;
    }
}
