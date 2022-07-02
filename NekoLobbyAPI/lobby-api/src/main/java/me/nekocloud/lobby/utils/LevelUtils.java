package me.nekocloud.lobby.utils;

import lombok.experimental.UtilityClass;
import lombok.val;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.api.scoreboard.DisplaySlot;
import me.nekocloud.api.scoreboard.Objective;
import me.nekocloud.api.scoreboard.ScoreBoardAPI;
import me.nekocloud.base.gamer.sections.NetworkingSection;
import org.bukkit.entity.Player;

@UtilityClass
public class LevelUtils {

    private final GamerManager GAMER_MANAGER = NekoCloud.getGamerManager();
    private final ScoreBoardAPI SCORE_BOARD_API = NekoCloud.getScoreBoardAPI();

    private Objective objectives;

    //перерасчитывать уровень игрока, опыт и лвл, когда он получает опыт (в баре и в обджективе)
    //выдавать опыт вместе с другими предметами из кейсов (от редкости зависит)
    //поменять сообщение при выдаче награды из кейса
    public void setExpData(BukkitGamer gamer) {
        val player = gamer.getPlayer();
        if (player == null || !player.isOnline()) {
            return;
        }

        if (objectives == null) {
            registerObjectives();
        }

        objectives.setScore(player, gamer.getLevelNetwork());
        player.setExp((float) NetworkingSection.getCurrentXPLVL(gamer.getExp()) /
                (float) NetworkingSection.checkXPLVL(gamer.getLevelNetwork() + 1));
        player.setLevel(gamer.getLevelNetwork());
    }

    private void registerObjectives() {
        objectives = SCORE_BOARD_API.createObjective("level", "dummy");
        objectives.setDisplayName("§e✫");
        objectives.setDisplaySlot(DisplaySlot.BELOW_NAME);
        objectives.setPublic(true);
    }

}
