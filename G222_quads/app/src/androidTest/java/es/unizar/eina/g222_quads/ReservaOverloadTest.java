package es.unizar.eina.g222_quads;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;

import es.unizar.eina.g222_quads.database.Reserva;
import es.unizar.eina.g222_quads.database.ReservaRepository;
import es.unizar.eina.g222_quads.ui.quads.G222_quads;

@RunWith(AndroidJUnit4.class)
public class ReservaOverloadTest {

    @Rule
    public ActivityScenarioRule<G222_quads> scenarioRule =
            new ActivityScenarioRule<>(G222_quads.class);

    private long dateToMillis(int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(year, month - 1, day, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    private final long FECHA_1 = dateToMillis(2030, 5, 1);
    private final long FECHA_2 = dateToMillis(2030, 5, 2);

    @Before
    public void setup() {
        scenarioRule.getScenario().onActivity(activity -> {
            try {
                activity.getReservaRepositoryMain().deleteAll().get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private String generarCadena(int longitud) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < longitud; i++) {
            sb.append('A');
        }
        return sb.toString();
    }

    @Test
    public void testInsertLargeNames() {
        scenarioRule.getScenario().onActivity(activity -> {
            ReservaRepository repo = activity.getReservaRepositoryMain();

            int[] longitudes = {10, 50, 100, 500, 1000};

            for (int i = 0; i < longitudes.length; i++) {
                Reserva r = new Reserva(
                        generarCadena(longitudes[i]),
                        "600000" + i,
                        FECHA_1,
                        false,
                        FECHA_2,
                        true
                );

                long id = repo.insert(r);

                assertNotEquals(-1, id);

                Reserva inserted = repo.getReservaByIdSync((int) id);
                assertNotNull(inserted);
            }
        });
    }
}