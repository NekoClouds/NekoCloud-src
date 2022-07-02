package pw.novit.nekocloud.bungee.api.event.gamer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.AsyncEvent;
import net.md_5.bungee.api.plugin.Cancellable;
import pw.novit.nekocloud.bungee.api.gamer.BungeeGamer;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter @Setter
@ToString
public final class AsyncGamerQuitEvent extends AsyncEvent<AsyncGamerQuitEvent> implements Cancellable {
    BungeeGamer gamer;
    PendingConnection connection;

    @NonFinal boolean cancelled;
    @NonFinal BaseComponent cancelReason;

    public AsyncGamerQuitEvent(
            final BungeeGamer gamer,
            final PendingConnection connection,
            final Callback<AsyncGamerQuitEvent> done
    ) {
        super(done);
        this.gamer = gamer;
        this.connection = connection;
    }
}
