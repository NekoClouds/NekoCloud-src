package me.nekocloud.vanish;

import lombok.Getter;
import lombok.Setter;
import lombok.Value;
import org.bukkit.entity.Player;

@Getter
@Setter
@Value
public class VanishPlayer {

    Vanish vanish;
    Player player;
    boolean itemPickUps;
}
