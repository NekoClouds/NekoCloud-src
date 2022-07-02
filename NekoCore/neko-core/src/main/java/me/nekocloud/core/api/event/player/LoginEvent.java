package me.nekocloud.core.api.event.player;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import me.nekocloud.core.api.connection.server.Bungee;
import me.nekocloud.core.api.event.Cancellable;
import me.nekocloud.core.api.event.Event;

import java.net.InetSocketAddress;

@Getter
@ToString
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LoginEvent extends Event implements Cancellable {

    @Setter @NonFinal
    boolean cancelled;
    @Setter @NonFinal
    String cancelReason;

    String playerName;
    InetSocketAddress virtualHost;
    Bungee bungee;

    public String getCancelReasonJson() {
        return cancelReason == null ? "Причина не указана"
                : cancelReason;
    }
}
