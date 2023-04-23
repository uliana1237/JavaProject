import app.Beam;
import app.Task;
import app.Triangle;
import misc.CoordinateSystem2d;
import misc.Vector2d;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Класс тестирования
 */
public class UnitTest {
    @Test
    public void testTriangle () {
        Triangle triangle = new Triangle(new Vector2d(-2, -2), new Vector2d(0, 4), new Vector2d(2, -2));
        assert triangle.isInside(new Vector2d(0, 0));
        assert !triangle.isInside(new Vector2d(5, 0));
        assert !triangle.isInside(new Vector2d(-5, 0));
        assert !triangle.isInside(new Vector2d(0, - 6));
    }

    @Test
    public void testPrimitive() {
        Beam beam = new Beam(new Vector2d(0, 2), new Vector2d(2, 0));
        assert !beam.isInside(new Vector2d(1, 0));
        assert !beam.isInside(new Vector2d(0, 0));
        assert !beam.isInside(new Vector2d(-3, -3));
        assert !beam.isInside(new Vector2d(5, 0));
        assert !beam.isInside(new Vector2d(-5, 0));
        assert beam.isInside(new Vector2d(6, 6));
        assert beam.isInside(new Vector2d(6, 5));
    }

//    private static void test(ArrayList<Triangle> triangles, ArrayList<Beam> beams) {
//        Task task = new Task(new CoordinateSystem2d(10, 10, 20, 20), triangles, beams);
//        task.solve();
//
//    }
}
