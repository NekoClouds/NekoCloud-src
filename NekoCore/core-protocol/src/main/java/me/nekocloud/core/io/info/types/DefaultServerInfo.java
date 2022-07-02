package me.nekocloud.core.io.info.types;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.nekocloud.core.io.info.ServerInfo;
import me.nekocloud.core.io.info.ServerInfoType;
import me.nekocloud.core.io.info.fields.ServerField;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DefaultServerInfo implements ServerInfo {

    private final Map<ServerField, Object> fieldsValues = new HashMap<>();
    @Setter
    private int protocolVersion;

    @Override
    public ServerInfoType getType() {
        return ServerInfoType.DEFAULT;
    }

    @Override
    public <T> T getFieldValue(ServerField serverField) {
        return (T) fieldsValues.get(serverField);
    }

    @Override
    public void addFieldValue(ServerField serverField, Object fieldValue) {
        this.fieldsValues.put(serverField, fieldValue);
    }
}
