package me.nekocloud.base.gamer.sections;

import lombok.Getter;
import me.nekocloud.base.gamer.IBaseGamer;
import me.nekocloud.base.gamer.IBaseGamerImpl;

public abstract class Section {

    @Getter
    protected final IBaseGamerImpl baseGamer;

    public Section(IBaseGamer baseGamer) {
        this.baseGamer = (IBaseGamerImpl) baseGamer;
    }

    public abstract boolean loadData();
}