package me.unraveledmc.unraveledmcmod.blocking;

import java.util.Arrays;
import java.util.List;
import me.unraveledmc.unraveledmcmod.FreedomService;
import me.unraveledmc.unraveledmcmod.UnraveledMCMod;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import me.libraryaddict.disguise.events.DisguiseEvent;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;

public class DisguiseBlocker extends FreedomService
{

    public static final List<DisguiseType> BLOCKED_DISGUISES = Arrays.asList(
            DisguiseType.ITEM_FRAME, DisguiseType.ENDER_DRAGON, DisguiseType.PLAYER, 
            DisguiseType.GIANT, DisguiseType.GHAST, DisguiseType.MAGMA_CUBE, DisguiseType.SLIME, 
            DisguiseType.DROPPED_ITEM, DisguiseType.ENDER_CRYSTAL, DisguiseType.AREA_EFFECT_CLOUD, 
            DisguiseType.WITHER, DisguiseType.SPLASH_POTION);
    
    public DisguiseBlocker(UnraveledMCMod plugin)
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
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onDisguise(DisguiseEvent event)
    {
        DisguiseType disguise = event.getDisguise().getType();
        if (event.getEntity() instanceof Player)
        {
            final Player player = (Player)event.getEntity();
            if (BLOCKED_DISGUISES.contains(disguise))
            {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "That disguise is forbidden.");
            }
        }
    }
}
