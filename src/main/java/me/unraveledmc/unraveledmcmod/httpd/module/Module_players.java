package me.unraveledmc.unraveledmcmod.httpd.module;

import me.unraveledmc.unraveledmcmod.UnraveledMCMod;
import me.unraveledmc.unraveledmcmod.staff.StaffMember;
import me.unraveledmc.unraveledmcmod.httpd.NanoHTTPD;
import me.unraveledmc.unraveledmcmod.util.FUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Module_players extends HTTPDModule
{

    public Module_players(UnraveledMCMod plugin, NanoHTTPD.HTTPSession session)
    {
        super(plugin, session);
    }

    @Override
    @SuppressWarnings("unchecked")
    public NanoHTTPD.Response getResponse()
    {
        final JSONObject responseObject = new JSONObject();

        final JSONArray players = new JSONArray();
        final JSONArray mods = new JSONArray();
        final JSONArray admins = new JSONArray();
        final JSONArray senioradmins = new JSONArray();
        final JSONArray developers = new JSONArray();

        // All online players
        for (Player player : Bukkit.getOnlinePlayers())
        {
            players.add(player.getName());
        }

        // Staff
        for (StaffMember staffMember : plugin.al.getAllStaff().values())
        {
            final String username = staffMember.getName();

            switch (staffMember.getRank())
            {
                case MOD:
                    mods.add(username);
                    break;
                case ADMIN:
                    admins.add(username);
                    break;
                case SENIOR_ADMIN:
                    senioradmins.add(username);
                    break;
            }
        }

        // Developers
        developers.addAll(FUtil.UMCDEVS);

        responseObject.put("players", players);
        responseObject.put("mods", mods);
        responseObject.put("admins", admins);
        responseObject.put("senioradmins", senioradmins);
        responseObject.put("developers", developers);

        final NanoHTTPD.Response response = new NanoHTTPD.Response(NanoHTTPD.Response.Status.OK, NanoHTTPD.MIME_JSON, responseObject.toString());
        response.addHeader("Access-Control-Allow-Origin", "*");
        return response;
    }
}
