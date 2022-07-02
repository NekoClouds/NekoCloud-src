package me.nekocloud.skyblock.dependencies;

import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.api.scoreboard.Board;
import me.nekocloud.api.scoreboard.ScoreBoardAPI;
import me.nekocloud.base.locale.Language;
import me.nekocloud.base.util.StringUtil;
import me.nekocloud.nekoapi.achievements.achievement.AchievementPlayer;
import me.nekocloud.market.api.MarketAPI;
import me.nekocloud.market.api.MarketPlayer;
import me.nekocloud.market.api.MarketPlayerManager;
import me.nekocloud.skyblock.achievement.IslandAchievements;
import me.nekocloud.skyblock.api.SkyBlockAPI;
import me.nekocloud.skyblock.api.entity.SkyGamer;
import me.nekocloud.skyblock.api.island.Island;
import me.nekocloud.skyblock.api.island.member.MemberType;
import me.nekocloud.skyblock.api.manager.IslandManager;
import me.nekocloud.skyblock.api.manager.SkyGamerManager;
import me.nekocloud.skyblock.module.BorderModule;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.Locale;

public class SkyBlockBoard {

    private final ScoreBoardAPI API = NekoCloud.getScoreBoardAPI();
    private final SkyGamerManager SKY_GAMER_MANAGER = SkyBlockAPI.getSkyGamerManager();
    private final IslandManager MANAGER = SkyBlockAPI.getIslandManager();
    private final MarketPlayerManager MARKET_PLAYER_MANAGER = MarketAPI.getMarketPlayerManager();
    private final GamerManager GAMER_MANAGER = NekoCloud.getGamerManager();

    public void sendBoard(Player player) {
        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        MarketPlayer marketPlayer = MARKET_PLAYER_MANAGER.getMarketPlayer(player);
        SkyGamer skyGamer = SKY_GAMER_MANAGER.getSkyGamer(player);
        if (gamer == null || marketPlayer == null || skyGamer == null)
            return;

        AchievementPlayer achievementPlayer = skyGamer.getAchievementPlayer();
        if (achievementPlayer == null)
            return;

        Language lang = gamer.getLanguage();

        Board board = API.createBoard();
        board.setDynamicDisplayName("SkyBlock");

        board.setLine(15, StringUtil.getLineCode(15));
        board.setLine(13, StringUtil.getLineCode(12));
        board.setLine(10, StringUtil.getLineCode(9));
        board.setLine(9, "§7" + lang.getMessage("BOARD_ISLAND_YOU") + ":");
        board.setLine(2, StringUtil.getLineCode(2));
        board.updater(() -> {
            Location location = player.getLocation();
            Island island = MANAGER.getIsland(player);
            Island islandLocation = MANAGER.getIsland(location);
            String nameOwner = "§cN/A";

            if (islandLocation != null)
                nameOwner = islandLocation.getOwner().getName();

            board.setDynamicLine(14, lang.getMessage("BOARD_ONLINE") +": ",
                    "§a" + StringUtil.getNumberFormat(Bukkit.getOnlinePlayers().size()));
            board.setDynamicLine(12, lang.getMessage("BOARD_BALANCE") + ": ",
                    "§e" + NumberFormat.getNumberInstance(Locale.US).format(marketPlayer.getMoney()));
            board.setDynamicLine(11, lang.getMessage("BOARD_ACHIEVEMENT") + ": ",
                    "§e " + achievementPlayer.getCompleted().size()
                              + "§f/§c" + IslandAchievements.values().length);

            if (island != null) {
                String percent = "100%";

                BorderModule borderModule = island.getModule(BorderModule.class);
                if (borderModule != null)
                    percent = borderModule.getPercent() + "%";

                MemberType memberType = island.getMemberType(gamer);
                String rank = lang.getMessage(memberType.getKey());
                board.setDynamicLine(8, " " + lang.getMessage("BOARD_OWNER")
                        + ": §c", island.getOwner().getName());
                board.setDynamicLine(7, " " + lang.getMessage("BOARD_RANK_YOU") + ": ",
                        "§7" + rank);
                board.setDynamicLine(6, " " + lang.getMessage("BOARD_BALANCE_ISLAND") +
                        ": §6", StringUtil.getNumberFormat(island.getMoney()));
                board.setDynamicLine(5, " " + lang.getMessage("BOARD_OPEN_TERRITORY_ISLAND")
                        + ": ", "§a" + percent);
                board.setDynamicLine(4, " " + lang.getMessage("BOARD_MEMBER_ISLAND") + ": ",
                        "§a" + island.getMembers().size() + "§f/§c" + island.getLimitMembers());
                //board.setDynamicLine(3, " " + Localization.getMessage(lang, "BOARD_LEVEL_ISLAND")
                //        + ": ", "§6" + String.valueOf(island.getLevel()));
            } else {
                board.removeLine(8);
                board.removeLine(7);
                board.removeLine(6);
                board.removeLine(5);
                board.removeLine(4);
                board.setLine(4, "§f" + lang.getMessage("BOARD_NO_ISLAND1"));
                board.setLine(3, "§f" + lang.getMessage("BOARD_NO_ISLAND2"));
            }

            board.setDynamicLine(1, lang.getMessage("BOARD_ON_ISLAND") + ": §a",
                     nameOwner);
        });

        board.showTo(player);
    }
}
