import lombok.SneakyThrows;
import lombok.val;
import me.nekocloud.base.locale.Language;
import me.nekocloud.core.CoreSql;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.connection.player.CorePlayer;
import me.nekocloud.core.connection.server.BukkitServer;
import me.nekocloud.core.connection.server.BungeeServer;
import org.junit.Test;

public class FindTest {

	@SneakyThrows
//	@Test
	public void handleFindPlayer() {
		Language.reloadAll();
		val core = NekoCore.getInstance();
		new CoreSql(core);

		val corePlayer = CorePlayer.getOrCreate("_Novit_", "0.0.0.0",
						new BungeeServer("bungee-2", 13377), 1);

		core.handlePlayer(corePlayer);
		corePlayer.setBukkit(new BukkitServer("auth-1", 228));


	}
}
