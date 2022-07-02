package me.nekocloud.packetlib.libraries.scoreboard.objective;

import io.netty.util.internal.ConcurrentSet;

import java.util.Set;

public class ObjectiveManager {

    private final Set<CraftObjective> objectives = new ConcurrentSet<>();

    public void addObjective(CraftObjective craftObjective) {
        objectives.add(craftObjective);
    }

    public void removeObjective(CraftObjective craftObjective) {
        objectives.remove(craftObjective);
    }

    public Set<CraftObjective> getObjectives() {
        return objectives;
    }
}
