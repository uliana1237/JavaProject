import app.Beam;
import app.Task;
import app.Triangle;
import misc.CoordinateSystem2d;
import misc.Vector2d;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Класс тестирования
 */
public class UnitTest {
    @Test
    public void testTriangle () {
        Triangle triangle = new Triangle(new ArrayList<>(List.of(new Vector2d(-2, -2), new Vector2d(0, 4), new Vector2d(2, -2))));
        assert triangle.isInside(new Vector2d(0, 0));
        assert !triangle.isInside(new Vector2d(5, 0));
        assert !triangle.isInside(new Vector2d(-5, 0));
        assert !triangle.isInside(new Vector2d(0, - 6));
    }

    @Test
    public void testBeam() {
        Beam beam = new Beam(new ArrayList<>(List.of(new Vector2d(0, 2), new Vector2d(2, 0))));
        assert !beam.isInside(new Vector2d(1, 0));
        assert !beam.isInside(new Vector2d(0, 0));
        assert !beam.isInside(new Vector2d(-3, -3));
        assert !beam.isInside(new Vector2d(5, 0));
        assert !beam.isInside(new Vector2d(-5, 0));
        assert beam.isInside(new Vector2d(6, 6));
        assert beam.isInside(new Vector2d(6, 5));
    }

    @Test
    public void testSolve() {
        ArrayList<Triangle> triangles = new ArrayList<>();
        triangles.add(new Triangle(new ArrayList<>(List.of(
                new Vector2d(-7, -5),
                new Vector2d(-1, 1),
                new Vector2d(6, 5)))));
        triangles.add(new Triangle(new ArrayList<>(List.of(
                new Vector2d(-5, 7),
                new Vector2d(-2, 4),
                new Vector2d(4, 9)
        ))));

        ArrayList<Beam> beams = new ArrayList<>();
        beams.add(new Beam(new ArrayList<>(List.of(
                new Vector2d(7, -1),
                new Vector2d(-5, 6)
        ))));
        beams.add(new Beam(new ArrayList<>(List.of(
                new Vector2d(1, -4),
                new Vector2d(9, -9)
        ))));

        Task task = new Task(new CoordinateSystem2d(-10, -10, 10, 10), triangles, beams);
        task.solve();

        assert task.getIndexBeam() == 0 && task.getIndexTriangle() == 0;
    }
}
