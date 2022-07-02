package pw.novit.nekocloud.bungee;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class IpProtect {

    HashMap<String, String> protect = new HashMap<>();

    IpProtect(final @NotNull NekoBungeeAPI nekoBungeeAPI) {
        nekoBungeeAPI.getConfig().getStringList("protection").forEach(protect -> {
            val name = protect.split(";")[0];
            val ip = protect.split(";")[1];

            this.protect.put(name.toLowerCase(), ip);
        });
    }
}