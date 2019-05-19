import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandDuel implements CommandExecutor {

    private CNMCServer plugin;

    public CommandDuel(CNMCServer plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) return false;
        if (!(sender instanceof Player)) {
            sender.sendMessage("Use in game.");
            return true;
        }
        if (Bukkit.getPlayerExact(args[0]) == null) {
            sender.sendMessage("Player Not Found");
            return true;
        }

        if(Bukkit.getPlayerExact(args[0]).getUniqueId().equals(((Player) sender).getUniqueId())) {
            sender.sendMessage("You cannot send a duel to yourself.");
            return true;
        }
        plugin.getCompetiveManager().getPlayerMap().putIfAbsent(Bukkit.getPlayerExact(args[0]).getUniqueId(), new PreCompetivePlayer(((Player) sender).getUniqueId()));
        plugin.getCompetiveManager().getPlayerMap().get(Bukkit.getPlayerExact(args[0]).getUniqueId()).getWaitingList().add(((Player) sender).getUniqueId());
        sender.sendMessage("You sent a duel to ".concat(args[0]));
        plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), "tellraw "+ args[0] + " " + "[{\"text\":\"" + sender.getName() + " sent a duel to you. \"},{\"text\":\"[Click Here]\",\"color\":\"yellow\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/accept " + sender.getName() + "\"}}]");
        return true;
    }
}
