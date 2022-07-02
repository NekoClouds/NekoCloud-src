package me.nekocloud.packetlib.libraries.effect.data;

public class CraftParticleNoteColor extends CraftParticleColor {
    private final int note;

    public CraftParticleNoteColor(int note) throws IllegalArgumentException {
        if (note < 0 || note > 24) {
            this.note = 1;
        } else {
            this.note = note;
        }
    }

    @Override
    public float getValueX() {
        return (float) note / 24F;
    }

    @Override
    public float getValueY() {
        return 0;
    }

    @Override
    public float getValueZ() {
        return 0;
    }
}
