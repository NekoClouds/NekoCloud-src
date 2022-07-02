package me.nekocloud.api.scoreboard;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Collides {
    ALWAYS("always"),
    PUSH_OTHER_TEAMS("pushOtherTeams"),
    PUSH_OWN_TEAMS("pushOwnTeam"),
    NEVER("never");

    private final String value;
}
