package es.unizar.eina.g222_quads;

import static org.junit.Assert.assertEquals;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import es.unizar.eina.g222_quads.database.Quad;
import es.unizar.eina.g222_quads.database.QuadRepository;
import es.unizar.eina.g222_quads.ui.quads.G222_quads;

@RunWith(AndroidJUnit4.class)
public class QuadOverloadTest {

    @Rule
    public ActivityScenarioRule<G222_quads> scenarioRule =
            new ActivityScenarioRule<>(G222_quads.class);

    @Before
    public void setup() {
        scenarioRule.getScenario().onActivity(activity -> {
            QuadRepository repo = activity.getQuadRespositoryMain();
            try {
                repo.deleteAll().get();
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
    public void testInsertLargeDescriptions() {
        scenarioRule.getScenario().onActivity(activity -> {
            QuadRepository repo = activity.getQuadRespositoryMain();

            int[] longitudes = {10, 50, 100, 500, 1000};

            for (int i = 0; i < longitudes.length; i++) {
                Quad q = new Quad(
                        String.format("%04dBBB", i),
                        true,
                        10.0,
                        generarCadena(longitudes[i])
                );
                try {
                    repo.insert(q).get();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            int count = repo.numQuads();
            assertEquals("Debe haber tantos quads como pruebas", longitudes.length, count);
        });
    }
}