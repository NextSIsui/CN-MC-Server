import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;

import java.util.UUID;

public class PlayerCompetive {
    private UUID uuid;
    private Location prevLocation;
    private GameMode prevGameMode;

    public PlayerCompetive(UUID uuid) {
        this.uuid = uuid;
        this.prevLocation = Bukkit.getPlayer(uuid).getLocation();
        this.prevGameMode = Bukkit.getPlayer(uuid).getGameMode();
    }

    public UUID getUuid() {
        return uuid;
    }

    public Location getPrevLocation() {
        return prevLocation;
    }

    public GameMode getPrevGameMode() {
        return prevGameMode;
    }
}
