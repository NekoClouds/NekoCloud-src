package me.nekocloud.survival.commons.managers;

import me.nekocloud.survival.commons.api.User;
import me.nekocloud.survival.commons.api.manager.UserManager;
import me.nekocloud.survival.commons.object.CraftUser;
import me.nekocloud.nekoapi.utils.bukkit.BukkitUtil;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CraftUserManager implements UserManager {
    private Map<String, User> USERS = new ConcurrentHashMap<>();

    @Override
    public User getUser(Player player) {
        return getUser(player.getName());
    }

    @Override
    public User getUser(String name) {
        String nameLower = name.toLowerCase();
        return USERS.get(nameLower);
    }

    @Override
    public Map<String, User> getUsers() {
        return USERS;
    }

    @Override
    public void addUser(User user) {
        if (USERS.containsValue(user))
            return;

        USERS.put(user.getName().toLowerCase(), user);
    }

    @Override
    public void removeUser(User user) {
        if (!USERS.containsValue(user))
            return;

        if (user == null)
            return;

        removeUser(user.getName());
    }

    @Override
    public void removeUser(String name) {
        String nameLower = name.toLowerCase();
        User user = USERS.remove(nameLower);
        if (user == null)
            return;

        CraftUser craftUser = (CraftUser) user;
        BukkitUtil.runTaskAsync(craftUser::save);
    }
}
