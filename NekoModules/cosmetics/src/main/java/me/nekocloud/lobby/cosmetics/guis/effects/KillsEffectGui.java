package me.nekocloud.lobby.cosmetics.guis.effects;

import lombok.val;
import me.nekocloud.api.inventory.DItem;
import me.nekocloud.api.util.ItemUtil;
import me.nekocloud.base.gamer.constans.PurchaseType;
import me.nekocloud.base.util.Cooldown;
import me.nekocloud.lobby.cosmetics.api.Effect;
import me.nekocloud.lobby.cosmetics.api.EffectType;
import me.nekocloud.lobby.cosmetics.guis.EffectsMainGui;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class KillsEffectGui extends EffectsMainGui {

    public KillsEffectGui(Player player) {
        super(player);
    }

    @Override
    protected void createInventory() {
        inventory = INVENTORY_API.createInventory(player, gamer.getLanguage().getMessage("KILLS_EFFECTS_GUI_NAME"), 5);
    }

    @Override
    protected void setItems() {
        val lang = gamer.getLanguage();
//        INVENTORY_API.backButton(this.inventory,
//                (clicker, clickType, slot) -> GUI_MANAGER.getGui(EffectsMainGui.class, this.player).open(), 40);
        int index = 0;
        for (Effect effect : Effect.values()) {
            inventory.setItem(SLOTS[index], new DItem(ItemUtil.getBuilder(effect.getIcon())
                    .setName(lang.getMessage("EFFECT_NAME", lang.getMessage(("EFFECT_" + effect.name() + "_NAME"))))
                    .setLore(lang.getList((cosmeticPlayer.getSelectedParticle(EffectType.KILLS) ==
                            effect.getParticleEffect() ? "EFFECT_IS_SELECTED_LORE" :
                            (cosmeticPlayer.getParticles().get(EffectType.KILLS).contains(effect.getParticleEffect()) ?
                                    "EFFECT_IS_BUYED_LORE" : "EFFECT_NOT_BUYED_LORE")), effect.getPrice()))

                    .build(), (player, clickType, i) -> {
                if (cosmeticPlayer.getSelectedParticle(EffectType.KILLS) == effect.getParticleEffect()) {
                    gamer.sendMessageLocale("EFFECT_IS_SELECTED");
                    return;
                }

                if (cosmeticPlayer.getParticles().get(EffectType.KILLS).contains(effect.getParticleEffect())) {
                    if (Cooldown.hasCooldown(gamer.getName(), "EFFECT_SELECT")) {
                        gamer.sendMessageLocale("EFFECT_SELECT_COOLDOWN",
                                Cooldown.getSecondCooldown(gamer.getName(), "EFFECT_SELECT"));
                        return;
                    }
                    Cooldown.addCooldown(gamer.getName(), "EFFECT_SELECT", 60L);
                    cosmeticPlayer.setSelectedParticle(EffectType.KILLS, effect.getParticleEffect());
                    setItems();

                    gamer.sendMessageLocale("EFFECT_SELECT", lang.getMessage(("EFFECT_" + effect.name() + "_NAME")),
                            "\u0443\u0431\u0438\u0439\u0441\u0442\u0432");
                    return;
                }
                if (gamer.getMoney(PurchaseType.COINS) < effect.getPrice()) {
                    gamer.sendMessageLocale("EFFECTS_NO_COINS", effect.getPrice());
                    return;
                }

                gamer.setMoney(PurchaseType.COINS, gamer.getMoney(PurchaseType.COINS) - effect.getPrice());
                cosmeticPlayer.addParticle(EffectType.KILLS, effect.getParticleEffect());

                setItems();
                gamer.sendMessageLocale("EFFECT_BUY", lang.getMessage(("EFFECT_" + effect.name() + "_NAME")),
                        "\u0443\u0431\u0438\u0439\u0441\u0442\u0432", effect.getPrice());
            }));
            ++index;
        }

        inventory.setItem(6, 5, new DItem(ItemUtil.getBuilder(Material.BARRIER)
                .setName(lang.getMessage("EFFECT_CLEAR_NAME"))
                .setLore(lang.getList("EFFECT_CLEAR_LORE"))
                .build(), (player, clickType, i) -> {
            if (cosmeticPlayer.getSelectedParticle(EffectType.KILLS) == null) {
                gamer.sendMessageLocale("EFFECT_NOT_SELECTED", "\u0443\u0431\u0438\u0439\u0441\u0442\u0432\u0430");
                return;
            }

            cosmeticPlayer.setSelectedParticle(EffectType.KILLS, null);

            setItems();
            gamer.sendMessageLocale("EFFECT_CLEAR", "\u0443\u0431\u0438\u0439\u0441\u0442\u0432");
        }));
    }
}

