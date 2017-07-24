package me.unraveledmc.unraveledmcmod.staff;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import me.unraveledmc.unraveledmcmod.FreedomService;
import me.unraveledmc.unraveledmcmod.UnraveledMCMod;
import me.unraveledmc.unraveledmcmod.config.ConfigEntry;
import me.unraveledmc.unraveledmcmod.rank.Rank;
import me.unraveledmc.unraveledmcmod.util.FLog;
import me.unraveledmc.unraveledmcmod.util.FUtil;
import net.pravian.aero.config.YamlConfig;
import net.pravian.aero.util.Ips;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;

public class StaffList extends FreedomService
{

    public static final String CONFIG_FILENAME = "staff.yml";

    @Getter
    private final Map<String, StaffMember> allStaff = Maps.newHashMap(); // Includes disabled staff members
    // Only active staff members below
    @Getter
    private final Set<StaffMember> activeStaff = Sets.newHashSet();
    private final Map<String, StaffMember> nameTable = Maps.newHashMap();
    private final Map<String, StaffMember> ipTable = Maps.newHashMap();
    //
    private final YamlConfig config;

    public StaffList(UnraveledMCMod plugin)
    {
        super(plugin);

        this.config = new YamlConfig(plugin, CONFIG_FILENAME, true);
    }

    @Override
    protected void onStart()
    {
        load();

        server.getServicesManager().register(Function.class, new Function<Player, Boolean>()
        {
            @Override
            public Boolean apply(Player player)
            {
                return isStaffMember(player);
            }
        }, plugin, ServicePriority.Normal);

        deactivateOldEntries(false);
    }

    @Override
    protected void onStop()
    {
        save();
    }

    public void load()
    {
        config.load();

        allStaff.clear();
        for (String key : config.getKeys(false))
        {
            ConfigurationSection section = config.getConfigurationSection(key);
            if (section == null)
            {
                logger.warning("Invalid staff member list format: " + key);
                continue;
            }

            StaffMember staffMember = new StaffMember(key);
            staffMember.loadFrom(section);

            if (!staffMember.isValid())
            {
                FLog.warning("Could not load staff member: " + key + ". Missing details!");
                continue;
            }

            allStaff.put(key, staffMember);
        }

        updateTables();
        FLog.info("Loaded " + allStaff.size() + " staff members (" + nameTable.size() + " active,  " + ipTable.size() + " IPs)");
    }

    public void save()
    {
        // Clear the config
        for (String key : config.getKeys(false))
        {
            config.set(key, null);
        }

        for (StaffMember staffMember : allStaff.values())
        {
            staffMember.saveTo(config.createSection(staffMember.getConfigKey()));
        }

        config.save();
    }

    public synchronized boolean isStaffSync(CommandSender sender)
    {
        return isStaffMember(sender);
    }

    public boolean isStaffMember(CommandSender sender)
    {
        if (!(sender instanceof Player))
        {
            return true;
        }

        StaffMember staffMember = getStaffMember((Player) sender);

        return staffMember != null && staffMember.isActive();
    }
    
    public boolean isAdmin(CommandSender sender)
    {
        StaffMember staffMember = getStaffMember(sender);
        if (staffMember == null)
        {
            return false;
        }

        return staffMember.getRank().ordinal() >= Rank.ADMIN.ordinal();
    }

    public boolean isSeniorAdmin(CommandSender sender)
    {
        StaffMember staffMember = getStaffMember(sender);
        if (staffMember == null)
        {
            return false;
        }

        return staffMember.getRank().ordinal() >= Rank.SENIOR_ADMIN.ordinal();
    }

    public StaffMember getStaffMember(CommandSender sender)
    {
        if (sender instanceof Player)
        {
            return getStaffMember((Player) sender);
        }

        return getEntryByName(sender.getName());
    }

