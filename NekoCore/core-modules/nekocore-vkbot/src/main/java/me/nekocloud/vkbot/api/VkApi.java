package me.nekocloud.vkbot.api;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.val;
import me.nekocloud.base.util.GsonUtil;
import me.nekocloud.vkbot.api.callback.ResponseCallback;
import me.nekocloud.vkbot.api.context.VkCallbackApiContextHandler;
import me.nekocloud.vkbot.api.handler.CallbackApiHandler;
import org.jetbrains.annotations.NotNull;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Getter
@FieldDefaults(level = PRIVATE)
public class VkApi {

    final List<CallbackApiHandler> callbackApiHandlerList = new ArrayList<>();

    public static final Gson GSON = new Gson();
    public static final ExecutorService EXECUTOR_SERVICE   = Executors.newSingleThreadExecutor();

    static long startSessionMillis;
    HttpServer httpServer;

    final String API_VERSION                               = "5.131";
    final String DEFAULT_URL                               = "https://api.vk.com/api.php";
    final String accessToken                               = "4293b6b161121d8a816191690333850e920295f0380738df0075c8cbd12749c655a642e526cf2a844866c",
                 confirmationCode                          = "506ef2cf",
                 secretKey                                 = "NekoCloudMe_LowIqException";

    final InetSocketAddress inetSocketAddress              = new InetSocketAddress("51.178.156.252", 2345);

    public void runCallbackApi() throws IOException {
        httpServer = HttpServer.create();
        httpServer.setExecutor(EXECUTOR_SERVICE);

        HttpContext alertContext = httpServer.createContext("/bot");
        alertContext.setHandler(new VkCallbackApiContextHandler(this));

        httpServer.bind(inetSocketAddress, 0);
        httpServer.start();

        startSessionMillis = System.currentTimeMillis();
    }

    public static long getSessionMillis() {
        return System.currentTimeMillis() - startSessionMillis;
    }

    public void stop() {
        if (httpServer != null) {
            httpServer.stop(0);
        }
    }

    public void call(@NotNull String method,
                     @NotNull JsonObject params,
                     @NotNull ResponseCallback callback) {

        EXECUTOR_SERVICE.execute(() -> callSync(method, params, callback));
    }

    public void callSync(@NotNull String method,
                         @NotNull JsonObject params,
                         @NotNull ResponseCallback callback) {

        params.addProperty("access_token", accessToken);
        params.addProperty("v", API_VERSION);
        params.addProperty("method", method);
        params.addProperty("oauth", 1);

        makeRequest(mapToURLParamsQuery(params), callback);
    }

    public void call(String method, JsonObject params) {
        call(method, params, ResponseCallback.EMPTY_CALLBACK);
    }

    public void callSync(String method, JsonObject params) {
        callSync(method, params, ResponseCallback.EMPTY_CALLBACK);
    }

    private void makeRequest(String params, ResponseCallback responseCallback) {
        try {
            val url = new URL(DEFAULT_URL);
            val connection = (HttpsURLConnection) url.openConnection();

            connection.setRequestProperty("Content-Length", String.valueOf(params.length()));
            connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");

            connection.setRequestMethod("POST");
            connection.setReadTimeout(2000);
            connection.setDoOutput(true);

            val stream = connection.getOutputStream();
            stream.write(params.getBytes(StandardCharsets.UTF_8));

            val reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            responseCallback.onResponse(reader.lines().collect(Collectors.joining()));
        } catch (Exception exception) {
            responseCallback.onException(exception);
        }
    }

    private static String mapToURLParamsQuery(@NotNull JsonObject jsonObject) {
        val answer = new StringBuilder();

        int count = 0;

        Set<Map.Entry<String, JsonElement>> parameters = jsonObject.entrySet();

        for (Map.Entry<String, JsonElement> entry : parameters) {
            answer.append(entry.getKey()).append("=").append(toString(entry.getValue()));

            if (count++ < parameters.size()) {
                answer.append("&");
            }
        }

        return answer.toString();
    }

    public static String toString(@NotNull JsonElement jsonElement) {
        if (jsonElement.isJsonArray()) {
            List<String> elements = new ArrayList<>();

            for (JsonElement arrayElement : jsonElement.getAsJsonArray()) {
                elements.add(arrayElement.getAsString());
            }

            return String.join(",", elements);
        }

        if (jsonElement.isJsonObject()) {
            return GsonUtil.toJson(jsonElement.getAsJsonObject());
        }

        return jsonElement.getAsString();
    }

    /**
     * Add new callback api handler
     *
     * @param callbackApiHandler - callback api handler
     */
    public void addCallbackApiHandler(CallbackApiHandler callbackApiHandler) {
        callbackApiHandlerList.add(callbackApiHandler);
    }

    /**
     * Delete callback api handler
     *
     * @param callbackApiHandler - callback api handler
     */
    public void removeCallbackApiHandler(CallbackApiHandler callbackApiHandler) {
        callbackApiHandlerList.remove(callbackApiHandler);
    }

    /**
     * Get callback api handlers
     *
     * @return - callback api handlers
     */
    public List<CallbackApiHandler> getCallbackApiHandlerList() {
        return Collections.unmodifiableList(callbackApiHandlerList);
    }

}
