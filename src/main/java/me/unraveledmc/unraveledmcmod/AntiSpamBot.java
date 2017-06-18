package me.unraveledmc.unraveledmcmod;

import me.unraveledmc.unraveledmcmod.util.FLog;
import me.unraveledmc.unraveledmcmod.config.ConfigEntry;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import me.unraveledmc.unraveledmcmod.util.FUtil;
    

public class AntiSpamBot extends FreedomService
{
    
    public final List<String> SPAMBOT_IPS = new ArrayList();
    
    public AntiSpamBot(UnraveledMCMod plugin)
    {
        super(plugin);
    }

    @Override
    protected void onStart()
    {
        try
        {
            loadIps();
        }
        catch (FileNotFoundException e)
        {
            try
            {
                FUtil.copyFile(plugin.getResource("deathbotips.txt"), "deathbotips.txt");
                loadIps();
            }
            catch (IOException ex)
            {
                FLog.warning("Failed to copy deathbotips.txt, disabling Anti-SpamBot");
                this.unregister();
            }
        }
    }

    @Override
    protected void onStop()
    {
        SPAMBOT_IPS.clear();
    }
    
    public void loadIps() throws FileNotFoundException
    {
        Scanner scanner = new Scanner(new File(plugin.getDataFolder().getPath() + "/deathbotips.txt"));
        while (scanner.hasNextLine())
        {
            SPAMBOT_IPS.add(scanner.nextLine());
        }
        FLog.info("Loaded " + SPAMBOT_IPS.size() + " spambot ips");
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event)
    {
        final String ip = event.getAddress().getHostAddress().trim();
        if (SPAMBOT_IPS.contains(ip))
        {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.RED + "You've been detected as a spambot. If you believe this is an error please file a permban appeal on " + ChatColor.DARK_BLUE + ConfigEntry.SERVER_PERMBAN_URL.getString());
        }
    }
}
