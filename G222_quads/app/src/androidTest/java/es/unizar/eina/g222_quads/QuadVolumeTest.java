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
public class QuadVolumeTest {

    @Rule
    public ActivityScenarioRule<G222_quads> scenarioRule =
            new ActivityScenarioRule<>(G222_quads.class);

    @Before
    public void setup() {
        scenarioRule.getScenario().onActivity(activity -> {
            try {
                activity.getQuadRespositoryMain().deleteAll().get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    public void testInsertManyQuads() {
        scenarioRule.getScenario().onActivity(activity -> {
            QuadRepository repo = activity.getQuadRespositoryMain();

            int total = 100;

            for (int i = 0; i < total; i++) {
                String matricula = String.format("%04dAAA", i);
                Quad q = new Quad(matricula, true, 10.0, "Quad " + i);

                try {
                    repo.insert(q).get();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            int count = repo.numQuads();
            assertEquals("Debe haber exactamente 100 quads", total, count);
        });
    }
}