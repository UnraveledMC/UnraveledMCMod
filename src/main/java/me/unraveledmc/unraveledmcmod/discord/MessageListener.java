package me.unraveledmc.unraveledmcmod.discord;

import me.unraveledmc.unraveledmcmod.staff.StaffMember;
import net.dv8tion.jda.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;

public class MessageListener extends ListenerAdapter
{
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event)
    {
        if (!event.getAuthor().getId().equals(Discord.bot.getSelfInfo().getId()))
        {
            
            // Handle link code
            if (event.getMessage().getRawContent().matches("[0-9][0-9][0-9][0-9][0-9]"))
            {
                String code = event.getMessage().getRawContent();
                if (Discord.LINK_CODES.get(code) != null)
                {
                    StaffMember staffMember = Discord.LINK_CODES.get(code);
                    staffMember.setDiscordID(event.getMessage().getAuthor().getId());
                    Discord.LINK_CODES.remove(code);
                    Discord.sendMessage(event.getChannel(), "Link successful. Now this Discord account is linked with the Minecraft account `" + staffMember.getName() + "`.");
                    Discord.sendMessage(event.getChannel(), "Now when you are an impostor on the server you may now use `/verify` to verify.");
                }
            }
        }
    }
}
