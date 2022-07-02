package me.nekocloud.api.util;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;

@UtilityClass
public class SVersionUtil {

	public final String SERVER_VERSION = Bukkit.getServer().getClass().getPackage().getName().substring(23);
    public final int SERVER_VERSION_NUMBER = Integer.parseInt(SERVER_VERSION.split("_")[1]);

    public boolean is1_8() {
        return SERVER_VERSION.startsWith("v1_8");
    }

    public boolean is1_12() {
        return SERVER_VERSION.startsWith("v1_12");
    }

    public boolean is1_16() {
        return SERVER_VERSION.startsWith("v1_16");
    }

    public boolean is1_18() {
        return SERVER_VERSION.startsWith("v1_18");
    }
}
