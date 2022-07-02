package me.nekocloud.base.skin.services;

import lombok.val;
import me.nekocloud.base.skin.Skin;
import me.nekocloud.base.skin.SkinType;
import me.nekocloud.base.skin.exeptions.SkinRequestException;
import me.nekocloud.base.skin.exeptions.TooManyRequestsSkinException;
import me.nekocloud.base.skin.response.SkinProperty;
import me.nekocloud.base.skin.response.SkinResponse;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public final class ElySkinService extends SkinService {

    private final String serverKey = ""; //todo мб он нужен

    public ElySkinService(String uuidUrl, String skinUrl) {
        super(uuidUrl, skinUrl);
    }

    @Override
    protected HttpURLConnection makeConnection(String url) throws IOException, SkinRequestException {
        val connection = super.makeConnection(url);
        return switch (connection.getResponseCode()) {
            case 204 -> throw new SkinRequestException("Скин не загружен... кажется что-то пошло не так");
            case 429 -> throw new TooManyRequestsSkinException();
            default -> connection;
        };
    }

    @Override
    public SkinType getSkinType() {
        return SkinType.ELY;
    }

    @Override
    public Skin getSkinByName(String name) throws SkinRequestException {
        try {
            val skinResponse = readResponse(skinUrl + URLEncoder.encode(name, StandardCharsets.UTF_8)
                    + "?unsigned=false&token="
                    + URLEncoder.encode(serverKey, StandardCharsets.UTF_8), SkinResponse.class);
            val property = skinResponse.getProperties();
            return property.toSkin(skinResponse.getName(), getSkinType());
        } catch (IOException ignored) {
            throw new SkinRequestException("Произошла ошибка при загрузке скина");
        }
    }
}
