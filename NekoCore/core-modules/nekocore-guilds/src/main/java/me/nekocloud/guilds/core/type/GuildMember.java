package me.nekocloud.guilds.core.type;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.*;
import lombok.experimental.FieldDefaults;
import me.nekocloud.core.api.connection.player.CorePlayer;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class GuildMember {
    Guild guild;
    CorePlayer corePlayer;
    String guildName;
    Type type;

    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @RequiredArgsConstructor
    public enum Type {
        MEMBER(0),
        MODER(1),
        LEADER(2);

        int id;
        static final TIntObjectMap<Type> MEMBER_TYPES = new TIntObjectHashMap<>();

        public static Type getType(final int id) {
            return MEMBER_TYPES.get(id);
        }

        static {
            for (val codeType : values()) {
                MEMBER_TYPES.put(codeType.id, codeType);
            }
        }
    }
}
