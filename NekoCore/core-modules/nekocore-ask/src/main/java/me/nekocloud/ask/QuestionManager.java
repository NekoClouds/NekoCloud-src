package me.nekocloud.ask;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import me.nekocloud.ask.type.Question;
import me.nekocloud.ask.type.QuestionCategory;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.connection.player.CorePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.stream.Collectors;

public final class QuestionManager {
    public static final QuestionManager INSTANCE = new QuestionManager();

    public static final int MAXIMUM_QUESTION_COUNT = 1;

    public Multimap<String, Question> getQuestionMap() {
        return this.questionMap;
    }

    private final Multimap<String, Question> questionMap = HashMultimap.create();

    public Question createQuestion(@NotNull CorePlayer player, @NotNull QuestionCategory questionCategory, @NotNull String playerQuestion) {
        Question question = new Question(player.getName(), playerQuestion, questionCategory, System.currentTimeMillis());
        this.questionMap.put(player.getName().toLowerCase(), question);
        return question;
    }

    public boolean canQuestionAccept(@NotNull Question currentQuestion) {
        CorePlayer player = NekoCore.getInstance().getPlayer(currentQuestion.getPlayerName());
        return (getPlayerQuestions(player.getName(), currentQuestion.getQuestionCategory()).size() <= 1);
    }

    public Collection<Question> getPlayerQuestions(@NotNull String playerName, @NotNull QuestionCategory questionCategory) {
        return this.questionMap.get(playerName.toLowerCase()).stream().filter(question -> question.getQuestionCategory().equals(questionCategory))
                .collect(Collectors.toCollection(java.util.ArrayList::new));
    }

    public void removeQuestion(@NotNull Question question) {
        this.questionMap.remove(question.getPlayerName().toLowerCase(), question);
    }

    public Collection<Question> getActiveQuestions(@NotNull QuestionCategory questionCategory) {
        return this.questionMap.values().stream().filter(question -> (NekoCore.getInstance().getPlayer(question.getPlayerName()) != null && question.getQuestionCategory().equals(questionCategory)))
                .collect(Collectors.toCollection(java.util.ArrayList::new));
    }
}
