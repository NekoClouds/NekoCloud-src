package me.nekocloud.packetlib.libraries.scoreboard.board.lines;

import me.nekocloud.api.scoreboard.Board;
import me.nekocloud.api.scoreboard.BoardLine;
import me.nekocloud.packetlib.libraries.scoreboard.board.CraftBoard;
import me.nekocloud.packetlib.nms.scoreboard.DTeam;

public class CraftBoardLine implements BoardLine {

    private final CraftBoard board;

    private int number;
    private DTeam team;
    private String text;
    private boolean dynamic;

    public CraftBoardLine(CraftBoard board, int number, String text, boolean dynamic) {
        this.board = board;
        this.number = number;
        this.text = text;
        this.dynamic = dynamic;
    }

    public CraftBoardLine(CraftBoard board, int number, String text, boolean dynamic, DTeam team) {
        this(board, number, text, dynamic);
        this.team = team;
    }

    public DTeam getTeam() {
        return team;
    }

    public String getText() {
        return text;
    }

    public void setTeam(DTeam team) {
        this.team = team;
    }

    @Override
    public Board getBoard() {
        if (board.isActive()) {
            return board;
        }

        return null;
    }

    @Override
    public int getNumber() {
        return number;
    }

    @Override
    public void setNumber(int number) {
        if (board == null)
            return;

        this.number = number;
        board.lines.put(number, this);

        if (isDynamic()) {
            String pref = team.getPrefix();
            String name = team.getPlayers().get(0);
            String suf = team.getSuffix();

            board.createDynamicLine(number, pref, name, suf);
            return;
        }

        board.sendLine(this, number, team != null);
    }

    @Override
    public void remove() {
        if (board == null)
            return;
        board.removeLine(number);
    }

    @Override
    public boolean isDynamic() {
        return dynamic;
    }
}
