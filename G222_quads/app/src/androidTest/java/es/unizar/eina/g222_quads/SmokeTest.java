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
public class SmokeTest {

    @Rule
    public ActivityScenarioRule<G222_quads> scenarioRule = new ActivityScenarioRule<>(G222_quads.class);

    @Test
    public void testCreationIncreasesNumberOfNotes() {
        scenarioRule.getScenario().onActivity(activity -> {
            // Acceso al repositorio a través de la actividad
            QuadRepository quadRepository = activity.getQuadRespositoryMain();
            int cont_prev = quadRepository.numQuads();
            try {
                Quad q = new Quad("6767ABC", true, 65.0, "Rojo");
                quadRepository.insert(q).get();
                int cont_post = quadRepository.numQuads();
                assertEquals(cont_prev + 1, cont_post);
            } catch (Exception e) {
                fail(e.getMessage());
            }
        });
    }

    @Test
    public void testCreationVerificationInfo() {
        scenarioRule.getScenario().onActivity(activity -> {
            // Acceso al repositorio a través de la actividad
            QuadRepository quadRepository = activity.getQuadRespositoryMain();

            String matricula = "1234ABC";
            Boolean tipo = true;
            Double precio = 55.0;
            String descripcion = "Verde";
            Quad quadOriginal = new Quad(matricula, tipo, precio, descripcion);

            // 2. Insertamos el quad
            try {
                quadRepository.insert(quadOriginal).get();
            } catch (Exception e) {
                fail(e.getMessage());
            }

            // 3. Recuperamos el quad (necesitarás un método en el repo que busque por matrícula)
            Quad quadRecuperado = quadRepository.getQuadByMatriculaSync(matricula);

            // 4. Verificación exhaustiva campo por campo [cite: 120]
            assertNotNull("El quad no debería ser nulo", quadRecuperado);
            assertEquals("La matrícula no coincide", matricula, quadRecuperado.getMatricula());
            assertEquals("El tipo no coincide", tipo, quadRecuperado.getTipo());

            // Usamo un delta para verificar que no haya imprecisiones a la hora de guardar, por ejemplo en formato binario
            assertEquals("El precio no coincide", precio, quadRecuperado.getPrecio(), 0.01);
            assertEquals("La descipción no coincide", descripcion, quadRecuperado.getDescripcion());
        });
    }

    @After
    public void tearDown(){
        scenarioRule.getScenario().onActivity(activity -> {
            QuadRepository quadRepository = activity.getQuadRespositoryMain();
            try {
                quadRepository.deleteByMatricula("6767ABC").get();  // Esperar
                quadRepository.deleteByMatricula("1234ABC").get();  // Esperar
            } catch (Exception e) {
                // Ignorar si no existen
            }
        });
    }



























    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("es.unizar.eina.g222_quads", appContext.getPackageName());
    }
}