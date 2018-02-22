package me.unraveledmc.unraveledmcmod.discord;

import me.unraveledmc.unraveledmcmod.staff.StaffMember;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class MessageListener extends ListenerAdapter
{
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event)
    {
        if (!event.getAuthor().getId().equals(Discord.bot.getSelfUser().getId()))
        {
            
            // Handle link code
            String code = event.getMessage().getContentRaw();
            if (code.matches("[0-9][0-9][0-9][0-9][0-9]"))
            {
                if (Discord.LINK_CODES.get(code) != null)
                {
                    StaffMember staffMember = Discord.LINK_CODES.get(code);
                    staffMember.setDiscordID(event.getMessage().getAuthor().getId());
                    Discord.LINK_CODES.remove(code);
                    event.getChannel().sendMessage("Link successful. Now this Discord account is linked with the Minecraft account `" + staffMember.getName() + "`.\nNow when you are an impostor on the server, you may use `/verify` to verify.").complete();
                }
            }
        }
    }
}
