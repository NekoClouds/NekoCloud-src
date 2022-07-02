package me.nekocloud.base.gamer;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.gamer.sections.*;
import me.nekocloud.base.skin.Skin;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Getter
public abstract class IBaseGamerImpl implements IBaseGamer {

    private final Map<String, Object> data      = new HashMap<>();
    private final Map<String, Section> sections = new HashMap<>();

    @Setter
    protected String name;

    private long start; //когда началась загрузка данных(для дебага)

    IBaseGamerImpl(String name) {
        this.name = name;
        this.start = System.currentTimeMillis();

        initSection(BaseSection.class);
        initSection(SkinSection.class);

        if (initSections() != null) {
            initSections().forEach(this::initSection);
        }
    }

    /**
     * в этом методе мы пишем дополнительные секции которые будут загружены
     */
    protected Set<Class<? extends Section>> initSections() {
        return null;
    }

    public final <T extends Section> void initSection(Class<T> clazz) {
        T section = null;
        try {
            section = clazz.getConstructor(IBaseGamer.class).newInstance(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (section == null) {
            throw new IllegalArgumentException("кажется что-то пошло не так, секция - "
                    + clazz.getSimpleName() + ", ник игрока - " + name);
        }

        val nameSection = clazz.getSimpleName();
        sections.put(nameSection, section);
    }

    public final long getStart() {
        return start;
    }

    @Override
    public int getLevelNetwork() {
        val networkingSection = getSection(NetworkingSection.class);
        if (networkingSection == null) {
            return -1;
        }
        return networkingSection.getLevel();
    }

    @Override
    public int getExp() {
        val networkingSection = getSection(NetworkingSection.class);
        if (networkingSection == null) {
            return -1;
        }
        return networkingSection.getExp();
    }

    @Override
    public int getExpNextLevel() {
        val networkingSection = getSection(NetworkingSection.class);
        if (networkingSection == null) {
            return -1;
        }
        return networkingSection.getExpNextLevel();
    }

    @Override
    public String getDisplayName() {
        return "§r" + getPrefix() + getName();
    }

    @Override
    public Skin getSkin() {
        return getSection(SkinSection.class).getSkin();
    }

    @Override
    public int getPlayerID() {
        return getSection(BaseSection.class).getPlayerID();
    }

    @Override
    public Group getGroup() {
        return getSection(BaseSection.class).getGroup();
    }

    @Override
    public void setGroup(Group group) {
        setGroup(group, true);
    }

    //вызываем этот метод в коре или коннекторе, чтобы обновить группу игрока
    public void setGroup(Group group, boolean saveToMysql) {
        getSection(BaseSection.class).setGroup(group, saveToMysql);
        val section = getSection(MoneySection.class);
        if (section != null) {
            section.setMultiple(MoneySection.getMultiple(group));
        }
        val friendsSection = getSection(FriendsSection.class);
        if (friendsSection != null) {
            friendsSection.setFriendsLimit(FriendsSection.getFriendsLimit(group));
        }
    }

    @Override
    public String getPrefix() {
        return getSection(BaseSection.class).getPrefix();
    }

    @Override
    public void setPrefix(String prefix) {
        getSection(BaseSection.class).setPrefix(prefix);
    }

    @Override
    public void addData(String name, Object data) {
        this.data.put(name.toLowerCase(), data);
    }

    @Override
    public <T> T getData(String name) {
        return (T) this.data.get(name.toLowerCase());
    }

    @Override
    public void clearData(String name) {
        this.data.remove(name.toLowerCase());
    }

    @Override
    public Map<String, Object> getData() {
        return data;
    }

    @Override
    public Map<String, Section> getSections() {
        return sections;
    }

    @Override
    public boolean isDonater() {
        val level = getGroup().getLevel();
        return level > Group.DEFAULT.getLevel()
                && level < Group.MODERATOR.getLevel()
                && getGroup() != Group.BUILDER
                && getGroup() != Group.SRBUILDER;
    }

    @Override
    public boolean isStaff() {
        val group = getGroup();
        return group == Group.ADMIN || group == Group.SRMODERATOR
                || group == Group.OWNER || group == Group.DEVELOPER;
    }

    @Override
    public boolean isDefault() {
        return getGroup() == Group.DEFAULT;
    }

    @Override
    public boolean isHegent() {
        val level = getGroup().getLevel();
        return level >= Group.HEGENT.getLevel();
    }

    @Override
    public boolean isAkio() {
        val level = getGroup().getLevel();
        return level >= Group.AKIO.getLevel();
    }

    @Override
    public boolean isTrival() {
        val level = getGroup().getLevel();
        return level >= Group.TRIVAL.getLevel();
    }

    @Override
    public boolean isAxside() {
        val level = getGroup().getLevel();
        return level >= Group.AXSIDE.getLevel();
    }

    @Override
    public boolean isNeko() {
        val level = getGroup().getLevel();
        return level >= Group.NEKO.getLevel();
    }

    @Override
    public boolean isTikTok() {
        val level = getGroup().getLevel();
        return getGroup() == Group.TIKTOK || level >= Group.NEKO.getLevel();
    }

    @Override
    public boolean isYouTube() {
        val level = getGroup().getLevel();
        return getGroup() == Group.YOUTUBE || level >= Group.ADMIN.getLevel();
    }

    @Override
    public boolean isBuilder() {
        val level = getGroup().getLevel();
        return getGroup() == Group.BUILDER || level >= Group.ADMIN.getLevel();
    }

    @Override
    public boolean isJunior() {
        val level = getGroup().getLevel();
        return level >= Group.JUNIOR.getLevel();
    }

    @Override
    public boolean isModerator() {
        val level = getGroup().getLevel();
        return level >= Group.MODERATOR.getLevel();
    }

    @Override
    public boolean isStModerator() {
        val level = getGroup().getLevel();
        return level >= Group.SRMODERATOR.getLevel();
    }
}
