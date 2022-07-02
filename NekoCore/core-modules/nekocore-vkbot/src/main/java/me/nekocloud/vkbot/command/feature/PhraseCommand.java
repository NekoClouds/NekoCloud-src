package me.nekocloud.vkbot.command.feature;

import lombok.SneakyThrows;
import lombok.val;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.chat.ChatColor;
import me.nekocloud.vkbot.api.objects.message.Message;
import me.nekocloud.vkbot.bot.VkBot;
import me.nekocloud.vkbot.command.VkCommand;
import me.nekocloud.vkbot.user.VkUser;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class PhraseCommand extends VkCommand {

    public PhraseCommand() {
        super("фраза", "цитата");

        setGroup(Group.HEGENT);
        setOnlyChats(true);
    }

    @Override
    @SneakyThrows
    protected void execute(VkUser vkUser, @NotNull Message message, @NotNull String[] args, @NotNull VkBot vkBot) {
        if (message.getForwardedMessages().isEmpty()) {
            vkBot.printMessage(message.getPeerId(), "Ошибка, Вы не переслали сообщение с фразой!");
            return;
        }

        val forwardedMessage = message.getForwardedMessages().get(0);
        val bufferedImage = getPhraseImage(forwardedMessage.getUserId(), forwardedMessage);

        new Message()
                .peerId(message.getPeerId())
                .body("Цитата \"" + forwardedMessage.getBody() + "\" была успешно создана!")
                .photos(bufferedImage)
                .send(vkBot);

        File file = new File(".");
        ImageIO.write(bufferedImage, "png", file);

    }


    protected final int MAX_CHARSET_COUNT_IN_LINE = 50;

    protected BufferedImage getPhraseImage(int forwardPeerId, @NotNull Message forwardedMessage) {
        String playerName = "Ноунейм";
        val botUser = VkUser.getVkUser(forwardPeerId);

        if (botUser.hasPrimaryAccount()) {
            playerName = botUser.getPrimaryAccountName();
        }

        val bufferedImage = new BufferedImage(1155, 600, BufferedImage.TYPE_INT_RGB);
//        val userImg = ImageIO.read(forwardedMessage.getAttachments().getById(1));

        val gr = bufferedImage.getGraphics();

        gr.drawImage(bufferedImage, 0, 0, null);
//        gr.drawImage(userImg, 100, 100, null);

        gr.setFont(new Font("Manrope", Font.BOLD, 20));
        gr.drawString("todo user name", 540, 450);
        gr.setColor(Color.DARK_GRAY);

        gr.dispose();

        if (playerName.equals("Ноунейм")) gr.drawString(playerName, 540, 490);
        else gr.drawString(ChatColor.stripColor(NekoCore.getInstance().getOfflinePlayer(playerName).getDisplayName()), 540, 490);

        val textMessage = forwardedMessage.getBody();
        if (textMessage.length() > 18) {

            for (int i = 0 ; i < (textMessage.length() - i * MAX_CHARSET_COUNT_IN_LINE) % MAX_CHARSET_COUNT_IN_LINE; i++) {
                gr.drawString(textMessage.substring(i * MAX_CHARSET_COUNT_IN_LINE), 540, 60);
            }

        } else {
            gr.setColor(Color.WHITE);
            gr.setFont(new Font("Manrope", Font.BOLD, 40));
            gr.drawString("\"" + textMessage + "\"", 540, 130);
        }

        return bufferedImage;
    }

}
