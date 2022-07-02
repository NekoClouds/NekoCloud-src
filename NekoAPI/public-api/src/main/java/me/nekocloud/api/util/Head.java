package me.nekocloud.api.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.Getter;
import me.nekocloud.base.skin.Skin;
import org.apache.commons.codec.binary.Base64;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Skull;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.UUID;

@Getter
public enum Head {
    MAIN_MENU_ITEM("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDVjNmRjMmJiZjUxYzM2Y2ZjNzcxNDU4NWE2YTU2ODNlZjJiMTRkNDdkOGZmNzE0NjU0YTg5M2Y1ZGE2MjIifX19"),
    GADGETS_ITEM("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGNlZWI3N2Q0ZDI1NzI0YTljYWYyYzdjZGYyZDg4Mzk5YjE0MTdjNmI5ZmY1MjEzNjU5YjY1M2JlNDM3NmUzIn19fQ=="),
    HUB_SELECTOR_ITEM("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjNkMDJjZGMwNzViYjFjYzVmNmZlM2M3NzExYWU0OTc3ZTM4YjkxMGQ1MGVkNjAyM2RmNzM5MTNlNWU3ZmNmZiJ9fX0="),

    JOIN_MESSAGE("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWFmNzgxZjNlMTI4YmE3NzFkY2ZkZDExNjgyNzU1YjQ5MGM2MWYxNzNiY2YzN2E5YzZiOGE0Nzg3ZTRmOSJ9fX0="),
    MONKEY("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2ViZmRlNjZhN2JiOTU3ZDRlYWFlMjk4ZmNiYWQ1Y2ExNzFhNTNhYTIxNTNmZDA5ZjI5N2M0MTc0OGNiNjI4In19fQ=="),
    PARTY("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDAyYzEwYWRjMzFiMWMyMWNjOThlY2Y4MDkyMjVmODdlMjVlNzIzNzhjZjQxN2RiNGJlM2Y2N2U5MWJiMSJ9fX0="),

    PREFIX("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTQzZWZkN2FlZDZiNjk3ZDYwMWFjNzczODY2MWFmNjhkZDU3NTE4ZWM5N2IzMWU5Yzk5ZDU2YjY4NjJiZDBjMSJ9fX0="),
    FAST_MESSAGE("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTM0NzRkMGFhMWZhMTA5YmY5ZjRmMDVlYWEyZDJjNmZlZjc1Y2ViNmUyOTIzZjg1ZThlM2E2MmNjNWE0NDk1OCJ9fX0="),
    GLOW_GUI("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjVhOTkxMDQ1MDE5ZjhiODRhMDE3YWFlYzU2NTkyMzI1ZTJmNmQwZTgwMmY2YTg1MzBjNzFlZDg1MmI4MWJhOCJ9fX0="),
    TITULS("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQyMGNkZjJhZjU3Y2M3NTc3NzNhY2UzZjE5MTVjYjcyYjU0MzJhMmZkYTMzNzNiMTY3OGY5OGJlYTdhYzcifX19"),

    NOT_FOUND("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTZiNTQzNDY0OTMwZThkMzg2NzZkYjZiZTk5MjBkZDgyYTU0ODU5YzM0OTdkOWZlMzFlZjkyYTg4NGM4NCJ9fX0="),
    LOBBY_ANOTHER("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmY3NjI5NDAxMWNiZGNkMmU5Mjk0MWRhZmU2YjM3MjZkZmYwMmMzZTFmODRkZmE1N2M2YWJhYjZmYzMzY2U2In19fQ=="),
    LOBBY_ON("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjgzOWUzODFkOWZlZGFiNmY4YjU5Mzk2YTI3NjQyMzhkY2ViMmY3ZWVhODU2ZGM2ZmM0NDc2N2RhMzgyZjEifX19"),
    LOBBY_FRIEND("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjE4NTZjN2IzNzhkMzUwMjYyMTQzODQzZDFmOWZiYjIxOTExYTcxOTgzYmE3YjM5YTRkNGJhNWI2NmJlZGM2In19fQ=="),

