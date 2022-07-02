package me.nekocloud.core.common.auth.manager;

import com.google.common.hash.Hashing;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import me.nekocloud.base.util.map.MultikeyHashMap;
import me.nekocloud.base.util.map.MultikeyMap;
import me.nekocloud.core.api.connection.player.CorePlayer;
import me.nekocloud.core.common.NetworkManager;
import me.nekocloud.core.common.auth.objects.CoreAuthPlayer;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

@Getter @Log4j2
public final class CoreAuthManager {

	private final MultikeyMap<CoreAuthPlayer> authPlayerMap = new MultikeyHashMap<CoreAuthPlayer>()
            .register(String.class, CoreAuthPlayer::getPlayerName)
            .register(Integer.class, CoreAuthPlayer::getPlayerID);
    private final List<Integer> twoFactorLoginList          = new LinkedList<>();


	public CoreAuthPlayer loadOrCreate(int playerID) {
        CoreAuthPlayer authPlayer = authPlayerMap.get(Integer.class, playerID);
        if (authPlayer != null) return authPlayerMap.get(Integer.class, playerID);

        authPlayer = new CoreAuthPlayer(NetworkManager.INSTANCE.getPlayerName(playerID), playerID);
        authPlayer.initialize();

        authPlayerMap.put(authPlayer);

        return authPlayer;
    }

    public CoreAuthPlayer loadOrCreate(String playerName) {
        CoreAuthPlayer authPlayer = authPlayerMap.get(String.class, playerName);
        if (authPlayer != null) return authPlayerMap.get(String.class, playerName);

        authPlayer = new CoreAuthPlayer(playerName, NetworkManager.INSTANCE.getPlayerID(playerName));
        authPlayer.initialize();

        authPlayerMap.put(authPlayer);

        return authPlayer;
    }

	public boolean hasPlayerAccount(int playerId) {
        return loadOrCreate(playerId) != null &&
			   loadOrCreate(playerId).getPlayerPassword() != null;
    }

    public void removeSession(final @NotNull CorePlayer player) {
        authPlayerMap.delete(loadOrCreate(player.getPlayerID()));
    }

	//------ 2FA ------//
	public boolean has2FASession(int playerID) {
        return twoFactorLoginList.contains(playerID);
    }

    public void add2FASession(int playerID) {
        twoFactorLoginList.add(playerID);
    }

    public void remove2FASession(int playerID) {
        twoFactorLoginList.remove(playerID);
    }
	//------ 2FA ------//

    public boolean equalsPassword(final @NotNull String password, final String playerPassword) {
        return playerPassword == null || !hashPassword(password).equals(playerPassword);
    }

    @SuppressWarnings("all")
    public String hashPassword(final @NotNull String password) {
        return Hashing.sha512().hashString(Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString(), StandardCharsets.UTF_8).toString();
    }


}
