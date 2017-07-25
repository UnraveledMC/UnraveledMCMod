package me.unraveledmc.unraveledmcmod.command;

import java.util.Date;
import me.unraveledmc.unraveledmcmod.staff.StaffMember;
import me.unraveledmc.unraveledmcmod.player.FPlayer;
import me.unraveledmc.unraveledmcmod.rank.Rank;
import me.unraveledmc.unraveledmcmod.util.FUtil;
import net.pravian.aero.util.Ips;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.BOTH)
@CommandParameters(description = "Manage staff.", usage = "/<command> <list | clean | reload | | setrank <username> <rank> | <add | remove | info> <username>>")
public class Command_saconfig extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length < 1)
        {
            return false;
        }

        switch (args[0])
        {
            case "list":
            {
                msg("Staff members: " + StringUtils.join(plugin.al.getStaffNames(), ", "), ChatColor.GOLD);

                return true;
            }

            case "clean":
            {
                checkRank(Rank.ADMIN);

                FUtil.staffAction(sender.getName(), "Cleaning the staff list", true);
                plugin.al.deactivateOldEntries(true);
                msg("Staff members: " + StringUtils.join(plugin.al.getStaffNames(), ", "), ChatColor.GOLD);

                return true;
            }

            case "reload":
            {
                checkRank(Rank.MOD);

                FUtil.staffAction(sender.getName(), "Reloading the staff list", true);
                plugin.al.load();
                msg("Staff list reloaded!");
                return true;
            }

            case "setrank":
            {
                checkRank(Rank.SENIOR_ADMIN);

                if (args.length < 3)
                {
                    return false;
                }

                Rank rank = Rank.findRank(args[2]);
                if (rank == null)
                {
                    msg("Unknown rank: " + rank);
                    return true;
                }

                if (!rank.isAtLeast(Rank.MOD))
                {
                    msg("Rank must be Mod or higher.", ChatColor.RED);
                    return true;
                }
                
                if (rank.equals(Rank.ADMIN_CONSOLE) || rank.equals(Rank.SENIOR_CONSOLE))
                {
                    msg("You can not use the " + rank.getName() + " rank", ChatColor.RED);
                    return true;
                }

                StaffMember staffMember = plugin.al.getEntryByName(args[1]);
                if (staffMember == null)
                {
                    msg("Unknown staff member: " + args[1]);
                    return true;
                }

                FUtil.staffAction(sender.getName(), "Setting " + staffMember.getName() + "'s rank to " + rank.getName(), true);

                staffMember.setRank(rank);
                plugin.al.save();

                msg("Set " + staffMember.getName() + "'s rank to " + rank.getName());
                return true;
            }

            case "info":
            {
                if (args.length < 2)
                {
                    return false;
                }

                checkRank(Rank.MOD);

                StaffMember staffMember = plugin.al.getEntryByName(args[1]);

                if (staffMember == null)
                {
                    final Player player = getPlayer(args[1]);
                    if (player != null)
                    {
                        staffMember = plugin.al.getStaffMember(player);
                    }
                }

                if (staffMember == null)
                {
                    msg("Staff member not found: " + args[1]);
                }
                else
                {
                    msg(staffMember.toString());
                }

                return true;
            }

            case "add":
            {
                if (args.length < 2)
                {
                    return false;
                }
                checkRank(Rank.ADMIN);

                // Player already on the list?
                final Player player = getPlayer(args[1]);
                if (player != null && plugin.al.isStaffMember(player))
                {
                    msg("That player is already on the staff list.");
                    return true;
                }

                // Find the old staff list entry
                String name = player != null ? player.getName() : args[1];
                StaffMember staffMember = null;
                for (StaffMember loopStaffMember : plugin.al.getAllStaff().values())
                {
                    if (loopStaffMember.getName().equalsIgnoreCase(name))
                    {
                        staffMember = loopStaffMember;
                        break;
                    }
                }

                if (staffMember == null) // New staff member
                {
                    if (player == null)
                    {
                        msg(FreedomCommand.PLAYER_NOT_FOUND);
                        return true;
                    }

                    FUtil.staffAction(sender.getName(), "Adding " + player.getName() + " to the staff list", true);
                    plugin.al.addStaffMember(new StaffMember(player));
                }
                else // Existing staff member
                {
                    FUtil.staffAction(sender.getName(), "Readding " + staffMember.getName() + " to the staff list", true);

                    if (player != null)
                    {
                        staffMember.setName(player.getName());
                        staffMember.addIp(Ips.getIp(player));
                    }

                    staffMember.setActive(true);
                    staffMember.setLastLogin(new Date());

                    plugin.al.save();
                    plugin.al.updateTables();
                }

                if (player != null)
                {
                    final FPlayer fPlayer = plugin.pl.getPlayer(player);
                    if (fPlayer.getFreezeData().isFrozen())
                    {
                        fPlayer.getFreezeData().setFrozen(false);
                        msg(player.getPlayer(), "You have been unfrozen.");
                    }
                }

                return true;
            }

            case "remove":
            {
                if (args.length < 2)
                {
                    return false;
                }

                checkRank(Rank.ADMIN);

                Player player = getPlayer(args[1]);
                StaffMember staffMember = player != null ? plugin.al.getStaffMember(player) : plugin.al.getEntryByName(args[1]);

                if (staffMember == null)
                {
                    msg("Staff member not found: " + args[1]);
                    return true;
                }

                FUtil.staffAction(sender.getName(), "Removing " + staffMember.getName() + " from the staff list", true);
                staffMember.setActive(false);
                plugin.al.save();
                plugin.al.updateTables();
                return true;
            }

            default:
            {
                return false;
            }
        }
    }

}
