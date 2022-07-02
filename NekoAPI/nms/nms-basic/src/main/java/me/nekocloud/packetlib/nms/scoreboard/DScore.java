package me.nekocloud.packetlib.nms.scoreboard;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DScore {

    private final String name;
    private final DObjective dObjective;
    private int score;

    @Override
    public String toString() {
        return "DScore{name=" + name + ", score=" + score +"}";
    }
}
