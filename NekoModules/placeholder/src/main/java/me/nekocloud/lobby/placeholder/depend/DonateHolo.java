package me.nekocloud.lobby.placeholder.depend;

import lombok.Getter;
import lombok.experimental.FieldDefaults;
import me.nekocloud.api.util.SVersionUtil;
import me.nekocloud.base.locale.Language;
import me.nekocloud.lobby.placeholder.hologram.AbstractHolderHolo;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static lombok.AccessLevel.PRIVATE;

@Getter
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class DonateHolo extends AbstractHolderHolo {

    Language lang;

    public DonateHolo(final @NotNull Language lang, final @NotNull Location loc) {
        super(loc.clone());
        this.lang = lang;

        hologram.addTextLine(lang.getList("HOLO_DONATE"));

        ItemStack item;
        if (SVersionUtil.is1_12())
             item = new ItemStack(Material.getMaterial("EXP_BOTTLE"));
        else item = new ItemStack(Material.getMaterial("EXPERIENCE_BOTTLE"));
        hologram.addDropLine(false, item);
    }
}
