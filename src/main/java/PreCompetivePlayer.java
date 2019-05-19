import java.util.*;

public class PreCompetivePlayer {
    private UUID uuid;
    private List<UUID> waitingList = new ArrayList<>();
    private boolean isInMatch = false;

    public PreCompetivePlayer(UUID uuid) {
        this.uuid = uuid;
    }

    public List<UUID> getWaitingList() {
        return waitingList;
    }

    public UUID getUuid() {
        return uuid;
    }

    public boolean isInMatch() {
        return isInMatch;
    }

    public void setInMatch(boolean inMatch) {
        isInMatch = inMatch;
    }
}
