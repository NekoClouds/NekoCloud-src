package me.nekocloud.api.hologram.lines;

import me.nekocloud.api.hologram.HoloLine;

public interface TextHoloLine extends HoloLine {

    void setText(String text);

    String getText();
}
