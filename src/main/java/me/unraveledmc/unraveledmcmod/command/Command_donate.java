package me.unraveledmc.unraveledmcmod.command;

import me.unraveledmc.unraveledmcmod.rank.Rank;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.NON_OP, source = SourceType.BOTH)
@CommandParameters(description = "Shows how to donate to the server", usage = "/<command>", aliases = "don")
public class Command_donate extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        msg("Hey you, you want to donate?", ChatColor.AQUA);
        msg("AwesomePinch has to keep the server up, so your donation would be helpful.", ChatColor.DARK_GREEN);
        msg("Here is how, but first you will will need a paypal account for this.", ChatColor.GOLD);
        msg(" - Go to https://paypal.com/ and login.", ChatColor.DARK_GREEN);
        msg(" - Go to https://www.paypal.com/myaccount/transfer/send", ChatColor.GOLD);
        msg(" - In the box, type drivono@yahoo.com", ChatColor.DARK_AQUA);
        msg(" - Enter the amount you would like to send, in USD or your currency.", ChatColor.RED);
        msg(" - Be sure to leave a note with your minecraft username, so we know that you donated!", ChatColor.YELLOW);
        msg(" - After that, send the payment, AwesomePinch will look later at the paypal account, and give you a special donator rank.", ChatColor.AQUA);
        return true;
    }
}
