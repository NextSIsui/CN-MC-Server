import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.*;

public class CompetiveManager implements Listener {
    private Map<UUID, PreCompetivePlayer> playerMap = new HashMap<>();
    private List<World> worlds = new ArrayList<>();
    private int id = 0;

    public CompetiveManager() {
        worlds.add(Bukkit.getWorld("Area1"));
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        playerMap.putIfAbsent(event.getPlayer().getUniqueId(), new PreCompetivePlayer(event.getPlayer().getUniqueId()));
    }

    public Map<UUID, PreCompetivePlayer> getPlayerMap() {
        return playerMap;
    }

    public int getId() {
        return ++id;
    }

    public World getNextWorld() {
        return worlds.get(id % worlds.size());
    }
}
