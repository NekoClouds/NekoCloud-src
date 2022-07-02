package pw.novit.nekocloud.bungee.api.event.gamer;

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

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(level = PRIVATE, makeFinal = true)
@Getter @Setter
@ToString
public final class AsyncGamerLoginEvent extends AsyncEvent<AsyncGamerLoginEvent> implements Cancellable {
    BungeeGamer gamer;
    PendingConnection connection;

    @NonFinal boolean cancelled;
    @NonFinal BaseComponent cancelReason;

    public AsyncGamerLoginEvent(
            final BungeeGamer gamer,
            final PendingConnection connection,
            final Callback<AsyncGamerLoginEvent> done
    ) {
        super(done);
        this.gamer = gamer;
        this.connection = connection;
    }

}
