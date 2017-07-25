package me.unraveledmc.unraveledmcmod;

import me.unraveledmc.unraveledmcmod.util.FUtil;
import me.unraveledmc.unraveledmcmod.config.ConfigEntry;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandSpy extends FreedomService
{

    public CommandSpy(UnraveledMCMod plugin)
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

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
    {
        for (Player player : server.getOnlinePlayers())
        {
            if (plugin.al.isStaffMember(player) && plugin.pl.getPlayer(player).cmdspyEnabled())
            {
                if (plugin.al.isStaffMember(event.getPlayer()) && !ConfigEntry.SERVER_OWNERS.getStringList().contains(player.getName()) || !ConfigEntry.SERVER_FOUNDERS.getStringList().contains(player.getName()) || !FUtil.UMCDEVS.contains(player.getName()))
                {
                    continue;
                }
                if (event.getPlayer() != player)
                {
                    FUtil.playerMsg(player, event.getPlayer().getName() + ": " + event.getMessage());
                }
            }
        }
    }
}
