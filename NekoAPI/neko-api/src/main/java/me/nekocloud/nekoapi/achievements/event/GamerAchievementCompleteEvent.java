package me.nekocloud.nekoapi.achievements.event;

import lombok.Getter;
import lombok.Setter;
import me.nekocloud.api.event.gamer.GamerEvent;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.nekoapi.achievements.achievement.Achievement;
import org.bukkit.event.Cancellable;

@Getter
public final class GamerAchievementCompleteEvent extends GamerEvent implements Cancellable {

    private final Achievement achievement;
    @Setter
    private boolean cancelled;

    public GamerAchievementCompleteEvent(BukkitGamer gamer, Achievement achievement) {
        super(gamer);
        this.achievement = achievement;
    }
}