    MAINLOBBY("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWQ4NmU3YmQyOGMxNDZmNzE1MTRjNzgyY2FjMDU1ODYwZDFmMzcyYjRhOWJlM2ZlNjVjZmUxMTA0NzMzYmEifX19"),

    //games and minigames
    ANARCHY("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWU0MzUyNjgwZDBiYjI5YjkxMzhhZjc4MzMwMWEzOTFiMzQwOTBjYjQ5NDFkNTJjMDg3Y2E3M2M4MDM2Y2I1MSJ9fX0="),
    GRIEF("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODQ0OWI5MzE4ZTMzMTU4ZTY0YTQ2YWIwZGUxMjFjM2Q0MDAwMGUzMzMyYzE1NzQ5MzJiM2M4NDlkOGZhMGRjMiJ9fX0="),

    LUCKYWARS("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjNiNzEwYjA4YjUyM2JiYTdlZmJhMDdjNjI5YmEwODk1YWQ2MTEyNmQyNmM4NmJlYjM4NDU2MDNhOTc0MjZjIn19fQ=="),
    CREATIVE("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjMxMGQzZmQ0Mjk3NzhmM2U5NzIxZGY4NjVlNTY2ZTU0NjE3ZDU5MDZhODkzY2EwZTdhZmQ3NzE3MWZkOTAifX19"),

    //social
    VK("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmJkNzNkNjE2ZDIxYzE5MzAxZjJmMDc2Y2JjNTQ3YzdjMWI1MWJkNWUxYTQ1ZDdjNTlkNWFkYjgyODA4ZSJ9fX0="),
    DISCORD("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGQ0MjMzN2JlMGJkY2EyMTI4MDk3ZjFjNWJiMTEwOWU1YzYzM2MxNzkyNmFmNWZiNmZjMjAwMDAwMTFhZWI1MyJ9fX0="),
    GLOBE("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODc5ZTU0Y2JlODc4NjdkMTRiMmZiZGYzZjE4NzA4OTQzNTIwNDhkZmVjZDk2Mjg0NmRlYTg5M2IyMTU0Yzg1In19fQ=="),


    //page arrows
    LEFT("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ=="),
    RIGHT("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTliZjMyOTJlMTI2YTEwNWI1NGViYTcxM2FhMWIxNTJkNTQxYTFkODkzODgyOWM1NjM2NGQxNzhlZDIyYmYifX19"),
    BACK("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY1NDI2YTMzZGY1OGI0NjVmMDYwMWRkOGI5YmVjMzY5MGIyMTkzZDFmOTUwM2MyY2FhYjc4ZjZjMjQzOCJ9fX0="),

    // profile gui
    HELP("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2Y2YmY5NThhYmQ3ODI5NWVlZDZmZmMyOTNiMWFhNTk1MjZlODBmNTQ5NzY4MjllYTA2ODMzN2MyZjVlOCJ9fX0="),
    DONATE_MENU("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzg0NzJhMmRjYzIzOWI0YTQ4M2FjNDRjMWRiZjhmZGJhMGZjYTFkMjUzZWI2NDNmYTBiZDkzYWY4M2EzNzMifX19"),
    DONATE("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWViMWVjMTEyZjY4ZDE4NDk3NTQxNDk1ODkzYTM3NWMzMTdhNzc3ZTAxM2JiZjEzNTg5YjFkODg1MzJjYyJ9fX0="),

    // mode gui
    EMPTY_HEAD("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGU2NDZkODc4NTkxYzU4YzM5ZjJkN2ExZGFmMDFlMTE0NDU0NGQ0NjU5NzdlYmE1YzNmZTc0M2I5ZmNmMTQwIn19fQ=="),

