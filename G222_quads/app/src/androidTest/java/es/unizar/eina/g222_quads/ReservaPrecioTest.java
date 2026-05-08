package es.unizar.eina.g222_quads;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.app.Application;

import androidx.test.core.app.ApplicationProvider;
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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import es.unizar.eina.g222_quads.database.Quad;
import es.unizar.eina.g222_quads.database.QuadDao;
import es.unizar.eina.g222_quads.database.Quad_Reserva_RoomDataBase;
import es.unizar.eina.g222_quads.database.Reserva;
import es.unizar.eina.g222_quads.database.ReservaDao;
import es.unizar.eina.g222_quads.database.ReservaQuadCascosRepository;
import es.unizar.eina.g222_quads.database.ReservaRepository;
import es.unizar.eina.g222_quads.ui.quads.G222_quads;
import es.unizar.eina.g222_quads.utils.DateUtils;

/**
 * Prueba de desarrollo (particiones de equivalencia) para comprobar que
 * el precio total de una reserva se mantiene aunque se modifique
 * posteriormente el precio por día de un quad.
 *
 * Requisito: "Mantener el precio total de una reserva aunque se modifique
 * posteriormente el precio del alquiler por día de un quad."
 */
@RunWith(AndroidJUnit4.class)
public class ReservaPrecioMantenidoTest {

    @Rule
    public ActivityScenarioRule<G222_quads> scenarioRule =
            new ActivityScenarioRule<>(G222_quads.class);
    private ReservaRepository reservaRepository;
    private ReservaQuadCascosRepository quadCascosRepository;

    // DAOs directos para poder modificar el precio del quad
    private QuadDao quadDao;
    private ReservaDao reservaDao;

    // Fechas futuras válidas (mañana y pasado mañana)
    private long fechaRecogida;
    private long fechaDevolucion;

