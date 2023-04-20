package app;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.humbleui.jwm.MouseButton;
import io.github.humbleui.skija.*;
import misc.CoordinateSystem2d;
import misc.CoordinateSystem2i;
import misc.Vector2d;
import misc.Vector2i;
import panels.PanelLog;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import static app.Colors.*;

/**
 * Класс задачи
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
public class Task {
    /**
     * Текст задачи
     */
    public static final String TASK_TEXT = """
            ПОСТАНОВКА ЗАДАЧИ:
            На плоскости задано множество "широких лучей" и 
            множество треугольников. Найти такую пару 
            "широкий луч"-треугольник, что фигура, находящаяся 
            внутри "широкого луча" и треугольника, имеет 
            максимальную площадь.""";


    /**
     *  коэффициент колёсика мыши
     */
    private static final float WHEEL_SENSITIVE = 0.001f;

    /**
     * Вещественная система координат задачи
     */
    private final CoordinateSystem2d ownCS;
//    /**
//     * Список точек
//     */
//    private final ArrayList<Point> points;
    /**
     * Размер точки
     */
    private static final int POINT_SIZE = 3;
    /**
     * Последняя СК окна
     */
    private CoordinateSystem2i lastWindowCS;
    /**
     * Флаг, решена ли задача
     */
    private boolean solved;
//    /**
//     * Список точек в пересечении
//     */
//    private final ArrayList<Point> crossed;
//    /**
//     * Список точек в разности
//     */
//    private final ArrayList<Point> single;
    /**
     * Порядок разделителя сетки, т.е. раз в сколько отсечек
     * будет нарисована увеличенная
     */
    private static final int DELIMITER_ORDER = 10;

    /**
     * Задача
     *
     * @param ownCS  СК задачи
     * @param points массив точек
     */
    @JsonCreator
    public Task(@JsonProperty("ownCS") CoordinateSystem2d ownCS, @JsonProperty("points") ArrayList<Point> points) {
        this.ownCS = ownCS;
//        this.points = points;
//        this.crossed = new ArrayList<>();
//        this.single = new ArrayList<>();
    }

    /**
     * Рисование
     *
     * @param canvas   область рисования
     * @param windowCS СК окна
     */
    public void paint(Canvas canvas, CoordinateSystem2i windowCS) {
        // Сохраняем последнюю СК
        lastWindowCS = windowCS;
        // рисуем координатную сетку
        renderGrid(canvas, lastWindowCS);
        // рисуем задачу
        renderTask(canvas, windowCS);
    }

    /**
     * Рисование задачи
     *
     * @param canvas   область рисования
     * @param windowCS СК окна
     */
    private void renderTask(Canvas canvas, CoordinateSystem2i windowCS) {
        canvas.save();
        // создаём перо
        try (var paint = new Paint()) {
//            for (Point p : points) {
//                if (!solved) {
//                    paint.setColor(p.getColor());
//                } else {
//                    if (crossed.contains(p))
//                        paint.setColor(CROSSED_COLOR);
//                    else
//                        paint.setColor(SUBTRACTED_COLOR);
//                }
//                // y-координату разворачиваем, потому что у СК окна ось y направлена вниз,
//                // а в классическом представлении - вверх
//                Vector2i windowPos = windowCS.getCoords(p.pos.x, p.pos.y, ownCS);
//                // рисуем точку
//                canvas.drawRect(Rect.makeXYWH(windowPos.x - POINT_SIZE, windowPos.y - POINT_SIZE, POINT_SIZE * 2, POINT_SIZE * 2), paint);
//            }
        }
        canvas.restore();
    }

