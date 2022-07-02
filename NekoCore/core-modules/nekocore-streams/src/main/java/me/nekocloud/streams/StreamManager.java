package me.nekocloud.streams;

import com.google.common.base.Joiner;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import gnu.trove.TCollections;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntLongMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntLongHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.chat.JsonChatMessage;
import me.nekocloud.core.api.chat.event.ClickEvent;
import me.nekocloud.core.api.chat.event.HoverEvent;
import me.nekocloud.core.api.schedulerT.CommonScheduler;
import me.nekocloud.core.api.utility.CooldownUtil;
import me.nekocloud.core.api.utility.NumberUtil;
import me.nekocloud.core.api.connection.player.CorePlayer;
import me.nekocloud.streams.detail.AbstractStreamDetails;
import me.nekocloud.streams.exception.StreamException;
import me.nekocloud.streams.platform.StreamPlatform;
import me.nekocloud.streams.platform.TwitchStreamPlatform;
import me.nekocloud.streams.platform.YouTubeStreamPlatform;
import me.nekocloud.streams.platform.settings.StreamPlatformSettings;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public final class StreamManager {

    /**
     * Интервал оповещения игроков (в мс) о проходящих сейчас стримах
     */
    private static final int STREAM_BROADCAST_INTERVAL = 60_000 * 5;

    /**
     * Время задержки оповещения (в мс), если недавно был добавлен стрим
     */
    private static final int RECENTLY_ADDED_STREAM_COOLDOWN = 60_000;

    /**
     * Время интервала между выходом игрока из сети и удалением стрима
     * как стрима оффлайн-игрока (в мс)
     * <p>
     * Проще говоря, даем возможность стримеру перезайти за минуту
     */
    private static final int AUTO_OFFLINE_STREAM_REMOVE_INTERVAL = 60_000;

    // ----------------------------------------------------------------

    public static final StreamManager INSTANCE = new StreamManager();

    // ----------------------------------------------------------------

    private StreamManager() {
        final AtomicLong lastBroadcastTime = new AtomicLong(System.currentTimeMillis());

        TIntLongMap playerQuitTimeMap = new TIntLongHashMap();

        //запускаем поток для обновления всех стримов
        new CommonScheduler("streams_update") {

            @Override
            public void run() {

                //костыль, чтобы избежать ConcurrentModificationException...
                TIntList playerIDsToRemove = new TIntArrayList();

                activeStreamMap.forEachEntry((playerID, streamDetails) -> {
                    CorePlayer player = NekoCore.getInstance().getPlayer(playerID);

                    //удаляем стрим оффлайн-игрока
                    if (player == null) {
                        //тут делаем проще. есть время, когда игрок вышел. есть текущее время
                        //если времени прошло межу текущим временем и временем когда игрок вышел
                        //больше, чем AUTO_OFFLINE_STREAM_REMOVE_INTERVAL, то тогда удаляем. Иначе
                        //этого игрока не трогаем

                        if (!playerQuitTimeMap.containsKey(playerID)) {
                            playerQuitTimeMap.put(playerID, System.currentTimeMillis());
                        } else if (System.currentTimeMillis() - playerQuitTimeMap.get(playerID) >= AUTO_OFFLINE_STREAM_REMOVE_INTERVAL) {
                            playerIDsToRemove.add(playerID);
                            playerQuitTimeMap.remove(playerID);

                            addPlayerCooldown(playerID);
                        }

                        return true;
                    }

                    //информацию обновлять пока не нужно
                    if (streamDetails.isActual() && !streamDetails.shouldUpdate()) {
                        return true;
                    }

                    StreamPlatform<?> platform = streamDetails.getStreamPlatform();

                    //делаем запрос на обновление
                    JsonObject jsonObject = null;

                    try {
                        jsonObject = platform.makeRequest(streamDetails.getIdentity());
                    } catch (JsonSyntaxException exception) {
                        exception.printStackTrace();
                    }

                    //чет пошло не так - удаляем стрим, он ведь больше не обновляется
                    if (jsonObject == null) {
                        playerIDsToRemove.add(playerID);
                        return true;
                    }

                    //меняем время последнего обновления
                    streamDetails.setLastUpdateTime(System.currentTimeMillis());

                    //обновляем информацию о стриме
                    try {
                        //если до обновления инфа была не актуальной - это новый стрим
                        //поэтому будем оповещать о нем всех игроков
                        boolean wasUnActual = !streamDetails.isActual();

                        platform.updateStreamDetails(streamDetails, jsonObject);

                        if (wasUnActual) {

                            for (CorePlayer online : NekoCore.getInstance().getOnlinePlayers()) {
                                List<String> messageList = online.getLanguage().getList("STREAM_NEW_ANNOUNCE");

                                messageList.replaceAll(s -> s.replace("%viewers%", NumberUtil.spaced(streamDetails.getViewers()))
                                        .replace("%streamer%", player.getDisplayName())
                                        .replace("%stream_title%", streamDetails.getTitle().length() > 48 ? streamDetails.getTitle().substring(0, 48) + "..." : streamDetails.getTitle())
                                        .replace("%platform%", streamDetails.getStreamPlatform().getDisplayName()));


                                JsonChatMessage jsonChatMessage = JsonChatMessage.create(Joiner.on("\n").join(messageList));

                                jsonChatMessage.addClick(ClickEvent.Action.OPEN_URL, streamDetails.getStreamPlatform().makeBeautifulUrl(streamDetails));
                                jsonChatMessage.addHover(HoverEvent.Action.SHOW_TEXT, "§aНажмите, чтобы перейти на трансляцию!");

                                jsonChatMessage.sendMessage(online);
                            }
                        }

                    } catch (StreamException e) {

                        if (streamDetails.isActual()) {
                            player.sendMessageLocale("STREAM_UPDATE_REMOVED",
                                    "%stream_title%", streamDetails.getTitle().length() > 48 ? streamDetails.getTitle().substring(0, 48) + "..." : streamDetails.getTitle());
                        }

                        playerIDsToRemove.add(playerID);
                    }

                    return true;
                });

                //удаляем все не нужные нам стримы...
                playerIDsToRemove.forEach(playerID -> {
                    activeStreamMap.remove(playerID);
                    return true;
                });

                //каждые 5 минут сообщаем игрокам о том сколько стримов идет
                //для начала проверим есть ли они вообще
                if (activeStreamMap.isEmpty()) {
                    return;
                }

                long currentTimeMillis = System.currentTimeMillis();

                //не будем также оповещать игроков, если с момента добавления последнего стрима прошло менее 1 минуты
                for (AbstractStreamDetails streamDetails : activeStreamMap.valueCollection()) {
                    if ((currentTimeMillis - RECENTLY_ADDED_STREAM_COOLDOWN) <= streamDetails.getStartedAtServerTime()) {
                        return;
                    }
                }

                //теперь проверим дату последней отправки инфы, если интервал прошел - оповещаем
                if (currentTimeMillis - lastBroadcastTime.get() >= STREAM_BROADCAST_INTERVAL) {
                    lastBroadcastTime.set(System.currentTimeMillis());

                    int streams = activeStreamMap.size();

                    for (CorePlayer online : NekoCore.getInstance().getOnlinePlayers()) {

                        List<String> messageList = online.getLanguage().getList("STREAMS_ANNOUNCE");
                        messageList.replaceAll(s -> s.replace("%count%", NumberUtil.spaced(streams)));


                        JsonChatMessage jsonChatMessage = JsonChatMessage.create(Joiner.on("\n").join(messageList));

                        jsonChatMessage.addClick(ClickEvent.Action.RUN_COMMAND, "/streams");
                        jsonChatMessage.addHover(HoverEvent.Action.SHOW_TEXT, "§aНажмите, чтобы открыть список стримов!");

                        jsonChatMessage.sendMessage(online);
                    }
                }
            }

        }.runTimer(1, 1, TimeUnit.SECONDS);
    }

    /**
     * Доступные платформы для добавления стримов
     */
    private final List<StreamPlatform<?>> availableStreamPlatforms = new ArrayList<StreamPlatform<?>>() {{

        add(new YouTubeStreamPlatform(StreamPlatformSettings.YOUTUBE_API_KEY));
        add(new TwitchStreamPlatform(StreamPlatformSettings.TWITCH_CLIENT_ID));
    }};

    /**
     * Мапа с текущими стримами игроком
     */
    private final TIntObjectMap<AbstractStreamDetails> activeStreamMap
            = TCollections.synchronizedMap(new TIntObjectHashMap<>());

    /**
     * Получить информацию об активном стриме игрока
     */
    public AbstractStreamDetails getActiveStream(@NotNull CorePlayer player) {
        return activeStreamMap.get(player.getPlayerID());
    }

    /**
     * Получить информацию о стрме по его ID
     */
    public AbstractStreamDetails getActiveStream(@NotNull String identity) {
        for (AbstractStreamDetails details : activeStreamMap.values(new AbstractStreamDetails[0])) {
            if (details.getIdentity().equals(identity)) {
                return details;
            }
        }

        return null;
    }

    /**
     * Добавить кулдаун на добавление стрима
     */
    public void addPlayerCooldown(@NotNull int playerID) {
        CooldownUtil.putCooldown("stream_remove_" + playerID, 5 * 1000 * 60);
    }

    /**
     * Проверить наличие кулдауна на добавление стрима у игрока
     */
    public boolean hasStreamCooldown(@NotNull int playerID) {
        return CooldownUtil.hasCooldown("stream_remove_" + playerID);
    }

    /**
     * Добавить информацию о новом стриме
     */
    public void addPlayerStream(@NotNull CorePlayer player, @NotNull AbstractStreamDetails details) {
        activeStreamMap.put(player.getPlayerID(), details);
    }

    /**
     * Удаляем информацию о стриме игрока
     */
    public void removePlayerStream(@NotNull CorePlayer player) {
        activeStreamMap.remove(player.getPlayerID());
    }

    /**
     * Получить информацию об активных стримах
     */
    public TIntObjectMap<AbstractStreamDetails> getActiveStreams() {
        return TCollections.unmodifiableMap(activeStreamMap);
    }

    /**
     * Получить доступные платформы для стримов
     */
    public List<StreamPlatform<?>> getAvailableStreamPlatforms() {
        return Collections.unmodifiableList(availableStreamPlatforms);
    }

}
