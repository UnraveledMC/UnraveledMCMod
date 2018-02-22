package me.unraveledmc.unraveledmcmod.command;

import me.unraveledmc.unraveledmcmod.rank.Rank;
import me.unraveledmc.unraveledmcmod.util.FUtil;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import org.bukkit.entity.AreaEffectCloud;

@CommandPermissions(level = Rank.MOD, source = SourceType.BOTH)
@CommandParameters(description = "Clears lingering potion area effect clouds.", usage = "/<command>", aliases="aec")
public class Command_aeclear extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        FUtil.staffAction(sender.getName(), "Removing all area effect clouds.", true);
        int removed = 0;
        for (World world : server.getWorlds())
        {
            for (Entity entity : world.getEntities())
            {
                if (entity instanceof AreaEffectCloud)
                {
                    entity.remove();
                    removed++;
                }
            }
        }
        msg(removed + " area effect clouds removed.");
        return true;
    }
}