package me.nekocloud.core.rcon.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.chat.ChatColor;
import me.nekocloud.core.rcon.RconCommandSender;

import java.nio.charset.StandardCharsets;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Log4j2
public final class RconHandler extends SimpleChannelInboundHandler<ByteBuf> {

    static byte FAILURE = -1;
    static byte TYPE_RESPONSE = 0;
    static byte TYPE_COMMAND = 2;
    static byte TYPE_LOGIN = 3;

    @NonFinal boolean loggedIn = false;

    String password;
    RconCommandSender commandSender;

    public RconHandler(String password) {
        this.password = password;
        this.commandSender = new RconCommandSender();
    }

    @Override
    public void channelRead0(final ChannelHandlerContext ctx,
                             final ByteBuf buf
    ) {
        if (buf.readableBytes() < 8)
            return;

        val requestId = buf.readIntLE();
        val type = buf.readIntLE();

        byte[] payloadData = new byte[buf.readableBytes() - 2];

        buf.readBytes(payloadData);
        val payload = new String(payloadData, StandardCharsets.UTF_8);

        buf.readBytes(2); // two byte padding

        if (type == TYPE_LOGIN)
            handleLogin(ctx, payload, requestId);
        else if (type == TYPE_COMMAND)
            handleCommand(ctx, payload, requestId);
        else
            sendLargeResponse(ctx, requestId, "Unknown request " + Integer.toHexString(type));
    }

    private void handleLogin(final ChannelHandlerContext ctx,
                             final String payload,
                             final int requestId
    ) {
        if (password.equals("") || password.equals(payload)) {
            loggedIn = true;

            sendResponse(ctx, requestId, TYPE_COMMAND, "");

            log.info("[RconHandler] Handle connection " + ctx.channel().remoteAddress());
        } else {
            loggedIn = false;
            sendResponse(ctx, FAILURE, TYPE_COMMAND, "");
        }
    }

    private void handleCommand(final ChannelHandlerContext ctx,
                               final String payload,
                               final int requestId
    ) {
        if (!loggedIn) {
            sendResponse(ctx, FAILURE, TYPE_COMMAND, "");
            return;
        }

        if (NekoCore.getInstance().getCommandManager().dispatchCommand(commandSender, payload)) {
            String message = commandSender.flush();
            message = ChatColor.stripColor(message);

            sendLargeResponse(ctx, requestId, message);
        } else {
            String message = ChatColor.RED + "Команда не найдена!";
            message = ChatColor.stripColor(message);

            sendLargeResponse(ctx, requestId, String.format("Ошибка выполнения: %s (%s)",
                    payload, message));
        }
    }

    private void sendResponse(final ChannelHandlerContext ctx,
                              final int requestId,
                              final int type,
                              final String payload
    ) {
        val buf = ctx.alloc().buffer();
        buf.writeIntLE(requestId);
        buf.writeIntLE(type);

        buf.writeBytes(payload.getBytes(StandardCharsets.UTF_8));

        buf.writeByte(0);
        buf.writeByte(0);

        ctx.write(buf);
    }

    private void sendLargeResponse(final ChannelHandlerContext ctx,
                                   final int requestId,
                                   final String payload) {
        if (payload.length() == 0) {
            sendResponse(ctx, requestId, TYPE_RESPONSE, "");
            return;
        }

        int start = 0;
        while (start < payload.length()) {
            final int length = payload.length() - start;
            final int truncated = Math.min(length, 2048);

            sendResponse(ctx, requestId, TYPE_RESPONSE, payload.substring(start, truncated));
            start += truncated;
        }
    }
}