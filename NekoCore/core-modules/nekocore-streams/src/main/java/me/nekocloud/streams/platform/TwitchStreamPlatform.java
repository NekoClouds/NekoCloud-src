package me.nekocloud.streams.platform;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.nekocloud.core.connection.http.RequestBuilder;
import me.nekocloud.streams.detail.AbstractStreamDetails;
import me.nekocloud.streams.exception.StreamException;
import me.nekocloud.streams.exception.StreamNotFoundException;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class TwitchStreamPlatform implements StreamPlatform<TwitchStreamPlatform.TwitchStreamDetails> {

    private static final String TWITCH_STREAM_URL = "https://twitch.tv/";
    private static final String TWITCH_API_URL = "https://api.twitch.tv/helix/streams";

    //matcher group - 3
    private static final Pattern TWITCH_STREAM_PATTERN = Pattern.compile(
            "(https://(www.|)|www.)twitch.tv/(.+)", Pattern.CASE_INSENSITIVE);

    private final String clientId;

    @Override
    public TwitchStreamDetails parseStreamUrl(@NotNull String streamUrl) {
        Matcher matcher = TWITCH_STREAM_PATTERN.matcher(streamUrl);

        //это не ссылка на стрим ютуба
        if (!matcher.matches()) {
            return null;
        }

        return new TwitchStreamDetails(this, matcher.group(3));
    }

    @Override
    public String makeBeautifulUrl(@NotNull AbstractStreamDetails streamDetails) {
        return TWITCH_STREAM_URL + streamDetails.getIdentity();
    }

    @Override
    public JsonObject makeRequest(@NotNull String streamId) {
        System.out.println("получаю response");
        String response = new RequestBuilder(TWITCH_API_URL)
                .header("Client-ID", clientId)
                .parameter("user_login", streamId)
                .makeRequest();

        if (response == null) {
            System.out.println("response null :(");
            return null;
        }

        System.out.println("response: " + response);
        return GSON.fromJson(response, JsonObject.class);
    }

    @Override
    public void updateStreamDetails(@NotNull AbstractStreamDetails details, @NotNull JsonObject jsonObject) throws StreamException {
        JsonArray data = jsonObject.getAsJsonArray("data");

        if (data.size() == 0) {
            throw new StreamNotFoundException();
        }

        JsonObject streamDetails = data.get(0).getAsJsonObject();

        details.setTitle(streamDetails.get("title").getAsString());
        details.setViewers(streamDetails.get("viewer_count").getAsInt());
        details.setStartedAtServiceTime(Instant.parse(streamDetails.get("started_at").getAsString()).toEpochMilli());
    }

    @Override
    public String getDisplayName() {
        return "§3Twitch";
    }

    @Getter
    static class TwitchStreamDetails extends AbstractStreamDetails {

        private final String identity;

        TwitchStreamDetails(@NotNull StreamPlatform<?> streamPlatform, @NotNull String identity) {
            super(streamPlatform);

            this.identity = identity;
        }
    }

}
