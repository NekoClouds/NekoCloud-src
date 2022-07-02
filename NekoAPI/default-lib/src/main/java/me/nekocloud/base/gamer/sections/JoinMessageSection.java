package me.nekocloud.base.gamer.sections;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import me.nekocloud.base.gamer.IBaseGamer;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.gamer.constans.JoinMessage;
import me.nekocloud.base.sql.GlobalLoader;
import me.nekocloud.base.sql.api.query.MysqlQuery;
import me.nekocloud.base.sql.api.query.QuerySymbol;
import me.nekocloud.base.sql.api.table.ColumnType;
import me.nekocloud.base.sql.api.table.TableColumn;
import me.nekocloud.base.sql.api.table.TableConstructor;

import java.util.ArrayList;
import java.util.List;

public class JoinMessageSection extends Section {

    private JoinMessage joinMessage;
    private final Int2ObjectMap<JoinMessage> available = new Int2ObjectOpenHashMap<>();

    public JoinMessageSection(IBaseGamer baseGamer) {
        super(baseGamer);
    }

    @Override
    public boolean loadData() {
        int playerId = baseGamer.getPlayerID();
        GlobalLoader.getMysqlDatabase().executeQuery(MysqlQuery.selectFrom("join_messages")
                .where("id", QuerySymbol.EQUALLY, playerId)
                .limit(JoinMessage.getMessages().size()), (rs) -> {

            while (rs.next()) {
                JoinMessage joinMessage = JoinMessage.getMessage(rs.getInt("message"));
                available.putIfAbsent(joinMessage.getId(), joinMessage);

                if (rs.getBoolean("available")) {
                    this.joinMessage = joinMessage;
                }
            }
            return Void.TYPE;
        });

        if (!baseGamer.isDefault()) {
            available.putIfAbsent(JoinMessage.DEFAULT_MESSAGE.getId(), JoinMessage.DEFAULT_MESSAGE);
        }

        if (baseGamer.getGroup().getLevel() >= Group.ADMIN.getLevel()
                || baseGamer.getGroup() == Group.NEKO
                || baseGamer.getGroup() == Group.YOUTUBE
                || baseGamer.getGroup() == Group.TIKTOK) {
            for (JoinMessage joinMessage : JoinMessage.getMessages()) {
                if (!joinMessage.isNeko()) {
                    continue;
                }

                available.putIfAbsent(joinMessage.getId(), joinMessage);
            }
        }

        return true;
    }

    public JoinMessage getJoinMessage() {
        if (!baseGamer.isHegent()) {
            return null;
        }

        if (joinMessage == null) {
            return JoinMessage.DEFAULT_MESSAGE;
        }

        return joinMessage;
    }

    public void setDefaultJoinMessage(JoinMessage joinMessage, boolean mysql) {
        if (joinMessage == null || this.joinMessage == joinMessage)
            return;

        if (mysql) {
            int playerId = baseGamer.getPlayerID();

            if (this.joinMessage != null) {
                GlobalLoader.getMysqlDatabase().execute(MysqlQuery.update("join_messages") //старое отключить
                        .set("available", 0)
                        .where("id", QuerySymbol.EQUALLY, playerId)
                        .where("message", QuerySymbol.EQUALLY, this.joinMessage.getId()).limit());
            }

            GlobalLoader.getMysqlDatabase().executeQuery(MysqlQuery.selectFrom("join_messages")
                    .where("id", QuerySymbol.EQUALLY, playerId)
                    .where("message", QuerySymbol.EQUALLY, joinMessage.getId())
                    .limit(), (rs) -> {
                if (rs.next()) {
                    GlobalLoader.getMysqlDatabase().execute(MysqlQuery.update("join_messages")
                            .set("available", 1)
                            .where("id", QuerySymbol.EQUALLY, playerId)
                            .where("message", QuerySymbol.EQUALLY, joinMessage.getId()).limit());
                } else {
                    GlobalLoader.getMysqlDatabase().execute(MysqlQuery.insertTo("join_messages")
                            .set("available", 1)
                            .set("id", playerId)
                            .set("message", joinMessage.getId()));
                }

                return Void.TYPE;
            });
        }

        this.joinMessage = joinMessage;
    }

    public List<JoinMessage> getAvailableJoinMessage() {
        return new ArrayList<>(available.values());
    }

    public void addJoinMessage(JoinMessage joinMessage, boolean mysql) {
        if (joinMessage == null || available.containsKey(joinMessage.getId())) {
            return;
        }

        available.put(joinMessage.getId(), joinMessage);
        if (mysql) {
            GlobalLoader.getMysqlDatabase().execute(MysqlQuery.insertTo("join_messages")
                    .set("id", baseGamer.getPlayerID())
                    .set("message", joinMessage.getId()));
        }
    }


    static {
        new TableConstructor("join_messages",
                new TableColumn("id", ColumnType.INT_11).primaryKey(true),
                new TableColumn("message", ColumnType.INT_5).primaryKey(true),
                new TableColumn("available", ColumnType.BOOLEAN).setDefaultValue(0)
        ).create(GlobalLoader.getMysqlDatabase());
    }


}
