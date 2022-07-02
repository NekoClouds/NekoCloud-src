package me.nekocloud.packetlib.nms.scoreboard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public class DObjective {

    private final String name;
    @Setter
    private String displayName;
    private final ObjectiveType type;
}
