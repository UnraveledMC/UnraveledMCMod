package me.unraveledmc.unraveledmcmod.command;

import me.unraveledmc.unraveledmcmod.leveling.Level;
import me.unraveledmc.unraveledmcmod.rank.Rank;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.NON_OP, source = SourceType.BOTH)
@CommandParameters(description = "Gets your level or another player's level", usage = "/<command> [player]")
public class Command_level extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        Player player;
        if (senderIsConsole && args.length == 0)
        {
            msg("You most specify a player as you are sending this from the console.");
            return true;
        }
        
        if (args.length > 0)
        {
            player = getPlayer(args[0]);
            if (player == null)
            {
                msg(FreedomCommand.PLAYER_NOT_FOUND);
                return true;
            }
        }
        else
        {
            player = playerSender;
        }
        
        Level level = plugin.lvm.getLevel(player);
        msg(player.getName() + " is a " + level.getColoredName(), ChatColor.AQUA);
        return true;
    }
}
