package es.unizar.eina.g222_quads;

import static org.junit.Assert.assertEquals;

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
public class QuadUpdateTest {

    @Rule
    public ActivityScenarioRule<G222_quads> scenarioRule = new ActivityScenarioRule<>(G222_quads.class);

    @Before
    public void setup() {
        scenarioRule.getScenario().onActivity(activity -> {
            activity.getQuadRespositoryMain().deleteAll();
        });
    }

    @Test (expected = RuntimeException.class)
    public void testUpdateTypeValid() {
        scenarioRule.getScenario().onActivity(activity -> {
            QuadRepository repo = activity.getQuadRespositoryMain();
            Quad q = new Quad("1234ABC", true, 65.0, "Rojo");

            try {
                // Usamos get para forzar la espera de la inserción
                repo.insert(q).get();

                q.setTipo(false);
                repo.update(q).get();

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    public void testUpdateTypeInvlid() {
        scenarioRule.getScenario().onActivity(activity -> {
            QuadRepository repo = activity.getQuadRespositoryMain();
            Quad q = new Quad("1234ABC", true, 65.0, "Rojo");

            try {
                // Usamos get para forzar la espera de la inserción
                repo.insert(q).get();

                q.setTipo(null);
                repo.update(q).get();

                Quad recuperado = repo.getQuadByMatriculaSync("1234ABC");
                assertEquals(false, recuperado.getTipo());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test (expected = NoSuchElementException.class)
    public void testUpdateNonExistent() {
        scenarioRule.getScenario().onActivity(activity -> {
            QuadRepository repo = activity.getQuadRespositoryMain();
            Quad qNoExiste = new Quad("0000ZZZ", true, 65.0, "No existe");
            repo.update(qNoExiste);
        });
    }

    @Test (expected = IllegalArgumentException.class)
    public void testUpdateInvalidPrice() {
        scenarioRule.getScenario().onActivity(activity -> {
            QuadRepository repo = activity.getQuadRespositoryMain();
            Quad q = new Quad("1234ABC", true, 65.0, "Rojo");
            repo.insert(q);

            // Caso Precio Negativo
            q.setPrecio(-20.0);
            repo.update(q);

        });
    }

    @Test (expected = IllegalArgumentException.class)
    public void testUpdateInvalidDescription() {
        scenarioRule.getScenario().onActivity(activity -> {
            QuadRepository repo = activity.getQuadRespositoryMain();
            Quad q = new Quad("1234ABC", true, 65.0, "Rojo");
            repo.insert(q);

            // Caso Descripción Null (Si tu entidad tiene @NonNull)
            q.setPrecio(65.0); // restauramos
            q.setDescripcion(null);
            repo.update(q);

        });
    }
}
