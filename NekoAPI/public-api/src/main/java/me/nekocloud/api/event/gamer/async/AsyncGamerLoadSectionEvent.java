package me.nekocloud.api.event.gamer.async;

import lombok.Getter;
import me.nekocloud.api.event.gamer.GamerEvent;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.base.gamer.sections.Section;

import java.util.HashSet;
import java.util.Set;

public class AsyncGamerLoadSectionEvent extends GamerEvent {

    @Getter
    private final Set<Class<? extends Section>> sections = new HashSet<>();

    public AsyncGamerLoadSectionEvent(BukkitGamer gamer) {
        super(gamer, true);
    }

    public void setSections(Set<Class<? extends Section>> sections) {
        this.sections.addAll(sections);
    }
}
