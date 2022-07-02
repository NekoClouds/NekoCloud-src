package me.nekocloud.packetlib.nms.util;

import lombok.experimental.UtilityClass;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

import static me.nekocloud.api.util.SVersionUtil.SERVER_VERSION;

@UtilityClass
public class ReflectionUtils {

    public Object getValue(Object object, String name) {
        try {
            Field field = object.getClass().getDeclaredField(name);
            field.setAccessible(true);
            return field.get(object);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Object getStaticValue(Class<?> clazz, String name) {
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return field.get(null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static Field getField(Class<?> clazz, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setFieldValue(Object object, String fieldName, Object value) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(object, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> T getFieldValue(Object object, String fieldName) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return (T) field.get(object);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public <T> Class<? extends T[]> getArrayClass(Class<T> clazz) {
        return (Class<? extends T[]>) Array.newInstance(clazz, 0).getClass();
    }

    public Class<?> getNMSClass(String ver, String name) throws Exception {
        return Class.forName("net.minecraft.server." + SERVER_VERSION + "." + name);
    }

    public Class<?> getMapNMSClass(String dir, String name) throws Exception {
        return Class.forName("net.minecraft." + dir + name);
    }

    public Class<?> getCraftBukkitClass(String name) throws Exception  {
        return Class.forName("org.bukkit.craftbukkit." + SERVER_VERSION + "." + name);
    }

}