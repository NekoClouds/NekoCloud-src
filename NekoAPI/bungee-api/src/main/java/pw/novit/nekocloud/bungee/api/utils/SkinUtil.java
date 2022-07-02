package pw.novit.nekocloud.bungee.api.utils;

import com.google.common.io.ByteStreams;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import lombok.val;
import me.nekocloud.base.gamer.sections.SkinSection;
import me.nekocloud.base.skin.Skin;
import me.nekocloud.base.skin.SkinAPI;
import me.nekocloud.base.sql.GlobalLoader;
import me.nekocloud.base.sql.api.MySqlDatabase;
import me.nekocloud.base.sql.api.query.MysqlQuery;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.connection.LoginResult;
import net.md_5.bungee.connection.LoginResult.Property;
import org.jetbrains.annotations.NotNull;
import pw.novit.nekocloud.bungee.api.gamer.BungeeGamer;

import java.lang.reflect.Field;

import static lombok.AccessLevel.PRIVATE;

@UtilityClass
@FieldDefaults(level = PRIVATE)
public class SkinUtil {

    Field PROFILE_FIELD;
    MySqlDatabase MY_SQL_DATABASE = GlobalLoader.getMysqlDatabase();

    static {
        try {
            PROFILE_FIELD = InitialHandler.class.getDeclaredField("loginProfile");
        } catch (NoSuchFieldException e ) {
            e.printStackTrace();
        }
    }

    public Skin setSkin(final @NotNull PendingConnection connection, Skin skin, final Server server) {
        val name = connection.getName();
        val gamer = BungeeGamer.getGamer(name);
        if (gamer == null)
            return skin;

        val skinSection = gamer.getSection(SkinSection.class);
        if (skinSection == null)
            return skin;

        if (server == null)
            return skin;

        val out = ByteStreams.newDataOutput();

        out.writeUTF(name);
        out.writeUTF(skin.getSkinName());
        out.writeUTF(skin.getValue());
        out.writeUTF(skin.getSignature());
        out.writeInt(skin.getSkinType().getTypeId());

        server.sendData("nekoapi:skins", out.toByteArray());

        return skin;
    }

}
