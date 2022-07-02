package me.nekocloud.nekoapi.utils;

import lombok.experimental.UtilityClass;
import lombok.val;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.event.gamer.GamerChangeSkinEvent;
import me.nekocloud.api.event.gamer.async.AsyncGamerSkinApplyEvent;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.base.SoundType;
import me.nekocloud.base.gamer.sections.SkinSection;
import me.nekocloud.base.skin.Skin;
import me.nekocloud.base.skin.SkinType;
import me.nekocloud.entity.BukkitGamerImpl;
import me.nekocloud.nekoapi.utils.bukkit.BukkitUtil;
import me.nekocloud.packetlib.nms.NmsAPI;
import me.nekocloud.packetlib.nms.interfaces.NmsManager;
import org.bukkit.entity.Player;

/**
 * Written by _Novit_ on 01.02.22
 */
@UtilityClass
public class SkinUtil {

    private final NmsManager NMS_MANAGER = NmsAPI.getManager();
    private final GamerManager GAMER_MANAGER = NekoCloud.getGamerManager();

    public void setSkin(
            final Player player,
            final String skinName,
            final String value,
            final String signature,
            final SkinType skinType,
            final boolean save
    ) {
        val gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null || skinName == null || value == null || signature == null)
            return;

        val section = gamer.getSection(SkinSection.class);
        if (section == null)
            return;

        val skin = new Skin(skinName, value, signature, skinType, System.currentTimeMillis());

        if (save) section.setSkin(skin);

        ((BukkitGamerImpl)gamer).setHead(value);

        gamer.playSound(SoundType.DESTROY);

        val event = new GamerChangeSkinEvent(gamer, skin);
        BukkitUtil.runTask(() -> BukkitUtil.callEvent(event));

        val skinApplyEvent = new AsyncGamerSkinApplyEvent(gamer);
        BukkitUtil.callEvent(skinApplyEvent);

        if (skinApplyEvent.isCancelled())
            return;

        NMS_MANAGER.setSkin(player, value, signature);
    }

}