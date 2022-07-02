package me.nekocloud.games.trader.listener;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.val;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.event.gamer.GamerChangeLanguageEvent;
import me.nekocloud.api.event.gamer.GamerInteractNPCEvent;
import me.nekocloud.api.event.gamer.async.AsyncGamerJoinEvent;
import me.nekocloud.api.hologram.Hologram;
import me.nekocloud.base.locale.Language;
import me.nekocloud.games.trader.Trader;
import me.nekocloud.games.trader.gui.TraderGui;
import me.nekocloud.nekoapi.listeners.DListener;
import me.nekocloud.nekoapi.tops.HologramAnimation;
import org.bukkit.event.EventHandler;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TraderListener extends DListener<Trader> {

	TIntObjectMap<Hologram> traderHolograms = new TIntObjectHashMap<>();

	public TraderListener(Trader trader) {
		super(trader);

		val hologramAPI = NekoCloud.getHologramAPI();
		for (val lang : Language.VALUES) {
			val traderHolo = hologramAPI.createHologram(
					trader.getTraderNpc().getLocation().add(0, 1.6, 0));
			val lines = lang.getList("HOLO_TRADER_NPC");
			// TODO говнокод ебаный, сука блядь пиздец..... переписать
			lines.forEach(line -> {
				if (line.contains("%anim")) traderHolo.addAnimationLine(20, new HologramAnimation(lang,60));
				else traderHolo.addTextLine(lines);
			});

			traderHolograms.put(lang.getId(), traderHolo);
		}

	}

	@EventHandler
	public void onGamerJoin(AsyncGamerJoinEvent e) {
		val gamer = e.getGamer();
		if (gamer == null)
			return;

		val hologram = traderHolograms.get(gamer.getLanguage().getId());
		if (hologram != null) hologram.showTo(gamer.getPlayer());
	}

	@EventHandler
	public void onChangeLang(GamerChangeLanguageEvent e) {
		val player = e.getGamer().getPlayer();
		val oldLang = e.getOldLanguage();
		val newLang = e.getLanguage();

		Hologram hologram = traderHolograms.get(oldLang.getId());
        if (hologram != null)
            hologram.removeTo(player);

        hologram = traderHolograms.get(newLang.getId());
        if (hologram != null)
            hologram.showTo(player);
	}

	@EventHandler
    public void onClick(GamerInteractNPCEvent e) {
		if (e.getNpc() != javaPlugin.getTraderNpc())
			return;

		val gamer = e.getGamer();
		val player = gamer.getPlayer();

		NekoCloud.getGuiManager().getGui(TraderGui.class, player).open();
	}
}
