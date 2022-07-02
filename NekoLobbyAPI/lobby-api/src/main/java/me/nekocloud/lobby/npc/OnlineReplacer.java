package me.nekocloud.lobby.npc;

import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import me.nekocloud.base.locale.CommonWords;
import me.nekocloud.base.locale.Language;
import me.nekocloud.base.util.StringUtil;
import me.nekocloud.lobby.game.data.Channel;

import java.util.function.Supplier;

import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public final class OnlineReplacer implements Supplier<String> {

    Channel channel;
    Language lang;

    @Override
    public String get() {
        int online = channel.getOnline();

        return online >= 0 ? lang.getMessage( "HOLO_REPLACER_CHANNEL",
                StringUtil.getNumberFormat(online),
                CommonWords.PLAYERS_1.convert(online, lang)) :
                lang.getMessage( "HOLO_REPLACER_CHANNEL_ERROR");
    }
}
