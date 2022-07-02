package me.nekocloud.lobby.placeholder.depend;

import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import me.nekocloud.api.util.Head;
import me.nekocloud.api.util.ItemUtil;
import me.nekocloud.base.locale.Language;
import me.nekocloud.lobby.placeholder.hologram.AbstractHolderHolo;
import org.bukkit.Location;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Log4j2
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class HolderHolo extends AbstractHolderHolo {

    Language lang;

    public HolderHolo(
            final @NotNull Language lang,
            final @NotNull Location loc,
            final @NotNull String key
    ) {
        super(loc.clone());
        this.lang = lang;

        lang.getList(key)
                .stream()
                .filter(line -> !line.startsWith(";item") && !line.startsWith(";skull"))
                .forEach(hologram::addTextLine);

        // пиздец. Ну ничего, главное работает.
        lang.getList(key).forEach(line -> {
            if (line.startsWith(";item;")) {
                hologram.addDropLine(false,
                        ItemUtil.getBuilder(Material.getMaterial(line.split(";")[1].toUpperCase()))
                                .build());
            }
            if (line.startsWith(";skull;")) {
                hologram.addBigItemLine(Boolean.parseBoolean(line.split(";")[1]),
                        ItemUtil.getBuilder(Boolean.parseBoolean(line.split(";")[2])
                                        ? Head.getHeadByPlayerName(line.split(";")[3])
                                        : Head.getHeadByTextures(line.split(";")[3]))
                                .build());
            }
        });
    }
}
