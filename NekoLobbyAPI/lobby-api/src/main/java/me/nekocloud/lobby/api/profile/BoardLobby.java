package me.nekocloud.lobby.api.profile;

import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.scoreboard.ScoreBoardAPI;
import me.nekocloud.base.locale.Language;

public interface BoardLobby {

    ScoreBoardAPI SCORE_BOARD_API = NekoCloud.getScoreBoardAPI();

    void showBoard(BukkitGamer gamer, Language lang);
}
