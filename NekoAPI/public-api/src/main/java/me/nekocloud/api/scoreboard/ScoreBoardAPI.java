package me.nekocloud.api.scoreboard;

import me.nekocloud.base.gamer.constans.Group;
import org.bukkit.entity.Player;

import java.util.Map;

public interface ScoreBoardAPI {

    /**
     * Создать скорборд(панель справа)
     * по умолчанию скрыта для всех и
     * будет показана, когда будет вызван метод shotwTo
     * @return - вернет Board
     */
    Board createBoard();

    /**
     * Создать Objective(Строка под ником или в табе)
     * по умолчанию скрыта для всех
     * @param name - название Objective
     * @param value - value Objective
     * @return - вернет Objective
     */
    Objective createObjective(String name, String value);

    /**
     * Создать PlayerTag(цветной ник в табе и над головой)
     * по умолчанию не показывается никому и не хранится, удаляется сразу
     * @param name - название DTeam
     * @return - вернет PlayerTag
     */
    PlayerTag createTag(String name);

    /**
     * Создать Objective(Строка под ником или в табе)
     * сердечки(хпшки) и числа в табе
     * @param health - хпшки под ником
     * @param tab - включить цифры в табе
     */
    void createGameObjective(boolean health, boolean tab);

    /**
     * Поставить игроку score в табе
     * @param player - кому ставить
     * @param score - значени
     */
    void setScoreTab(Player player, int score);

    /**
     * Отключить Objective ХП над головой
     * и цифры в табе
     */
    void unregisterObjectives();

    /**
     * вернет список тагов, которые стоят у игроков
     * @return - список тагов
     */
    Map<String, PlayerTag> getActiveDefaultTag();

    void setDefaultTag(Player player, PlayerTag playerTag);
    void removeDefaultTag(Player player);
    void removeDefaultTags();
    void setPrefix(Player player, String prefix); //только для дефолтных тагов
    void setSuffix(Player player, String suffix); //только для дефолтных тагов

    /**
     * удалить борд игроку (если он есть)
     * @param player - игрок
     */
    void removeBoard(Player player);

    /**
     * получить активный борд игрока(если он есть)
     * @param player - чей борд
     * @return - борд
     */
    Board getBoard(Player player);

    /**
     * получить все активные скорборды
     * @return - скорборды
     */
    Map<String, Board> getActiveBoards();

    /**
     * получить приоритет для группы игрока
     * @param group - группа
     * @return - приоритет
     */
    int getPriorityScoreboardTag(Group group);
}
