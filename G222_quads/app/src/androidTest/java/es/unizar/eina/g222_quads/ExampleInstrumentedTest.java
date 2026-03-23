package es.unizar.eina.g222_quads;

import android.content.Context;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import es.unizar.eina.g222_quads.database.Quad;
import es.unizar.eina.g222_quads.database.QuadRepository;
import es.unizar.eina.g222_quads.ui.quads.G222_quads;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Rule
    public ActivityScenarioRule<G222_quads> scenarioRule = new ActivityScenarioRule<>(G222_quads.class);

    @Test
    public void testCreationIncreasesNumberOfNotes() {
        scenarioRule.getScenario().onActivity(activity -> {
            // Acceso al repositorio a través de la actividad
            int num = 0;
            int num2 = 0;
            QuadRepository quadRepository = activity.getQuadRespositoryMain();
            num = quadRepository.numQuads();
            Quad q = new Quad("6767ABC", true, 65.0, "Rojo");
            quadRepository.insert(q);
            num2 = quadRepository.numQuads();
            assertEquals(num,num2);
        });
    }

    @After
    public void tearDown(){
        scenarioRule.getScenario().onActivity(activity -> {
            QuadRepository quadRepository = activity.getQuadRespositoryMain();
            quadRepository.deleteByMatricula("6767ABC");
        });
    }



























    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("es.unizar.eina.g222_quads", appContext.getPackageName());
    }
}