package me.nekocloud.core.io.info.fields;

import io.netty.buffer.ByteBuf;
import lombok.experimental.UtilityClass;
import me.nekocloud.core.io.packet.DefinedPacket;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@UtilityClass
public class FieldsUtil {

    public Map<ServerField, Object> excludeFields(int argumentsSize, ByteBuf buf) {
        Map<ServerField, Object> fields = new HashMap<>();
        for (int i = 0; i < argumentsSize; i++) {
            ServerField serverField = DefinedPacket.readEnum(ServerField.class, buf);
            fields.put(serverField, readServerField(serverField.getFieldType(), buf));
        }
        return fields;
    }

    public void includeFields(Map<ServerField, Object> fields, ByteBuf buf) {
        for (Map.Entry<ServerField, Object> entry : fields.entrySet()) {
            DefinedPacket.writeEnum(entry.getKey(), buf);
            writeServerField(entry.getKey().getFieldType(), entry.getValue(), buf);
        }
    }

    //тут не нужны нахуй никакие массивы и прочее, поэтому мне ваще похуй
    private static Object readServerField(final Class<?> classType, final ByteBuf buf) {
        if (Integer.class.isAssignableFrom(classType)) {
            return buf.readInt();
        } else if (String.class.isAssignableFrom(classType)) {
            return DefinedPacket.readString(buf);
        } else if (UUID.class.isAssignableFrom(classType)) {
            return DefinedPacket.readUUID(buf);
        }

        return null;
    }

    private static void writeServerField(
            final Class<?> classType,
            final Object value,
            final ByteBuf buf
    ) {
        if (Integer.class.isAssignableFrom(classType)) {
            buf.writeInt((Integer) value);
        } else if (String.class.isAssignableFrom(classType)) {
            DefinedPacket.writeString((String) value, buf);
        } else if (UUID.class.isAssignableFrom(classType)) {
            DefinedPacket.writeUUID((UUID) value, buf);
        }
    }
}