    // locale
    ENGLISH(Head.getHeadByTextures("7d15d566202ac0e76cd897759df5d01c11f991bd46c5c9a04357ea89ee75")),
    RUSSIA("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTZlYWZlZjk4MGQ2MTE3ZGFiZTg5ODJhYzRiNDUwOTg4N2UyYzQ2MjFmNmE4ZmU1YzliNzM1YTgzZDc3NWFkIn19fQ=="),
    WORLD("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjFkZDRmZTRhNDI5YWJkNjY1ZGZkYjNlMjEzMjFkNmVmYTZhNmI1ZTdiOTU2ZGI5YzVkNTljOWVmYWIyNSJ9fX0="),
    UKRAINE("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjhiOWY1MmUzNmFhNWM3Y2FhYTFlN2YyNmVhOTdlMjhmNjM1ZThlYWM5YWVmNzRjZWM5N2Y0NjVmNWE2YjUxIn19fQ=="),

    // settings gui
    SETTINGS_ENABLE("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGIzYmNjMWE2NjhmYjMzOTc1MDczNTk2NzQxMjhhNDJjMjZmMzQ0OTc0MTg5ZWMzMjJhMzNkMTMxNjg0MjhlYSJ9fX0="),
    SETTINGS_DISABLE("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTVkNTNlZjQyOGIzNjlmZDVjY2U5NGNlMjA1ZDBkMmQ3YjA5NWZhZDY3NmE5YjM4Mzk3MWVlMTA0OWUzNjdhZCJ9fX0="),

    SETTINGS_FLY(Head.getHeadByTextures("cbbcec52915a9cfda041e6eebca459026f7c9e1351b9a0939ed22251a4329cc6")),
    SETTINGS_BOSSBAR("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzdmZDgxMThmMDc4MjhjZjdkOTM5MWM1YTAyMTRhYTI1YWYyNGM5OGU0NTAzZTBiMmZjYzhlZmRkMzE4OWJiMCJ9fX0="),
    SETTINGS_BOARD("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzY2OWRiYmM2MmNhODkzNDBmZGNjZDQwMTkwMmRmODkxODgyNzY3ZGMyMTMwMmVhOTk3NDMyMmRhMjBlMzc2ZiJ9fX0="),
    SETTINGS_GLOWING("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjZmZWRiNjM3ODJlNmE3OTdjODhjNDliNDExMTk4NmQyODY4N2ZjODVhZTRhNDFkOTUzMDMxNWZlMjQ2N2QifX19"),

