package me.nekocloud.ask.type;


import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public enum QuestionCategory {
    SYSTEM(21, "Система", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWY0YzIxZDE3YWQ2MzYzODdlYTNjNzM2YmZmNmFkZTg5NzMxN2UxMzc0Y2Q1ZDliMWMxNWU2ZTg5NTM0MzIifX19"),
    PERSONAL(22, "Персонал и сотрудничество", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzYyZjM2ODg4NGJjMzVmMTYyNjI0OTc0ZDZlYTgzNjg2MzJmMGU4MDlmZmMxNmNhNzYyZDE2ZTM3ZTI2ZGUifX19"),
    CHAT(23, "Чат", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjA4MGQwZjg5NzRkMjVkNjFjMjMxM2ExODJlNmE5YjFkMDQ4YzNmYTYxY2M5ODNlYTQ3N2E0ZmM4OTE5ZTZkZiJ9fX0=");

    QuestionCategory(int inventorySlot, String categoryName, String skullTexture) {
        this.inventorySlot = inventorySlot;
        this.categoryName = categoryName;
        this.skullTexture = skullTexture;
    }

    public static final QuestionCategory[] QUESTION_CATEGORIES;

    private final int inventorySlot;

    private final String categoryName;

    private final String skullTexture;

    static {
        QUESTION_CATEGORIES = values();
    }

    public static QuestionCategory getQuestionCategory(@NotNull String questionCategoryName) {
        return Arrays.stream(QUESTION_CATEGORIES).filter(questionCategory -> questionCategory.name().equalsIgnoreCase(questionCategoryName))
                .findFirst().orElse(null);
    }

    public int getInventorySlot() {
        return inventorySlot;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getSkullTexture() {
        return skullTexture;
    }
}
