package me.nekocloud.lobby.cosmetics.sql;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import lombok.experimental.UtilityClass;
import me.nekocloud.api.effect.ParticleEffect;
import me.nekocloud.base.sql.PlayerInfoLoader;
import me.nekocloud.base.sql.api.MySqlDatabase;
import me.nekocloud.base.sql.api.query.MysqlQuery;
import me.nekocloud.base.sql.api.query.QuerySymbol;
import me.nekocloud.base.sql.api.table.ColumnType;
import me.nekocloud.base.sql.api.table.TableColumn;
import me.nekocloud.base.sql.api.table.TableConstructor;
import me.nekocloud.lobby.cosmetics.api.EffectType;
import me.nekocloud.lobby.cosmetics.api.player.CosmeticPlayer;
import me.nekocloud.lobby.cosmetics.data.CachedParticleEffects;
import me.nekocloud.lobby.cosmetics.player.CraftCosmeticPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public final class CosmeticLoader {

    private final MySqlDatabase MYSQL_DATABASE = PlayerInfoLoader.getMysqlDatabase();

    public MySqlDatabase getDatabase() {
        return MYSQL_DATABASE;
    }

    static {
        new TableConstructor("player_effects",
                new TableColumn("player_id", ColumnType.INT_11).setDefaultValue(-1),
                new TableColumn("type", ColumnType.INT_5).setDefaultValue(1),
                new TableColumn("effect", ColumnType.INT_5)
        ).create(MYSQL_DATABASE);
        new TableConstructor("player_selected_effects",
                new TableColumn("player_id", ColumnType.INT_11).setDefaultValue(-1),
                new TableColumn("type", ColumnType.INT_5).setDefaultValue(1),
                new TableColumn("effect", ColumnType.INT_5)
        ).create(MYSQL_DATABASE);
    }
    
    public CosmeticPlayer getCosmeticPlayer(Player player, int playerID) {
        return new CraftCosmeticPlayer(player,
                CosmeticLoader.getEffects(playerID),
                CosmeticLoader.getSelectedEffects(playerID));
    }

    public Multimap<EffectType, ParticleEffect> getEffects(int playerID) {
        return MYSQL_DATABASE.executeQuery(MysqlQuery.selectFrom("player_effects")
                .where("player_id", QuerySymbol.EQUALLY, playerID), rs -> {
            Multimap<EffectType, ParticleEffect> data = MultimapBuilder.enumKeys(EffectType.class)
                    .enumSetValues(ParticleEffect.class).build();

            while (rs.next()) {
                data.get(EffectType.getById(rs.getInt(2)))
                        .add(CachedParticleEffects.getById(rs.getInt(3)));
            }

            return data;
        });

    }

    public Map<EffectType, ParticleEffect> getSelectedEffects(int playerID) {
        return MYSQL_DATABASE.executeQuery(MysqlQuery.selectFrom("player_selected_effects")
                .where("player_id", QuerySymbol.EQUALLY, playerID)
                .limit(EffectType.values().length), rs -> {
            Map<EffectType, ParticleEffect> data = new HashMap<>();

            while (rs.next()) {
                data.put(EffectType.getById(rs.getInt(2)),
                        CachedParticleEffects.getById(rs.getInt(3)));
            }

            return data;
        });
    }

    public void addEffect(int playerID,
                          EffectType effectType,
                          ParticleEffect particleEffect
    ) {
        MYSQL_DATABASE.execute(MysqlQuery.insertTo("player_effects")
                .set("player_id", playerID).set("type", effectType.getId())
                .set("effect", particleEffect.getId()));
    }

    public void setSelectedEffect(int playerID,
                                  EffectType effectType,
                                  ParticleEffect particleEffect,
                                  boolean insert
    ) {
        if (particleEffect == null) {
            MYSQL_DATABASE.execute(MysqlQuery.deleteFrom("player_selected_effects")
                    .where("player_id", QuerySymbol.EQUALLY, playerID)
                    .where("type", QuerySymbol.EQUALLY, effectType.getId())
                    .limit());
            return;
        }

        if (insert) {
            MYSQL_DATABASE.execute(MysqlQuery.insertTo("player_selected_effects")
                    .set("player_id", playerID).set("type", effectType.getId())
                    .set("effect", particleEffect.getId()));
            return;
        }

        MYSQL_DATABASE.execute(MysqlQuery.update("player_selected_effects")
                .where("player_id", QuerySymbol.EQUALLY, playerID)
                .where("type", QuerySymbol.EQUALLY, effectType.getId())
                .set("effect", particleEffect.getId()).limit());
    }
}

