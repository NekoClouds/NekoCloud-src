package me.nekocloud.base.skin.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import me.nekocloud.base.skin.exeptions.SkinRequestException;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@ToString
public abstract class SkinsResponse {

    @Getter
    String id;
    @Getter
    String name;

    String message;
    String type;
    String error;
    int status;

    public void check() throws SkinRequestException {
        if (error != null) {
            throw new SkinRequestException("Произошла ошибка при получении данных скина " + "\nError: " + error + "");
        } else if (name != null && message != null && type != null && status != 200) {
            throw new SkinRequestException("Произошла ошибка при получении данных скина" + "\nMessage: " + message + "");
        } else if (id != null && !id.isEmpty()) {
            if (name == null || name.isEmpty()) {
                throw new IllegalArgumentException("name");
            }
        } else {
            throw new IllegalArgumentException("id");
        }
    }
}