    @Before
    public void setUp() {
        scenarioRule.getScenario().onActivity(activity -> {
            try {
                activity.getQuadRespositoryMain().deleteAll().get();
                Quad_Reserva_RoomDataBase db =
                        Quad_Reserva_RoomDataBase.getDatabase(activity.getApplication());
                reservaRepository = activity.getReservaRepositoryMain();
                quadCascosRepository = activity.getReservaQuadCascosRepositoryMain();
                quadDao = db.quadDao();
                reservaDao = db.reservaDao();
                // Usar fechas futuras para pasar la validación de reservaValida()
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_YEAR, 1);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                fechaRecogida = cal.getTimeInMillis();

                cal.add(Calendar.DAY_OF_YEAR, 2); // 3 días en total desde hoy
                fechaDevolucion = cal.getTimeInMillis();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    @After
    public void tearDown() throws Exception {
        scenarioRule.getScenario().onActivity(activity -> {
            try {
                activity.getQuadRespositoryMain().deleteAll().get();
                Quad_Reserva_RoomDataBase db =
                        Quad_Reserva_RoomDataBase.getDatabase(activity.getApplication());
                db.reservaQuadCascosDao().deleteAll();  // primero hijos
                db.reservaDao().deleteAll();
                db.quadDao().deleteAll();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        // Limpiar BD tras cada test

    }

    // =========================================================
    // Partición EC1 – precio NO cambia al modificar quad
    // =========================================================

    /**
     * EC1 (válida): Se crea una reserva con un quad a 50 €/día durante 2 días
     * → precio esperado = 100 €. Después se cambia el precio del quad a 200 €/día.
     * El precio almacenado en la reserva debe seguir siendo 100 €.
     */
    @Test
    public void precioReservaSeMantieneTrasModificarPrecioQuad() throws Exception {

        // --- ARRANGE ---
        // 1. Insertar un quad monoplaza a 50 €/día
        Quad quad = new Quad("1234ABC", false, 50.0, "test"); // false = monoplaza
        Quad_Reserva_RoomDataBase.databaseWriteExecutor
                .submit(() -> quadDao.insert(quad))
                .get(5, TimeUnit.SECONDS);

        // 2. Insertar una reserva válida (recogida mañana mañana, devolución en 3 días)
        //    horaRecogida=false (mañana), horaDevolucion=false (mañana)
        //    → calcularDiasReserva devuelve 2.0 días
        Reserva reserva = new Reserva(
                "Ana García", "612345678",
                fechaRecogida, false,
                fechaDevolucion, false
        );

        long idReserva = reservaRepository.insert(reserva);
        assertTrue("La reserva debe insertarse correctamente", idReserva > 0);
        reserva.setId((int) idReserva);

        // 3. Asociar el quad a la reserva (0 cascos) y recalcular precio
        Map<String, Integer> cascos = new HashMap<>();
        cascos.put("1234ABC", 0);
        quadCascosRepository.updateCascos((int) idReserva, cascos);

        // 4. Recalcular precio: 2 días × 50 €/día = 100 €
        CountDownLatch latch = new CountDownLatch(1);
        Quad_Reserva_RoomDataBase.databaseWriteExecutor.execute(() -> {
            double dias = DateUtils.calcularDiasReserva(
                    fechaRecogida, false, fechaDevolucion, false);
            double precioDiario = Quad_Reserva_RoomDataBase
                    .getDatabase(app).reservaQuadCascosDao()
                    .getPrecioDiarioReserva((int) idReserva);
            double total = dias * precioDiario;
            reservaDao.updatePrecio((int) idReserva, total);
            latch.countDown();
        });
        latch.await(5, TimeUnit.SECONDS);

        // Verificar precio inicial
        Reserva reservaAntes = reservaRepository.getReservaByIdSync((int) idReserva);
        assertEquals("Precio inicial debe ser 100.0 €",
                100.0, reservaAntes.getPrecioTotal(), 0.01);

        // --- ACT: modificar el precio del quad a 200 €/día ---
        Quad quadModificado = new Quad("1234ABC", false, 200.0, "test");
        Quad_Reserva_RoomDataBase.databaseWriteExecutor
                .submit(() -> quadDao.update(quadModificado))
                .get(5, TimeUnit.SECONDS);

        // --- ASSERT: el precio de la reserva NO debe haber cambiado ---
        Reserva reservaDespues = reservaRepository.getReservaByIdSync((int) idReserva);
        assertEquals(
                "El precio de la reserva debe mantenerse en 100.0 € aunque el quad suba a 200 €/día",
                100.0, reservaDespues.getPrecioTotal(), 0.01
        );
    }

    // =========================================================
    // Partición EC2 – precio se calcula correctamente al crear
    // =========================================================

    /**
     * EC2 (válida): Verificar que el precio se calcula correctamente en la
     * creación (precio del quad × días). Base para confirmar que EC1 es significativo.
     */
    @Test
    public void precioInicialCalculadoCorrectamente() throws Exception {

        // Quad a 30 €/día, 2 días → esperado 60 €
        Quad quad = new Quad("9999ZZZ", false, 30.0, "test");
        Quad_Reserva_RoomDataBase.databaseWriteExecutor
                .submit(() -> quadDao.insert(quad))
                .get(5, TimeUnit.SECONDS);

        Reserva reserva = new Reserva(
                "Luis Pérez", "699887766",
                fechaRecogida, false,
                fechaDevolucion, false
        );
        long idReserva = reservaRepository.insert(reserva);
        assertTrue(idReserva > 0);

        Map<String, Integer> cascos = new HashMap<>();
        cascos.put("9999ZZZ", 0);
        quadCascosRepository.updateCascos((int) idReserva, cascos);

        CountDownLatch latch = new CountDownLatch(1);
        Quad_Reserva_RoomDataBase.databaseWriteExecutor.execute(() -> {
            double dias = DateUtils.calcularDiasReserva(
                    fechaRecogida, false, fechaDevolucion, false);
            double precioDiario = Quad_Reserva_RoomDataBase
                    .getDatabase(app).reservaQuadCascosDao()
                    .getPrecioDiarioReserva((int) idReserva);
            reservaDao.updatePrecio((int) idReserva, dias * precioDiario);
            latch.countDown();
        });
        latch.await(5, TimeUnit.SECONDS);

        Reserva r = reservaRepository.getReservaByIdSync((int) idReserva);
        assertEquals("Precio inicial: 30 €/día × 2 días = 60 €",
                60.0, r.getPrecioTotal(), 0.01);
    }

    // =========================================================
    // Partición EC3 – varios quads: precio no cambia si se modifica uno
    // =========================================================

    /**
     * EC3 (válida): Reserva con dos quads. Se modifica el precio de uno.
     * El precio total de la reserva debe permanecer igual.
     */
    @Test
    public void precioConVariosQuadsSeMantieneTrasModificacion() throws Exception {

        Quad q1 = new Quad("AAA001", false, 40.0, "test"); // 40 €/día
        Quad q2 = new Quad("BBB002", true,  60.0, "test"); // 60 €/día (biplaza)

        Quad_Reserva_RoomDataBase.databaseWriteExecutor.submit(() -> {
            quadDao.insert(q1);
            quadDao.insert(q2);
        }).get(5, TimeUnit.SECONDS);

        Reserva reserva = new Reserva(
                "María López", "655443322",
                fechaRecogida, false,
                fechaDevolucion, false
        );
        long idReserva = reservaRepository.insert(reserva);
        assertTrue(idReserva > 0);

        // 2 días × (40 + 60) = 200 €
        Map<String, Integer> cascos = new HashMap<>();
        cascos.put("AAA001", 0);
        cascos.put("BBB002", 1);
        quadCascosRepository.updateCascos((int) idReserva, cascos);

        CountDownLatch latch = new CountDownLatch(1);
        Quad_Reserva_RoomDataBase.databaseWriteExecutor.execute(() -> {
            double dias = DateUtils.calcularDiasReserva(
                    fechaRecogida, false, fechaDevolucion, false);
            double precioDiario = Quad_Reserva_RoomDataBase
                    .getDatabase(app).reservaQuadCascosDao()
                    .getPrecioDiarioReserva((int) idReserva);
            reservaDao.updatePrecio((int) idReserva, dias * precioDiario);
            latch.countDown();
        });
        latch.await(5, TimeUnit.SECONDS);

        Reserva antes = reservaRepository.getReservaByIdSync((int) idReserva);
        assertEquals("Precio inicial: 2 días × 100 €/día = 200 €",
                200.0, antes.getPrecioTotal(), 0.01);

        // Modificar precio de q2 a 999 €/día
        Quad q2Modificado = new Quad("BBB002", true, 999.0, "test");
        Quad_Reserva_RoomDataBase.databaseWriteExecutor
                .submit(() -> quadDao.update(q2Modificado))
                .get(5, TimeUnit.SECONDS);

        Reserva despues = reservaRepository.getReservaByIdSync((int) idReserva);
        assertEquals(
                "El precio total debe seguir siendo 200 € tras modificar el quad a 999 €/día",
                200.0, despues.getPrecioTotal(), 0.01
        );
    }
}