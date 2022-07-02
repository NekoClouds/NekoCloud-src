package me.nekocloud.base.gamer.sections;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import lombok.Getter;
import lombok.Setter;
import me.nekocloud.base.gamer.IBaseGamer;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.gamer.friends.Friend;
import me.nekocloud.base.gamer.friends.FriendAction;
import me.nekocloud.base.sql.GlobalLoader;
import me.nekocloud.base.sql.api.table.ColumnType;
import me.nekocloud.base.sql.api.table.TableColumn;
import me.nekocloud.base.sql.api.table.TableConstructor;
import org.jetbrains.annotations.NotNull;

public class FriendsSection extends Section {

    @Getter
    private final IntSet friends = new IntOpenHashSet();

    @Getter @Setter
    private int friendsLimit = 20;

    public FriendsSection(IBaseGamer baseGamer) {
        super(baseGamer);
    }

    @Override
    public boolean loadData() {
        this.friends.addAll(GlobalLoader.getFriends(baseGamer.getPlayerID()));
        this.friendsLimit = getFriendsLimit(baseGamer.getGroup());
        return true;
    }

    public boolean isFriend(int playerID) {
        return friends.contains(playerID);
    }

    public boolean isFriend(@NotNull Friend friend) {
        return isFriend(friend.getPlayerId());
    }

    public void changeFriend(@NotNull FriendAction friendAction, Friend friend) {
        switch (friendAction) {
            case ADD_FRIEND -> friends.add(friend.getPlayerId());
            case REMOVE_FRIEND -> friends.remove(friend.getPlayerId());
        }
    }

    public static int getFriendsLimit(Group group) {

        return switch (group) {
            case DEFAULT -> 20;
            case HEGENT -> 30;
            case AKIO -> 40;
            case TRIVAL -> 50;
            case NEKO -> 75;
            case ADMIN, OWNER, DEVELOPER -> 100;
            default -> 65;
        };
    }


    static {
        new TableConstructor("friends",
                new TableColumn("id", ColumnType.INT_11).primaryKey(true),
                new TableColumn("friend_id", ColumnType.INT_11).primaryKey(true)
        ).create(GlobalLoader.getMysqlDatabase());
    }

}