    COMPUTER("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTA5Y2RlMWFmYzk1YTQ3NGQyMjI1NTQwOTdlZDZkMzkxZTdjYzdhZTFmMjAyZmRiZmQyZDZkYmM5ODMwOTM3MCJ9fX0="),
    GAME("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzhlZjA2YTRjOTUzMjM2Yzg3NzVhMjAxY2EwMmZlOWRmNmNiYmE4N2Q3NmIyYThiNmExZDBmZGZlMzE4YmRlIn19fQ=="),
    MAILBOX("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGFjYmJjYTU2NzM3MmE5YjJiMzZjOGY2ODE1NDg1MWJkYTVlZTFkNTNlMmJjMjA4YTExNTJkOWExOGQyY2IifX19"),
    CHEST("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmY2OGQ1MDliNWQxNjY5Yjk3MWRkMWQ0ZGYyZTQ3ZTE5YmNiMWIzM2JmMWE3ZmYxZGRhMjliZmM2ZjllYmYifX19"),
    BOTTLE("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDRhYTlhZWFiYWYyNTY4Yjk3YTJlOGNmYTlhNTNiYWNkNGM4ZDg5ZGFkNGJhMzg3ZjZjNGI3NjFhZTA0YTE4In19fQ=="),
    NOTEBLOCK("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGNlZWI3N2Q0ZDI1NzI0YTljYWYyYzdjZGYyZDg4Mzk5YjE0MTdjNmI5ZmY1MjEzNjU5YjY1M2JlNDM3NmUzIn19fQ=="),
    SKELETON("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWNkNzEzYzVmNWU0NmRhNDM2YThmNTRiNTIzZDQzYWYyOWY3YWU4ZmIxODQ3OTJjY2E3M2IxNzE3ZmVhYTYxIn19fQ=="),
    JAKE("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTNkMTg3N2JlOTVhOWVkYjg2ZGYyMjU2ZjIzOTU4MzI0YzJlYzE5ZWY5NDI3N2NlMmZiNWMzMzAxODQxZGMifX19"),
    COIN("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDViYmFhMmIyN2UwZTJkOGJlYjc4ZDRlNmNlYTNhNmM5MjdhMmMxNDMyNTlhOWMzY2M4N2JlZGRmNzhlNDY2In19fQ=="),
    LUCKY("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjNiNzEwYjA4YjUyM2JiYTdlZmJhMDdjNjI5YmEwODk1YWQ2MTEyNmQyNmM4NmJlYjM4NDU2MDNhOTc0MjZjIn19fQ=="),
    BATMAN("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjVmYThkZTk1ZjU3ZjNhNjk1MTEzNzYxZTQzMTdkZmQ4YWJlMTIxYWE4NWIwMjNkYjIxNGVkNGZhMmY4YyJ9fX0="),
    BOOKS("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjMzNTk4NDM3ZTMxMzMyOWViMTQxYTEzZTkyZDliMDM0OWFhYmU1YzY0ODJhNWRkZTdiNzM3NTM2MzRhYmEifX19"),
    PERKS("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTRmMDNhNzQ2YWFiZGMzYWJjNTE1OGVhYTEwNDY2YTlmYTc3MGMyYmIxZDRkYWI3NjQ0ODQzOThlOWY4ZDQifX19"),
    SHOP("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTM2ZTk0ZjZjMzRhMzU0NjVmY2U0YTkwZjJlMjU5NzYzODllYjk3MDlhMTIyNzM1NzRmZjcwZmQ0ZGFhNjg1MiJ9fX0="),

