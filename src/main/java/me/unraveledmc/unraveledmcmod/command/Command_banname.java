package me.unraveledmc.unraveledmcmod.command;

import me.unraveledmc.unraveledmcmod.banning.Ban;
import me.unraveledmc.unraveledmcmod.rank.Rank;
import me.unraveledmc.unraveledmcmod.util.FUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.MOD, source = SourceType.BOTH, blockHostConsole = true)
@CommandParameters(description = "Ban a name", usage = "/<command> <username> [reason]")
public class Command_banname extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0)
        {
            return false;
        }

        String username = args[0];
        String reason = null;
        if (args.length > 1)
        {
            reason = StringUtils.join(ArrayUtils.subarray(args, 1, args.length), " ");
        }
        
        Ban ban = Ban.forPlayerName(username, sender, null, reason);
        plugin.bm.addBan(ban);
        
        final StringBuilder bcast = new StringBuilder()
                .append(ChatColor.RED)
                .append(sender.getName())
                .append(" - ")
                .append("Banning username: ")
                .append(username);
        if (reason != null)
        {
            bcast.append(" - Reason: ").append(ChatColor.YELLOW).append(reason);
        }
        FUtil.bcastMsg(bcast.toString());
        
        Player player = getPlayer(args[0]);
        if (player != null)
        {
            player.kickPlayer(ban.bakeKickMessage());
        }
        return true;
    }
}
