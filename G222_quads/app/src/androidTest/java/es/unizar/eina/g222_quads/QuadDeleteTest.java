package es.unizar.eina.g222_quads;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import java.util.NoSuchElementException;

import es.unizar.eina.g222_quads.database.Quad;
import es.unizar.eina.g222_quads.database.QuadRepository;
import es.unizar.eina.g222_quads.ui.quads.G222_quads;

@RunWith(AndroidJUnit4.class)
public class QuadDeleteTest {

    @Rule
    public ActivityScenarioRule<G222_quads> scenarioRule = new ActivityScenarioRule<>(G222_quads.class);

    @Before
    public void setup() {
        // Limpiamos la base de datos antes de cada test
        scenarioRule.getScenario().onActivity(activity -> {
            try {
                activity.getQuadRespositoryMain().deleteAll().get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void testDeleteExistingQuad() {
        scenarioRule.getScenario().onActivity(activity -> {
            QuadRepository repo = activity.getQuadRespositoryMain();
            Quad q = new Quad("1234ABC", true, 65.0, "Para borrar");

            try {
                // 1. Insertamos y ESPERAMOS
                repo.insert(q).get();
                assertEquals("Debería haber 1 quad", 1, repo.numQuads());

                // 2. Borramos y ESPERAMOS
                repo.deleteByMatricula("1234ABC").get();

                // 3. Verificamos que ya no existe
                assertNull("El quad debería haber sido borrado", repo.getQuadByMatriculaSync("1234ABC"));
                assertEquals("La base de datos debería estar vacía", 0, repo.numQuads());

            } catch (Exception e) {
                org.junit.Assert.fail("Error en la sincronización del borrado: " + e.getMessage());
            }
        });
    }

    @Test
    public void testDeleteNonExistentQuad() {
        scenarioRule.getScenario().onActivity(activity -> {
            QuadRepository repo = activity.getQuadRespositoryMain();

            try {
                // Intentamos borrar algo que no existe
                // Room devuelve 0 filas afectadas, pero no lanza excepción
                int filasAfectadas = repo.deleteByMatricula("9999ZZZ").get();

                assertEquals("No debería haberse borrado ninguna fila", 0, filasAfectadas);
            } catch (Exception e) {
                org.junit.Assert.fail("No debería lanzar excepción al borrar inexistente: " + e.getMessage());
            }
        });
    }

    @Test
    public void testDeleteAll() {
        scenarioRule.getScenario().onActivity(activity -> {
            QuadRepository repo = activity.getQuadRespositoryMain();
            try {
                // Insertamos varios
                repo.insert(new Quad("1111AAA", true, 10.0, "Q1")).get();
                repo.insert(new Quad("2222BBB", true, 20.0, "Q2")).get();

                assertEquals("Deberían haber 2 quads", 2, repo.numQuads());

                // Borramos todo
                repo.deleteAll().get();

                assertEquals("Debería haber 0 quads", 0, repo.numQuads());
            } catch (Exception e) {
                org.junit.Assert.fail("Error en deleteAll: " + e.getMessage());
            }
        });
    }
}
