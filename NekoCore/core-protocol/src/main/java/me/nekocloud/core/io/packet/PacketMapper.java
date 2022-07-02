package me.nekocloud.core.io.packet;

import io.netty.buffer.ByteBuf;
import lombok.SneakyThrows;
import me.nekocloud.core.io.packet.exception.LowIQPacketException;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PacketMapper {

    private static final Logger LOGGER = Logger.getGlobal();

    private final Map<Integer, Class<? extends DefinedPacket>> idToPacket = new HashMap<>();
    private final Map<Class<? extends DefinedPacket>, Integer> packetToId = new HashMap<>();

    private boolean registerPacket0(int id, Class<? extends DefinedPacket> clazz) {
        if (idToPacket.containsKey(id))
            return false;

        idToPacket.put(id, clazz);
        packetToId.put(clazz, id);
        return true;
    }

    public void registerPacket(int id, Class<? extends DefinedPacket> clazz) {
        if (registerPacket0(id, clazz)) {
            LOGGER.log(Level.INFO, "[Protocol] Registered {0} packet with {1} ID.", new Object[] { clazz.getSimpleName(), id });
        } else {
            LOGGER.log(Level.INFO, "[Protocol] Packet with {0} ID already registered!", id);
        }
    }

    private boolean unregisterPacket0(int id) {
        Class<? extends DefinedPacket> packet = idToPacket.remove(id);
        packetToId.remove(packet);
        return packet != null;
    }

    public void unregisterPacket(int id) {
        if (unregisterPacket0(id)) {
            LOGGER.log(Level.INFO, "[Protocol] Unregistered packet with {1} ID.", id);
        } else {
            LOGGER.log(Level.WARNING, "[Protocol] Packet with {0} ID not registered!", id);
        }
    }
    public Class<? extends DefinedPacket> getPacketClass(int id) {
        return idToPacket.get(id);
    }

    public int getPacketId(Class<? extends DefinedPacket> clazz) {
        return packetToId.get(clazz);
    }

    public DefinedPacket readPacket(final @NotNull ByteBuf buf) throws LowIQPacketException {
        int id = buf.readInt();
        Class<? extends DefinedPacket> clazz = getPacketClass(id);
        if (clazz == null) {
            buf.skipBytes(buf.writableBytes());
            throw new LowIQPacketException("Bad packet ID: " + id);
        }
        try {
            DefinedPacket packet = clazz.newInstance();
            packet.read(buf);
            if (buf.writableBytes() > 0) {
                buf.skipBytes(buf.writableBytes());
            }
            return packet;
        }
        catch (Exception ex) {
            buf.skipBytes(buf.writableBytes());
            ex.printStackTrace();
            return null;
        }
    }

    @SneakyThrows
    public void writePacket(@NotNull DefinedPacket packet, @NotNull ByteBuf buf) {
        buf.writeInt(getPacketId(packet.getClass()));
        packet.write(buf);
    }
}