    // symbols#1
    LETTER_A(Head.getHeadByTextures("9c60da2944a177dd08268fbec04e40812d1d929650be66529b1ee5e1e7eca")),
    LETTER_B(Head.getHeadByTextures("8041f5e86983d36eaec4e167b2bbb5a3727607cde88f7555ca1b522a039bb")),
    LETTER_C(Head.getHeadByTextures("d945996c8ae91e376196d4dc676fec31feac790a2f195b2981a703ca1d16cb6")),
    LETTER_D(Head.getHeadByTextures("1641150f481e8492f7128c948996254d2d91fc90f5a8ff4d8ac5c39a6a88a")),
    LETTER_E(Head.getHeadByTextures("db251487ff8eef2ebc7a57dab6e3d9f1db7fc926ddc66fea14afe3dff15a45")),
    LETTER_F(Head.getHeadByTextures("7e433656b443668ed03dac8c442722a2a41221be8bb48e23b35bd8c2e59f63")),
    LETTER_G(Head.getHeadByTextures("995863b73637605feacbb173b77d5e155e65204c78d5c7911f738f28deb60")),
    LETTER_H(Head.getHeadByTextures("3c1d358d927074289cc26bff5b1240746f9f4f0cc46f942f5981c6595f72dd")),
    LETTER_I(Head.getHeadByTextures("8f2295865bda4e47979d36b8a887a75a13b034e6988f78670b64a1e6442c")),
    LETTER_J(Head.getHeadByTextures("e34462b55d7f5823680ad13f2adbd7d1ed46ba5101017ed4b37aeeeb775d")),
    LETTER_K(Head.getHeadByTextures("773325a935c067b6ef227367f62ca4bf49f67adb9f6da32091e2d32c5dde328")),
    LETTER_L(Head.getHeadByTextures("25a1e3328c571aa495d9c5f494815cca176c3acb184feb5a7b9c96ce8e52fce")),
    LETTER_M(Head.getHeadByTextures("d467bf6be95e5c8e9d01977a2f0c487ed5b0de5c87963a2eb15411c442fb2b")),
    LETTER_N(Head.getHeadByTextures("823e434d6395fe7e63492431bdee5782bd5ee5bc8cab7559467bdd1f93b925a")),
    LETTER_O(Head.getHeadByTextures("88445466bdc5ad5bcea82239c4e1b510f6ea5262d82d8a96d7291c342fb89")),
    LETTER_P(Head.getHeadByTextures("f9de601dee3ffeca4d54595f844201d0ed2091acec4548c696bb16a8a158f6")),
    LETTER_Q(Head.getHeadByTextures("66ca769bde25d4cc41e19e42adc35ab4c1557b76af232649acc9967ff198f13")),
    LETTER_R(Head.getHeadByTextures("67a188805162ca5dd4f4649c661d3f6d23c42662aef01645b1a97f78b3f13219")),
    LETTER_S(Head.getHeadByTextures("60d09dfd9f5de6243233e0e3325b6c3479335e7ccf13f2448d4e1f7fc4a0df")),
    LETTER_T(Head.getHeadByTextures("64c75619b91d241f678350ad9237c134c5e08d87d6860741ede306a4ef91")),
    LETTER_U(Head.getHeadByTextures("e9f6d2c6d5285f882ae55d1e91b8f9efdfc9b377208bf4c83f88dd156415e")),
    LETTER_V(Head.getHeadByTextures("dce27a153635f835237d85c6bf74f5b1f2e638c48fee8c83038d0558d41da7")),
    LETTER_W(Head.getHeadByTextures("aedcf4ffcb53b56d42baac9d0dfb118e343462327442dd9b29d49f50a7d38b")),
    LETTER_X(Head.getHeadByTextures("83618ff1217640bec5b525fa2a8e671c75d2a7d7cb2ddc31d79d9d895eab1")),
    LETTER_Y(Head.getHeadByTextures("d9c1d29a38bcf113b7e8c34e148a79f9fe41edf41aa8b1de873bb1d433b3861")),
    LETTER_Z(Head.getHeadByTextures("b9295734195d2c7fa389b98757e9686ce6437c16c58bdf2b4cd538389b5912")),

    // symbols#2
    SYMBOL_0(Head.getHeadByTextures("3769fc56dc8f1acaaa2f1c598b4b6269f16e58eb1171c20783a86c23454abe7")),
    SYMBOL_1(Head.getHeadByTextures("6148891f75c6502b7cc48ea3d1f58752da4fe389efe7324f2b4446aaf2a823")),
    SYMBOL_2(Head.getHeadByTextures("e8ed5fff389cf8151b2bebac8513bccc58ec10a4fe3819c35efb340b8f2")),
    SYMBOL_3(Head.getHeadByTextures("3bf03e919cd7ac69ac214baffad998a5bd4b1d11ede333f1ce92d6da417b5c4")),
    SYMBOL_4(Head.getHeadByTextures("c2d164a1a96318cd6471d9a64a48ce02341fb126c3452bdcd4be188f2f973ce")),
    SYMBOL_5(Head.getHeadByTextures("7c5418df281e5bb2e175cc804b9244cdc95914c8d28deded5769f65b7ce02d5")),
    SYMBOL_6(Head.getHeadByTextures("7326af92298364546955377491ad8ac99db40528fbe3cdb817f768a7c9e63b8")),
    SYMBOL_7(Head.getHeadByTextures("a6d97c32a04c5b9f57712873bbade868488acb9ef6d9b5118debd06abec9a2")),
    SYMBOL_8(Head.getHeadByTextures("942e2979d1497c31ee03e19b088684ab155957527411f31b279b2a8dbb96ee")),
    SYMBOL_9(Head.getHeadByTextures("ee5553d1a51dd719c99e8e86d1b9b3901b11889a839ccd7129c1bd128264e1a8")),
    SYMBOL_PLUS(Head.getHeadByTextures("f8c874651f7124d142b707afd33b6c336ce09cea38cdcb5596e76ac757bf")),
    SYMBOL_GRID(Head.getHeadByTextures("b23578a8d6e4b5f2a53ca70d4925716bc8e56839596cc17be726c50252ccd")),
    SYMBOL_QUESTION(Head.getHeadByTextures("8b85be7db31e879ab75216b55414e1f3856f72f29b62d1f6bb945a8251cf7fb")),
    SYMBOL_EXCLAMATION(Head.getHeadByTextures("7a492fff53c47b5ec388aaee56ada7f4c60b65576b4161d66f53b5e63017bd")),

