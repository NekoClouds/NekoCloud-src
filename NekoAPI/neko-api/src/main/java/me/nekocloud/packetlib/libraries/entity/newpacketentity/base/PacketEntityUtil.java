package me.nekocloud.packetlib.libraries.entity.newpacketentity.base;

import lombok.experimental.UtilityClass;
import me.nekocloud.api.depend.CraftVector;
import org.bukkit.util.EulerAngle;

import java.util.Random;

@UtilityClass
public class PacketEntityUtil {

    private final Random RANDOM = new Random();

    public static int getRandomEntityId() {
        return RANDOM.nextInt(Integer.MAX_VALUE) + 500;
    }

    public CraftVector toNMS(EulerAngle old) {
        return new CraftVector((float)Math.toDegrees(old.getX()),
                (float)Math.toDegrees(old.getY()),
                (float)Math.toDegrees(old.getZ()));
    }

    public EulerAngle fromNMS(CraftVector old) {
        return new EulerAngle(Math.toRadians((double)old.getX()),
                Math.toRadians((double)old.getY()),
                Math.toRadians((double)old.getZ()));
    }
}
