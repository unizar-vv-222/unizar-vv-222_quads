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

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class QuadCreationTest {
    @Rule
    public ActivityScenarioRule<G222_quads> scenarioRule = new ActivityScenarioRule<>(G222_quads.class);

    @Before
    public void setup() {
        // Limpiamos antes de cada test para tener un entorno controlado
        scenarioRule.getScenario().onActivity(activity -> {
            activity.getQuadRespositoryMain().deleteAll();
        });
    }

    // 1. TEST DE PRECIO (Lógica de límite)
    @Test (expected = IllegalArgumentException.class)
    public void testInsertNegativePrice() {
        scenarioRule.getScenario().onActivity(activity -> {
            QuadRepository repo = activity.getQuadRespositoryMain();
            // Intentamos insertar precio negativo
            repo.insert(new Quad("9999ZZZ", true, -10.0, "Precio inválido"));
        });
    }

    // 2. TEST DE MATRÍCULA REPETIDA (Restricción de Primary Key)
    @Test//(expected = android.database.sqlite.SQLiteConstraintException.class)
    public void testInsertDuplicateMatricula() {
        scenarioRule.getScenario().onActivity(activity -> {
            QuadRepository repo = activity.getQuadRespositoryMain();

            // Insertamos el primero MONOPLAZA
            repo.insert(new Quad("1111AAA", true, 50.0, "Original_MONOPLAZA"));

            // Insertamos el primero BIPLAZA
            repo.insert(new Quad("2222AAA", false, 50.0, "Original_BIPLAZA"));
            int cont_prev = repo.numQuads();

            // El segundo con la misma PK debería lanzar la excepción y el test pasará
            repo.insert(new Quad("1111AAA", false, 60.0, "Duplicado"));
            int cont_post = repo.numQuads();

            //La segunda inserción no se ha ejecutado debido a que la matricula está duplicada
            assertEquals(cont_prev , cont_post);
        });
    }

    // 3. TEST DE DESCRIPCIÓN (Campos obligatorios no nulos)
    @Test (expected = IllegalArgumentException.class)
    public void testInsertNullDescription() {
        scenarioRule.getScenario().onActivity(activity -> {
            QuadRepository repo = activity.getQuadRespositoryMain();
            repo.insert(new Quad("2222BBB", true, 50.0, null)); // Descripción vacía

        });
    }


    /*
    // 4. TEST DE TIPO (Nulos) - No es posible
    @Test (expected = IllegalArgumentException.class)
    public void testInsertNullType() {
        scenarioRule.getScenario().onActivity(activity -> {
            QuadRepository repo = activity.getQuadRespositoryMain();
            repo.insert(new Quad("3333CCC", null, 50.0, "Tipo nulo"));
        });
    }
     */

    @Test (expected = IllegalArgumentException.class)
    public void testInsertInvalidMat() {
        scenarioRule.getScenario().onActivity(activity -> {
            QuadRepository repo = activity.getQuadRespositoryMain();
            // Intentamos insertar precio negativo
            repo.insert(new Quad("99ZZ", false, 10.0, "Matrícula inválida"));
        });
    }

}