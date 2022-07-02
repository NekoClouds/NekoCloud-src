package me.nekocloud.base.gamer.constans;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import me.nekocloud.base.locale.Language;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
@AllArgsConstructor
public class JoinMessage {

    int id;
    String message;
    int priceGold;
    final boolean neko;

    public String getMessage() {
        if (message == null) {
            return Language.DEFAULT.getMessage("JOIN_PLAYER_LO_LOBBY");
        }

        return " §3§l⇒ §f" + message;
    }

    public boolean can() {
        return neko || priceGold > 0;
    }

    static Int2ObjectMap<JoinMessage> MESSAGES = Int2ObjectMaps.synchronize(new Int2ObjectOpenHashMap<>());

    public static JoinMessage DEFAULT_MESSAGE = new JoinMessage(1, null, 0, true);

    public static List<JoinMessage> getMessages() {
        return MESSAGES.values().stream()
                .sorted(Comparator.comparingInt(JoinMessage::getId))
                .collect(Collectors.toList());
    }

    public static JoinMessage getMessage(int id) {
        JoinMessage message = MESSAGES.get(id);
        if (message != null) {
            return message;
        }

        return DEFAULT_MESSAGE;
    }


    private static void addMessage(int id, String key, int priceGold, boolean neko) {
        MESSAGES.put(id, new JoinMessage(id, key, (priceGold > 0 ? priceGold : 0), neko));
    }

    static {
        init();
    }

    private static void init() {
        MESSAGES.put(DEFAULT_MESSAGE.id, DEFAULT_MESSAGE);
        addMessage(2, "%s §6подключился и пошел работать", 6, false);
        addMessage(3, "%s §6решил забежать на чашечку чая", 6, false);
        addMessage(4, "%s §6влетел на сверхзвуковой скорости", 6, true);
        addMessage(5, "%s §6эпично появился из дымовой завесы", 6, false);
        addMessage(6, "%s §6чрезвычайно силён, вам с ним не справиться", 6, false);
        addMessage(7, "%s §6только что подключился. Кто-нибудь покормите его!", 6, false);
        addMessage(8, "%s §6заскочил на сервер. Кенгуруу!", 6, false);
        addMessage(9, "%s §6присоединился к нашей вечеринке", 6, false);
        addMessage(10, "%s §6а вот и ты, пиццу принес?", 6, false);
        addMessage(11, "%s §6только что подключился. Прячьте свои бананы", 6, false);
        addMessage(12, "%s §6только что подключился. Все сделайте вид, что заняты!", 6, false);
        addMessage(13, "%s §6где же ты? Ах, вот же!", 6, false);
        addMessage(14, "%s §6не его ли вы ищете?", 6, false);
        addMessage(15, "%s §6эй, народ! Он уже здесь!", 6, true);
        addMessage(16, "%s §6добро пожаловать, мы уже заждались...", 6, false);
        addMessage(17, "%s §6показался на горизонте. Подержите мое пиво", 6, false);
        addMessage(18, "%s §6добро пожаловать, располагайся и слушай", 6, false);
        addMessage(19, "%s §6залез в окно и крикнул: олды здесь?!", 6, false);
        addMessage(20, "%s §6подкрался! Он что, мышь?", 6, false);
        addMessage(21, "%s §6заряжен и готов к бою!", 6, false);
        addMessage(22, "%s §6он уже тут, подержите мой топ!", 6, false);
        addMessage(23, "%s §6зашел на сервер, спасайте ваших питомцев", 6, false);
        addMessage(24, "%s §6херобрин?! Нет, он еще ужаснее", 6, false);
        addMessage(25, "%s §6заходи, мы ждем только тебя!", 6, true);
        addMessage(26, "%s §6решил нас посетить", 6, true);
        addMessage(27, "%s §6кого-кого, а твоего визита мы не ждали", 6, false);
        addMessage(28, "%s §6плейбой, филантроп и просто красавчик", 6, false);
        addMessage(29, "%s §6зашел к нам на огонек", 6, false);
        addMessage(30, "%s §6медленно входит на сервер", 6, false);
        addMessage(31, "%s §6снова с нами. Ура!", 6, true);
        addMessage(32, "%s §6вернулся из IRL. Наконец-то!", 6, false);
        addMessage(33, "%s §6самый опасный тип на диком западе, осторожно!", 6, false);
        addMessage(34, "%s §6прокричал: смотрите, ИСПААААНЦЫЫЫ!", 6, false);
        addMessage(35, "%s §6притворился пожилой старушкой", 6, false);
        addMessage(36, "%s §6я енотик полоскун, полоскаю свой...", 6, false);
        addMessage(37, "%s §6пришел устроить нереальный флекс!", 6, false);
        addMessage(38, "%s §6материализовался. А ну не колдовать вне Хогвартса!", 6, false);
        addMessage(39, "%s §6просто мимо проходил... Крокодил... ", 6, false);
        addMessage(40, "%s §6упал с луны. Как он выжил? ", 6, false);
        addMessage(41, "%s §6тут. Тут вам это не там", 6, false);
        addMessage(42, "%s §6оглянулся посмотреть, не оглянулась ли она.. ", 6, false);
        addMessage(43, "%s§6. Как тебе такое, Илон Маск? ", 6, false);
        addMessage(44, "%s §6пришел. Кина не будет", 6, false);
        addMessage(45, "%s §6влетел в игру. Дерзко, резко, апасно", 6, false);
        addMessage(46, "%s §6прямиком из космоса. Осторожно, горячо!", 6, false);
        addMessage(47, "%s §6снят! Прекрасное фото", 6, false);
        addMessage(48, "%s §6зашёл на сервер и пошёл на репорты!", 6, false);
        addMessage(49, "%s §6покушал и зашел на сервер!", 6, true);
        addMessage(50, "%s §6вступил в наши ряды, теперь мы сможем захватить весь мир!", 6, false);
        addMessage(51, "%s §6выглянул из тени, бегите!", 6, true);
        addMessage(52, "%s §6причалил к берегу", 6, true);
        addMessage(53, "%s §6всполошил всю округу!", 6, false);
        addMessage(54, "%s §6выделился из толпы, вау!", 6, false);
        addMessage(55, "%s §6забежал за кофейком", 6, true);
        addMessage(56, "%s §6заехал проведать крестьян", 6, false);
        addMessage(57, "%s §6замечен. Всем занять свои позиции!", 6, false);
        addMessage(58, "%s §6конечно, тебя никто не заметит... ", 6, false);
        addMessage(59, "%s §6- на шаг впереди!", 6, true);
        addMessage(60, "%s §6подключился. Наши молитвы были услышаны!", 6, false);
        addMessage(61, "%s §6засекречен. Всем спрятаться!", 6, false);
        addMessage(62, "%s §6его боялись даже чеченцы... ", 6, false);
        addMessage(63, "%s §6пришёл со школы и снял ботинки ", 6, false);
        addMessage(64, "%s §6выжил после 1-ой минуты игры ", 6, false);
        addMessage(65, "%s §6Не успел поесть и пришёл. Всем бы так... ", 6, false);
        addMessage(66, "%s §6выделил время для нас. Поприветствуем!", 6, false);
    }


}
