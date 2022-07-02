package me.nekocloud.api.effect;

public enum ParticleProperty {

    /**
     * требуется вода для отображения
     */
    REQUIRES_WATER,

    /**
     * требуется мир или item achievement для отображения
     */
    REQUIRES_DATA,

    /**
     * используется offset для смещения позиции (куда улетать частички будут)
     */
    DIRECTIONAL,

    /**
     * использует offset как значение цвета (RGB)
     */
    COLORABLE
}
