package es.unizar.eina.g222_quads;



import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;



import es.unizar.eina.g222_quads.database.Quad;
import es.unizar.eina.g222_quads.database.QuadRepository;
import es.unizar.eina.g222_quads.database.Quad_Reserva_RoomDataBase;
import es.unizar.eina.g222_quads.ui.quads.G222_quads;

@RunWith(AndroidJUnit4.class)
public class QuadVolumeTest {

    @Rule
    public ActivityScenarioRule<G222_quads> scenarioRule =
            new ActivityScenarioRule<>(G222_quads.class);

    // Helper para obtener el QuadRepository fuera del hilo de UI
    private QuadRepository getQuadRepo() {
        QuadRepository[] holder = new QuadRepository[1];
        scenarioRule.getScenario().onActivity(activity -> {
            holder[0] = activity.getQuadRespositoryMain();
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
            getQuadRepo().deleteAll().get();
        } catch (Exception e) {
            throw new RuntimeException("Setup falló al limpiar reservas: " + e.getMessage(), e);
        }
    }

    // TESTS DE QUADS

    @Test
    public void testInsertManyQuads() {
        QuadRepository repo = getQuadRepo();

        int total = 100;
        int lastSuccessful = -1;

        try {
            for (int i = 0; i < total; i++) {
                Quad q = new Quad(
                        String.format("%04dAAA", i),
                        true,
                        25.0,
                        "Quad " + i
                );

                repo.insert(q).get();
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
    public void testExtremeQuadVolume() {
        QuadRepository repo = getQuadRepo();

        int total = 100000;
        int lastSuccessful = -1;
        long startTime = System.currentTimeMillis();

        try {
            for (int i = 0; i < total; i++) {
                char l1 = (char) ('A' + (i / 676) % 26);
                char l2 = (char) ('A' + (i / 26) % 26);
                char l3 = (char) ('A' + i % 26);
                String matricula = String.format("%04d", i % 10000) + "" + l1 + l2 + l3;

                Quad q = new Quad(
                        matricula,
                        true,
                        25.0,
                        "Quad " + i
                );

                repo.insert(q).get();
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
    public void clean() {
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
            getQuadRepo().deleteAll().get();
        } catch (Exception e) {
            throw new RuntimeException("Clean falló al limpiar reservas: " + e.getMessage(), e);
        }
    }
}