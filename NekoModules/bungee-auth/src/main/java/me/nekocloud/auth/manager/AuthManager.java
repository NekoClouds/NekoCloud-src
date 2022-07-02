package me.nekocloud.auth.manager;

import com.google.common.hash.Hashing;
import lombok.Getter;
import lombok.val;
import me.nekocloud.auth.BungeeAuth;
import me.nekocloud.auth.core.AuthAction;
import me.nekocloud.auth.core.AuthData;
import me.nekocloud.auth.objects.AuthPlayer;
import me.nekocloud.auth.sql.AuthSql;
import me.nekocloud.base.sql.GlobalLoader;
import me.nekocloud.base.util.map.MultikeyHashMap;
import me.nekocloud.base.util.map.MultikeyMap;
import me.nekocloud.core.common.NetworkManager;
import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static me.nekocloud.auth.sql.QueryType.INSERT_PLAYER_QUERY;

@Getter
public final class AuthManager {

	public static long EXPIRE_SESSION_MILLIS                    = TimeUnit.DAYS.toMillis(3);
	public static AuthManager INSTANCE 							= new AuthManager();

	private final MultikeyMap<AuthPlayer> authPlayerMap 	    = new MultikeyHashMap<AuthPlayer>()
            .register(String.class, AuthPlayer::getPlayerName)
            .register(Integer.class, AuthPlayer::getPlayerID);

	private final Map<UUID, Long> waitLicense 					= new ConcurrentHashMap<>();
	private final List<Integer> twoFactorLoginList          	= new LinkedList<>();

	public AuthPlayer loadOrCreate(int playerID) {
        AuthPlayer authPlayer = authPlayerMap.get(Integer.class, playerID);

        if (authPlayer != null) return authPlayerMap.get(Integer.class, playerID);

        authPlayer = new AuthPlayer(NetworkManager.INSTANCE.getPlayerName(playerID), playerID);
        authPlayer.initialize();

        authPlayerMap.put(authPlayer);

        return authPlayer;
    }

	public AuthPlayer loadOrCreate(final @NotNull String playerName) {
		val playerID = GlobalLoader.getPlayerID(playerName);
		if (playerID == -1)
			return null;

		return loadOrCreate(playerID);
	}

	public boolean hasPlayerAccount(int playerId) {
        return loadOrCreate(playerId) != null &&
			   loadOrCreate(playerId).getPlayerPassword() != null;
    }


    public boolean isWaitLicense(UUID player_uuid) {
        return waitLicense.containsKey(player_uuid);
    }

    public void addWaitLicense(UUID player_uuid) {
        waitLicense.put(player_uuid, System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5));
    }

    public void removeWaitLicense(UUID player_uuid) {
        waitLicense.remove(player_uuid);
    }

	public void loadLicenseData(UUID player_uuid) {
		if (AuthSql.getLicenseData(player_uuid)) {
			addWaitLicense(player_uuid);
		}
	}

	// DEFAULT SESSION //
    public void removeSession(@NotNull AuthPlayer authPlayer) {
        authPlayerMap.delete(authPlayer);
		// TODO дописать
    }
	// DEFAULT SESSION //

	// 2FA //
	public boolean has2FASession(int playerID) {
        return twoFactorLoginList.contains(playerID);
    }

    public void add2FASession(int playerID) {
        twoFactorLoginList.add(playerID);
    }

    public void remove2FASession(int playerID) {
        twoFactorLoginList.remove(playerID);
    }
	// 2FA //


	public boolean equalsPassword(String playerPassword, String password) {
        return playerPassword == null || !playerPassword.equals(hashPassword(password));
    }

	@SuppressWarnings("all")
    public String hashPassword(@NotNull String password) {
        return Hashing.sha512().hashString(Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString(), StandardCharsets.UTF_8).toString();
    }

	/**
	 * Зарегистрировать игрока
	 *
	 * @param playerID 	     ид
	 * @param playerPassword пароль
	 * @param ip             ип
	 */
	public void registerPlayer(int playerID,
							   String playerPassword,
							   InetAddress ip,
							   BungeeAuth plugin

	) {
        val authPlayer = loadOrCreate(playerID);
		val inetAddress = ip.getHostAddress();

        authPlayer.setPlayerPassword(playerPassword = plugin.getAuthManager().hashPassword(playerPassword));

        AuthSql.getDatabase().execute(INSERT_PLAYER_QUERY.get(),
				playerID,
                playerPassword,
				authPlayer.getVkId(),
				authPlayer.getDiscordId(),
				authPlayer.getCodeTypeId(),

                new Timestamp(System.currentTimeMillis()),
				new Timestamp(System.currentTimeMillis() + EXPIRE_SESSION_MILLIS),

                inetAddress,
				inetAddress);

		if (!ip.getHostAddress().contains("0.0.0.0"))
        	plugin.sendPacket(new AuthData(playerID, AuthAction.WAIT_COMPLETE));
    }
}
