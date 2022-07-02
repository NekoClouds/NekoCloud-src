package me.nekocloud.pexer.listeners;

import lombok.val;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.nekoapi.listeners.DListener;
import me.nekocloud.pexer.NekoPexer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

public class LPListener extends DListener<NekoPexer> {

    public LPListener(NekoPexer pexer) {
        super(pexer);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onJoin(final PlayerJoinEvent e) {
        val gamer = javaPlugin.getGamerManager().getGamer(e.getPlayer());
        val player = e.getPlayer();
        if (gamer == null)
            return;

        val user = javaPlugin.getLuckPermsApi().getUser(player.getName());
        if (user == null)
            return;

        if (gamer.getGroup() == Group.DEFAULT) {
            if (user.getPrimaryGroup().length() > 0 && !user.getPrimaryGroup().equalsIgnoreCase("default")) {
                user.setPrimaryGroup("default");
            }
        } else {
            user.setPrimaryGroup(gamer.getGroup().getGroupName());
        }
    }

}
