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

    private final long FECHA_1 = 1735689600000L; // 01/01/2026
    private final long FECHA_2 = 1735776000000L; // 02/01/2026
    private final boolean MAÑANA = false;
    private final boolean TARDE = true;

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
            Reserva r = new Reserva("Cliente Correcto", "612458920", FECHA_1, MAÑANA, FECHA_2, TARDE);

            long resultado = repo.insert(r);

            assertNotEquals("Debería devolver un ID válido (>0)", -1, resultado);
        });
    }

    // Test 2: nombre de cliente vacío.
    @Test
    public void testInsertNombreVacio() {
        scenarioRule.getScenario().onActivity(activity -> {
            ReservaRepository repo = activity.getReservaRepositoryMain();
            Reserva r = new Reserva("", "612458920", FECHA_1, MAÑANA, FECHA_2, TARDE);

            long resultado = repo.insert(r);

            assertEquals("Debería fallar (-1) por nombre vacío", -1, resultado);
        });
    }

    /**
     * Test de Robustez: Teléfono con formato inválido (letras).
     */
    @Test
    public void testInsertTelefonoInvalido() {
        scenarioRule.getScenario().onActivity(activity -> {
            ReservaRepository repo = activity.getReservaRepositoryMain();
            Reserva r = new Reserva("Cliente Test", "612ABC920", FECHA_1, MAÑANA, FECHA_2, TARDE);

            long resultado = repo.insert(r);

            assertEquals("Debería fallar (-1) por teléfono no numérico", -1, resultado);
        });
    }

    /**
     * Test de Valores Límite: Fecha de recogida posterior a la de devolución.
     */
    @Test
    public void testInsertFechasInvertidas() {
        scenarioRule.getScenario().onActivity(activity -> {
            ReservaRepository repo = activity.getReservaRepositoryMain();
            // Recogida día 2, Devolución día 1
            Reserva r = new Reserva("Cliente Fechas", "612458920", FECHA_2, MAÑANA, FECHA_1, TARDE);

            long resultado = repo.insert(r);

            assertEquals("Debería fallar (-1) por orden cronológico incorrecto", -1, resultado);
        });
    }

    /**
     * Test de Valores Límite: Mismo día, pero recogida por la tarde y devolución por la mañana.
     */
    @Test
    public void testInsertSlotsInvertidosMismoDia() {
        scenarioRule.getScenario().onActivity(activity -> {
            ReservaRepository repo = activity.getReservaRepositoryMain();
            // Mismo día (FECHA_1), pero TARDE (true) -> MAÑANA (false)
            Reserva r = new Reserva("Cliente Slots", "612458920", FECHA_1, TARDE, FECHA_1, MAÑANA);

            long resultado = repo.insert(r);

            assertEquals("Debería fallar (-1) por slot de devolución anterior al de recogida", -1, resultado);
        });
    }

    /**
     * Test de Integración: Verificar que los comparables se generan y el precio inicial es 0.0.
     */
    @Test
    public void testInsertVerificarComparablesYPrecioInicial() {
        scenarioRule.getScenario().onActivity(activity -> {
            ReservaRepository repo = activity.getReservaRepositoryMain();
            Reserva r = new Reserva("Test Comparables", "600111222", FECHA_1, MAÑANA, FECHA_2, MAÑANA);

            long id = repo.insert(r);
            assertNotEquals(-1, id);

            // Recuperamos síncronamente para verificar campos internos generados
            // Nota: debes tener implementado getReservaByIdSync o similar en el repo
            Reserva recuperada = repo.getReservaByIdSync((int)id);

            assertNotEquals("RecogidaComparable debería haberse generado", 0, recuperada.getRecogidaComparable());
            assertTrue("DevolucionComparable debería ser mayor que RecogidaComparable",
                    recuperada.getDevolucionComparable() > recuperada.getRecogidaComparable());
            assertEquals("El precio inicial tras el constructor debe ser 0.0", 0.0, recuperada.getPrecioTotal(), 0.001);
        });
    }
}