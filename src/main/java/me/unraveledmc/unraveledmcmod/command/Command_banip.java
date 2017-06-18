package me.unraveledmc.unraveledmcmod.command;

import me.unraveledmc.unraveledmcmod.banning.Ban;
import me.unraveledmc.unraveledmcmod.rank.Rank;
import me.unraveledmc.unraveledmcmod.util.FUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH, blockHostConsole = true)
@CommandParameters(description = "Ban an ip", usage = "/<command> <ip> [reason]")
public class Command_banip extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0)
        {
            return false;
        }

        String ip = args[0];
        if (ip.split("\\.").length != 4)
        {
            msg("That is not a valid ip!", ChatColor.RED);
            return true;
        }
        String reason = null;
        if (args.length > 1)
        {
            reason = StringUtils.join(ArrayUtils.subarray(args, 1, args.length), " ");
        }
        
        Ban ban = Ban.forPlayerIp(ip, sender, null, reason);
        plugin.bm.addBan(ban);
        
        final StringBuilder bcast = new StringBuilder()
                .append(ChatColor.RED)
                .append(sender.getName())
                .append(" - ")
                .append("Banning IP: ")
                .append(ip);
        if (reason != null)
        {
            bcast.append(" - Reason: ").append(ChatColor.YELLOW).append(reason);
        }
        FUtil.bcastMsg(bcast.toString());
        
        for (Player player : server.getOnlinePlayers())
        {
            if (player.getAddress().getAddress().getHostAddress().equals(ip))
            {
                player.kickPlayer(ban.bakeKickMessage());
            }
        }
        return true;
    }
}
