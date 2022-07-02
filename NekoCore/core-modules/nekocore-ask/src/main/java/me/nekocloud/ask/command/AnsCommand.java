package me.nekocloud.ask.command;


import com.google.common.base.Joiner;
import me.nekocloud.ask.QuestionManager;
import me.nekocloud.ask.type.Question;
import me.nekocloud.ask.type.QuestionCategory;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.core.api.chat.ChatColor;
import me.nekocloud.core.api.command.CommandExecutor;
import me.nekocloud.core.api.command.CommandSender;
import me.nekocloud.core.api.connection.player.CorePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class AnsCommand extends CommandExecutor {

    public AnsCommand() {
        super("answer", "ans");
        setGroup(Group.JUNIOR);
        setOnlyAuthorized(true);
        setOnlyPlayers(true);
    }

    protected void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        CorePlayer player = (CorePlayer) sender;
        if (args.length == 0) {
//            (new AskInventory()).openInventory(player);
            return;
        }
        if (args.length < 3) {
            player.sendMessage("§d§lNeko§f§lCloud §8| §fИспользуйте - §e/ans <игрок> <категория> <ответ>");
            return;
        }
        Question question = QuestionManager.INSTANCE.getPlayerQuestions(args[0], QuestionCategory.getQuestionCategory(args[1].toUpperCase())).stream().findFirst().orElse(null);
        if (question == null) {
            player.sendMessage("§d§lNeko§f§lCloud §8| §cОшибка, вопрос данного игрока в указанной категории не найден!");
            return;
        }
        question.answer(player,
                ChatColor.translateAlternateColorCodes('&', Joiner.on(" ").join(Arrays.copyOfRange((Object[]) args, 2, args.length))));
    }
}
