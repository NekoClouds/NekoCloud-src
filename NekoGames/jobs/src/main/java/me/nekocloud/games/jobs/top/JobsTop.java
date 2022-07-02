package me.nekocloud.games.jobs.top;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.base.gamer.IBaseGamer;
import me.nekocloud.base.locale.Language;
import me.nekocloud.base.util.StringUtil;
import me.nekocloud.games.jobs.config.JobsConfig;
import me.nekocloud.games.jobs.data.JobsStatsLoader;
import me.nekocloud.nekoapi.tops.hologram.HoloTop;
import me.nekocloud.nekoapi.tops.hologram.HoloTopData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JobsTop implements Runnable {

	HoloTop minesTop;
	HoloTop woodcutterTop;

	// TODO: переписать это ебаное говнище
	public JobsTop(JobsConfig jobsConfig) {
		Language lang = Language.DEFAULT;
        minesTop = new HoloTop(jobsConfig.getMinerTop(),
          	lang.getList("MINER_TOP_HEADER"), 15) {
			public void updateData() {
				List<HoloTopData> topData = new ArrayList<>();
				int index = 1;

				for (Map.Entry<IBaseGamer, Integer> entry : JobsStatsLoader.getTop(true, 10).entrySet()) {
					topData.add(new HoloTopData((entry.getKey()).getDisplayName(), "§e" +
							StringUtil.getNumberFormat((double) entry.getValue()) + "§6⛂", index));
					index++;
				}

				this.topData = topData;
			}
		};

      	woodcutterTop = new HoloTop(jobsConfig.getWoodcutterTop(),
              lang.getList("WOODCUTTER_TOP_HEADER"), 15) {
		 	  public void updateData() {
				  List<HoloTopData> topData = new ArrayList<>();
				  int index = 1;

				  for (Map.Entry<IBaseGamer, Integer> entry : JobsStatsLoader.getTop(false, 10).entrySet()) {
					  topData.add(new HoloTopData((entry.getKey()).getDisplayName(), "§e" +
							  StringUtil.getNumberFormat((double) entry.getValue()) + "§6⛂", index));
					  index++;
				  }

				  this.topData = topData;
			  }
		  };

      minesTop.update();
      woodcutterTop.update();
   }

   @Override
   public void run() {
      minesTop.update();
      woodcutterTop.update();
   }
}
