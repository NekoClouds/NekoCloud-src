package pw.novit.nekocloud.bungee.commands;

import com.google.common.collect.ImmutableSet;
import lombok.val;
import me.nekocloud.base.skin.SkinAPI;
import net.md_5.bungee.api.ProxyServer;
import org.jetbrains.annotations.NotNull;
import pw.novit.nekocloud.bungee.NekoBungeeAPI;
import pw.novit.nekocloud.bungee.api.gamer.BungeeEntity;
import pw.novit.nekocloud.bungee.api.gamer.BungeeGamer;

import java.util.HashSet;
import java.util.Set;

public final class SkinCommand extends ProxyCommand<NekoBungeeAPI> {

    public SkinCommand(final NekoBungeeAPI nekoBungeeAPI) {
        super(nekoBungeeAPI, "skin", "скин");

        setOnlyPlayers(true);
        setCooldown(60, "skin_command");
    }

    @Override
    public void execute(
            final BungeeEntity entity,
            final String @NotNull[] args
    ) {
        val gamer = (BungeeGamer) entity;

        if (args.length != 1) {
            notEnoughArguments(entity, "SKIN_PREFIX","SKIN_FORMAT");
            return;
        }

        val nameSkin = args[0];
        if (nameSkin.equals("reset")) {
            gamer.updateSkin(gamer.getName());
            gamer.sendMessageLocale("SKIN_HAS_BEES_RESET");
            return;
        }

        if (!nameSkin.matches("[a-zA-Z0-9_]+") || nameSkin.length() > 16 || nameSkin.length() < 3) {
            gamer.sendMessageLocale("SKIN_IS_INVALID");
            return;
        }

        val skin = SkinAPI.getSkin(nameSkin);
        if (skin == null) {
            gamer.sendMessageLocale("SKIN_REQUEST_EXCEPTION");
            return;
        }

        gamer.updateSkin(nameSkin);
        gamer.sendMessageLocale("SKIN_HAS_BEEN_UPDATED_TO", nameSkin);
    }

    @Override
    public @NotNull Iterable<String> tabComplete(
            final BungeeEntity entity,
            final String @NotNull[] args
    ) {
        if (args.length == 0) return ImmutableSet.of();

        Set<String> matches = new HashSet<>();
        if (args.length == 1) {
            matches.add("reset");
            val search = args[0].toLowerCase();
            for (val player : ProxyServer.getInstance().getPlayers())
                if (player.getName().toLowerCase().startsWith(search))
                    matches.add(player.getName());
          return matches;
        }

        return ImmutableSet.of();
    }
}