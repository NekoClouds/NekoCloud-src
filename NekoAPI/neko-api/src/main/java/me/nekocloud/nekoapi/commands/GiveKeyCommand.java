package me.nekocloud.nekoapi.commands;

import com.google.common.collect.ImmutableList;
import lombok.val;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.command.CommandInterface;
import me.nekocloud.api.command.CommandTabComplete;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.base.gamer.IBaseGamer;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.gamer.constans.KeyType;
import me.nekocloud.base.sql.PlayerInfoLoader;
import me.nekocloud.base.util.Pair;
import me.nekocloud.base.util.StringUtil;
import me.nekocloud.nekoapi.utils.bukkit.BukkitUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class GiveKeyCommand implements CommandInterface, CommandTabComplete {

    private final GamerManager gamerManager = NekoCloud.getGamerManager();

    private final ImmutableList<String> argsTabCompleteTypeKeys = ImmutableList.of(
            "1", "2", "3", "4", "5"
    );

    public GiveKeyCommand() {
        val command = COMMANDS_API.register("addkeys", this);
        command.setGroup(Group.ADMIN);
    }

    @Override
    public void execute(GamerEntity gamerEntity, String command, String[] args) {
        if (gamerEntity.isHuman() && !((BukkitGamer) gamerEntity).isDeveloper()) {
            return;
        }

        if (args.length < 3) {
            gamerEntity.sendMessage("§d§lСЕРВЕР §8| §fОшибка, пиши /addkeys <ник игрока> <type> <ключи>");
            return;
        }

        KeyType keyType;
        try {
            keyType = KeyType.getKey(Integer.parseInt(args[1]));
        } catch (Exception e) {
            keyType = null;
        }

        if (keyType == null) {
            gamerEntity.sendMessage("§d§lСЕРВЕР §8| §fОшибка, неверно указан тип ключей!");
            return;
        }

        int keys;
        try {
            keys = Integer.parseInt(args[2]);
        } catch (Exception e) {
            keys = 0;
        }

        KeyType finalKeyType = keyType;
        int finalKeys = keys;
        BukkitUtil.runTaskAsync(() -> addKeys(gamerEntity, args[0], finalKeyType, finalKeys));
    }

    private void addKeys(GamerEntity gamerEntity, String name, KeyType keyType, int keys) {
        IBaseGamer gamer = gamerManager.getOrCreate(name);
        if (gamer == null || gamer.getPlayerID() == -1) {
            COMMANDS_API.playerNeverPlayed(gamerEntity, name);
            return;
        }

        if (!gamer.isOnline()) {
            Map<KeyType, Pair<Integer, Integer>> keysData = PlayerInfoLoader.getData(gamer.getPlayerID()).getSecond();
            boolean insert = !keysData.containsKey(keyType);

            int random = 0;
            Pair<Integer, Integer> pair = keysData.get(keyType);
            if (pair != null) {
                random = pair.getSecond();
            }

            PlayerInfoLoader.updateData(gamer.getPlayerID(), keyType.getId(), keys, random, insert);

//            BukkitBalancePacket packet = new BukkitBalancePacket(gamer.getPlayerID(),
//                    BukkitBalancePacket.Type.KEYS,
//                    keyType.getId(),
//                    keys,
//                    true);
//            ConnectorPlugin.instance().getConnector().sendPacketAsync(packet);
        } else if (gamer instanceof BukkitGamer targetGamer) {
            targetGamer.changeKeys(keyType, keys);
        }

        gamerEntity.sendMessage("§d§lСЕРВЕР §8| §fТы выдал игроку "
                + gamer.getDisplayName() + " §a"
                + StringUtil.getNumberFormat(keys) + " §fключей ("
                + keyType.getName(gamerEntity.getLanguage()) + "§f)");
    }

    @Override
    public List<String> getComplete(GamerEntity gamerEntity, String alias, String[] args) {
        if (gamerEntity.isHuman() && ((BukkitGamer)gamerEntity).getGroup() != Group.ADMIN) {
            return ImmutableList.of();
        }

        if (args.length == 1) {
            List<String> names = Bukkit.getOnlinePlayers()
                    .stream()
                    .map(HumanEntity::getName)
                    .collect(Collectors.toList());
            return COMMANDS_API.getCompleteString(names, args);
        }

        if (args.length == 2) {
            return COMMANDS_API.getCompleteString(new ArrayList<>(argsTabCompleteTypeKeys), args);
        }

        return ImmutableList.of();
    }
}
