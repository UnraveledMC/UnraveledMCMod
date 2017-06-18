package me.unraveledmc.unraveledmcmod.leveling;

import me.unraveledmc.unraveledmcmod.FreedomService;
import me.unraveledmc.unraveledmcmod.UnraveledMCMod;
import me.unraveledmc.unraveledmcmod.shop.ShopData;
import me.unraveledmc.unraveledmcmod.config.ConfigEntry;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

public class LevelManager extends FreedomService
{

    public LevelManager(UnraveledMCMod plugin)
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

    public Level getLevel(Player player)
    {
        ShopData sd = plugin.sh.getData(player);
        return sd.getLevel();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        Level level = getLevel(player);
        if (!plugin.al.isAdmin(player) && !ConfigEntry.SERVER_MASTER_BUILDERS.getStringList().contains(player.getName()))
        {
            plugin.pl.getPlayer(player).setTag(level.getTag());
        }
    }
}
