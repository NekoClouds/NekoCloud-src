package me.nekocloud.packetlib.libraries.inventory.def;

import io.netty.util.internal.ConcurrentSet;
import lombok.Getter;
import lombok.val;
import me.nekocloud.api.gui.DefaultGui;
import me.nekocloud.api.manager.GuiManager;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class DefaultGuiManager implements GuiManager<DefaultGui<?>> {

	private final Map<String, Map<String, DefaultGui<?>>> playerGuis = new ConcurrentHashMap<>();
	private final Set<String> guis = new ConcurrentSet<>();

	@Override
	public void createGui(final Class<? extends DefaultGui<?>> clazz) {
		String name = clazz.getSimpleName().toLowerCase();
		if (guis.contains(name))
			return;

		guis.add(name);
	}

	@Override
	public void removeGui(final Class<? extends DefaultGui<?>> clazz) {
		String nameClazz = clazz.getSimpleName().toLowerCase();
		for (String name : playerGuis.keySet()) {
			Map<String, DefaultGui<?>> guis = playerGuis.get(name);
			for (String guiName : guis.keySet())
				if (guiName.equalsIgnoreCase(nameClazz))
					guis.remove(guiName);
		}
	}

	@Override
	public <T extends DefaultGui<?>> T getGui(Class<T> clazz, Player player) {
		T gui = null;

		val guiName = clazz.getSimpleName().toLowerCase();
		val name = player.getName().toLowerCase();

		if (guis.contains(guiName)) {
			Map<String, DefaultGui<?>> guis = playerGuis.get(name);
			if (guis == null) {
				guis = new ConcurrentHashMap<>();
				playerGuis.put(name, guis);
			}
			gui = (T) guis.get(guiName);

			if (gui == null) {
				try {
					gui = clazz.getConstructor(Player.class).newInstance(player);
					guis.put(guiName, gui);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return gui;
	}

	@Override
	public void removeALL(final Player player) {
		val name = player.getName().toLowerCase();
		playerGuis.remove(name);
	}
}
