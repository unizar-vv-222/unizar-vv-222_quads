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

    private final long FECHA_1 = 1746057600000L; // 01/05/2026
    private final long FECHA_2 = 1746144000000L; // 02/05/2026

    private final long FECHA_INVALIDA = 1746144000000L; // 02/05/2025
    private final boolean MAÑANA = true;
    private final boolean TARDE = false;

    @Before
    public void setup() {
        scenarioRule.getScenario().onActivity(activity -> {
            activity.getReservaRepositoryMain().deleteAll();
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

            r = new Reserva("CL_007", "612458924", FECHA_1, MAÑANA, FECHA_INVALIDA, TARDE);

            resultado = repo.insert(r);

            assertEquals("Debería fallar (-1) por fecha devolución inválida", -1, resultado);

        });
    }


    // Test 5: Mismo día, pero recogida por la tarde y devolución por la mañana.

    @Test
    public void testInsertSlotsDayInvalid() {
        scenarioRule.getScenario().onActivity(activity -> {
            ReservaRepository repo = activity.getReservaRepositoryMain();

            /*
            -- JAVA no permite asignar null a un Boolean --

            Reserva r = new Reserva("CL_010", "612458925", FECHA_1, null, FECHA_1, TARDE);

            long resultado = repo.insert(r);

            assertEquals("Debería fallar (-1) por slot de recogida nulo", -1, resultado);

             r = new Reserva("CL_011", "612458925", FECHA_1, MAÑANA, FECHA_1, null);

             resultado = repo.insert(r);

            assertEquals("Debería fallar (-1) por slot de devolución nulo", -1, resultado);
            */

            // Mismo día (FECHA_1), pero TARDE (true) -> MAÑANA (false)
            Reserva r = new Reserva("CL_012", "612458925", FECHA_1, TARDE, FECHA_1, MAÑANA);
            long resultado = repo.insert(r);

            assertEquals("Debería fallar (-1) por slot de devolución anterior al de recogida", -1, resultado);
        });
    }


}