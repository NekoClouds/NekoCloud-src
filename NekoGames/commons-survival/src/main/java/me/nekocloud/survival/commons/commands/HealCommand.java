package me.nekocloud.survival.commons.commands;

import me.nekocloud.base.locale.CommonWords;
import me.nekocloud.survival.commons.config.ConfigData;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerEntity;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.locale.Language;
import me.nekocloud.base.util.Cooldown;
import me.nekocloud.base.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

public class HealCommand extends CommonsCommand {

    public HealCommand(ConfigData configData) {
        super(configData,true, "heal");
        setMinimalGroup(configData.getInt("healCommand"));
    }

    @Override
    public void execute(GamerEntity gamerEntity, String command, String[] strings) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Player sender = gamer.getPlayer();

        if (strings.length == 1 && gamer.getGroup().getLevel() >= Group.ADMIN.getLevel()) {
            String name = strings[0];
            Player player = Bukkit.getPlayer(name);
            if (player == null || !player.isOnline()) {
                COMMANDS_API.playerOffline(gamerEntity, name);
                return;
            }
            heal(player);
            send("HEAL", gamer, player.getDisplayName());
            return;
        }

        int level = configData.getInt("ignoreCooldownHeal");
        if (Cooldown.hasCooldown(sender.getName(), getCooldownType()) && gamer.getGroup().getLevel() < level) {
            Language lang = gamer.getLanguage();
            int time = Cooldown.getSecondCooldown(sender.getName(), getCooldownType());
            gamerEntity.sendMessageLocale("COOLDOWN", String.valueOf(time),
                    CommonWords.SECONDS_1.convert(time, lang));
        } else {
            if (gamer.getGroup().getLevel() < level)
                Cooldown.addCooldown(sender.getName(), getCooldownType(), 5 * 60 * 20);

            heal(sender);
        }

    }

    private void heal(Player player) {
        if (player.getHealth() == 0)
            return;

        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.setFireTicks(0);
        for (PotionEffect effect : player.getActivePotionEffects())
            player.removePotionEffect(effect.getType());

        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        send("HEAL", gamer, null);
    }
}