//    /**
//     * Добавить точку
//     *
//     * @param pos      положение
//     * @param pointSet множество
//     */
//    public void addPoint(Vector2d pos, Point.PointSet pointSet) {
//        solved = false;
//        Point newPoint = new Point(pos, pointSet);
//        points.add(newPoint);
//        PanelLog.info("точка " + newPoint + " добавлена в " + newPoint.getSetName());
//    }


    /**
     * Клик мыши по пространству задачи
     *
     * @param pos         положение мыши
     * @param mouseButton кнопка мыши
     */
    public void click(Vector2i pos, MouseButton mouseButton) {
//        if (lastWindowCS == null) return;
//        // получаем положение на экране
//        Vector2d taskPos = ownCS.getCoords(pos, lastWindowCS);
//        // если левая кнопка мыши, добавляем в первое множество
//        if (mouseButton.equals(MouseButton.PRIMARY)) {
//            addPoint(taskPos, Point.PointSet.FIRST_SET);
//            // если правая, то во второе
//        } else if (mouseButton.equals(MouseButton.SECONDARY)) {
//            addPoint(taskPos, Point.PointSet.SECOND_SET);
//        }
    }


    /**
     * Добавить случайные точки
     *
     * @param cnt кол-во случайных точек
     */
    public void addRandomPoints(int cnt) {
        CoordinateSystem2i addGrid = new CoordinateSystem2i(30, 30);

//        for (int i = 0; i < cnt; i++) {
//            Vector2i gridPos = addGrid.getRandomCoords();
//            Vector2d pos = ownCS.getCoords(gridPos, addGrid);
//            // сработает примерно в половине случаев
//            if (ThreadLocalRandom.current().nextBoolean())
//                addPoint(pos, Point.PointSet.FIRST_SET);
//            else
//                addPoint(pos, Point.PointSet.SECOND_SET);
//        }
    }


    /**
     * Рисование сетки
     *
     * @param canvas   область рисования
     * @param windowCS СК окна
     */
    public void renderGrid(Canvas canvas, CoordinateSystem2i windowCS) {
        // сохраняем область рисования
        canvas.save();
        // получаем ширину штриха(т.е. по факту толщину линии)
        float strokeWidth = 0.03f / (float) ownCS.getSimilarity(windowCS).y + 0.5f;
        // создаём перо соответствующей толщины
        try (var paint = new Paint().setMode(PaintMode.STROKE).setStrokeWidth(strokeWidth).setColor(TASK_GRID_COLOR)) {
            // перебираем все целочисленные отсчёты нашей СК по оси X
            for (int i = (int) (ownCS.getMin().x); i <= (int) (ownCS.getMax().x); i++) {
                // находим положение этих штрихов на экране
                Vector2i windowPos = windowCS.getCoords(i, 0, ownCS);
                // каждый 10 штрих увеличенного размера
                float strokeHeight = i % DELIMITER_ORDER == 0 ? 5 : 2;
                // рисуем вертикальный штрих
                canvas.drawLine(windowPos.x, windowPos.y, windowPos.x, windowPos.y + strokeHeight, paint);
                canvas.drawLine(windowPos.x, windowPos.y, windowPos.x, windowPos.y - strokeHeight, paint);
            }
            // перебираем все целочисленные отсчёты нашей СК по оси Y
            for (int i = (int) (ownCS.getMin().y); i <= (int) (ownCS.getMax().y); i++) {
                // находим положение этих штрихов на экране
                Vector2i windowPos = windowCS.getCoords(0, i, ownCS);
                // каждый 10 штрих увеличенного размера
                float strokeHeight = i % 10 == 0 ? 5 : 2;
                // рисуем горизонтальный штрих
                canvas.drawLine(windowPos.x, windowPos.y, windowPos.x + strokeHeight, windowPos.y, paint);
                canvas.drawLine(windowPos.x, windowPos.y, windowPos.x - strokeHeight, windowPos.y, paint);
            }
        }
        // восстанавливаем область рисования
        canvas.restore();
    }


    /**
     * Очистить задачу
     */
    public void clear() {
//        points.clear();
        solved = false;
    }

    /**
     * Решить задачу
     */
    public void solve() {
        // очищаем списки
//        crossed.clear();
//        single.clear();
//
//        // перебираем пары точек
//        for (int i = 0; i < points.size(); i++) {
//            for (int j = i + 1; j < points.size(); j++) {
//                // сохраняем точки
//                Point a = points.get(i);
//                Point b = points.get(j);
//                // если точки совпадают по положению
//                if (a.pos.equals(b.pos) && !a.pointSet.equals(b.pointSet)) {
//                    if (!crossed.contains(a)) {
//                        crossed.add(a);
//                        crossed.add(b);
//                    }
//                }
//            }
//        }
//
//        /// добавляем вс
//        for (Point point : points)
//            if (!crossed.contains(point))
//                single.add(point);

        // задача решена
        solved = true;
    }

    /**
     * Получить тип мира
     *
     * @return тип мира
     */
    public CoordinateSystem2d getOwnCS() {
        return ownCS;
    }

//    /**
//     * Получить название мира
//     *
//     * @return название мира
//     */
//    public ArrayList<Point> getPoints() {
//        return points;
//    }

//    /**
//     * Получить список пересечений
//     *
//     * @return список пересечений
//     */
//    @JsonIgnore
//    public ArrayList<Point> getCrossed() {
//        return crossed;
//    }
//
//    /**
//     * Получить список разности
//     *
//     * @return список разности
//     */
//    @JsonIgnore
//    public ArrayList<Point> getSingle() {
//        return single;
//    }

    /**
     * Отмена решения задачи
     */
    public void cancel() {
        solved = false;
    }

    /**
     * проверка, решена ли задача
     *
     * @return флаг
     */
    public boolean isSolved() {
        return solved;
    }

    /**
     * Масштабирование области просмотра задачи
     *
     * @param delta  прокрутка колеса
     * @param center центр масштабирования
     */
    public void scale(float delta, Vector2i center) {
        if (lastWindowCS == null) return;
        // получаем координаты центра масштабирования в СК задачи
        Vector2d realCenter = ownCS.getCoords(center, lastWindowCS);
        // выполняем масштабирование
        ownCS.scale(1 + delta * WHEEL_SENSITIVE, realCenter);
    }

    /**
     * Получить положение курсора мыши в СК задачи
     *
     * @param x        координата X курсора
     * @param y        координата Y курсора
     * @param windowCS СК окна
     * @return вещественный вектор положения в СК задачи
     */
    @JsonIgnore
    public Vector2d getRealPos(int x, int y, CoordinateSystem2i windowCS) {
        return ownCS.getCoords(x, y, windowCS);
    }


    /**
     * Рисование курсора мыши
     *
     * @param canvas   область рисования
     * @param windowCS СК окна
     * @param font     шрифт
     * @param pos      положение курсора мыши
     */
    public void paintMouse(Canvas canvas, CoordinateSystem2i windowCS, Font font, Vector2i pos) {
        // создаём перо
        try (var paint = new Paint().setColor(TASK_GRID_COLOR)) {
            // сохраняем область рисования
            canvas.save();
            // рисуем перекрестие
            canvas.drawRect(Rect.makeXYWH(0, pos.y - 1, windowCS.getSize().x, 2), paint);
            canvas.drawRect(Rect.makeXYWH(pos.x - 1, 0, 2, windowCS.getSize().y), paint);
            // смещаемся немного для красивого вывода текста
            canvas.translate(pos.x + 3, pos.y - 5);
            // положение курсора в пространстве задачи
            Vector2d realPos = getRealPos(pos.x, pos.y, lastWindowCS);
            // выводим координаты
            canvas.drawString(realPos.toString(), 0, 0, font, paint);
            // восстанавливаем область рисования
            canvas.restore();
        }
    }

}
