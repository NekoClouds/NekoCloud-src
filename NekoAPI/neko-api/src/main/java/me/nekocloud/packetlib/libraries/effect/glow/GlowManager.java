package me.nekocloud.packetlib.libraries.effect.glow;

import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class GlowManager {

    private final Map<String, CraftPlayerGlowing> glowings = new ConcurrentHashMap<>();

    private final Map<String, CraftPlayerGlowing> memberGlowing = new ConcurrentHashMap<>();
}
