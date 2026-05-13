package es.unizar.eina.g222_quads;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;

import es.unizar.eina.g222_quads.database.Quad_Reserva_RoomDataBase;
import es.unizar.eina.g222_quads.database.Reserva;
import es.unizar.eina.g222_quads.database.ReservaRepository;
import es.unizar.eina.g222_quads.ui.quads.G222_quads;

@RunWith(AndroidJUnit4.class)
public class ReservaVolumeTest {

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

    // Helper para obtener el ReservaRepository fuera del hilo de UI
    private ReservaRepository getReservaRepo() {
        ReservaRepository[] holder = new ReservaRepository[1];
        scenarioRule.getScenario().onActivity(activity -> {
            holder[0] = activity.getReservaRepositoryMain();
        });
        return holder[0];
    }


    @Before
    public void setup() {
        // Resetea la instancia estática de Room en el hilo de UI
        scenarioRule.getScenario().onActivity(activity -> {
            Quad_Reserva_RoomDataBase db = Quad_Reserva_RoomDataBase.getDatabase(activity);
            if (db.isOpen()) {
                db.close();
            }
            Quad_Reserva_RoomDataBase.resetInstance();
        });

        // Limpia la BD — el bucle corre en el hilo del test, no en UI
        try {
            getReservaRepo().deleteAll().get();
        } catch (Exception e) {
            throw new RuntimeException("Setup falló al limpiar reservas: " + e.getMessage(), e);
        }
    }


    @Test
    public void testInsertManyReservas() {
        ReservaRepository repo = getReservaRepo();

        int total = 20000;
        int lastSuccessful = -1;

        try {
            for (int i = 0; i < total; i++) {
                Reserva r = new Reserva(
                        "Cliente " + i,
                        "600000" + String.format("%03d", i),
                        FECHA_1,
                        false,
                        FECHA_2,
                        true
                );

                long id = repo.insert(r);
                assertNotEquals("Fallo en inserción en índice " + i, -1, id);

                Reserva inserted = repo.getReservaByIdSync((int) id);
                assertNotNull("Registro nulo en índice " + i, inserted);

                lastSuccessful = i;
            }
        } catch (Exception e) {
            android.util.Log.e("VOLUME_TEST", "Excepción en índice: " + (lastSuccessful + 1));
            android.util.Log.e("VOLUME_TEST", "Último índice exitoso: " + lastSuccessful);
            android.util.Log.e("VOLUME_TEST", "Causa: " + e.getMessage(), e);
            throw new RuntimeException(
                    "Fallo en índice " + (lastSuccessful + 1) +
                            " (último OK: " + lastSuccessful + "): " + e.getMessage(), e
            );
        }
    }

    @Test
    public void testExtremeReservaVolume() {
        ReservaRepository repo = getReservaRepo();

        int total = 200000;
        int lastSuccessful = -1;
        long startTime = System.currentTimeMillis();

        try {
            for (int i = 0; i < total; i++) {
                Reserva r = new Reserva(
                        "Cliente Masivo Num " + i,
                        "6" + String.format("%08d", i),
                        FECHA_1 + (i * 1000L),
                        (i % 2 == 0),
                        FECHA_2 + (i * 1000L),
                        (i % 2 != 0)
                );

                long id = repo.insert(r);

                if (i % 100 == 0) {
                    assertNotEquals("Fallo en inserción crítica " + i, -1, id);
                }

                lastSuccessful = i;
            }
        } catch (Exception e) {
            long failTime = System.currentTimeMillis() - startTime;
            android.util.Log.e("STRESS_TEST", "=======================================");
            android.util.Log.e("STRESS_TEST", "EXCEPCIÓN EN ÍNDICE: " + (lastSuccessful + 1));
            android.util.Log.e("STRESS_TEST", "Último índice exitoso: " + lastSuccessful);
            android.util.Log.e("STRESS_TEST", "Tiempo hasta el fallo: " + failTime + " ms");
            android.util.Log.e("STRESS_TEST", "Causa: " + e.getMessage(), e);
            android.util.Log.e("STRESS_TEST", "=======================================");
            throw new RuntimeException(
                    "Fallo en índice " + (lastSuccessful + 1) +
                            " tras " + failTime + " ms: " + e.getMessage(), e
            );
        }

        long totalTime = System.currentTimeMillis() - startTime;
        android.util.Log.i("STRESS_TEST", "Total: " + total + " | Tiempo: " + totalTime + " ms");
    }

    @After
    public void tearDown() {
        // Limpiamos la base de datos después de cada test para no dejar basura
        // y que el siguiente test empiece de cero.
        try {
            getReservaRepo().deleteAll().get();
        } catch (Exception e) {
            android.util.Log.e("TEARDOWN", "Error limpiando la base de datos: " + e.getMessage());
        }
    }

}