package pw.novit.nekocloud.bungee.api.utils.hex;

import lombok.NonNull;
import lombok.val;

import java.awt.*;
import java.util.List;

/**
 * Дополнение градиента для хекс ресолвера
 */
public class GradientHelper {

    private final List<Color> colors;
    private final int stepSize;
    private int step, stepIndex;

    public GradientHelper(@NonNull List<Color> colors, int totalColors) {
        if (colors.size() < 2)
            throw new IllegalArgumentException("Нужно минимум 2 цвета, т.е. <g:#ED4264:#FFEDBC>");

        if (totalColors < 1)
            throw new IllegalArgumentException("Нужен хотя бы 1 цвет!");

        this.colors = colors;
        this.stepSize = totalColors / (colors.size() - 1);
        this.step = this.stepIndex = 0;
    }

    /**
     * @return следующий градиент.
     */
    public Color next() {

        Color color;
        if (this.stepIndex + 1 < this.colors.size()) {
            Color start = this.colors.get(this.stepIndex);
            val end = this.colors.get(this.stepIndex + 1);
            float interval = (float) this.step / this.stepSize;

            color = getGradientInterval(start, end, interval);
        } else {
            color = this.colors.get(this.colors.size() - 1);
        }

        this.step += 1;
        if (this.step >= this.stepSize) {
            this.step = 0;
            this.stepIndex++;
        }

        return color;
    }

    /**
      * Получает цвет по линейному градиенту между двумя цветами
      *
      * @param start Начальный цвет
      * @param end Конечный цвет
      * @param interval Получаемый интервал от 0 до 1 включительно
      * @return цвет в интервале между начальным и конечным цветами
      */
    public static Color getGradientInterval(@NonNull Color start, @NonNull Color end, float interval) {
        if (0 > interval || interval > 1)
            throw new IllegalArgumentException("Интервал должен быть от 0 до 1 включительно.");

        int r = (int) (end.getRed() * interval + start.getRed() * (1 - interval));
        int g = (int) (end.getGreen() * interval + start.getGreen() * (1 - interval));
        int b = (int) (end.getBlue() * interval + start.getBlue() * (1 - interval));

        return new Color(r, g, b);
    }
}
