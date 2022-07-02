package me.nekocloud.lobby.cosmetics.guis.effects;

import me.nekocloud.api.inventory.DItem;
import me.nekocloud.api.util.ItemUtil;
import me.nekocloud.base.gamer.constans.PurchaseType;
import me.nekocloud.base.locale.Language;
import me.nekocloud.base.util.Cooldown;
import me.nekocloud.lobby.cosmetics.api.Effect;
import me.nekocloud.lobby.cosmetics.api.EffectType;
import me.nekocloud.lobby.cosmetics.guis.EffectsMainGui;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class CritsEffectGui extends EffectsMainGui {

    public CritsEffectGui(Player player) {
        super(player);
    }

    @Override
    protected void createInventory() {
        this.inventory = INVENTORY_API.createInventory(this.player, gamer.getLanguage().getMessage("CRITS_EFFECTS_GUI_NAME"), 5);
    }

    @Override
    protected void setItems() {
        Language lang = gamer.getLanguage();
        //INVENTORY_API.backButton(this.inventory,
         //       (clicker, clickType, slot) -> GUI_MANAGER.getGui(EffectsMainGui.class, this.player).open(), 40);
        int index = 0;
        for (Effect effect : Effect.values()) {
            this.inventory.setItem(SLOTS[index], new DItem(ItemUtil.getBuilder(effect.getIcon()).setName(lang.getMessage("EFFECT_NAME", new Object[]{lang.getMessage(("EFFECT_" + effect.name() + "_NAME"))})).setLore(lang.getList((this.cosmeticPlayer.getSelectedParticle(EffectType.CRITS) == effect.getParticleEffect() ? "EFFECT_IS_SELECTED_LORE" : (this.cosmeticPlayer.getParticles().get(EffectType.CRITS).contains(effect.getParticleEffect()) ? "EFFECT_IS_BUYED_LORE" : "EFFECT_NOT_BUYED_LORE")), new Object[]{effect.getPrice()})).build(), (player, clickType, i) -> {
                if (this.cosmeticPlayer.getSelectedParticle(EffectType.CRITS) == effect.getParticleEffect()) {
                    this.gamer.sendMessageLocale("EFFECT_IS_SELECTED");
                    return;
                }
                if (this.cosmeticPlayer.getParticles().get(EffectType.CRITS).contains(effect.getParticleEffect())) {
                    if (Cooldown.hasCooldown(this.gamer.getName(), "EFFECT_SELECT")) {
                        this.gamer.sendMessageLocale("EFFECT_SELECT_COOLDOWN", Cooldown.getSecondCooldown(this.gamer.getName(), "EFFECT_SELECT"));
                        return;
                    }

                    Cooldown.addCooldown(this.gamer.getName(), "EFFECT_SELECT", 60L);
                    this.cosmeticPlayer.setSelectedParticle(EffectType.CRITS, effect.getParticleEffect());
                    this.setItems();
                    this.gamer.sendMessageLocale("EFFECT_SELECT", lang.getMessage(("EFFECT_" + effect.name() + "_NAME")), "\u043a\u0440\u0438\u0442\u043e\u0432");
                    return;
                }
                if (this.gamer.getMoney(PurchaseType.COINS) < effect.getPrice()) {
                    this.gamer.sendMessageLocale("EFFECTS_NO_COINS", effect.getPrice());
                    return;
                }
                this.gamer.setMoney(PurchaseType.COINS, this.gamer.getMoney(PurchaseType.COINS) - effect.getPrice());
                this.cosmeticPlayer.addParticle(EffectType.CRITS, effect.getParticleEffect());
                this.setItems();
                this.gamer.sendMessageLocale("EFFECT_BUY", lang.getMessage(("EFFECT_" + effect.name() + "_NAME")), "\u043a\u0440\u0438\u0442\u043e\u0432", effect.getPrice());
            }));
            ++index;
        }
        this.inventory.setItem(6, 5, new DItem(ItemUtil.getBuilder(Material.BARRIER)
                .setName(lang.getMessage("EFFECT_CLEAR_NAME"))
                .setLore(lang.getList("EFFECT_CLEAR_LORE"))
                .build(), (player, clickType, i) -> {
            if (this.cosmeticPlayer.getSelectedParticle(EffectType.CRITS) == null) {
                this.gamer.sendMessageLocale("EFFECT_NOT_SELECTED", "\u043a\u0440\u0438\u0442\u044b");
                return;
            }

            this.cosmeticPlayer.setSelectedParticle(EffectType.CRITS, null);
            this.setItems();
            this.gamer.sendMessageLocale("EFFECT_CLEAR", "\u043a\u0440\u0438\u0442\u043e\u0432");
        }));
    }
}

