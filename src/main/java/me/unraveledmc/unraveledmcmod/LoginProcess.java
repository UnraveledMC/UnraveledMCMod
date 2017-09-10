package me.unraveledmc.unraveledmcmod;

import java.util.regex.Pattern;
import java.util.List;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;
import me.unraveledmc.unraveledmcmod.command.FreedomCommand;
import me.unraveledmc.unraveledmcmod.config.ConfigEntry;
import me.unraveledmc.unraveledmcmod.util.FSync;
import me.unraveledmc.unraveledmcmod.util.FUtil;
import me.unraveledmc.unraveledmcmod.shop.ShopData;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class LoginProcess extends FreedomService
{

    public static final int DEFAULT_PORT = 25565;
    public static final int MIN_USERNAME_LENGTH = 2;
    public static final int MAX_USERNAME_LENGTH = 20;
    public static final Pattern USERNAME_REGEX = Pattern.compile("^[\\w\\d_]{3,20}$");
    //
    @Getter
    @Setter
    private static boolean lockdownEnabled = false;

    public LoginProcess(UnraveledMCMod plugin)
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

    /*
     * Banning and Permban checks are their respective services
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event)
    {
        final String ip = event.getAddress().getHostAddress().trim();
        final boolean isStaffMember = plugin.al.getEntryByIp(ip) != null;

        // Check if the player is already online
        for (Player onlinePlayer : server.getOnlinePlayers())
        {
            if (!onlinePlayer.getName().equalsIgnoreCase(event.getName()))
            {
                continue;
            }

            if (isStaffMember)
            {
                event.allow();
                FSync.playerKick(onlinePlayer, "A staff member just logged in with the username you are using.");
                return;
            }

            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "Your username is already logged into this server.");
            return;
        }
        // Prevent imposters of famous people from joining because I'm tired of seeing them join as them and claim to be real
        if (ConfigEntry.FAMOUS_PLAYERS.getStringList().contains(event.getName().toLowerCase()))
        {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "Stop trying to impose as famous people. Impersonation is illegal, I bet you didn't know that.");
            return;
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerLogin(PlayerLoginEvent event)
    {
        final Player player = event.getPlayer();
        final String username = player.getName();
        final String ip = event.getAddress().getHostAddress().trim();

        // Check username length
        if (username.length() < MIN_USERNAME_LENGTH || username.length() > MAX_USERNAME_LENGTH)
        {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Your username is an invalid length (must be between 3 and 20 characters long).");
            return;
        }

        // Check username characters
        if (!USERNAME_REGEX.matcher(username).find())
        {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Your username contains invalid characters.");
            return;
        }

        // Check force-IP match
        if (ConfigEntry.FORCE_IP_ENABLED.getBoolean())
        {
            final String hostname = event.getHostname().replace("\u0000FML\u0000", ""); // Forge fix - https://github.com/TotalFreedom/TotalFreedomMod/issues/493
            final String connectAddress = ConfigEntry.SERVER_ADDRESS.getString();
            final int connectPort = server.getPort();

            if (!hostname.equalsIgnoreCase(connectAddress + ":" + connectPort) && !hostname.equalsIgnoreCase(connectAddress + ".:" + connectPort))
            {
                final int forceIpPort = ConfigEntry.FORCE_IP_PORT.getInteger();
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER,
                        ConfigEntry.FORCE_IP_KICKMSG.getString()
                        .replace("%address%", ConfigEntry.SERVER_ADDRESS.getString() + (forceIpPort == DEFAULT_PORT ? "" : ":" + forceIpPort)));
                return;
            }
        }

        // Check if player is a staff member
        final boolean isStaffMember = plugin.al.getEntryByIp(ip) != null;

        // Validation below this point
        if (isStaffMember) // Player is a staff member
        {
            // Force-allow log in
            event.allow();

            int count = server.getOnlinePlayers().size();
            if (count >= server.getMaxPlayers())
            {
                for (Player onlinePlayer : server.getOnlinePlayers())
                {
                    if (!plugin.al.isStaffMember(onlinePlayer))
                    {
                        onlinePlayer.kickPlayer("You have been kicked to free up room for a staff member.");
                        count--;
                    }

                    if (count < server.getMaxPlayers())
                    {
                        break;
                    }
                }
            }

            if (count >= server.getMaxPlayers())
            {
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "The server is full and a player could not be kicked, sorry!");
                return;
            }

            return;
        }

        // Player is not a staff member
        // Server full check
        if (server.getOnlinePlayers().size() >= server.getMaxPlayers())
        {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Sorry, but this server is full.");
            return;
        }

        // StaffMember-only mode
        if (ConfigEntry.STAFF_ONLY_MODE.getBoolean())
        {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Server is temporarily open to staff members only.");
            return;
        }

        // Lockdown mode
        if (lockdownEnabled)
        {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Server is currently in lockdown mode.");
            return;
        }

        // Whitelist
        if (plugin.si.isWhitelisted())
        {
            if (!plugin.si.getWhitelisted().contains(username.toLowerCase()))
            {
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "You are not whitelisted on this server.");
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        final Player player = event.getPlayer();
        final ShopData sd = plugin.sh.getData(player);
        
        // Op player on join if the player is not opped
        if (ConfigEntry.OP_ON_JOIN.getBoolean() && !player.isOp() && !plugin.al.isStaffImposter(player))
        {
            player.setOp(true);
            player.sendMessage(FreedomCommand.YOU_ARE_OP);
        }
        
        // Has shop custom login message
        if (!plugin.al.isStaffMember(player) && !plugin.al.isStaffImposter(player) && sd.isCustomLoginMessage() && !sd.getLoginMessage().equalsIgnoreCase("none"))
        {
            FUtil.bcastMsg(plugin.sl.createLoginMessage(player, sd.getLoginMessage()));
        }

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                if (ConfigEntry.STAFF_ONLY_MODE.getBoolean())
                {
                    player.sendMessage(ChatColor.RED + "Server is currently closed to non-staff.");
                }

                if (lockdownEnabled)
                {
                    FUtil.playerMsg(player, "Warning: Server is currenty in lockdown-mode, new players will not be able to join!", ChatColor.RED);
                }
                
                if (plugin.al.isStaffMember(player) && !ConfigEntry.STAFF_LOGIN_MESSAGE.getList().isEmpty())
                {
                    List<String> messages = new ArrayList();
                    for (Object msg : ConfigEntry.STAFF_LOGIN_MESSAGE.getList())
                    {
                        messages.add(FUtil.colorize((String) msg));
                    }
                    for (int i = 0; i < messages.size(); i++)
                    {
                        player.sendMessage(messages.get(i));
                    }
                }
            }
        }.runTaskLater(plugin, 20L * 1L);
    }
}
