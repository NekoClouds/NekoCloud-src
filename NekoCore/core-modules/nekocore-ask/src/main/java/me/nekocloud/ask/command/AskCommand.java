package me.nekocloud.ask.command;

import com.google.common.base.Joiner;
import me.nekocloud.ask.QuestionManager;
import me.nekocloud.ask.type.Question;
import me.nekocloud.ask.type.QuestionCategory;
import me.nekocloud.base.gamer.IBaseGamer;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.command.CommandExecutor;
import me.nekocloud.core.api.command.CommandSender;
import me.nekocloud.core.api.connection.player.CorePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;

public class AskCommand extends CommandExecutor {
    public AskCommand() {
        super("question", "ask", "вопрос", "баг", "квестион");
        setOnlyAuthorized(true);
        setOnlyPlayers(true);
    }

    protected void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        CorePlayer player = (CorePlayer) sender;
        if (args.length < 2) {
            player.sendMessage("§d§lNeko§f§lCloud §8| §fИспользуйте - §e/ask <категория> <вопрос>");
            return;
        }
        QuestionCategory questionCategory = QuestionCategory.getQuestionCategory(args[0].toUpperCase());
        String playerQuestion = Joiner.on(" ").join(Arrays.copyOfRange((Object[]) args, 1, args.length));
        if (questionCategory == null) {
            player.sendMessage("§d§lNeko§f§lCloud §8| §сОшибка, данной категории не существует!",
                    "§7Список доступных категорий:",
                    "§сSYSTEM",
                    "§сPERSONAL",
                    "§сCHAT",
                    "§сRULES",
                    "§сDONATE");
            return;
        }
        Question question = QuestionManager.INSTANCE.createQuestion(player, questionCategory, playerQuestion);
        if (!QuestionManager.INSTANCE.canQuestionAccept(question)) {
            player.sendMessage("§d§lNEKOCLOUD §8| §cОшибка, Вы привысили лимит по вопросам за раз: 1");
            QuestionManager.INSTANCE.removeQuestion(question);
            return;
        }
        Collection<CorePlayer> staffOnlineCollection = NekoCore.getInstance().getOnlinePlayers(IBaseGamer::isStaff);
        player.sendMessage("§d§lNEKOCLOUD §8| §fВаш вопрос успешно создан и был послан команде проекта");
        for (CorePlayer staffOnline : staffOnlineCollection)
            staffOnline.sendMessage("§d§lNEKOCLOUD §8| " + player.getDisplayName() + " §fзадал вопрос по категории: " + questionCategory.getCategoryName() + " §7(" + QuestionManager.INSTANCE
                            .getPlayerQuestions(player.getName(), questionCategory).size() + ")" + question.getPlayerQuestion(),
                    "§d§lNEKOCLOUD §8| §fДля проверки напишите §c/ans");

        if (staffOnlineCollection.isEmpty())
            player.sendMessage("§cНа данный момент персонала нет в сети, придется ждать(( :c");
    }
}
