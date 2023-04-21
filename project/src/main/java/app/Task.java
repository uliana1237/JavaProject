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
    /**
     * Список треугольников
     */
    private final ArrayList<Triangle> triangles;
    /**
     * Список точек незаконченного треугольника
     */
    public ArrayList<Vector2d> pointsTriangle;
    /**
     * Список широких лучей
     */
    private final ArrayList<Beam> beams;
    /**
     * Список точек незаконченного широкого луча
     */
    public ArrayList<Vector2d> pointsBeam;
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
    /**
     * Список точек в пересечении
     */
    private final ArrayList<Vector2d> crossed;
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
        this.triangles = new ArrayList<>();
        this.beams = new ArrayList<>();
        this.crossed = new ArrayList<>();
        this.pointsTriangle = new ArrayList<>();
        this.pointsBeam = new ArrayList<>();

        // вручную
//        triangles.add(new Triangle(new Vector2d(0, 0), new Vector2d(3, 0), new Vector2d(0, 3)));
//        beams.add(new Beam(new Vector2d(0, 6), new Vector2d(6, 0)));
//        crossed.add(new Vector2d(0, 0));
//        crossed.add(new Vector2d(-3, 0));
//        crossed.add(new Vector2d(-5, -6));
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
            if (!pointsTriangle.isEmpty()) {
                paint.setColor(POINTS_TRIANGLE);
                paintPoints(pointsTriangle, paint, windowCS, canvas);
            }
            for (Triangle t: triangles) {
                paint.setColor(Triangle.getColor());
                paintLines(t.peaks,true, paint, windowCS, canvas);
            }
            if (!pointsBeam.isEmpty()) {
                paint.setColor(POINTS_BEAM);
                paintPoints(pointsBeam, paint, windowCS, canvas);
            }
            for (Beam b: beams) {
                paint.setColor(Beam.getColor());
                Vector2d p1, p2, l = Vector2d.subtract(b.peaks.get(1), b.peaks.get(0));
                Vector2d n = new Vector2d(-l.y, l.x);

                Vector2d m = Vector2d.subtract(ownCS.getMax(), b.peaks.get(0));
                if (m.x / n.x > m.y / n.y) {
                    p1 = new Vector2d(m.y / n.y * n.x, m.y);
                } else {
                    p1 = new Vector2d(m.x, m.x / n.x * n.y);
                }
                p1.add(b.peaks.get(0));

                m = Vector2d.subtract(ownCS.getMax(), b.peaks.get(1));
                if (m.x / n.x > m.y / n.y) {
                    p2 = new Vector2d(m.y / n.y * n.x, m.y);
                } else {
                    p2 = new Vector2d(m.x, m.x / n.x * n.y);
                }
                p2.add(b.peaks.get(1));

                ArrayList<Vector2d> points = new ArrayList<>();
                points.add(p1);
                points.add(b.peaks.get(0));
                points.add(b.peaks.get(1));
                points.add(p2);
                paintLines(points, false, paint, windowCS, canvas);
            }
            if (!crossed.isEmpty()) {
                paint.setColor(CROSSED_COLOR);
                paintLines(crossed, true, paint, windowCS, canvas);
                paint.setColor(Beam.getColor());
            }
        }
        canvas.restore();
    }

    /**
     * Рисует линии по списку вершин
     *
     * @param points   список вершин
     * @param paint    перо
     * @param windowCS СК окна
     * @param canvas   Полотно
     */

    private void paintLines(ArrayList<Vector2d> points, Boolean isClosed, Paint paint, CoordinateSystem2i windowCS, Canvas canvas) {
        for (int i = 0; i + 1 < points.size(); ++i) {
            paintLine(points.get(i), points.get(i + 1), paint, windowCS, canvas);
        }
        if (isClosed) {
            paintLine(points.get(points.size()-1), points.get(0), paint, windowCS, canvas);
        }
    }

    /**
     * Рисует линию двум вершинам
     *
     * @param p1       вершина №1
     * @param p2       вершина №2
     * @param paint    перо
     * @param windowCS СК окна
     * @param canvas   Полотно
     */

    private void paintLine(Vector2d p1, Vector2d p2, Paint paint, CoordinateSystem2i windowCS, Canvas canvas) {
        // y-координату разворачиваем, потому что у СК окна ось y направлена вниз,
        // а в классическом представлении - вверх
        Vector2i windowPos1 = windowCS.getCoords(p1.x, p1.y, ownCS);
        Vector2i windowPos2 = windowCS.getCoords(p2.x, p2.y, ownCS);
        // рисуем точку
        canvas.drawLine(windowPos1.x, windowPos1.y, windowPos2.x, windowPos2.y, paint);
    }

    /**
     * Рисует точки
     *
     * @param points   список точек
     * @param paint    перо
     * @param windowCS СК окна
     * @param canvas   Полотно
     */

    private void paintPoints(ArrayList<Vector2d> points, Paint paint, CoordinateSystem2i windowCS, Canvas canvas) {
        for (Vector2d p: points) {
            paintPoint(p, paint, windowCS, canvas);
        }
    }

    /**
     * Рисует точку
     *
     * @param p        точка
     * @param paint    перо
     * @param windowCS СК окна
     * @param canvas   Полотно
     */

    private void paintPoint(Vector2d p, Paint paint, CoordinateSystem2i windowCS, Canvas canvas) {
        // y-координату разворачиваем, потому что у СК окна ось y направлена вниз,
        // а в классическом представлении - вверх
        Vector2i windowPos = windowCS.getCoords(p.x, p.y, ownCS);
        // рисуем точку
        canvas.drawRect(Rect.makeXYWH(windowPos.x - POINT_SIZE, windowPos.y - POINT_SIZE, POINT_SIZE * 2, POINT_SIZE * 2), paint);
    }

    /**
     * Добавить точку треугольника
     *
     * @param pos      положение
     */
    public void addPointTriangle(Vector2d pos) {
        solved = false;
        pointsTriangle.add(pos);
        if (pointsTriangle.size() < 3) {
            PanelLog.info("точка " + pos + " добавлена");
        } else {
            Triangle t = new Triangle(pointsTriangle.get(0), pointsTriangle.get(1), pointsTriangle.get(2));
            triangles.add(t);
            pointsTriangle.clear();
            PanelLog.info("тругольник " + t.toString() + " добавлен");
        }
    }

    /**
     * Добавить точку широкого угла
     *
     * @param pos      положение
     */
    public void addPointBeam(Vector2d pos) {
        solved = false;
        pointsBeam.add(pos);
        if (pointsBeam.size() < 2) {
            PanelLog.info("точка " + pos + " добавлена");
        } else {
            Beam b = new Beam(pointsBeam.get(0), pointsBeam.get(1));
            beams.add(b);
            pointsBeam.clear();
            PanelLog.info("широкоий луч " + b.toString() + " добавлен");
        }
    }


    /**
     * Клик мыши по пространству задачи
     *
     * @param pos         положение мыши
     * @param mouseButton кнопка мыши
     */
    public void click(Vector2i pos, MouseButton mouseButton) {
        if (lastWindowCS == null) return;
        // получаем положение на экране
        Vector2d taskPos = ownCS.getCoords(pos, lastWindowCS);
        // если левая кнопка мыши, добавляем треугольник
        if (mouseButton.equals(MouseButton.PRIMARY)) {
            addPointTriangle(taskPos);
            // если правая, то широкий луч
        } else if (mouseButton.equals(MouseButton.SECONDARY)) {
            addPointBeam(taskPos);
        }
    }


    /**
     * Добавить случайные треугольники
     *
     * @param cnt кол-во случайных треугольников
     */
    public void addRandomTriangles(int cnt) {
        for (int i = 0; i < cnt; i++) {
            for (int j = 0; j < 3; ++j) {
                Vector2d pos = ownCS.getRandomCoords();
                // сработает примерно в половине случаев
                addPointTriangle(pos);
            }
        }
    }

    /**
     * Добавить случайные широкие лучи
     *
     * @param cnt кол-во случайных широких лучей
     */
    public void addRandomBeams(int cnt) {
        for (int i = 0; i < cnt; i++) {
            for (int j = 0; j < 2; ++j) {
                Vector2d pos = ownCS.getRandomCoords();
                // сработает примерно в половине случаев
                addPointBeam(pos);
            }
        }
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
        pointsTriangle.clear();
        pointsBeam.clear();
        triangles.clear();
        beams.clear();
        crossed.clear();
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
