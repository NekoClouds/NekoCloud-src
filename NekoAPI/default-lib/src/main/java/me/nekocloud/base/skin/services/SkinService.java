package me.nekocloud.base.skin.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.val;
import me.nekocloud.base.skin.Skin;
import me.nekocloud.base.skin.SkinType;
import me.nekocloud.base.skin.exeptions.SkinRequestException;
import me.nekocloud.base.skin.response.SkinProperty;
import me.nekocloud.base.skin.response.SkinSerializer;
import me.nekocloud.base.skin.response.SkinsResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public abstract class SkinService {

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(SkinProperty.class, new SkinSerializer())
            .create();

    protected final String uuidUrl;
    protected final String skinUrl;

    protected SkinService(String uuidUrl, String skinUrl) {
        this.uuidUrl = uuidUrl;
        this.skinUrl = skinUrl;
    }

    protected <T extends SkinsResponse> T readResponse(String url, Class<T> responseClass) throws IOException, SkinRequestException {
        val inputStream = makeConnection(url).getInputStream();
        val byteArrayOutputStream = new ByteArrayOutputStream();
        val buffer = new byte[1024];

        int r;
        while((r = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, r);
        }

        val read = byteArrayOutputStream.toString(StandardCharsets.UTF_8);

        try {
            val response = GSON.fromJson(read, responseClass);
            if (response == null) {
                throw new NullPointerException("response");
            } else {
                response.check();
                return response;
            }
        } catch (RuntimeException e) {
            throw new IOException("Ошибка при парсинге... сосатб: " + read, e);
        }
    }

    protected HttpURLConnection makeConnection(String urlString) throws IOException, SkinRequestException {
        try {
            val url = new URL(urlString);

            val connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "NekoCloud");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setDoOutput(true);
            return connection;
        } catch (MalformedURLException e) {
            throw new IOException("Произошла ошибка при подключении: " + urlString, e);
        }
    }

    public abstract SkinType getSkinType();

    public abstract Skin getSkinByName(String name) throws SkinRequestException;
}
