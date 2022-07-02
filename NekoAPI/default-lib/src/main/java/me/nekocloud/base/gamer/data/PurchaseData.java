package me.nekocloud.base.gamer.data;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import me.nekocloud.base.gamer.constans.PurchaseType;

@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public final class PurchaseData {

    PurchaseType type;
    int amount;

}

