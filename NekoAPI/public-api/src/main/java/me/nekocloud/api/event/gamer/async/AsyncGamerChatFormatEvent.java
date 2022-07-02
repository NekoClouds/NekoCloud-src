package me.nekocloud.api.event.gamer.async;

import lombok.Getter;
import lombok.Setter;
import me.nekocloud.api.event.gamer.GamerEvent;
import me.nekocloud.api.player.BukkitGamer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Getter
public class AsyncGamerChatFormatEvent extends GamerEvent {

    @Setter
    private String message;

    private final String baseFormat;

    private final Map<BukkitGamer, String> recipients = new HashMap<>();

    public AsyncGamerChatFormatEvent(BukkitGamer sender, Set<BukkitGamer> recipients, String baseFormat, String message) {
        super(sender, true);
        this.baseFormat = baseFormat;
        this.message = message;

        for (BukkitGamer recipient : recipients) {
            this.recipients.put(recipient, baseFormat);
        }

        this.recipients.put(sender, baseFormat);
    }

    public String getFormat(BukkitGamer recipient) {
        assertRecipient(recipient);

        return recipients.get(recipient);
    }

    public Set<BukkitGamer> getRecipients() {
        return recipients.keySet();
    }

    public void setFormat(BukkitGamer recipient, String format) {
        assertRecipient(recipient);

        recipients.put(recipient, format);
    }

    public void appendFormat(BukkitGamer recipient, String format) {
        assertRecipient(recipient);

        String recipientFormat = getFormat(recipient);

        recipients.put(recipient, format + recipientFormat);
    }

    public void removeRecipient(BukkitGamer recipient) {
        assertRecipient(recipient);

        recipients.remove(recipient);
    }

    public void removeRecipients() {
        recipients.clear();
    }

    public void addRecipient(BukkitGamer recipient) {
        recipients.put(recipient, baseFormat);
    }

    public void addRecipients(Collection<BukkitGamer> recipients) {
        for (BukkitGamer recipient : recipients) {
            this.recipients.put(recipient, baseFormat);
        }
    }

    private void assertRecipient(BukkitGamer recipient) {
        assert recipients.containsKey(recipient) : "Recipients does not contain this player";
    }

}