    //colored heads
    DARK_RED_COLOR("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjQzOWE3MTk1N2NjMWRkNDU0NmY0MTMyOTY1ZWZmYmUzNDI5Yzg4OWIxYzZiYTcyMTJjYjRiOGMwMzU0MGY4In19fQ=="),
    RED_COLOR("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZkZTNiZmNlMmQ4Y2I3MjRkZTg1NTZlNWVjMjFiN2YxNWY1ODQ2ODRhYjc4NTIxNGFkZDE2NGJlNzYyNGIifX19"),
    GOLD_COLOR("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTdjMmQ1ZWVlODRiYmExZDdlOTRmOTMzYTBhNTU2ZWQ3ZWE0ZTRmYTY1ZThlOWY1NjMyNTgxM2IifX19"),
    YELLOW_COLOR("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTQzYzc5Y2Q5YzJkMzE4N2VhMDMyNDVmZTIxMjhlMGQyYWJiZTc5NDUyMTRiYzU4MzRkZmE0MDNjMTM0ZTI3In19fQ=="),
    DARK_GREEN_COLOR("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmM5ZTYwMWVkOTE5OGRiYjM0YzUxZGRmMzIzOTI5ZjAxYTVmOTU4YWIxMTEzM2UzZTA0MDdiNjk4MzkzYjNmIn19fQ=="),
    GREEN_COLOR("=="), //todo
    AQUA_COLOR("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMDdjNzhmM2VlNzgzZmVlY2QyNjkyZWJhNTQ4NTFkYTVjNDMyMzA1NWViZDJmNjgzY2QzZTgzMDJmZWE3YyJ9fX0="),
    DARK_AQUA_COLOR("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTViOWE0ODQ2N2YwMjEyYWE2ODg2NGU2MzQyMTE2ZjhmNzlhMjc1NDU0YmYyMTVmNjdmNzAxYTZmMmM4MTgifX19"),
    DARK_BLUE_COLOR("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmE0NjA1MzAxMmM2OGYyODlhYmNmYjE3YWI4MDQyZDVhZmJhOTVkY2FhOTljOTljMWUwMzYwODg2ZDM1In19fQ=="),
    BLUE_COLOR("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjhhZmExNTU1ZTlmODc2NDgxZTNjNDI5OWVjNmU5MWQyMmI0MDc1ZTY3ZTU4ZWY4MGRjZDE5MGFjZTY1MTlmIn19fQ=="),
    PINK_COLOR("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmMxYjRjYjdmNmYxNjEyMjNiZjBiM2U2YjNkOGFhZDRjMTBhYmIyNGJhNTI1NGYwZWY0MDBjOTY3NGY5YTYzIn19fQ=="),
    PURPLE_COLOR("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTQ4NzJjOGY3YTZjZWY3OTg2NDc2OWYwY2ZmNzFlZjIzZjU2NTY1NjdkZTAwMDFhZWYzNmM2YjYxNjJhYjAyZCJ9fX0="),
    WHITE_COLOR("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzY2YTVjOTg5MjhmYTVkNGI1ZDViOGVmYjQ5MDE1NWI0ZGRhMzk1NmJjYWE5MzcxMTc3ODE0NTMyY2ZjIn19fQ=="),
    GRAY_COLOR("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmQzY2ZjMjM5MDA2YjI1N2I4YjIwZjg1YTdiZjQyMDI2YzRhZGEwODRjMTQ0OGQwNGUwYzQwNmNlOGEyZWEzMSJ9fX0="),
    DARK_GRAY_COLOR("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjliYTdmZWY2YTFhOGJkODk5YWJhZTRhNWI1NGNiMGVjZTUzYmFkYzY3N2MxNjY4YmVlMGE0NjIxYTgifX19"),
    BLACK_COLOR("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTY3YTJmMjE4YTZlNmUzOGYyYjU0NWY2YzE3NzMzZjRlZjliYmIyODhlNzU0MDI5NDljMDUyMTg5ZWUifX19"),

