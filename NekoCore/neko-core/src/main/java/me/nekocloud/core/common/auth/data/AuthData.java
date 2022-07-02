package me.nekocloud.core.common.auth.data;

import io.netty.buffer.ByteBuf;
import lombok.*;
import lombok.experimental.FieldDefaults;
import me.nekocloud.core.common.auth.CoreAuth;
import me.nekocloud.core.io.handler.PacketHandler;
import me.nekocloud.core.io.packet.DefinedPacket;

import static lombok.AccessLevel.PRIVATE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
@EqualsAndHashCode(callSuper = false)
public class AuthData extends DefinedPacket {

	int playerID;
	AuthAction action;

	@Override
	public void read(ByteBuf buf) {
		playerID = readVarInt(buf);
		action = readEnum(AuthAction.class, buf);

		val authPlayer = CoreAuth.getAuthManager().loadOrCreate(playerID);
		switch (action) {
			case WAIT_COMPLETE -> authPlayer.completeDefault();
			case WAIT_2FA_CODE -> authPlayer.completeWith2FACode();
		}
	}

	@Override
	public void write(ByteBuf buf) {
		writeVarInt(playerID, buf);
		writeEnum(action, buf);
	}

	@Override
	public void handle(PacketHandler handler) {}
}
