package me.nekocloud.grief;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.scoreboard.ScoreBoardAPI;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GriefBoard {

    ScoreBoardAPI SCORE_BOARD_API             = NekoCloud.getScoreBoardAPI();

}
