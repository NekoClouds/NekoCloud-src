package me.nekocloud.party.core.command;

import com.google.common.base.Joiner;
import lombok.val;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.chat.ChatMessageType;
import me.nekocloud.core.api.chat.JsonChatMessage;
import me.nekocloud.core.api.chat.event.ClickEvent;
import me.nekocloud.core.api.chat.event.HoverEvent;
import me.nekocloud.core.api.command.CommandExecutor;
import me.nekocloud.core.api.command.CommandSender;
import me.nekocloud.core.api.connection.player.CorePlayer;
import me.nekocloud.party.core.type.Party;
import me.nekocloud.party.core.type.PartyManager;
import me.nekocloud.party.core.type.PartyRequestManager;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.Collectors;

// говнокод
public class PartyCommand extends CommandExecutor {

    public PartyCommand() {
        super("party", "пати", "парти", "p");

        setOnlyAuthorized(true);
        setOnlyPlayers(true);
    }

    @Override
    protected void execute(@NotNull CommandSender sender, String[] args) {
        val player = (CorePlayer) sender;

        if (args.length == 0) {
            sendHelpMessage(player);
            return;
        }

        switch (args[0].toLowerCase()) {
            case "inv", "invite" -> {
                if (args.length < 2) {
                    notEnoughArguments(sender, "PARTY_PREFIX", "PARTY_INVITE_FORMAT");
                    break;
                }

                val targetPlayer = NekoCore.getInstance().getPlayer(args[1]);
                if (targetPlayer == null) {
                    playerOffline(sender, args[1]);
                    break;
                }

                if (targetPlayer.getName().equals(player.getName())) {
                    player.sendMessage("§cВы не можете добавить в компанию самого себя!");
                    break;
                }

                Party party = PartyManager.INSTANCE.getParty(player) == null
                        ? PartyManager.INSTANCE.createParty(player) : PartyManager.INSTANCE.getParty(player);

                if (party.isMember(targetPlayer)) {
                    player.sendMessage("§cДанный игрок уже состоит в Вашей компании!");
                    return;
                }

                if (PartyRequestManager.INSTANCE.hasPartyRequest(player.getPlayerID(), targetPlayer.getPlayerID())) {
                    player.sendMessage("§d§lКОМПАНИЯ §8| §fПриглашение в компанию было §dуспешно §fотправлено игроку " + targetPlayer.getDisplayName());
                    targetPlayer.sendMessage(ChatMessageType.CHAT,
                            JsonChatMessage.create(("§d§lКОМПАНИЯ §8| " +
                                            player.getDisplayName() + " §fпригласил Тебя в кооперативную игру: "))
                                    .addComponents(JsonChatMessage.create("    ").build())
                                    .addComponents(JsonChatMessage.create("§a§l[ПРИНЯТЬ]")
                                            .addClick(ClickEvent.Action.RUN_COMMAND, "/party accept " + player.getName())
                                            .addHover(HoverEvent.Action.SHOW_TEXT, "§aПринять приглашение в кооперативную игру с "
                                                    + player.getName())
                                            .build()).addComponents(JsonChatMessage.create("    ")
                                            .build()).addComponents(JsonChatMessage.create("§c§l[ОТКЛОНИТЬ]")
                                            .addClick(ClickEvent.Action.RUN_COMMAND, "/party cancel " + player.getName())
                                            .addHover(HoverEvent.Action.SHOW_TEXT, "§cОтклонить приглашение в кооперативную игру с "
                                                    + player.getName()).build()).addText(" ").build());
                    break;
                }
            }
            case "join", "accept" -> {
                if (args.length < 2) {
                    notEnoughArguments(sender, "PARTY_PREFIX", "PARTY_ACCEPT_FORMAT");
                    break;
                }

                CorePlayer targetPlayer = NekoCore.getInstance().getOfflinePlayer(args[1]);
                if (targetPlayer == null) {
                    playerOffline(sender, args[1]);
                    break;
                }

                if (targetPlayer.getName().equals(player.getName())) {
                    player.sendMessage("§d§lКОМПАНИЯ §8| §fОшибка, ты не можешь добавить в компанию самого себя!");
                    break;
                }
                if (PartyManager.INSTANCE.hasParty(player)) {
                    player.sendMessage("§d§lКОМПАНИЯ §8| §fОшибка, ты уже состоишь в другой компании!");
                    break;
                }
                if (!PartyRequestManager.INSTANCE.hasPartyRequest(targetPlayer.getPlayerID(), player.getPlayerID())) {
                    player.sendMessage("§d§lКОМПАНИЯ §8| §fОшибка, у тебя нет приглашения в компанию от данного игрока!");
                    break;
                }
                PartyRequestManager.INSTANCE.removeAll(player.getPlayerID());
                val party = PartyManager.INSTANCE.getParty(targetPlayer) == null ? PartyManager.INSTANCE.createParty(targetPlayer) : PartyManager.INSTANCE.getParty(targetPlayer);

                party.addMember(player);
                party.alertLocale("PARTY_JOIN_ALERT", player.getDisplayName(), targetPlayer.getDisplayName());
            }

            case "can", "cancel" -> {
                if (args.length < 2) {
                    notEnoughArguments(sender, "PARTY_PREFIX", "PARTY_CANCEL_FORMAT");
                    break;
                }

                val targetPlayer = NekoCore.getInstance().getPlayer(args[1]);
                if (targetPlayer == null) {
                    playerOffline(sender, args[1]);
                    break;
                }

                if (targetPlayer.getName().equals(player.getName())) {
                    player.sendMessage("§d§lКОМПАНИЯ §8| §fОшибка, ты не можешь добавить в компанию самого себя!");
                    break;
                }

                if (!PartyRequestManager.INSTANCE.hasPartyRequest(targetPlayer.getPlayerID(), player.getPlayerID())) {
                    player.sendMessage("§d§lКОМАНИЯ §8| §fОшибка, у тебя нет приглашения в компанию от данного игрока!");
                    break;
                }

                PartyRequestManager.INSTANCE.removeAll(player.getPlayerID());
                targetPlayer.sendMessage("§d§lКОМАНИЯ §8| " + player.getDisplayName() + " §cотклонил §fтвоё приглашение!");

                player.sendMessage("§d§lКОМАНИЯ §8| §fТы успешно §cотклонил §fприглашение от игрока " + targetPlayer.getDisplayName());
                PartyRequestManager.INSTANCE.removePartyRequest(player.getPlayerID(), targetPlayer.getPlayerID());
            }

            case "kick" -> {
                if (args.length < 2) {
                    notEnoughArguments(sender, "PARTY_PREFIX", "PARTY_CANCEL_FORMAT");
                    break;
                }

                if (!PartyManager.INSTANCE.hasParty(player)) {
                    player.sendMessageLocale("PARTY_NOT_IN_PARTY");
                    break;
                }

                val targetPlayer = NekoCore.getInstance().getOfflinePlayer(args[1]);
                if (targetPlayer == null) {
                    playerOffline(sender, args[1]);
                    break;
                }

                val party = PartyManager.INSTANCE.getParty(player);
                if (!party.isLeader(player)) {
                    player.sendMessage("§d§lКОМПАНИЯ §8| §fОшибка, ты не являешься лидером компании!");
                    return;
                }

                if (!party.isMember(targetPlayer)) {
                    player.sendMessage("§d§lКОМПАНИЯ §8| §fОшибка, данный игрок не состоит в Твоей компании!");
                    break;
                }

                if (targetPlayer.getName().equals(player.getName())) {
                    player.sendMessageLocale("PARTY_CANT_KICK_YOURSELF");
                    break;
                }

                party.alert("§d§lКОМПАНИЯ §8| " + targetPlayer.getDisplayName() +
                        " §fбыл кикнут из компании лидером " + player.getDisplayName() + "!");
                party.removeMember(targetPlayer);
                if (party.getMembers().size() > 1) break;
                party.alert("§d§lКОМПАНИЯ §8| §fКомпания была расформирована потому как все ее участники вышли!");
                PartyManager.INSTANCE.deleteParty(party);
            }
            case "warp" -> {
                val party = PartyManager.INSTANCE.getParty(player);
                if (party == null) {
                    player.sendMessageLocale("PARTY_NOT_IN_PARTY");
                    break;
                }

                if (!party.isLeader(player)) {
                    player.sendMessage("§d§lКОМПАНИЯ §8| §fОшибка, ты не являешься лидером компании!");
                    break;
                }

                party.warp(player.getBukkit());
                party.alert("§d§lКОМПАНИЯ §8| §fЛидер " + player.getDisplayName() + " §fпереместил Тебя на сервер §e" + player.getBukkit().getName());
            }
            case "list" -> {
                val party = PartyManager.INSTANCE.getParty(player);
                if (party == null) {
                    player.sendMessageLocale("PARTY_NOT_IN_PARTY");
                    break;
                }

                player.sendMessagesLocale("PARTY_LIST");
                for (val partyMember : party.getMembers().stream().map(partyPlayer ->
                        (NekoCore.getInstance()).getPlayer(partyPlayer)).collect(Collectors.toSet())) {
                    sender.sendMessage(" §8• " + partyMember.getDisplayName()
                            + " §7(" + partyMember.getBukkit().getName() + ")"
                            + (party.isLeader(partyMember) ? " §8§l| §c§lЛИДЕР" : ""));
                }
            }
            case "leave" -> {
                val party = PartyManager.INSTANCE.getParty(player);
                if (party == null) {
                    player.sendMessageLocale("PARTY_NOT_IN_PARTY");
                    break;
                }

                if (party.isLeader(player)) {
                    player.sendMessage("§d§lКОМАНИЯ §8| §fОшибка, лидер группы не может покинуть ее! Чтобы удалить группы, пиши - /party disband");
                    break;
                }

                player.sendMessagesLocale("");
                party.alertLocale("PARTY_LEAVE_BROADCAST", player.getDisplayName());
                party.removeMember(player);

                if (party.getMembers().size() > 1) break;

                party.alert("§d§lКОМАНИЯ §8| §fКомпания была расформирована потому как все ее участники вышли!");
                PartyManager.INSTANCE.deleteParty(party);
            }
            case "disband" -> {
                val party = PartyManager.INSTANCE.getParty(player);
                if (party == null) {
                    player.sendMessageLocale("PARTY_NOT_IN_PARTY");
                    break;
                }

                if (!party.isLeader(player)) {
                    player.sendMessage("§d§lКОМАНИЯ §8| §fОшибка, ты не являешься лидером компании!");
                    break;
                }

                party.alert("§d§lКОМАНИЯ §8| " + player.getDisplayName() + " §fраспустил компанию!");
                PartyManager.INSTANCE.deleteParty(party);
            }

            case "chat" -> {
                val party = PartyManager.INSTANCE.getParty(player);
                if (party == null) {
                    player.sendMessageLocale("PARTY_NOT_IN_PARTY");
                    break;
                }

                if (args.length < 2) {
                    notEnoughArguments(sender, "PARTY_PREFIX", "PARTY_CHAT_FORMAT");
                    break;
                }

                String chatMessage = Joiner.on(" ").join(Arrays.copyOfRange(args, 1, args.length));
                party.alertLocale("PARTY_CHAT", player.getDisplayName() + player.getGroup().getSuffix(), chatMessage);
            }
            default -> sendHelpMessage(player);
        }
    }

    private void sendHelpMessage(@NotNull CorePlayer player) {
        val party = PartyManager.INSTANCE.getParty(player);

        if (party != null && party.isLeader(player)) {
            player.sendMessagesLocale("PARTY_EMPTY_HELP");
            player.sendMessagesLocale("PARTY_HELP");
            return;
        }

        player.sendMessagesLocale("PARTY_EMPTY_HELP");
    }

}