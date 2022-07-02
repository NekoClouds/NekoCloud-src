package me.nekocloud.core.io.info;

import me.nekocloud.core.io.info.fields.ServerField;

import java.util.Map;

public interface ServerInfo {

    ServerInfoType getType();

    int getProtocolVersion();

    void setProtocolVersion(int protocolVersion);

    Map<ServerField, Object> getFieldsValues();

    <T> void addFieldValue(ServerField serverField, Object fieldValue);

    <T> T getFieldValue(ServerField serverField);
}
