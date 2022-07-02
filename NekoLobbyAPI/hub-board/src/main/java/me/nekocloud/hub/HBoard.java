package me.nekocloud.hub;

import lombok.RequiredArgsConstructor;
import lombok.val;
import me.nekocloud.api.CoreAPI;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.base.gamer.constans.PurchaseType;
import me.nekocloud.base.locale.Language;
import me.nekocloud.base.util.ProgressBar;
import me.nekocloud.base.util.StringUtil;
import me.nekocloud.lobby.api.profile.BoardLobby;

@RequiredArgsConstructor
public final class HBoard implements BoardLobby {

    private final CoreAPI coreAPI = NekoCloud.getCoreAPI();

    @Override
    public void showBoard(BukkitGamer gamer, Language lang) {
        val board = SCORE_BOARD_API.createBoard();
        board.setDisplayName("§7⚔ §d§lNeko§f§lCloud §7⚔");

        val groupName = gamer.getGroup().getLocaleName(lang);

        board.updater(() -> {
            ProgressBar levelProgressBar = new ProgressBar(gamer.getExp(), gamer.getExp() + gamer.getExpNextLevel(),
                    10, "§e", "§7", "―");

            board.setDynamicLine(13, StringUtil.getLineCode(13) + " ", "§8" + StringUtil.getDate());

            board.setDynamicLine(11, "" + lang.getMessage("BOARD_GROUP") + ": ", groupName);
            board.setDynamicLine(10, "", levelProgressBar.getProgressBar());
            board.setDynamicLine(7, "§d ╔ §f" + lang.getMessage("BOARD_COINS") + ": §e", StringUtil.getNumberFormat(gamer.getMoney(PurchaseType.COINS)));
            board.setDynamicLine(6, "§d ╠ §f" + lang.getMessage("BOARD_VIRTS") + ": §d", StringUtil.getNumberFormat(gamer.getMoney(PurchaseType.VIRTS)));
            board.setDynamicLine(5, "§d ╚ §f" + lang.getMessage("BOARD_LEVEL") + ": §a", StringUtil.getNumberFormat(gamer.getLevelNetwork()));

            board.setDynamicLine(3, lang.getMessage("BOARD_GLOBAL_ONLINE") + ": §5", String.valueOf(coreAPI.getGlobalOnline()));

        });

        board.setLine(14, StringUtil.getLineCode(14) + " ");
        board.setLine(12, StringUtil.getLineCode(12) + lang.getMessage("BOARD_PROFILE") + ":");
        board.setLine(9, StringUtil.getLineCode(9));
        board.setLine(8, StringUtil.getLineCode(8) + lang.getMessage("BOARD_STATISTIC") + ":");
        board.setLine(4, StringUtil.getLineCode(4));
        board.setLine(2, StringUtil.getLineCode(2));
        board.setLine(1, StringUtil.getLineCode(1) + "§d §l⋙ §d§lNeko§f§lCloud.me");

        board.showTo(gamer);
    }
}
