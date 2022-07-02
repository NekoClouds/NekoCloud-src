package me.nekocloud.box.data;

import com.google.common.collect.Iterators;
import lombok.RequiredArgsConstructor;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.base.locale.CommonWords;
import me.nekocloud.base.locale.Language;
import me.nekocloud.base.util.StringUtil;
import me.nekocloud.box.util.BoxUtil;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Supplier;

@RequiredArgsConstructor
public final class BoxHoloReplacer implements Supplier<String> {

    private final BukkitGamer gamer;
    private final Iterator<Character> colors = Iterators.cycle(Arrays.asList('d', '6', 'e', 'a', 'b'));

    @Override
    public String get() {
        Language lang = gamer.getLanguage();
        int keys = BoxUtil.getKeys(gamer);
        return lang.getMessage("KEY_BOX",
                "§" + colors.next(),
                StringUtil.getNumberFormat(keys),
                CommonWords.KEYS_1.convert(keys, lang));
    }
}