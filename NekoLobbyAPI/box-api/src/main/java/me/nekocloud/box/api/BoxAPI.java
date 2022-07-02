package me.nekocloud.box.api;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import me.nekocloud.box.data.ItemBoxManagerImpl;
import me.nekocloud.box.data.Box;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public final class BoxAPI {

    public static final ImmutableList<Integer> EMPTY_SLOTS_PURPLE = ImmutableList.of(
            0, 1, 7, 8, 9, 17, 36, 44, 45, 46, 52, 53
    ); // ID = 10
    public static final ImmutableList<Integer> EMPTY_SLOTS_MAGENTA = ImmutableList.of(
            2, 6, 10, 16, 18, 26, 27, 35, 37, 43, 47, 51
    ); // ID = 2

    private ItemBoxManager itemBoxManager;

    @Getter
    private final int amountItems = 5; //сколько айтемов будет вылетать

    @Getter
    private final List<Box> boxes = new ArrayList<>();

    public ItemBoxManager getItemBoxManager() {
        if (itemBoxManager == null) {
            itemBoxManager = new ItemBoxManagerImpl();
        }

        return itemBoxManager;
    }


}
