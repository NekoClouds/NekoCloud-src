package me.nekocloud.packetlib.libraries.effect.data;

import lombok.Getter;
import org.bukkit.Color;

@Getter
public class CraftParticleOrdinaryColor extends CraftParticleColor {
    private final int red;
    private final int green;
    private final int blue;

    public CraftParticleOrdinaryColor(int red, int green, int blue) {
        if (red < 0 || red > 255) {
            this.red = 255;
        } else {
            this.red = red;
        }

        if (green < 0 || green > 255) {
            this.green = 255;
        } else {
            this.green = green;
        }

        if (blue < 0 || blue > 255) {
            this.blue = 255;
        } else {
            this.blue = blue;
        }

    }

    public CraftParticleOrdinaryColor(Color color) {
        this(color.getRed(), color.getGreen(), color.getBlue());
    }

    @Override
    public float getValueX() {
        return (float) red / 255F;
    }

    @Override
    public float getValueY() {
        return (float) green / 255F;
    }

    @Override
    public float getValueZ() {
        return (float) blue / 255F;
    }
}
