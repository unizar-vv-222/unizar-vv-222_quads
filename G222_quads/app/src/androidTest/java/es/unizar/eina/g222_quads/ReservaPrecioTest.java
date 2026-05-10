package es.unizar.eina.g222_quads;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import es.unizar.eina.g222_quads.database.Quad;
import es.unizar.eina.g222_quads.database.QuadRepository;
import es.unizar.eina.g222_quads.database.Reserva;
import es.unizar.eina.g222_quads.database.ReservaQuadCascosRepository;
import es.unizar.eina.g222_quads.database.ReservaRepository;
import es.unizar.eina.g222_quads.ui.quads.G222_quads;

/**
 * Prueba de desarrollo (particiones de equivalencia) para comprobar que
 * el precio total de una reserva se mantiene aunque se modifique
 * posteriormente el precio por día de un quad.
 *
 * Requisito: "Mantener el precio total de una reserva aunque se modifique
 * posteriormente el precio del alquiler por día de un quad."
 */
@RunWith(AndroidJUnit4.class)
public class ReservaPrecioTest {

    @Rule
    public ActivityScenarioRule<G222_quads> scenarioRule =
            new ActivityScenarioRule<>(G222_quads.class);

    private ReservaRepository reservaRepository;
    private QuadRepository quadRepository;
    private ReservaQuadCascosRepository reservaQuadCascosRepository;

    private long fechaRecogida;
    private long fechaDevolucion;

