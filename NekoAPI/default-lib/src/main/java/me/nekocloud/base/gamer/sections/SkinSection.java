package me.nekocloud.base.gamer.sections;

import lombok.Getter;
import lombok.Setter;
import me.nekocloud.base.gamer.IBaseGamer;
import me.nekocloud.base.skin.Skin;
import me.nekocloud.base.sql.GlobalLoader;
import me.nekocloud.base.sql.api.table.ColumnType;
import me.nekocloud.base.sql.api.table.TableColumn;
import me.nekocloud.base.sql.api.table.TableConstructor;

@Getter
public class SkinSection extends Section {

    private String skinName;
    @Setter
    private Skin skin = Skin.DEFAULT_SKIN;

    public SkinSection(IBaseGamer baseGamer) {
        super(baseGamer);
    }

    @Override
    public boolean loadData() {
        skinName = GlobalLoader.getSkinName(baseGamer.getName(), baseGamer.getPlayerID());
        if (!skinName.isEmpty()) {
            skin = GlobalLoader.getSkin(skinName);
        }

        return true;
    }

    public void updateSkinName(String skinName) {
        if (skinName == null || skinName.length() < 3 || skinName.equalsIgnoreCase(this.skinName)) {
            return;
        }

        this.skinName = skinName;
        GlobalLoader.setSkinName(baseGamer.getPlayerID(), skinName);
    }
    /*
    static {
        new TableConstructor("selected_skins",
                new TableColumn("id", ColumnType.INT_11).primaryKey(true).unigue(true),
                new TableColumn("player_name", ColumnType.VARCHAR_16),
                new TableColumn("skin", ColumnType.VARCHAR_16)
        ).create(GlobalLoader.getMysqlDatabase());
    }
     */
}