    EGG_ORB_RAINBOW("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTNmOWVlZGEzYmEyM2ZlMTQyM2M0MDM2ZTdkZDBhNzQ0NjFkZmY5NmJhZGM1YjJmMmI5ZmFhN2NjMTZmMzgyZiJ9fX0="),
    EGG_ORB_YELLOW("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzNkMTQ1NjFiYmQwNjNmNzA0MjRhOGFmY2MzN2JmZTljNzQ1NjJlYTM2ZjdiZmEzZjIzMjA2ODMwYzY0ZmFmMSJ9fX0="),
    EGG_ORB_RED("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWRiNWNlMGQ0NGMzZTgxMzhkYzJlN2U1MmMyODk3YmI4NzhlMWRiYzIyMGQ3MDY4OWM3YjZiMThkMzE3NWUwZiJ9fX0="),
    EGG_ORB_AQUA("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTgwNTU3ZmU2M2YwNmI0YzcwZTJjNWViMmVmNmMwZDhkNWI1NWZkMDljYzVkZTNiY2I2NWY4MzJlMDVkNGMyZSJ9fX0="),
    EGG_ORB_GREEN("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjQxNDQ5MDk3YjRiNzlhOWY2Y2FmNjM0NDQxOGYyMDM0ZGU0YmI5NzFmZWI3YThlNGFhY2JmYjkwNWFjZGNlZiJ9fX0="),
    EGG_ORB_PINK("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTllY2JkY2I1NTQxMjNmYTRkMzE4NzY1MzhiYzdmYjI0NzQ5NGFlYTMyNWJkMjY1OTU2OTQ1MDVhZWJkMTBlZCJ9fX0="),
    EGG_ORB_WHITE("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmFkYzRhMDI0NzE4ZDQwMWVlYWU5ZTk1YjNjOTI3NjdmOTE2ZjMyM2M5ZTgzNjQ5YWQxNWM5MjY1ZWU1MDkyZiJ9fX0="),
    EGG_ORB_BLUE("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTY3OTliZmFhM2EyYzYzYWQ4NWRkMzc4ZTY2ZDU3ZDlhOTdhM2Y4NmQwZDlmNjgzYzQ5ODYzMmY0ZjVjIn19fQ=="),

    ORB_PINK("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTVhZTYxODAyYjJhYWQ3NmI4YjIwMWFlNGI0ZWZhODYxNjEyM2VkMjNlYmE3NWE1Y2QzN2Q2Y2ExZGEzZDFlYiJ9fX0="),
    ORB_RED("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGY3YTlkY2RkMWI5MDE4NzE5ZjI5N2I0MmU0NWI5MjViZjliMjg2ZWJiNTNmNjU0YjIxYmI1YTk4ZDhiZWNiNCJ9fX0="),
    ORB_AQUA("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmNmOWJmZDIxMmIwOTk2NDEzMmQxNDAzNzc0ZTdmMWJmODljNDZiYjU2ODc5NmE4OGY5NDZkODYyYWJhYmIwOSJ9fX0="),
    ORB_GREEN("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTMxYTRmYWIyZjg3ZGI1NDMzMDEzNjUxN2I0NTNhYWNiOWQ3YzBmZTc4NDMwMDcwOWU5YjEwOWNiYzUxNGYwMCJ9fX0="),
    ORB_BLUE("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTAxZTA0MGNiMDFjZjJjY2U0NDI4MzU4YWUzMWQyZTI2NjIwN2M0N2NiM2FkMTM5NzA5YzYyMDEzMGRjOGFkNCJ9fX0="),

