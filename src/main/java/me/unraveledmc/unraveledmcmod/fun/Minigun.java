package me.unraveledmc.unraveledmcmod.fun;

import me.unraveledmc.unraveledmcmod.FreedomService;
import me.unraveledmc.unraveledmcmod.UnraveledMCMod;
import me.unraveledmc.unraveledmcmod.shop.ShopData;
import me.unraveledmc.unraveledmcmod.util.FUtil;
import java.util.List;
import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.Sound;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.entity.Arrow;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;

public class Minigun extends FreedomService
{
    public List<Integer> bullets = new ArrayList<>();

    public Minigun(UnraveledMCMod plugin)
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
    public void onPlayerRightClick(PlayerInteractEvent event)
    {
        if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
        {
            Player p = event.getPlayer();
            ShopData sd = plugin.sh.getData(p);
            if (p.getInventory().getItemInMainHand().equals(getMinigun()) && sd.isMinigun())
            {
                Arrow bullet = p.launchProjectile(Arrow.class, p.getLocation().getDirection());
                bullets.add(bullet.getEntityId());
                bullet.setVelocity(bullet.getVelocity().normalize().multiply(25));
                p.getWorld().playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 30, 2.0f);
            }
        }
    }
    
    public ItemStack getMinigun()
    {
        ItemStack minigun = new ItemStack(Material.IRON_BARDING);
        ItemMeta datMeta = minigun.getItemMeta();
        datMeta.setDisplayName(ChatColor.DARK_RED + "Minigun");
        List<String> lore = new ArrayList();
        lore.add(FUtil.colorize("&6&oEvery game needs a minigun"));
        datMeta.setLore(lore);
        datMeta.addEnchant(Enchantment.ARROW_DAMAGE, 420, true);
        datMeta.setUnbreakable(true);
        minigun.setItemMeta(datMeta);
        return minigun;
    }
}
