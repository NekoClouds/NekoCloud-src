package me.nekocloud.pexer.listeners;

import lombok.val;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.nekoapi.listeners.DListener;
import me.nekocloud.pexer.NekoPexer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class PEXListener extends DListener<NekoPexer> {

    public PEXListener(NekoPexer pexer) {
        super(pexer);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onJoin(final PlayerJoinEvent e) {
        val gamer = javaPlugin.getGamerManager().getGamer(e.getPlayer());
        if (gamer == null)
            return;

        val user = PermissionsEx.getUser(e.getPlayer());
        if (gamer.getGroup() == Group.DEFAULT) {
            if (user.getGroups().length > 0 && !user.getGroups()[0].getName().equalsIgnoreCase("default")) {
                user.setGroups(new String[] { "default" });
            }
        }
        else {
            user.setGroups(new String[] { gamer.getGroup().getGroupName() });
        }
    }
}
