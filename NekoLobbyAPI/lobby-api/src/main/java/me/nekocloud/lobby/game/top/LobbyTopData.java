package me.nekocloud.lobby.game.top;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.nekocloud.api.util.Head;
import me.nekocloud.base.gamer.IBaseGamer;
import me.nekocloud.base.locale.Language;
import me.nekocloud.base.util.StringUtil;
import me.nekocloud.nekoapi.tops.armorstand.StandTopData;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
@Getter
public class LobbyTopData implements StandTopData  {

    private final StatsTop top;
    private IBaseGamer gamer;

    private final int statValue;
    private final int position;

    @Override
    public int getPlayerID() {
        return gamer.getPlayerID();
    }

    @Override
    public ItemStack getHead() {
        return Head.getHeadBySkin(gamer.getSkin());
    }

    @Override
    public String getLastString(Language language) {
        return StringUtil.getNumberFormat(statValue) + "" + top.getTopTable()
                .getCommonWords().convert(statValue, language);
    }

    @Override
    public String getDisplayName() {
        return gamer.getDisplayName();
    }

    @Override
    public IBaseGamer getGamer() {
        return gamer;
    }

    @Override
    public void setGamer(IBaseGamer gamer) {
        this.gamer = gamer;
    }
}
