package app;

import com.fasterxml.jackson.annotation.JsonIgnore;
import misc.Misc;
import misc.Vector2d;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс треугольника
 */
public class Triangle {
    /**
     * Координаты вершин треугольника
     */
    public final ArrayList<Vector2d> peaks;

    /**
     * Площать треугольника
     */
    public final double S;

    /**
     * Конструктор треугольника
     * @param p1 вершина №1
     * @param p2 вершина №2
     * @param p3 вершина №3
     */
    Triangle(Vector2d p1, Vector2d p2, Vector2d p3) {
        this.peaks = new ArrayList<Vector2d>(List.of(new Vector2d[]{p1, p2, p3}));
        Vector2d v1 = Vector2d.subtract(p2, p1);
        Vector2d v2 = Vector2d.subtract(p3, p1);
        this.S = (Math.abs(v1.x * v2.y - v1.y * v2.x)) / 2;
    }

    /**
     * Получить цвет треугольника
     *
     * @return цвет точки
     */
    public int getColor() {
        return Misc.getColor(0xCC, 0x00, 0x00, 0xFF);
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
                ", peak №3=" + peaks.get(2) +
                '}';
    }

    /**
     * Проверка, находится ли точка внутри треугольника
     *
     * @param point точка
     * @return      находится ли точка внутри треугольника
     */

    public boolean isInside(Vector2d point) {
        return this.S == (new Triangle(peaks.get(0), peaks.get(1), point).S) +
                        (new Triangle(peaks.get(1), peaks.get(2), point).S) +
                        (new Triangle(peaks.get(0), peaks.get(2), point).S);
    }
}
