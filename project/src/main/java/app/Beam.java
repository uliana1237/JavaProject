package app;

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
     * @param p1 вершина №1
     * @param p2 вершина №2
     */
    Beam(Vector2d p1, Vector2d p2) {
        this.peaks = new ArrayList<Vector2d>(List.of(new Vector2d[]{p1, p2}));
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
        return projection > 0 && projection < l.length();
    }
}
