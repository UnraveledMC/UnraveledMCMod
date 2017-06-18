package me.unraveledmc.unraveledmcmod;

import me.unraveledmc.unraveledmcmod.player.FPlayer;
import me.unraveledmc.unraveledmcmod.shop.ShopData;
import me.unraveledmc.unraveledmcmod.util.FLog;
import me.unraveledmc.unraveledmcmod.util.FSync;
import me.unraveledmc.unraveledmcmod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;

public class ChatManager extends FreedomService
{
    public static ChatColor acc = ChatColor.GOLD;
    public static boolean acr = false;
    public static boolean acn = false;
    // Putting an end to free hosted server advertisements
    public static final List<String> DISGUSTING_HOST_DOMAINS = Arrays.asList(new String[]
            {
                "my-serv.com", "mymcserver.org", "serv.gs", "myserver.gs", "g-s.nu", "mcserv.me",
                "mcpro.io", "1337srv.com", "mcnetwork.me", "serv.nu", "mygs.co", "mchosting.pro",
                "server-minecraft.pro", "mcraft.pro", "mcserv.pro", "mchost.pro", "crafted.pro", 
                "cubed.pro", "minecraft-crafting.pro", "aternos.me"
            });

    public ChatManager(UnraveledMCMod plugin)
    {
        super(plugin);
    }

    @Override
    protected void onStart()
    {
    }

    @Override
    protected void onStop()
    {
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerChatFormat(AsyncPlayerChatEvent event)
    {
        try
        {
            handleChatEvent(event);
        }
        catch (Exception ex)
        {
            FLog.severe(ex);
        }
    }

    private void handleChatEvent(AsyncPlayerChatEvent event)
    {
        final Player player = event.getPlayer();
        ShopData sd = plugin.sh.getData(player);
        String message = event.getMessage().trim();
        
        if (!sd.isColoredchat())
        {
            // Strip color from messages
            message = ChatColor.stripColor(message);
        }
        else
        {
            // Format color
            message = FUtil.colorize(message);
        }
        
        // Execs can use formatting :^)
        if (!FUtil.isExecutive(player.getName()))
        {
            message = message.replaceAll(ChatColor.BOLD.toString(), "&l");
            message = message.replaceAll(ChatColor.MAGIC.toString(), "&k");
            message = message.replaceAll(ChatColor.ITALIC.toString(), "&o");
            message = message.replaceAll(ChatColor.UNDERLINE.toString(), "&n");
            message = message.replaceAll(ChatColor.STRIKETHROUGH.toString(), "&m");
        }

        // Truncate messages that are too long - 256 characters is vanilla client max
        if (message.length() > 256)
        {
            message = message.substring(0, 256);
            FSync.playerMsg(player, "Message was shortened because it was too long to send.");
        }

        // Check for caps
        if (!plugin.al.isAdmin(player))
        {
            if (message.length() >= 6)
            {
                int caps = 0;
                for (char c : message.toCharArray())
                {
                    if (Character.isUpperCase(c))
                    {
                        caps++;
                    }
                }
                if (((float) caps / (float) message.length()) > 0.65) //Compute a ratio so that longer sentences can have more caps.
                {
                    message = message.toLowerCase();
                }
            }
        }
        
        if (!plugin.al.isAdmin(player))
        {
            for (String domain : DISGUSTING_HOST_DOMAINS)
            {
                if (ChatColor.stripColor(message).toLowerCase().contains(domain))
                {
                    player.sendMessage(ChatColor.RED + "Ew, stop trying to advertise that server ran on a terrible host. Get real hosting.");
                    event.setCancelled(true);
                    return;
                }
            }
        }

        // Check for adminchat
        final FPlayer fPlayer = plugin.pl.getPlayerSync(player);
        if (fPlayer.inAdminChat())
        {
            FSync.adminChatMessage(player, message);
            event.setCancelled(true);
            return;
        }

        // Finally, set message
        event.setMessage(message);

        // Make format
        String format = "<%1$s> %2$s";

        String tag = fPlayer.getTag();
        if (tag != null && !tag.isEmpty())
        {
            format = tag.replace("%", "%%") + " " + format;
        }
        
        // Check for mentions
        Boolean mentionEveryone = ChatColor.stripColor(message).toLowerCase().contains("@everyone") && plugin.al.isAdmin(player);
        for (Player p : server.getOnlinePlayers())
        {
            if (ChatColor.stripColor(message).toLowerCase().contains("@" + p.getName().toLowerCase()) || mentionEveryone)
            {
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, SoundCategory.MASTER, 1337F, 0.9F);
            }
        }

        // Set format
        event.setFormat(format);
    }

    public void adminChat(CommandSender sender, String message)
    {
        String name = sender.getName() + " " + plugin.rm.getDisplay(sender).getColoredTag() + ChatColor.WHITE;
        FLog.info("[ADMIN] " + name + ": " + message);

        for (Player player : server.getOnlinePlayers())
        {
            if (plugin.al.isAdmin(player))
            {
                ChatColor cc = acc;
                if (acr == true)
                {
                    cc = FUtil.randomChatColor();
                    player.sendMessage("[" + ChatColor.AQUA + "ADMIN" + ChatColor.WHITE + "] " + ChatColor.DARK_RED + name + ": " + cc + message);
                }
                else if (acn == true)
                {
                    String rm = "";
                    for (char c : message.toCharArray())
                    {
                        ChatColor rc = FUtil.randomChatColor();
                        rm = rm + rc + c;
                    }
                    player.sendMessage("[" + ChatColor.AQUA + "ADMIN" + ChatColor.WHITE + "] " + ChatColor.DARK_RED + name + ": " + rm);
                }
                else
                {
                    player.sendMessage("[" + ChatColor.AQUA + "ADMIN" + ChatColor.WHITE + "] " + ChatColor.DARK_RED + name + ": " + cc + message);
                }
         
            }
        }
    }

    public void reportAction(Player reporter, Player reported, String report)
    {
        for (Player player : server.getOnlinePlayers())
        {
            if (plugin.al.isAdmin(player))
            {
                FUtil.playerMsg(player, ChatColor.RED + "[REPORTS] " + ChatColor.GOLD + reporter.getName() + " has reported " + reported.getName() + " for " + report);
            }
        }
    }

}
