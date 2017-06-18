package me.unraveledmc.unraveledmcmod.command;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import me.unraveledmc.unraveledmcmod.rank.Rank;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Gives you a random food item (nero's idea ok)", usage = "/<command>")
public class Command_crumb extends FreedomCommand
{
    
    public static final List<Material> ITEMS = Arrays.asList(new Material[]
    {
        Material.CAKE, Material.APPLE, Material.BREAD, Material.COOKED_BEEF,
        Material.COOKED_CHICKEN, Material.COOKED_FISH, Material.COOKED_MUTTON, Material.COOKED_RABBIT,
        Material.BEETROOT_SOUP, Material.CARROT, Material.CHORUS_FRUIT, Material.COOKIE, Material.RABBIT_STEW,
        Material.MUSHROOM_SOUP, Material.MELON
    });
    
    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        Random random = new Random();
        int index = random.nextInt(ITEMS.toArray().length);
        Material material = ITEMS.get(index);
        ItemStack itemstack = new ItemStack(material);
        playerSender.getInventory().addItem(itemstack);
        return true;
    }
}
