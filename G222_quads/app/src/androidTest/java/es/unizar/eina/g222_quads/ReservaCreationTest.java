package es.unizar.eina.g222_quads;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import es.unizar.eina.g222_quads.database.Reserva;
import es.unizar.eina.g222_quads.database.ReservaRepository;
import es.unizar.eina.g222_quads.ui.quads.G222_quads;

@RunWith(AndroidJUnit4.class)
public class ReservaCreationTest {

    @Rule
    public ActivityScenarioRule<G222_quads> scenarioRule = new ActivityScenarioRule<>(G222_quads.class);

    private long dateToMillis(int year, int month, int day) {
        java.util.Calendar c = java.util.Calendar.getInstance();
        c.set(year, month - 1, day, 0, 0, 0);
        c.set(java.util.Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    // Luego modifica las constantes:
    private final long FECHA_1 = dateToMillis(2030, 5, 1);  // 1 mayo 2030
    private final long FECHA_2 = dateToMillis(2030, 5, 2);  // 2 mayo 2030
    private final long FECHA_INVALIDA = dateToMillis(2025, 5, 2); // 2 mayo 2025
    private final boolean MAÑANA = false;
    private final boolean TARDE = true;

    @Before
    public void setup() {
        scenarioRule.getScenario().onActivity(activity -> {
            ReservaRepository repo = activity.getReservaRepositoryMain();
            try {
                repo.deleteAll().get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }


    // Test 1: Inserción válida con todos los campos correctos.
    @Test
    public void testInsertValido() {
        scenarioRule.getScenario().onActivity(activity -> {
            ReservaRepository repo = activity.getReservaRepositoryMain();
            Reserva r = new Reserva("CL_001", "612458920", FECHA_1, MAÑANA, FECHA_2, TARDE);

            long resultado = repo.insert(r);

            assertNotEquals("Debería devolver un ID válido (>0)", -1, resultado);

            r = new Reserva("CL_001", "612458921", FECHA_1, TARDE, FECHA_2, MAÑANA);

            resultado = repo.insert(r);

            assertNotEquals("Debería devolver un ID válido (>0)", -1, resultado);
        });
    }

    // Test 2: nombre de cliente vacío.
    @Test
    public void testInsertEmptyName() {
        scenarioRule.getScenario().onActivity(activity -> {
            ReservaRepository repo = activity.getReservaRepositoryMain();
            Reserva r = new Reserva("", "612458922", FECHA_1, MAÑANA, FECHA_2, TARDE);

            long resultado = repo.insert(r);

            assertEquals("Debería fallar (-1) por nombre vacío", -1, resultado);
        });
    }

    // Test 3: Teléfono con formato inválido (letras).

    @Test
    public void testInsertInvalidPhone() {
        scenarioRule.getScenario().onActivity(activity -> {
            ReservaRepository repo = activity.getReservaRepositoryMain();
            Reserva r = new Reserva("CL_003", "612ABC920", FECHA_1, MAÑANA, FECHA_2, TARDE);

            long resultado = repo.insert(r);

            assertEquals("Debería fallar (-1) por teléfono no numérico", -1, resultado);

            r = new Reserva("CL_004", null, FECHA_1, MAÑANA, FECHA_2, TARDE);

            resultado = repo.insert(r);

            assertEquals("Debería fallar (-1) por teléfono nulo", -1, resultado);

        });
    }

    // Test 4: Fecha de recogida posterior a la de devolución.

    @Test
    public void testInsertInvalidDates() {
        scenarioRule.getScenario().onActivity(activity -> {
            ReservaRepository repo = activity.getReservaRepositoryMain();
            // Recogida día 2, Devolución día 1
            Reserva r = new Reserva("CL_005", "612458923", FECHA_2, MAÑANA, FECHA_1, TARDE);

            long resultado = repo.insert(r);

            assertEquals("Debería fallar (-1) por orden cronológico incorrecto", -1, resultado);

            r = new Reserva("CL_006", "612458924", FECHA_INVALIDA , MAÑANA, FECHA_2, TARDE);

            resultado = repo.insert(r);

            assertEquals("Debería fallar (-1) por fecha recogida inválida", -1, resultado);
        });
    }


    // Test 5: Mismo día, pero recogida por la tarde y devolución por la mañana.

    @Test
    public void testInsertSlotsDayInvalid() {
        scenarioRule.getScenario().onActivity(activity -> {
            ReservaRepository repo = activity.getReservaRepositoryMain();

            // Mismo día (FECHA_1), pero TARDE (true) -> MAÑANA (false)
            Reserva r = new Reserva("CL_012", "612458925", FECHA_1, TARDE, FECHA_1, MAÑANA);
            long resultado = repo.insert(r);

            assertEquals("Debería fallar (-1) por slot de devolución anterior al de recogida", -1, resultado);
        });
    }


}