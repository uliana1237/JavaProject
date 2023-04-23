package app;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import misc.Misc;
import misc.Vector2d;

import java.util.ArrayList;
import java.util.List;

/**
 * класс широкого луча
 */
public class Beam {
    /**
     * Координаты вершин
     */
    public final ArrayList<Vector2d> peaks;

    /**
     * Конструктор широкого луча
     * @param peaks вершины
     */
    @JsonCreator
    public Beam(@JsonProperty("peaks") ArrayList<Vector2d> peaks) {
        this.peaks = peaks;
    }

    /**
     * Получить цвет широкого луча
     *
     * @return цвет точки
     */
    public static int getColor() {
        return Misc.getColor(0xCC, 0x00, 0xFF, 0x0);
    }

    /**
     * Строковое представление объекта
     *
     * @return строковое представление объекта
     */
    @Override
    public String toString() {
        return "Tringle{" +
                "peak №1=" + peaks.get(0) +
                ", peak №2=" + peaks.get(1) +
                '}';
    }

    /**
     * Проверка, находится ли точка внутри широкой полосы
     *
     * @param point точка
     * @return      находится ли точка широкой полосы
     */

    public boolean isInside(Vector2d point) {
        Vector2d l = Vector2d.subtract(peaks.get(1), peaks.get(0));
        Vector2d d = Vector2d.subtract(point, peaks.get(0));
        double projection = (l.x * d.x + l.y * d.y) / l.length();
        return projection > 0 && projection < l.length() && (l.x * d.y - l.y * d.x > 0);
    }
}
