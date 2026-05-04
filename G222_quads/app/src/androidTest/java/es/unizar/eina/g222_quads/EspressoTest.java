package es.unizar.eina.g222_quads;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollTo;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.hasSibling;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

import es.unizar.eina.g222_quads.database.Quad;

//import es.unizar.eina.notepad.ui.Notepad;

// Importo varios UI porque no tengo claro cual voy a necesitar
import es.unizar.eina.g222_quads.ui.quads.G222_QuadsList;
import es.unizar.eina.g222_quads.ui.quads.G222_quads;


public class EspressoTest {
    private static final String TEST_NAME  = "Espresso";

    private static final String TEST_MATRICULA_NUM = "123";
    private static final String TEST_MATRICULA_LET = "ABC";
    private static final String TEST_UPDATED_TITLE = TEST_NAME + " Updated Quad Description";
    private static final String TEST_PRICE =  "10.2";
    private static final String TEST_DESCRIPTION = TEST_NAME + " Quad Description";



    private static final int TEST_NUMBER_OF_QUADS = 4;

    @Rule
    public ActivityScenarioRule<G222_quads> scenarioRule = new ActivityScenarioRule<>(G222_quads.class);

    @Test
    public void testAddAndUpdateQuads() {
        // Accedemos a la sección de quads
        onView(ViewMatchers.withId(R.id.quad)).perform(click());

        // Esperamos para verificar que entre en la siguiente pantalla
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));

        for (int i = 0; i < TEST_NUMBER_OF_QUADS; i++) {

            // Accedemos a añadir quad
            onView(withId(R.id.fab)).perform(click());



            // Se asegura de que la actividad actual es QuadModify comprobando que hay un EditText para el matricula
            onView(withId(R.id.matricula)).check(matches(isDisplayed()));

            // En la matricula inserta
            final String matricula = TEST_MATRICULA_NUM + i + TEST_MATRICULA_LET;
            // Utilizo «replaceText» en lugar de «typeText» para evitar modificaciones por parte del autocorrector
            onView(withId(R.id.matricula)).perform(replaceText(matricula), closeSoftKeyboard());

            // En el precio inserta un precio genérico para las pruebas
            final String precio = TEST_PRICE;
            // Utilizo «replaceText» en lugar de «typeText» para evitar modificaciones por parte del autocorrector
            onView(withId(R.id.precio)).perform(replaceText(precio), closeSoftKeyboard());

            // En la descripción inserta "Espresso Note Description <i>"
            final String descripcion = TEST_DESCRIPTION + " " + i;
            // Utilizo «replaceText» en lugar de «typeText» para evitar modificaciones por parte del autocorrector
            onView(withId(R.id.descripcion)).perform(replaceText(descripcion), closeSoftKeyboard());

            // En el tipo altenrmos entre biplaza y monoplaza, dependiendo de i
            if (i % 2 == 0) {
                onView(withId(R.id.tipo_monoplaza)).perform(click());
            } else {
                onView(withId(R.id.tipo_biplaza)).perform(click());
            }


            goBackToQuadsList(i, false);

            // Aserción: comprobación de que la nota se visualiza en el listado
            onView(withId(R.id.recyclerview))
                    .perform(scrollTo(hasDescendant(withText(matricula))));

            // Verificamos que el TextView (R.id.textView) tiene la matrícula correcta
            onView(allOf(withId(R.id.textView), withText(matricula)))
                    .check(matches(isDisplayed()));

            // Aserción: comprobación de que la nota está en la base de datos
            scenarioRule.getScenario().onActivity(activity -> {
                Quad actualQuad = activity.getQuadRespositoryMain().getQuadByMatriculaSync(matricula);
                assertQuadEquals(matricula, descripcion, actualQuad);
            });

            // Actualización
            onView(withId(R.id.recyclerview)).perform(scrollTo(hasDescendant(withText(matricula))));
            onView(allOf(
                    withId(R.id.btn_edit),
                    withParent(hasSibling(withText(matricula)))
            )).perform(click());
            // Se asegura de que la actividad actual es QuadModify comprobando que hay un EditText para la descripción
            onView(withId(R.id.descripcion)).check(matches(isDisplayed()));

            // Cambia la descripción por "Espresso Updated Quad Description <i>"
            final String updatedDescription = TEST_UPDATED_TITLE + " " + i;
            // Utilizo «replaceText» en lugar de «typeText» para evitar modificaciones por parte del autocorrector
            onView(withId(R.id.descripcion)).perform(replaceText(updatedDescription), closeSoftKeyboard());

            goBackToQuadsList(i, false);

            // Aserción: comprobación de que la nota se visualiza en el listado
            onView(withId(R.id.recyclerview)).perform(scrollTo(hasDescendant(withText(matricula))));
            onView(withText(matricula)).check(matches(isDisplayed()));

            // Aserción: comprobación de que la nota está en la base de datos
            scenarioRule.getScenario().onActivity(activity -> {
                Quad actualNote = activity.getQuadRespositoryMain().getQuadByMatriculaSync(matricula);
                assertQuadEquals(matricula, updatedDescription, actualNote);
            });
        }
    }

    private void goBackToQuadsList(int noteNumber, boolean errorIntroduction) {

        if (errorIntroduction) {
            // En uno de cada 2 quads confirma; en otra cancela
            if (noteNumber % 2 == 0) {
                // Confirma y vuelve a la actividad anterior
                onView(withId(R.id.button_save)).perform(click());
            } else {
                // Vuelve a la actividad anterior sin confirmar
                pressBack();
            }
        } else {
            onView(withId(R.id.button_save)).perform(click());
        }

    }

    @After
    public void borrarQuads() {
        for (int i = 0; i < TEST_NUMBER_OF_QUADS; i++) {
            // Busca y borra la nota con titulo "Espresso Note Title <i>"
            String matricula = TEST_MATRICULA_NUM + i + TEST_MATRICULA_LET;
            // 1. Scroll hasta la posición
            onView(withId(R.id.recyclerview))
                    .perform(scrollTo(hasDescendant(withText(matricula))));

            // 2. Click en el ImageView de borrar que está en la misma fila que la matrícula
            onView(allOf(
                    withId(R.id.btn_delete),
                    withParent(hasSibling(withText(matricula)))
            )).perform(click());
            onView(withText(R.string.delete)).perform(click());
        }
    }

    private void assertQuadEquals(String mat, String desc, Quad quad) {
        assertThat(quad.getMatricula(), is(equalTo(mat)));
        assertThat(quad.getDescripcion(), is(equalTo(desc)));
    }


}
