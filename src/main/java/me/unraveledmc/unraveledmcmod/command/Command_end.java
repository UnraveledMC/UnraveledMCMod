package me.unraveledmc.unraveledmcmod.command;

import me.unraveledmc.unraveledmcmod.rank.Rank;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.NON_OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Goto the end.", usage = "/<command>")
public class Command_end extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        plugin.wm.gotoWorld(playerSender, server.getWorlds().get(0).getName() + "_the_end");
        return true;
    }
}
