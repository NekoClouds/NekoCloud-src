package me.nekocloud.reports;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import me.nekocloud.base.gamer.IBaseGamer;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.connection.player.CorePlayer;
import me.nekocloud.reports.type.Report;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

// TODO: в базу данных хранить
public final class ReportManager {
    public static final ReportManager INSTANCE = new ReportManager();

    public Multimap<String, Report> getReportMap() {
        return this.reportMap;
    }

    private final Multimap<String, Report> reportMap = HashMultimap.create();

    public void createReport(@NotNull CorePlayer intruderPlayer, @NotNull CorePlayer ownerPlayer, @NotNull String reportReason) {

        Report report = new Report(intruderPlayer.getName(), ownerPlayer.getName(), reportReason, System.currentTimeMillis());
        this.reportMap.put(intruderPlayer.getName().toLowerCase(), report);
        for (CorePlayer onlineStaff : NekoCore.getInstance().getOnlinePlayers(IBaseGamer::isStaff))
            onlineStaff.sendMessage("§d§lЖАЛОБЫ §8| §fНа игрока " + intruderPlayer.getDisplayName() + " §fбыло подано §e" + this.reportMap.get(intruderPlayer.getName().toLowerCase()).size() + " жалоб");
    }

    public Collection<Report> getReportsByIntruder(@NotNull String intruderName) {
        return this.reportMap.get(intruderName.toLowerCase());
    }

    public boolean hasReport(@NotNull String ownerName, @NotNull String intruderName) {
        return this.reportMap.values().stream().anyMatch(report -> (report.getReportIntruder().equalsIgnoreCase(intruderName) && report.getReportOwner().equalsIgnoreCase(ownerName)));
    }
}
