import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandAcceptDuel implements CommandExecutor {

    private CNMCServer plugin;

    public CommandAcceptDuel(CNMCServer plugin) {
        this.plugin = plugin;
    }

    /**
     * Accept args[0]'s player's duel.
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) return false;
        if (!(sender instanceof Player)) {
            sender.sendMessage("Use in game.");
            return true;
        }
        Player player2 = (Player)sender;
        if (Bukkit.getPlayerExact(args[0]) == null) {
            sender.sendMessage("Player not found.");
            return true;
        }
        Player player1 = Bukkit.getPlayerExact(args[0]);
        if (plugin.getCompetiveManager().getPlayerMap().get(((Player) sender).getUniqueId()) == null) {
            sender.sendMessage("You have no waiting player for duel.");
            return true;
        }
        plugin.getCompetiveManager().getPlayerMap().putIfAbsent(player2.getUniqueId(), new PreCompetivePlayer(player1.getUniqueId()));
        if (plugin.getCompetiveManager().getPlayerMap().get(player2.getUniqueId()).getWaitingList().contains(player1.getUniqueId())) {
            player2.sendMessage(ChatColor.YELLOW + "You accepted the duel against ".concat(player1.getDisplayName()));
            player1.sendMessage(ChatColor.YELLOW + "You were accepted the duel from ".concat(player2.getDisplayName()));
            new Event1vs1(plugin, plugin.getCompetiveManager().getId(), plugin.getCompetiveManager().getNextWorld(), new PlayerCompetive(player1.getUniqueId()), new PlayerCompetive(player2.getUniqueId()));
        }
        return true;
    }
}
