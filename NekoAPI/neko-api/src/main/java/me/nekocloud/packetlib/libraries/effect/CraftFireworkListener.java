package me.nekocloud.packetlib.libraries.effect;

import me.nekocloud.packetlib.nms.NmsAPI;
import me.nekocloud.packetlib.nms.interfaces.NmsManager;
import me.nekocloud.nekoapi.listeners.DListener;
import me.nekocloud.nekoapi.loader.NekoAPI;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Random;

public class CraftFireworkListener extends DListener<NekoAPI> {

    private final Random random = new Random();
    private final NmsManager nmsManager = NmsAPI.getManager();

    private final FireworkEffect.Type[] TypeList = new FireworkEffect.Type[] {
        FireworkEffect.Type.BALL, FireworkEffect.Type.BALL_LARGE,
                FireworkEffect.Type.BURST, FireworkEffect.Type.CREEPER,
                FireworkEffect.Type.STAR };

    public CraftFireworkListener(NekoAPI javaPlugin) {
        super(javaPlugin);
    }

    @EventHandler
    public void onFWshot(EntityDamageByEntityEvent e) {
        Entity damager = e.getDamager();
        if (damager instanceof Firework && damager.hasMetadata("nekocloud_FW")) {
            e.setCancelled(true);
        }
    }

    public void shootRandomFirework(Location location) {
        Firework fw = location.getWorld().spawn(location, Firework.class);
        FireworkMeta fm = fw.getFireworkMeta();
        FireworkEffect.Type type = TypeList[random.nextInt(5)];
        Color color1 = ParticleUtils.getColour(random.nextInt(16) + 1);
        Color color2 = ParticleUtils.getColour(random.nextInt(16) + 1);
        FireworkEffect effect = FireworkEffect.builder().flicker(random.nextBoolean())
                .withColor(color1)
                .withFade(color2)
                .with(type)
                .trail(random.nextBoolean()).build();
        fm.addEffect(effect);
        fm.setPower(random.nextInt(2) + 1);
        fw.setFireworkMeta(fm);
    }

    public void launchInstantFirework(FireworkEffect fe, Location location) {
        Firework firework = location.getWorld().spawn(location, Firework.class);
        FireworkMeta fm = firework.getFireworkMeta();
        fm.addEffect(fe);
        firework.setFireworkMeta(fm);

        firework.setMetadata("nekocloud_FW", new FixedMetadataValue(javaPlugin, firework.toString()));

        nmsManager.launchInstantFirework(firework);
    }
}
