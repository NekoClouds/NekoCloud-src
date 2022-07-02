package me.nekocloud.reports.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Report {

    private final String reportIntruder;
    private final String reportOwner;
    private final String reportReason;

    private final long reportDate;

}