    BED_RED("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmZiMjkwYTEzZGY4ODI2N2VhNWY1ZmNmNzk2YjYxNTdmZjY0Y2NlZTVjZDM5ZDQ2OTcyNDU5MWJhYmVlZDFmNiJ9fX0="),

    KITS_HEAD("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmIyZjAwMDBjOTYzYmVkYTkzYTdiODdlNjhkZTkzYTllZDhjYjhhZjViMGU2MzkyMjVkMWQwYjQyY2VkZTc3MSJ9fX0="),
    AXOLOTL_COMPUTER("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjQ5YWYzNDQwZjdmYzIxNzM2MDY3OWZkZmZlYzViNWRhM2I0Mzk0NGYyZmQ5MTBmNDI3NTJlYTBmOWY3M2U0In19fQ"),
    SUN_1("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjdkODQ1YTM1YTU2Njg0YTc0MmZlYzMzOWY1MDI3MmNjMTQ1YmVlMzYzYmVjODFhODBkNmVmMWY3NDhlYTYxYiJ9fX0="),


    ;

    private final ItemStack head;

    Head(ItemStack itemStackTexture) {
        this.head = itemStackTexture;
    }

    Head(String value) {
        this.head = Head.getHeadByValue(value);
    }

    public ItemStack getHead() {
        return head.clone();
    }

    public static ItemStack getHeadBySkin(Skin skin) {
        return getHeadByValue(skin.getValue());
    }

    public static ItemStack getHeadByPlayerName(String playerName) {
        ItemStack itemStack;
        if (SVersionUtil.is1_12()) {
            itemStack = new ItemStack(Material.getMaterial("SKULL_ITEM"), 1, (short)3);
        } else {
            itemStack = new ItemStack(Material.getMaterial("PLAYER_HEAD"), 1);
        }
        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
        skullMeta.setOwner(playerName);
        itemStack.setItemMeta(skullMeta);
        return itemStack;
    }

    public static ItemStack getHeadByTextures(String signature) {
        String texture = "http://textures.minecraft.net/texture/" + signature;
        ItemStack item;
        if (SVersionUtil.is1_12()) {
            item = new ItemStack(Material.getMaterial("SKULL_ITEM"), 1, (short) SkullType.PLAYER.ordinal());
        } else {
            item = new ItemStack(Material.getMaterial("PLAYER_HEAD"), 1);
        }
        SkullMeta skullMeta = (SkullMeta)item.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        byte[] encodedData = Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", texture).getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));

        setProfile(skullMeta, profile);
        item.setItemMeta(skullMeta);

        return item;
    }

    public static ItemStack getHeadByValue(String value) {
        ItemStack head;
        if (SVersionUtil.is1_12()) {
            head = new ItemStack(Material.getMaterial("SKULL_ITEM"), 1, (short)3);
        } else {
            head = new ItemStack(Material.getMaterial("PLAYER_HEAD"));
        }

        if (value == null || value.isEmpty()) {
            return head;
        }

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", value));
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();

        setProfile(skullMeta, profile);
        head.setItemMeta(skullMeta);

        return head;
    }

    public static void setProfile(SkullMeta skullMeta, GameProfile gameProfile) {
        try {
            Field field = Class.forName("org.bukkit.craftbukkit." + SVersionUtil.SERVER_VERSION + ".inventory.CraftMetaSkull")
                    .getDeclaredField("profile");
            field.setAccessible(true);
            field.set(skullMeta, gameProfile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setProfile(Skull skull, GameProfile gameProfile) {
        try {
            Field field = Class.forName("org.bukkit.craftbukkit." + SVersionUtil.SERVER_VERSION + ".block.CraftSkull")
                    .getDeclaredField("profile");
            field.setAccessible(true);
            field.set(skull, gameProfile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}