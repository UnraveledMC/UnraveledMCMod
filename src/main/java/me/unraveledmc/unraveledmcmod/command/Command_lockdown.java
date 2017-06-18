package me.unraveledmc.unraveledmcmod.command;


import me.unraveledmc.unraveledmcmod.rank.Rank;
import me.unraveledmc.unraveledmcmod.util.FUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Toggles the lockdown mode", usage = "/<command>", aliases = "ld")
public class Command_lockdown extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
       boolean active = !plugin.lp.isLockdownEnabled();
       plugin.lp.setLockdownEnabled(active);
       FUtil.adminAction(sender.getName(), (active ? "A" : "De-a") + "ctivating server lockdown", true);
       return true;
    }
}
