package me.unraveledmc.unraveledmcmod.bridge;

import me.unraveledmc.unraveledmcmod.FreedomService;
import me.unraveledmc.unraveledmcmod.UnraveledMCMod;
import me.unraveledmc.unraveledmcmod.util.FLog;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.io.File;
import java.util.List;
import java.util.Arrays;

public class CoreProtectBridge extends FreedomService
{
    private CoreProtectAPI coreProtectAPI = null;
    
    private final List<String> tables = Arrays.asList("co_sign", "co_session", "co_container", "co_block");

    public CoreProtectBridge(UnraveledMCMod plugin)
    {
        super(plugin);
    }

    @Override
    protected void onStart()
    {
    }

    @Override
    protected void onStop()
    {
    }
    
    public CoreProtect getCoreProtect()
    {
        CoreProtect coreProtect = null;
        try
        {
            final Plugin coreProtectPlugin = Bukkit.getServer().getPluginManager().getPlugin("CoreProtect");

            if (coreProtectPlugin != null && coreProtectPlugin instanceof CoreProtect)
            {
                coreProtect = (CoreProtect)coreProtectPlugin;
            }
        }
        catch (Exception ex)
        {
            FLog.severe(ex);
        }
        return coreProtect;
    }

    public CoreProtectAPI getCoreProtectAPI()
    {
        if (coreProtectAPI == null)
        {
            try
            {
                final CoreProtect coreProtect = getCoreProtect();
                
                coreProtectAPI = coreProtect.getAPI();
                
                // Check if the plugin or api is not enabled, if so, return null
                if (!coreProtect.isEnabled() || !coreProtectAPI.isEnabled())
                {
                    return null;
                }
            }
            catch (Exception ex)
            {
                FLog.severe(ex);
            }
        }
        return coreProtectAPI;
    }
    
    public boolean isEnabled()
    {
        final CoreProtect coreProtect = getCoreProtect();

        return coreProtect != null && coreProtect.isEnabled();
    }

    // Rollback the specifed player's edits that were in the last 24 hours.
    public void rollback(final String name)
    {
        final CoreProtectAPI coreProtect = getCoreProtectAPI();

        if (!isEnabled())
        {
            return;
        }
        
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                coreProtect.performRollback(86400, Arrays.asList(name), null, null, null, null, 0, null);
            }
        }.runTaskAsynchronously(plugin);
    }
    
    // Reverts a rollback for the specifed player's edits that were in the last 24 hours.
    public void undoRollback(final String name)
    {
        final CoreProtectAPI coreProtect = getCoreProtectAPI();

        if (!isEnabled())
        {
            return;
        }

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                coreProtect.performRestore(86400, Arrays.asList(name), null, null, null, null, 0, null);
            }
        }.runTaskAsynchronously(plugin);
    }
    
    // Wipes DB for the specified world
    public void clearDatabase(World world)
    {
        final CoreProtect coreProtect = getCoreProtect();

        if (coreProtect == null)
        {
            return;
        }
        
        /* As CoreProtect doesn't have an api method for deleting all of the data for a specific world
           we have to do this manually via sql */
        File databaseFile = new File(coreProtect.getDataFolder(), "database.db");
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            
            // Obtain world ID from CoreProtect database
            ResultSet resultSet = statement.executeQuery("SELECT id FROM co_world WHERE world = '" + world.getName() + "'");
            String worldID = null;
            while (resultSet.next())
            {
                worldID = String.valueOf(resultSet.getInt("id"));
            }

            // Ensure the world ID is not null
            if (worldID == null)
            {
                FLog.warning("Failed to obtain the world ID for the " + world.getName());
                return;
            }

            for (String table : tables)
            {
                statement.executeUpdate("DELETE FROM " + table + " WHERE wid = " + worldID);
            }
            
            // This shrinks down the file size
            statement.executeUpdate("VACUUM");

            connection.close();
        }
        catch (SQLException e)
        {
            FLog.warning("Failed to delete the CoreProtect data for the " + world.getName());
        }
    }
}
