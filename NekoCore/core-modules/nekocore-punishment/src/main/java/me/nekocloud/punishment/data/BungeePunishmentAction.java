package me.nekocloud.punishment.data;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import me.nekocloud.core.io.handler.PacketHandler;
import me.nekocloud.core.io.packet.DefinedPacket;
import me.nekocloud.punishment.PunishmentType;

import static lombok.AccessLevel.PRIVATE;

/**
 * @author xwhilds
 */
public class BungeePunishmentAction {

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@FieldDefaults(level = PRIVATE)
	@EqualsAndHashCode(callSuper = true)
	public static class Issue extends DefinedPacket {
		String ownerName;
		String intruderName;

		String reason;
		PunishmentType type;

		long time;

		@Override
		public void read(final ByteBuf buf) {
			ownerName = readString(buf);
			intruderName = readString(buf);
			reason = readString(buf);
			type = readEnum(PunishmentType.class, buf);
			time = readVarLong(buf);

			// TODO xwhilds:
			switch (type) {
				case PERMANENT_MUTE -> {

				}
				case TEMP_MUTE -> {
				}
			}
		}

		@Override
		public void write(final ByteBuf buf) {
			writeString(ownerName, buf);
			writeString(intruderName, buf);
			writeString(reason, buf);
			writeEnum(type, buf);
			writeVarLong(time, buf);
		}

		@Override
		public void handle(final PacketHandler handler) {}
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@FieldDefaults(level = PRIVATE)
	@EqualsAndHashCode(callSuper = true)
	public static class Clear extends DefinedPacket {

		String intruderName;
		PunishmentType type;

		@Override
		public void read(final ByteBuf buf) {
			intruderName = readString(buf);
			type = readEnum(PunishmentType.class, buf);
		}

		@Override
		public void write(final ByteBuf buf) {
			writeString(intruderName, buf);
			writeEnum(type, buf);
		}

		@Override
		public void handle(final PacketHandler handler) {}
	}
}
