package me.nekocloud.lobby.config;

import lombok.Getter;
import lombok.experimental.FieldDefaults;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.entity.EntityAPI;
import me.nekocloud.api.entity.npc.NPC;
import me.nekocloud.api.entity.npc.types.HumanNPC;
import me.nekocloud.api.util.ItemUtil;
import me.nekocloud.api.util.LocationUtil;
import me.nekocloud.base.game.SubType;
import me.nekocloud.lobby.Lobby;
import me.nekocloud.lobby.api.game.GameUpdateType;
import me.nekocloud.lobby.game.commands.GameInfoCommand;
import me.nekocloud.lobby.game.data.Channel;
import me.nekocloud.lobby.npc.LobbyNPC;
import me.nekocloud.lobby.npc.NPCListener;
import me.nekocloud.lobby.npc.StartGameNPC;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static lombok.AccessLevel.PRIVATE;

@Getter
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class GameConfig extends LobbyConfig {

    static EntityAPI ENTITY_API = NekoCloud.getEntityAPI();

    Map<NPC, StartGameNPC> startGameNPCs = new HashMap<>();
    Map<String, Channel> channels  = new ConcurrentHashMap<>();

    public GameConfig(Lobby lobby) {
        super(lobby, "game");
    }

    @Override
    public void load() {
        FileConfiguration config = getConfig();

        for (String nameChannel : config.getConfigurationSection("Channels").getKeys(false)) {
            String patch = "Channels." + nameChannel + ".";

            GameUpdateType gameUpdateType = GameUpdateType.DEFAULT;

            SubType type = SubType.getByName(config.getString(patch + "Type"));
            Channel channel = new Channel(nameChannel.toLowerCase(), type);
            this.channels.put(nameChannel.toLowerCase(), channel);

            String name = config.getString(patch + "Name");
            Location location = LocationUtil.stringToLocation(config.getString(patch + "Location"), true);
            String value = config.getString(patch + "Value");
            String signature = config.getString(patch + "Signature");

            if (config.getBoolean(patch + "New"))
                gameUpdateType = GameUpdateType.NEW;

            if (config.getBoolean(patch + "Update"))
                gameUpdateType = GameUpdateType.UPDATE;

            createStartNPC(name, location, channel, value, signature, gameUpdateType);
        }


//        if (config.contains("LocationShop")) {
//            Location location = LocationUtil.stringToLocation(config.getString("LocationShop"), true);
//            HumanNPC humanNPC = ENTITY_API.createNPC(location, Skin.SKIN_SHOP);
//            humanNPC.getEntityEquip().setItemInMainHand(new ItemStack(Material.EMERALD));
//            humanNPC.setPublic(true);
//            shopNPC = new ShopNPC(humanNPC, location);
//        }
//
//        if (config.contains("LocationSpectator")) {
//            Location location = LocationUtil.stringToLocation(config.getString("LocationSpectator"), true);
//            HumanNPC humanNPC = ENTITY_API.createNPC(location, Skin.SKIN_SPECTATOR);
//            humanNPC.getEntityEquip().setItemInMainHand(new ItemStack(Material.COMPASS));
//            humanNPC.setPublic(true);
//            spectatorNpc = new SpectatorNpc(humanNPC, location);
//        }
    }

    @Override
    public void init() {
        if (getAllNpc().isEmpty()) {
            return;
        }

        new NPCListener(lobby, this);
        new GameInfoCommand(this);
    }

    private void createStartNPC(String name, Location location, Channel channel,
                                String value, String signature, GameUpdateType gameUpdateType) {
        HumanNPC humanNPC = ENTITY_API.createNPC(location, value, signature);
        humanNPC.getEntityEquip().setItemInMainHand(ItemUtil.getBuilder(Material.REDSTONE).build());
        humanNPC.setGlowing(gameUpdateType.getChatColor());
        humanNPC.setPublic(true);

        StartGameNPC startGameNPC = new StartGameNPC(name, location, channel, humanNPC, gameUpdateType);
        startGameNPCs.put(humanNPC, startGameNPC);
    }

    public Map<NPC, LobbyNPC> getAllNpc() {

        return new HashMap<>(startGameNPCs);
    }
}
