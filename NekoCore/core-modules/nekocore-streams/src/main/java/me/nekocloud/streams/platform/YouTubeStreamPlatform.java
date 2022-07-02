package me.nekocloud.streams.platform;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.nekocloud.core.connection.http.RequestBuilder;
import me.nekocloud.streams.detail.AbstractStreamDetails;
import me.nekocloud.streams.exception.StreamEndedException;
import me.nekocloud.streams.exception.StreamException;
import me.nekocloud.streams.exception.StreamNotFoundException;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class YouTubeStreamPlatform implements StreamPlatform<YouTubeStreamPlatform.YouTubeStreamDetails> {

    private static final String YOUTUBE_STREAM_URL = "https://youtu.be/";
    private static final String YOUTUBE_API_URL = "https://www.googleapis.com/youtube/v3/videos";

    //matcher group - 4
    private static final Pattern YOUTUBE_VIDEO_PATTERN = Pattern.compile(
            "(https://(www.|)|www.)(youtube.com/watch\\?v=|youtu.be/)(.+)", Pattern.CASE_INSENSITIVE);

    /**
     * API ключ для YouTube
     */
    private final String apiKey;

    @Override
    public YouTubeStreamDetails parseStreamUrl(@NotNull String streamUrl) {
        Matcher matcher = YOUTUBE_VIDEO_PATTERN.matcher(streamUrl);

        //это не ссылка на стрим ютуба
        if (!matcher.matches()) {
            return null;
        }

        return new YouTubeStreamDetails(this, matcher.group(4));
    }

    @Override
    public String makeBeautifulUrl(@NotNull AbstractStreamDetails streamDetails) {
        return YOUTUBE_STREAM_URL + streamDetails.getIdentity();
    }

    @Override
    public JsonObject makeRequest(@NotNull String streamId) {
        String response = new RequestBuilder(YOUTUBE_API_URL)
                .parameter("key", apiKey)
                .parameter("part", "snippet,liveStreamingDetails")
                .parameter("id", streamId)
                .makeRequest();

        if (response == null) {
            return null;
        }

        return GSON.fromJson(response, JsonObject.class);
    }

    @Override
    public void updateStreamDetails(@NotNull AbstractStreamDetails streamDetails,
                                    @NotNull JsonObject jsonObject) throws StreamException {

        JsonArray items = jsonObject.getAsJsonArray("items");

        if (items.size() == 0) {
            throw new StreamNotFoundException();
        }

        JsonObject videoDetails = items.get(0).getAsJsonObject();

        JsonObject snippet = videoDetails.getAsJsonObject("snippet");
        JsonObject liveStreamingDetails = videoDetails.getAsJsonObject("liveStreamingDetails");

        if (liveStreamingDetails.has("actualEndTime")) {
            throw new StreamEndedException();
        }

        streamDetails.setTitle(snippet.get("title").getAsString());
        streamDetails.setViewers(liveStreamingDetails.get("concurrentViewers").getAsInt());
        streamDetails.setStartedAtServiceTime(Instant.parse(liveStreamingDetails.get("actualStartTime").getAsString()).toEpochMilli());
    }

    @Override
    public String getDisplayName() {
        return "§6YouTube";
    }

    @Getter
    class YouTubeStreamDetails extends AbstractStreamDetails {

        private final String identity;

        YouTubeStreamDetails(@NotNull StreamPlatform<?> streamPlatform, @NotNull String identity) {
            super(streamPlatform);

            this.identity = identity;
        }
    }

}
