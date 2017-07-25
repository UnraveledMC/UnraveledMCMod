package me.unraveledmc.unraveledmcmod.command;

import me.unraveledmc.unraveledmcmod.rank.Rank;
import me.unraveledmc.unraveledmcmod.util.FUtil;
import me.unraveledmc.unraveledmcmod.world.WorldTime;
import me.unraveledmc.unraveledmcmod.world.WorldWeather;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.BOTH)
@CommandParameters(description = "Go to the StaffWorld.",
        usage = "/<command> [guest < list | purge | add <player> | remove <player> > | time <morning | noon | evening | night> | weather <off | on | storm>]", aliases = "aw")
public class Command_staffworld extends FreedomCommand
{

    private enum CommandMode
    {

        TELEPORT, GUEST, TIME, WEATHER;
    }

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        CommandMode commandMode = null;

        if (args.length == 0)
        {
            commandMode = CommandMode.TELEPORT;
        }
        else if (args.length >= 2)
        {
            if ("guest".equalsIgnoreCase(args[0]))
            {
                commandMode = CommandMode.GUEST;
            }
            else if ("time".equalsIgnoreCase(args[0]))
            {
                commandMode = CommandMode.TIME;
            }
            else if ("weather".equalsIgnoreCase(args[0]))
            {
                commandMode = CommandMode.WEATHER;
            }
        }

        if (commandMode == null)
        {
            return false;
        }

        try
        {
            switch (commandMode)
            {
                case TELEPORT:
                {
                    if (!(sender instanceof Player) || playerSender == null)
                    {
                        return true;
                    }

                    World staffWorld = null;
                    try
                    {
                        staffWorld = plugin.wm.staffworld.getWorld();
                    }
                    catch (Exception ex)
                    {
                    }

                    if (staffWorld == null || playerSender.getWorld() == staffWorld)
                    {
                        msg("Going to the main world.");
                        playerSender.teleport(server.getWorlds().get(0).getSpawnLocation());
                    }
                    else
                    {
                        if (plugin.wm.staffworld.canAccessWorld(playerSender))
                        {
                            msg("Going to the StaffWorld.");
                            plugin.wm.staffworld.sendToWorld(playerSender);
                        }
                        else
                        {
                            msg("You don't have permission to access the StaffWorld.");
                        }
                    }

                    break;
                }
                case GUEST:
                {
                    checkRank(Rank.SENIOR_ADMIN);
                
                    if (args.length == 2)
                    {
                        if ("list".equalsIgnoreCase(args[1]))
                        {
                            msg("StaffWorld guest list: " + plugin.wm.staffworld.guestListToString());
                        }
                        else if ("purge".equalsIgnoreCase(args[1]))
                        {
                            assertCommandPerms(sender, playerSender);
                            plugin.wm.staffworld.purgeGuestList();
                            FUtil.staffAction(sender.getName(), "StaffWorld guest list purged.", false);
                        }
                        else
                        {
                            return false;
                        }
                    }
                    else if (args.length == 3)
                    {
                        assertCommandPerms(sender, playerSender);

                        if ("add".equalsIgnoreCase(args[1]))
                        {
                            final Player player = getPlayer(args[2]);

                            if (player == null)
                            {
                                sender.sendMessage(FreedomCommand.PLAYER_NOT_FOUND);
                                return true;
                            }

                            if (plugin.wm.staffworld.addGuest(player, playerSender))
                            {
                                FUtil.staffAction(sender.getName(), "StaffWorld guest added: " + player.getName(), false);
                            }
                            else
                            {
                                msg("Could not add player to guest list.");
                            }
                        }
                        else if ("remove".equals(args[1]))
                        {
                            final Player player = plugin.wm.staffworld.removeGuest(args[2]);
                            if (player != null)
                            {
                                FUtil.staffAction(sender.getName(), "StaffWorld guest removed: " + player.getName(), false);
                            }
                            else
                            {
                                msg("Can't find guest entry for: " + args[2]);
                            }
                        }
                        else
                        {
                            return false;
                        }
                    }

                    break;
                }
                case TIME:
                {
                    assertCommandPerms(sender, playerSender);

                    if (args.length == 2)
                    {
                        WorldTime timeOfDay = WorldTime.getByAlias(args[1]);
                        if (timeOfDay != null)
                        {
                            plugin.wm.staffworld.setTimeOfDay(timeOfDay);
                            msg("StaffWorld time set to: " + timeOfDay.name());
                        }
                        else
                        {
                            msg("Invalid time of day. Can be: sunrise, noon, sunset, midnight");
                        }
                    }
                    else
                    {
                        return false;
                    }

                    break;
                }
                case WEATHER:
                {
                    assertCommandPerms(sender, playerSender);

                    if (args.length == 2)
                    {
                        WorldWeather weatherMode = WorldWeather.getByAlias(args[1]);
                        if (weatherMode != null)
                        {
                            plugin.wm.staffworld.setWeatherMode(weatherMode);
                            msg("StaffWorld weather set to: " + weatherMode.name());
                        }
                        else
                        {
                            msg("Invalid weather mode. Can be: off, rain, storm");
                        }
                    }
                    else
                    {
                        return false;
                    }

                    break;
                }
                default:
                {
                    return false;
                }
            }
        }
        catch (PermissionDeniedException ex)
        {
            if (ex.getMessage().isEmpty())
            {
                return noPerms();
            }
            sender.sendMessage(ex.getMessage());
            return true;
        }

        return true;
    }

    // TODO: Redo this properly
    private void assertCommandPerms(CommandSender sender, Player playerSender) throws PermissionDeniedException
    {
        if (!(sender instanceof Player) || playerSender == null || !isStaffMember(sender))
        {
            throw new PermissionDeniedException();
        }
    }

    private class PermissionDeniedException extends Exception
    {

        private static final long serialVersionUID = 1L;

        private PermissionDeniedException()
        {
            super("");
        }

        private PermissionDeniedException(String string)
        {
            super(string);
        }
    }

}
