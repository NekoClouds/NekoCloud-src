package me.nekocloud.core.api.event.player;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import me.nekocloud.base.gamer.sections.Section;
import me.nekocloud.core.api.connection.player.CorePlayer;
import me.nekocloud.core.api.event.Event;

import java.util.HashSet;
import java.util.Set;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LoadSectionEvent extends Event {

    CorePlayer player;
    Set<Class<? extends Section>> sections = new HashSet<>();

    public void setSections(Set<Class<? extends Section>> sections) {
        this.sections.addAll(sections);
    }
}
