package me.nekocloud.api.event.gamer;

import lombok.Getter;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.entity.npc.NPC;

@Getter
public class GamerInteractNPCEvent extends GamerEvent {

    private final NPC npc;
    private final GamerInteractNPCEvent.Action action;

    public GamerInteractNPCEvent(BukkitGamer gamer, NPC npc, GamerInteractNPCEvent.Action action){
        super(gamer);
        this.npc = npc;
        this.action = action;
    }

    public enum Action {
        LEFT_CLICK, RIGHT_CLICK
    }

}
