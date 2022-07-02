package me.nekocloud.pexer.listeners;

import lombok.val;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.nekoapi.listeners.DListener;
import me.nekocloud.pexer.NekoPexer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import ru.Den_Abr.SimplePerms.Entity.SimpleGroup;
import ru.Den_Abr.SimplePerms.SimplePerms;
import ru.Den_Abr.SimplePerms.SimplePermsCommon;

import java.util.Objects;

public class SPListener extends DListener<NekoPexer> {

    protected SimplePerms spc = SimplePermsCommon.getInstance();

    public SPListener(NekoPexer nekoPexer) {
        super(nekoPexer);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onJoin(final PlayerJoinEvent e) {
        val gamer = javaPlugin.getGamerManager().getGamer(e.getPlayer());
        if (gamer == null)
            return;

        val user = spc.getPermissionManager().getUser(gamer.getName());

        if (gamer.getGroup() == Group.DEFAULT) {
            SimpleGroup simpleGroup = spc.getPermissionManager().getGroup("default");
            if (!user.getUserGroup().equals(simpleGroup)) {
                setGroup(gamer.getName(), "default");
            }
        } else {
            setGroup(gamer.getName(), gamer.getGroup().getGroupName());
        }

    }

    private void setGroup(String player, String group) {
        val gr = spc.getPermissionManager().getGroup(group);
        val user = spc.getPermissionManager().getUser(player);

        if (!gr.isVirtual() && !Objects.equals(gr, user.getUserGroup())) {
            user.setUserGroup(gr);
            user.save();
            user.updateOrUncache();
        }
    }

}
