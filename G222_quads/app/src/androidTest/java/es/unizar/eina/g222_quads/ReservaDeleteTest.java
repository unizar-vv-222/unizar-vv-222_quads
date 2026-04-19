package es.unizar.eina.g222_quads;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Before;

import java.util.Calendar;

import es.unizar.eina.g222_quads.database.Reserva;
import es.unizar.eina.g222_quads.database.ReservaRepository;
import es.unizar.eina.g222_quads.ui.quads.G222_quads;

@RunWith(AndroidJUnit4.class)
public class ReservaDeleteTest {

    @Rule
    public ActivityScenarioRule<G222_quads> scenarioRule = new ActivityScenarioRule<>(G222_quads.class);

    private long dateToMillis(int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(year, month - 1, day, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    private final long FECHA_INICIO = dateToMillis(2030, 1, 1);  // 1 ene 2030
    private final long FECHA_FIN = dateToMillis(2030, 1, 2);     // 2 ene 2030

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

    @Test
    public void testDeleteExistingReserva() {
        scenarioRule.getScenario().onActivity(activity -> {
            ReservaRepository repo = activity.getReservaRepositoryMain();
            Reserva r = new Reserva("A BORRAR", "612458920", FECHA_INICIO, true, FECHA_FIN, false);

            long resultado = repo.insert(r);

            // Asignar ID que ha devuelto la base de datos para vincular al objeto y que el borrados e haga sin problema
            r.setId((int) resultado);
            assertNotEquals("Debería devolver un ID válido (>0)", -1, resultado);

            int filasBorradas = repo.delete(r);
            assertEquals("Debería borrar exactamente 1 fila", 1, filasBorradas);
        });
    }

    @Test
    public void testDeleteNotExistingReserva() {
        scenarioRule.getScenario().onActivity(activity -> {
            ReservaRepository repo = activity.getReservaRepositoryMain();
            Reserva r = new Reserva("No Existe", "612458920", FECHA_INICIO, false, FECHA_FIN, false);

            int filasBorradas = repo.delete(r);
            assertEquals("Debería devolver 0 filas borradas", 0, filasBorradas);
        });
    }
}