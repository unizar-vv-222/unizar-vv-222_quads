package es.unizar.eina.g222_quads;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollTo;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.hasSibling;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import android.view.View;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import es.unizar.eina.g222_quads.database.Quad;
import es.unizar.eina.g222_quads.ui.quads.G222_quads;

public class EspressoTest {

    private static final String TEST_NAME = "Espresso";

    private static final String TEST_MATRICULA_NUM = "123";
    private static final String TEST_MATRICULA_LET = "ABC";

    private static final String TEST_UPDATED_TITLE =
            TEST_NAME + " Updated Quad Description";

    private static final String TEST_PRICE = "10.2";

    private static final String TEST_DESCRIPTION =
            TEST_NAME + " Quad Description";

    private static final int TEST_NUMBER_OF_QUADS = 4;

    @Rule
    public ActivityScenarioRule<G222_quads> scenarioRule =
            new ActivityScenarioRule<>(G222_quads.class);

    @Before
    public void borrarQuadsIniciales() {

        scenarioRule.getScenario().onActivity(activity -> {
            try {
                activity.getQuadRespositoryMain().deleteAll().get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    public void testAddAndUpdateQuads() {

        // Entrar en sección quads
        onView(ViewMatchers.withId(R.id.quad)).perform(click());

        // Verificar RecyclerView visible
        onView(withId(R.id.recyclerview))
                .check(matches(isDisplayed()));

        for (int i = 0; i < TEST_NUMBER_OF_QUADS; i++) {

            // Abrir formulario
            onView(withId(R.id.fab)).perform(click());

            // Verificar formulario visible
            onView(withId(R.id.matricula))
                    .check(matches(isDisplayed()));

            // Matrícula
            final String matricula =
                    TEST_MATRICULA_NUM + i + TEST_MATRICULA_LET;

            onView(withId(R.id.matricula))
                    .perform(replaceText(matricula));

            // Precio
            onView(withId(R.id.precio))
                    .perform(replaceText(TEST_PRICE));

            // Descripción
            final String descripcion =
                    TEST_DESCRIPTION + " " + i;

            onView(withId(R.id.descripcion))
                    .perform(replaceText(descripcion));

            // Tipo quad (force click)
            if (i % 2 == 0) {

                onView(withId(R.id.tipo_monoplaza))
                        .perform(forceClick());

            } else {

                onView(withId(R.id.tipo_biplaza))
                        .perform(forceClick());
            }

            // Guardar
            goBackToQuadsList(i, false);

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Verificar RecyclerView
            onView(withId(R.id.recyclerview))
                    .perform(scrollTo(
                            hasDescendant(withText(matricula))
                    ));

            onView(allOf(
                    withId(R.id.textView),
                    withText(matricula)
            )).check(matches(isDisplayed()));

            // Verificar BD
            scenarioRule.getScenario().onActivity(activity -> {

                Quad actualQuad =
                        activity.getQuadRespositoryMain()
                                .getQuadByMatriculaSync(matricula);

                assertQuadEquals(
                        matricula,
                        descripcion,
                        actualQuad
                );
            });

            // EDITAR
            onView(withId(R.id.recyclerview))
                    .perform(scrollTo(
                            hasDescendant(withText(matricula))
                    ));

            onView(allOf(
                    withId(R.id.btn_edit),
                    withParent(hasSibling(withText(matricula)))
            )).perform(click());

            // Verificar pantalla edición
            onView(withId(R.id.descripcion))
                    .check(matches(isDisplayed()));
            // Nueva descripción
            final String updatedDescription =
                    TEST_UPDATED_TITLE + " " + i;

            onView(withId(R.id.descripcion))
                    .perform(replaceText(updatedDescription));
            // Guardar cambios
            goBackToQuadsList(i, false);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Verificar RecyclerView
            onView(withId(R.id.recyclerview))
                    .perform(scrollTo(
                            hasDescendant(withText(matricula))
                    ));
            onView(withText(matricula))
                    .check(matches(isDisplayed()));
            // Verificar BD actualizada
            scenarioRule.getScenario().onActivity(activity -> {
                Quad actualQuad =
                        activity.getQuadRespositoryMain()
                                .getQuadByMatriculaSync(matricula);
                assertQuadEquals(
                        matricula,
                        updatedDescription,
                        actualQuad
                );
            });
        }
    }

    private void goBackToQuadsList(int noteNumber,
                                   boolean errorIntroduction) {
        if (errorIntroduction) {
            if (noteNumber % 2 == 0) {
                onView(withId(R.id.button_save))
                        .perform(click());
            } else {
                pressBack();
            }
        } else {
            onView(withId(R.id.button_save))
                    .perform(click());
        }
    }

    @After
    public void borrarQuads() {

        // Borrar quads creados
        for (int i = 0; i < TEST_NUMBER_OF_QUADS; i++) {

            try {
                String matricula =
                        TEST_MATRICULA_NUM + i + TEST_MATRICULA_LET;

                onView(withId(R.id.recyclerview))
                        .perform(scrollTo(
                                hasDescendant(withText(matricula))
                        ));
                onView(allOf(
                        withId(R.id.btn_delete),
                        withParent(hasSibling(withText(matricula)))
                )).perform(click());

                onView(withText(R.string.delete))
                        .perform(click());
                Thread.sleep(300);
            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }

    private void assertQuadEquals(String mat,
                                  String desc,
                                  Quad quad) {
        assertThat(
                quad.getMatricula(),
                is(equalTo(mat))
        );
        assertThat(
                quad.getDescripcion(),
                is(equalTo(desc))
        );
    }

    // FORCE CLICK
    public static ViewAction forceClick() {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isEnabled();
            }
            @Override
            public String getDescription() {
                return "force click";
            }
            @Override
            public void perform(UiController uiController,
                                View view) {

                view.performClick();
            }
        };
    }
}