package me.nekocloud.base.gamer.data;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.net.InetAddress;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Getter
public final class JoinData {

    InetAddress lastIp;
    String lastServer;

    long lastOnline;

}

