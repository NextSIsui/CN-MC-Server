import org.bukkit.plugin.java.JavaPlugin;

public class CNMCServer extends JavaPlugin {

    private CompetiveManager competiveManager = new CompetiveManager();

    @Override
    public void onEnable() {
        getCommand("accept").setExecutor(new CommandAcceptDuel(this));
        getCommand("duel").setExecutor(new CommandDuel(this));
    }

    @Override
    public void onDisable() {
    }

    public CompetiveManager getCompetiveManager() {
        return competiveManager;
    }
}
