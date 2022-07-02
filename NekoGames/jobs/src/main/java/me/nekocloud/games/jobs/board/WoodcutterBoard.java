package me.nekocloud.games.jobs.board;

import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.base.locale.Language;
import me.nekocloud.base.util.StringUtil;
import me.nekocloud.games.jobs.api.JobsAPI;
import me.nekocloud.games.jobs.api.data.JobsManager;
import me.nekocloud.games.jobs.api.data.JobsPlayer;
import me.nekocloud.survival.commons.api.board.CommonsBoard;
import org.bukkit.Bukkit;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WoodcutterBoard extends CommonsBoard {
   private static final JobsManager JOBS_MANAGER = JobsAPI.getJobsManager();

   public WoodcutterBoard(BukkitGamer gamer, Language lang) {
      super(gamer);

      JobsPlayer jobsPlayer = JOBS_MANAGER.getOrLoad(gamer);
      board.setLine(15, StringUtil.getLineCode(15));
      board.setLine(13, StringUtil.getLineCode(13));
      board.setLine(12, lang.getMessage("JOBS_BOARD_STATS_LINE"));
      board.setLine(6, StringUtil.getLineCode(6));
      board.setLine(3, StringUtil.getLineCode(3));
      board.setLine(5, lang.getMessage("JOBS_BOARD_WOODCUTTER_LINE"));
      board.setLine(2, lang.getMessage("LOBBY_BOARD_SITE_LINE"));

      board.updater(() -> {
         board.setDynamicLine(16, lang.getMessage("LOBBY_BOARD_DATE_LINE"),
                 new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));

         board.setDynamicLine(14, lang.getMessage("LOBBY_BOARD_GROUP_LINE"),
                 gamer.getDisplayName());

         board.setDynamicLine(11, lang.getMessage("JOBS_BOARD_SALARY_LINE"),
                 StringUtil.getNumberFormat(jobsPlayer.getWoodsEarned()));

         board.setDynamicLine(10, lang.getMessage("JOBS_BOARD_MINED_LINE"),
                 StringUtil.getNumberFormat(jobsPlayer.getWoodsBlocks()));

         board.setDynamicLine(9, lang.getMessage("JOBS_BOARD_BOOSTER_LINE"),
                 String.valueOf(jobsPlayer.getMultiple()));

         board.setDynamicLine(8, lang.getMessage("JOBS_BOARD_COMBO_LINE"),
                 String.valueOf(jobsPlayer.getCombo()));

//         this.board.setDynamicLine(7, v.getMessage("SURVIVAL_BOARD_MONEY_LINE"),
//                 StringUtil.getNumberFormat(EconomyUtil.getBalance(this.gamer.getPlayer())));
         board.setDynamicLine(4, lang.getMessage("SURVIVAL_BOARD_LOCAL_ONLINE_LINE"),
                 String.valueOf(Bukkit.getOnlinePlayers().size()));
      }, 60L);
   }
}