    public StaffMember getStaffMember(Player player)
    {
        // Find staff member
        String ip = Ips.getIp(player);
        StaffMember staffMember = getEntryByName(player.getName());

        // Staff member by name
        if (staffMember != null)
        {
            // Check if we're in online mode,
            // Or the players IP is in the staff member entry
            if (Bukkit.getOnlineMode() || staffMember.getIps().contains(ip))
            {
                if (!staffMember.getIps().contains(ip))
                {
                    // Add the new IP if we have to
                    staffMember.addIp(ip);
                    save();
                    updateTables();
                }
                return staffMember;
            }

            // Impostor
        }

        // Staff member by ip
        staffMember = getEntryByIp(ip);
        if (staffMember != null)
        {
            // Set the new username
            staffMember.setName(player.getName());
            save();
            updateTables();
        }

        return null;
    }

    public StaffMember getEntryByName(String name)
    {
        return nameTable.get(name.toLowerCase());
    }

    public StaffMember getEntryByIp(String ip)
    {
        return ipTable.get(ip);
    }

    public StaffMember getEntryByIpFuzzy(String needleIp)
    {
        final StaffMember directStaffMember = getEntryByIp(needleIp);
        if (directStaffMember != null)
        {
            return directStaffMember;
        }

        for (String ip : ipTable.keySet())
        {
            if (FUtil.fuzzyIpMatch(needleIp, ip, 3))
            {
                return ipTable.get(ip);
            }
        }

        return null;
    }

    public void updateLastLogin(Player player)
    {
        final StaffMember staffMember = getStaffMember(player);
        if (staffMember == null)
        {
            return;
        }

        staffMember.setLastLogin(new Date());
        staffMember.setName(player.getName());
        save();
    }

    public boolean isStaffImposter(Player player)
    {
        return getEntryByName(player.getName()) != null && !isStaffMember(player);
    }

    public boolean isIdentityMatched(Player player)
    {
        if (Bukkit.getOnlineMode())
        {
            return true;
        }

        StaffMember staffMember = getStaffMember(player);
        return staffMember == null ? false : staffMember.getName().equalsIgnoreCase(player.getName());
    }

    public boolean addStaffMember(StaffMember staffMember)
    {
        if (!staffMember.isValid())
        {
            logger.warning("Could not add staff member: " + staffMember.getConfigKey() + " staff member is missing details!");
            return false;
        }

        final String key = staffMember.getConfigKey();

        // Store staff member, update views
        allStaff.put(key, staffMember);
        updateTables();

        // Save staff member
        staffMember.saveTo(config.createSection(key));
        config.save();

        return true;
    }

    public boolean removeStaffMember(StaffMember staffMember)
    {
        // Remove staff member, update views
        if (allStaff.remove(staffMember.getConfigKey()) == null)
        {
            return false;
        }
        updateTables();

        // 'Unsave' staff member
        config.set(staffMember.getConfigKey(), null);
        config.save();

        return true;
    }

    public void updateTables()
    {
        activeStaff.clear();
        nameTable.clear();
        ipTable.clear();

        for (StaffMember staffMember : allStaff.values())
        {
            if (!staffMember.isActive())
            {
                continue;
            }

            activeStaff.add(staffMember);
            nameTable.put(staffMember.getName().toLowerCase(), staffMember);

            for (String ip : staffMember.getIps())
            {
                ipTable.put(ip, staffMember);
            }

        }

        plugin.wm.staffworld.wipeAccessCache();
    }

    public Set<String> getStaffNames()
    {
        return nameTable.keySet();
    }

    public Set<String> getStaffIps()
    {
        return ipTable.keySet();
    }

    public void deactivateOldEntries(boolean verbose)
    {
        for (StaffMember staffMember : allStaff.values())
        {
            if (!staffMember.isActive() || staffMember.getRank().isAtLeast(Rank.SENIOR_ADMIN))
            {
                continue;
            }

            final Date lastLogin = staffMember.getLastLogin();
            final long lastLoginHours = TimeUnit.HOURS.convert(new Date().getTime() - lastLogin.getTime(), TimeUnit.MILLISECONDS);

            if (lastLoginHours < ConfigEntry.STAFFLIST_CLEAN_THESHOLD_HOURS.getInteger())
            {
                continue;
            }

            if (verbose)
            {
                FUtil.adminAction(plugin.getName(), "Deactivating staff member " + staffMember.getName() + ", inactive for " + lastLoginHours + " hours", true);
            }

            staffMember.setActive(false);
        }

        save();
        updateTables();
    }
}