    @Before
    public void setUp() {
        scenarioRule.getScenario().onActivity(activity -> {
            try {
                // Limpiar BD antes de cada test y esperar a que termine
                activity.getQuadRespositoryMain().deleteAll().get();
                activity.getReservaQuadCascosRepositoryMain().deleteAll().get();
                activity.getReservaRepositoryMain().deleteAll().get();

                reservaRepository           = activity.getReservaRepositoryMain();
                quadRepository              = activity.getQuadRespositoryMain();
                reservaQuadCascosRepository = activity.getReservaQuadCascosRepositoryMain();

                // Fechas futuras para pasar la validación de reservaValida()
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_YEAR, 1);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                fechaRecogida = cal.getTimeInMillis();

                cal.add(Calendar.DAY_OF_YEAR, 2); // 2 días de reserva
                fechaDevolucion = cal.getTimeInMillis();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @After
    public void tearDown() {
        scenarioRule.getScenario().onActivity(activity -> {
            try {
                reservaQuadCascosRepository.deleteAll().get();
                reservaRepository.deleteAll().get();
                quadRepository.deleteAll().get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // =========================================================
    // precio NO cambia al modificar quad
    // =========================================================

    @Test
    public void precioReservaSeMantieneTrasModificarPrecioQuad() {
        scenarioRule.getScenario().onActivity(activity -> {
            try {
                // 1. Insertar un quad a 50 €/día y esperar
                Quad quad = new Quad("1234ABC", false, 50.0, "test");
                quadRepository.insert(quad).get();

                // 2. Insertar reserva válida
                Reserva reserva = new Reserva(
                        "Ana García", "612345678",
                        fechaRecogida, false,
                        fechaDevolucion, false
                );
                long idReserva = reservaRepository.insert(reserva);
                assertTrue("La reserva debe insertarse correctamente", idReserva > 0);

                // 3. Asociar quad (bloquea hasta que termina gracias al submit().get() interno)
                Map<String, Integer> cascos = new HashMap<>();
                cascos.put("1234ABC", 0);
                reservaQuadCascosRepository.updateCascos((int) idReserva, cascos);

                // 4. Recalcular precio (bloquea hasta que termina)
                reservaRepository.recalcularPrecioReserva(
                        (int) idReserva,
                        fechaRecogida, false,
                        fechaDevolucion, false
                );

                // Verificar precio inicial
                Reserva reservaAntes = reservaRepository.getReservaByIdSync((int) idReserva);
                assertEquals("Precio inicial debe ser 100.0 €",
                        100.0, reservaAntes.getPrecioTotal(), 0.01);

                // --- ACT: modificar el precio del quad a 200 €/día ---
                quad.setPrecio(200.0);
                quadRepository.update(quad).get();

                // --- ASSERT: el precio de la reserva NO debe haber cambiado ---
                Reserva reservaDespues = reservaRepository.getReservaByIdSync((int) idReserva);
                assertEquals(
                        "El precio de la reserva debe mantenerse en 100.0 € aunque el quad suba a 200 €/día",
                        100.0, reservaDespues.getPrecioTotal(), 0.01
                );

            } catch (Exception e) {
                org.junit.Assert.fail("Error en el test EC1: " + e.getMessage());
            }
        });
    }

    // =========================================================
    // precio se calcula correctamente al crear
    // =========================================================


    @Test
    public void precioInicialCalculadoCorrectamente() {
        scenarioRule.getScenario().onActivity(activity -> {
            try {
                Quad quad = new Quad("9999ZZZ", false, 30.0, "test");
                quadRepository.insert(quad).get();

                Reserva reserva = new Reserva(
                        "Luis Pérez", "699887766",
                        fechaRecogida, false,
                        fechaDevolucion, false
                );
                long idReserva = reservaRepository.insert(reserva);
                assertTrue("La reserva debe insertarse correctamente", idReserva > 0);

                Map<String, Integer> cascos = new HashMap<>();
                cascos.put("9999ZZZ", 0);
                reservaQuadCascosRepository.updateCascos((int) idReserva, cascos);

                reservaRepository.recalcularPrecioReserva(
                        (int) idReserva,
                        fechaRecogida, false,
                        fechaDevolucion, false
                );

                Reserva r = reservaRepository.getReservaByIdSync((int) idReserva);
                assertEquals("Precio inicial: 30 €/día × 2 días = 60 €",
                        60.0, r.getPrecioTotal(), 0.01);

            } catch (Exception e) {
                org.junit.Assert.fail("Error en el test EC2: " + e.getMessage());
            }
        });
    }

    // =========================================================
    // varios quads: precio no cambia si se modifica uno
    // =========================================================


    @Test
    public void precioConVariosQuadsSeMantieneTrasModificacion() {
        scenarioRule.getScenario().onActivity(activity -> {
            try {
                Quad q1 = new Quad("0001AAA", false, 40.0, "test");
                Quad q2 = new Quad("0002BBB", true,  60.0, "test");
                quadRepository.insert(q1).get();
                quadRepository.insert(q2).get();

                Reserva reserva = new Reserva(
                        "María López", "655443322",
                        fechaRecogida, false,
                        fechaDevolucion, false
                );
                long idReserva = reservaRepository.insert(reserva);
                assertTrue("La reserva debe insertarse correctamente", idReserva > 0);

                Map<String, Integer> cascos = new HashMap<>();
                cascos.put("0001AAA", 0);
                cascos.put("0002BBB", 1);
                reservaQuadCascosRepository.updateCascos((int) idReserva, cascos);

                reservaRepository.recalcularPrecioReserva(
                        (int) idReserva,
                        fechaRecogida, false,
                        fechaDevolucion, false
                );

                Reserva antes = reservaRepository.getReservaByIdSync((int) idReserva);
                assertEquals("Precio inicial: 2 días × (40+60) €/día = 200 €",
                        200.0, antes.getPrecioTotal(), 0.01);

                // Modificar precio de q2 a 999 €/día
                q2.setPrecio(999.0);
                quadRepository.update(q2).get();

                Reserva despues = reservaRepository.getReservaByIdSync((int) idReserva);
                assertEquals(
                        "El precio total debe seguir siendo 200 € tras modificar el quad a 999 €/día",
                        200.0, despues.getPrecioTotal(), 0.01
                );

            } catch (Exception e) {
                org.junit.Assert.fail("Error en el test EC3: " + e.getMessage());
            }
        });
    }
}