package pw.novit.nekocloud.bungee.api.event.player;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.AsyncEvent;
import net.md_5.bungee.api.plugin.Cancellable;

import static lombok.AccessLevel.PRIVATE;

@Getter @Setter
@FieldDefaults(level = PRIVATE, makeFinal = true)
public final class DPreLoginEvent extends AsyncEvent<DPreLoginEvent> implements Cancellable {
	String name;
	PendingConnection connection;

	@NonFinal boolean cancelled;
	@NonFinal BaseComponent cancelReason;

	public DPreLoginEvent(
			final String name,
			final PendingConnection connection,
			final Callback<DPreLoginEvent> done
	) {
		super(done);
		this.name = name;
		this.connection = connection;
	}
}
