package me.nekocloud.auth.core;

import io.netty.buffer.ByteBuf;
import lombok.*;
import lombok.experimental.FieldDefaults;
import me.nekocloud.auth.manager.AuthManager;
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

		val manager = new AuthManager();
		val authPlayer = manager.loadOrCreate(playerID);
		switch (action) {
			case WAIT_COMPLETE, WAIT_2FA_CODE -> {}
			case SUCCESS_COMPLETE_DEFAULT -> authPlayer.completeDefault();
			case SUCCESS_COMPLETE_WITH_2FA -> {
				manager.add2FASession(playerID);
				authPlayer.completeDefault();
			}
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
