package me.nekocloud.ask.type;


import me.nekocloud.ask.QuestionManager;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.connection.player.CorePlayer;
import org.jetbrains.annotations.NotNull;

public class Question {
    private final String playerName;

    private final String playerQuestion;

    private final QuestionCategory questionCategory;

    private final long questionDate;

    public Question(String playerName, String playerQuestion, QuestionCategory questionCategory, long questionDate) {
        this.playerName = playerName;
        this.playerQuestion = playerQuestion;
        this.questionCategory = questionCategory;
        this.questionDate = questionDate;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getPlayerQuestion() {
        return playerQuestion;
    }

    public QuestionCategory getQuestionCategory() {
        return questionCategory;
    }

    public long getQuestionDate() {
        return this.questionDate;
    }

    public void answer(@NotNull CorePlayer player, @NotNull String playerAnswer) {
        QuestionManager.INSTANCE.removeQuestion(this);
        CorePlayer offlineAuthor = NekoCore.getInstance().getOfflinePlayer(this.playerName);

        player.sendMessage("§6Вопросы §8| §fОтвет на вопрос " + offlineAuthor.getDisplayName() + " был успешно отправлен!");
        offlineAuthor.sendMessage("§6Вопросы §8| §fНа ваш вопрос по категории §e"
                + questionCategory.getCategoryName() +
                " §fответил " + player.getDisplayName()
                + "§f: \n Ответ: §e" + playerAnswer);

        for (CorePlayer staffOnline : NekoCore.getInstance().getOnlinePlayers(playerr -> (!playerr.getName().equalsIgnoreCase(player.getName()) && playerr.isStaff()))) {
            staffOnline.sendMessage("§d§lNeko§f§lCloud §8| " + player.getDisplayName() + " §fответил на вопрос игрока " + offlineAuthor.getDisplayName() + " §fпо категории: §e" + questionCategory.getCategoryName(), " §fОтвет: §e" + playerAnswer);
        }
    }
}
