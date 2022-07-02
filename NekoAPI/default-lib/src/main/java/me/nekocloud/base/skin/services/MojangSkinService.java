package me.nekocloud.base.skin.services;

import me.nekocloud.base.skin.Skin;
import me.nekocloud.base.skin.SkinType;
import me.nekocloud.base.skin.exeptions.SkinRequestException;
import me.nekocloud.base.skin.response.SkinProperty;
import me.nekocloud.base.skin.response.SkinResponse;
import me.nekocloud.base.skin.response.UUIDResponse;
import me.nekocloud.base.sql.GlobalLoader;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public final class MojangSkinService extends SkinService {

    public MojangSkinService(String uuidUrl, String skinUrl) {
        super(uuidUrl, skinUrl);
    }

    @Override
    public SkinType getSkinType() {
        return SkinType.MOJANG;
    }

    @Override
    public Skin getSkinByName(String name) throws SkinRequestException {
        try {
            return getSkinByUUID(getUUID(name));
        } catch (Exception e) {
            return GlobalLoader.getSkin(name);
        }
    }

    public String getUUID(String name) throws SkinRequestException {
        UUIDResponse response;
        try {
            response = readResponse(uuidUrl + URLEncoder.encode(name, StandardCharsets.UTF_8), UUIDResponse.class);
            if (!response.getName().equalsIgnoreCase(name)) {
                throw new IllegalArgumentException("name");
            }
        } catch (SkinRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new SkinRequestException(name + " не имеет лицензии", e);
        }

        return response.getId();
    }

    public Skin getSkinByUUID(String name) throws SkinRequestException {
        try {
            SkinResponse skinResponse = readResponse(skinUrl + URLEncoder.encode(name, StandardCharsets.UTF_8)
                    + "?unsigned=false", SkinResponse.class);
            SkinProperty property = skinResponse.getProperties();
            return property.toSkin(skinResponse.getName(), getSkinType());
        } catch (IOException e) {
            throw new SkinRequestException("Произошла ошибка при загрузке скина");
        }
    }
}
