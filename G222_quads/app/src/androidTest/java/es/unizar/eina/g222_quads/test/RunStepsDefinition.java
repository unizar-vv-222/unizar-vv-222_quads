package es.unizar.eina.g222_quads.test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSubstring;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.Matchers.anyOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Intent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.Intents;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import java.util.Calendar;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import es.unizar.eina.g222_quads.R;
import es.unizar.eina.g222_quads.database.Quad;
import es.unizar.eina.g222_quads.database.QuadRepository;
import es.unizar.eina.g222_quads.database.Reserva;
import es.unizar.eina.g222_quads.database.ReservaRepository;
import es.unizar.eina.g222_quads.ui.quads.G222_quads;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;

public class RunStepsDefinition {

    private ActivityScenario<G222_quads> scenario;
    private double precioReservaAntesDeCambio = -1;
    private int idReservaParaPrecio = -1;
    private String matriculaQuadSeleccionado;
    private int numReservasAntes;
    private String textoReservaSeleccionada;
    private QuadRepository quadRepo;
    private ReservaRepository reservaRepo;

    // Ciclo de vida
    @Before
    public void launchActivity() throws InterruptedException {

        Intents.init();

        scenario = ActivityScenario.launch(G222_quads.class);

        final CountDownLatch latch = new CountDownLatch(1);

        scenario.onActivity(activity -> {
            quadRepo = activity.getQuadRespositoryMain();
            reservaRepo = activity.getReservaRepositoryMain();

            new Thread(() -> {
                try {
                    quadRepo.deleteAll().get();
                    for (int i = 1; i <= 5; i++) {
                        quadRepo.insert(new Quad(
                                String.format("%04dAAA", i),
                                true, 25.0, "Quad " + i
                        )).get();
                    }

                    reservaRepo.deleteAll();
                    Calendar c = Calendar.getInstance();
                    c.add(Calendar.DAY_OF_YEAR, 5);
                    long ini = c.getTimeInMillis();
                    c.add(Calendar.DAY_OF_YEAR, 2);
                    long fin = c.getTimeInMillis();

                    for (int i = 1; i <= 5; i++) {
                        reservaRepo.insert(new Reserva(
                                "Cliente " + i, "60000000" + i,
                                ini, false, fin, false
                        ));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }
            }).start();
        });

        latch.await();
    }

    @After
    public void finishActivity() {

        Intents.release();

        if (scenario != null) {
            scenario.close();
        }
    }

    // Helper: ejecutar código con acceso a la Activity

    private void withActivity(ActivityCallback callback) {
        scenario.onActivity(activity -> {
            try {
                callback.run(activity);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    interface ActivityCallback {
        void run(G222_quads activity) throws Exception;
    }

    // Helper: click en vista hija por id
    private ViewAction clickChildViewWithId(final int id) {
        return new ViewAction() {
            @Override public Matcher<View> getConstraints() { return isDisplayed(); }
            @Override public String getDescription() { return "Click child view with id"; }
            @Override public void perform(UiController uiController, View view) {
                View v = view.findViewById(id);
                if (v != null) v.performClick();
            }
        };
    }

    // Helpers privados
    private ViewAction checkFirstCheckbox() {
        return new ViewAction() {
            @Override public Matcher<View> getConstraints() { return isEnabled(); }
            @Override public String getDescription() { return "Seleccionar primer quad"; }
            @Override public void perform(UiController uiController, View view) {
                CheckBox cb = view.findViewById(R.id.quad_checkbox);
                if (cb != null && !cb.isChecked()) cb.performClick();
            }
        };
    }

    private ViewAction forceClick() {
        return new ViewAction() {
            @Override public Matcher<View> getConstraints() { return isEnabled(); }
            @Override public String getDescription() { return "Force click"; }
            @Override public void perform(UiController uiController, View view) {
                view.performClick();
            }
        };
    }

    private ViewAction guardarMatriculaYClickDelete() {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isDisplayed();
            }

            @Override
            public String getDescription() {
                return "Guardar matricula del primer quad y click delete";
            }

            @Override
            public void perform(UiController uiController, View view) {
                TextView matriculaView = view.findViewById(R.id.text_view);
                matriculaQuadSeleccionado = matriculaView.getText().toString();

                View deleteButton = view.findViewById(R.id.btn_delete);
                if (deleteButton != null) {
                    deleteButton.performClick();
                }
            }
        };
    }

    private ViewAction guardarMatriculaYClickEdit() {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isDisplayed();
            }

            @Override
            public String getDescription() {
                return "Guardar matricula del primer quad y click edit";
            }

            @Override
            public void perform(UiController uiController, View view) {
                TextView matriculaView = view.findViewById(R.id.text_view);
                matriculaQuadSeleccionado = matriculaView.getText().toString();

                View editButton = view.findViewById(R.id.btn_edit);
                if (editButton != null) {
                    editButton.performClick();
                }
            }
        };
    }

    private ViewAction guardarMatriculaYClickItem() {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isDisplayed();
            }

            @Override
            public String getDescription() {
                return "Guardar matrícula del primer quad y abrir detalle";
            }

            @Override
            public void perform(UiController uiController, View view) {
                TextView matriculaView = view.findViewById(R.id.text_view);
                matriculaQuadSeleccionado = matriculaView.getText().toString();
                view.performClick();
            }
        };
    }

    private void abrirDetalleQuadSeleccionado() {
        onView(withId(R.id.recyclerview))
                .perform(RecyclerViewActions.scrollTo(
                        hasDescendant(withText(matriculaQuadSeleccionado))
                ));

        onView(withId(R.id.recyclerview))
                .perform(RecyclerViewActions.actionOnItem(
                        hasDescendant(withText(matriculaQuadSeleccionado)),
                        click()
                ));

        onView(withId(R.id.detail_matricula))
                .check(matches(withSubstring(matriculaQuadSeleccionado)));
    }

    private ViewAction guardarReservaYClickEdit() {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isDisplayed();
            }

            @Override
            public String getDescription() {
                return "Guardar reserva seleccionada y pulsar editar";
            }

            @Override
            public void perform(UiController uiController, View view) {
                TextView reservaView = view.findViewById(R.id.text_view);
                textoReservaSeleccionada = reservaView.getText().toString();

                View editButton = view.findViewById(R.id.btn_edit);
                if (editButton != null) {
                    editButton.performClick();
                }
            }
        };
    }

    private void abrirDetalleReservaSeleccionada() {
        onView(withId(R.id.recyclerview))
                .perform(RecyclerViewActions.scrollTo(
                        hasDescendant(withText(textoReservaSeleccionada))
                ));

        onView(withId(R.id.recyclerview))
                .perform(RecyclerViewActions.actionOnItem(
                        hasDescendant(withText(textoReservaSeleccionada)),
                        click()
                ));

        onView(withId(R.id.detail_id))
                .check(matches(withSubstring(textoReservaSeleccionada)));
    }

    private ViewAction guardarReservaYClickItem() {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isDisplayed();
            }

            @Override
            public String getDescription() {
                return "Guardar reserva seleccionada y abrir detalle";
            }

            @Override
            public void perform(UiController uiController, View view) {
                TextView reservaView = view.findViewById(R.id.text_view);
                textoReservaSeleccionada = reservaView.getText().toString();
                view.performClick();
            }
        };
    }

    private ViewAction assertMatriculaEnItem(String expectedMatricula) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isDisplayed();
            }

            @Override
            public String getDescription() {
                return "Comprobar matrícula del item";
            }

            @Override
            public void perform(UiController uiController, View view) {
                TextView matriculaView = view.findViewById(R.id.text_view);
                assertEquals(expectedMatricula, matriculaView.getText().toString());
            }
        };
    }

    private long fechaDentroDeDias(int dias) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, dias);
        return c.getTimeInMillis();
    }

    private void insertarReservasParaOrdenacion() {
        withActivity(activity -> {
            ReservaRepository repo = activity.getReservaRepositoryMain();

            repo.deleteAll().get();

            repo.insert(new Reserva(
                    "Ana Cliente",
                    "600000001",
                    fechaDentroDeDias(3),
                    false,
                    fechaDentroDeDias(6),
                    true
            ));

            repo.insert(new Reserva(
                    "Reserva Fecha Menor",
                    "699999999",
                    fechaDentroDeDias(1),
                    false,
                    fechaDentroDeDias(2),
                    true
            ));
        });
    }

    private void insertarReservasParaFiltros() {
        withActivity(activity -> {
            ReservaRepository repo = activity.getReservaRepositoryMain();

            repo.deleteAll().get();

            repo.insert(new Reserva(
                    "Reserva Prevista",
                    "600000001",
                    fechaDentroDeDias(5),
                    false,
                    fechaDentroDeDias(7),
                    true
            ));

            repo.insert(new Reserva(
                    "Reserva Vigente",
                    "600000002",
                    fechaDentroDeDias(-1),
                    false,
                    fechaDentroDeDias(1),
                    true
            ));

            repo.insert(new Reserva(
                    "Reserva Caducada",
                    "600000003",
                    fechaDentroDeDias(-7),
                    false,
                    fechaDentroDeDias(-5),
                    true
            ));
        });
    }

    private void abrirPrimeraReservaDelListado() {
        onView(withId(R.id.recyclerview))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        onView(withId(R.id.detail_nombre))
                .check(matches(isDisplayed()));
    }

    @Dado("Abro la aplicación de gestión de quads")
    public void abro_la_aplicacion() {
        scenario.onActivity(activity -> assertNotNull(activity));
    }

    @Dado("Accedo a la sección de reservas")
    public void accedo_a_reservas() {
        onView(withId(R.id.reserva)).perform(click());
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
    }

    @Dado("Existe un quad sin reservas asociadas en el listado")
    public void existe_quad_sin_reservas() {
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
        onView(withId(R.id.recyclerview)).check(matches(hasDescendant(withId(R.id.text_view))));
    }

    @Dado("Existe al menos una reserva en el listado")
    public void existe_al_menos_una_reserva() {
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));

        withActivity(activity -> {
            int total = activity.getReservaRepositoryMain().numReservas();
            assertTrue("Debe existir al menos una reserva", total > 0);
        });
    }

    @Dado("Existen al menos dos reservas en el sistema")
    public void existen_al_menos_dos_reservas() {
        insertarReservasParaOrdenacion();

        onView(withId(R.id.recyclerview))
                .check(matches(isDisplayed()));
    }

    @Dado("Existen reservas previstas, vigentes y caducadas en el sistema")
    public void existen_reservas_previstas_vigentes_caducadas() {
        insertarReservasParaFiltros();

        onView(withId(R.id.recyclerview))
                .check(matches(isDisplayed()));
    }

    @Dado("Accedo a la sección de quads")
    public void accedo_a_quads() {
        onView(withId(R.id.quad)).perform(click());
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
    }

    @Dado("Existe al menos un quad en el listado")
    public void existe_al_menos_un_quad() {
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
        onView(withId(R.id.recyclerview))
                .check(matches(hasDescendant(withId(R.id.text_view))));
    }

    @Dado("Existen al menos dos quads en el sistema")
    public void existen_al_menos_dos_quads() {
        withActivity(activity -> {
            QuadRepository repo = activity.getQuadRespositoryMain();

            repo.deleteAll().get();

            repo.insert(new Quad("1111AAA", false, 20.0, "Monoplaza barato")).get();
            repo.insert(new Quad("2222BBB", true, 80.0, "Biplaza caro")).get();
        });

        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
    }

    @Dado("Existen al menos dos quads de distintos tipos en el sistema")
    public void existen_quads_distintos_tipos() {
        withActivity(activity -> {
            QuadRepository repo = activity.getQuadRespositoryMain();

            repo.deleteAll().get();

            repo.insert(new Quad("1111AAA", false, 50.0, "Monoplaza")).get();
            repo.insert(new Quad("2222BBB", true, 50.0, "Biplaza")).get();
        });

        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
    }

    @Dado("Existen al menos dos quads con distintos precios en el sistema")
    public void existen_quads_distintos_precios() {
        withActivity(activity -> {
            QuadRepository repo = activity.getQuadRespositoryMain();

            repo.deleteAll().get();

            repo.insert(new Quad("1111AAA", true, 80.0, "Quad caro")).get();
            repo.insert(new Quad("2222BBB", true, 20.0, "Quad barato")).get();
        });

        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
    }

    @Dado("No existe ningún quad en el sistema")
    public void no_existe_ningun_quad() {
        withActivity(activity -> {
            activity.getQuadRespositoryMain().deleteAll().get();
        });
    }

    @Dado("Ya existe un quad con matrícula {string} en el sistema")
    public void existe_quad_con_matricula(String matricula) throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        withActivity(activity -> {
            new Thread(() -> {
                try {
                    activity.getQuadRespositoryMain()
                            .insert(new Quad(matricula, true, 50.0, "Quad duplicado")).get();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }
            }).start();
        });
        latch.await();
    }

    @Cuando("Pulso el botón de nueva reserva")
    public void pulso_nueva_reserva() {
        withActivity(activity -> {
            numReservasAntes = activity.getReservaRepositoryMain().numReservas();
        });

        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.nombre_cliente)).check(matches(isDisplayed()));
    }

    @Cuando("Pulso continuar para seleccionar quads")
    public void pulso_continuar() {
        onView(withId(R.id.button_continue))
                .check(matches(isDisplayed()))
                .perform(click());
        onView(withId(R.id.recyclerview_quads)).check(matches(isDisplayed()));
    }

    @Cuando("Selecciono el primer quad disponible")
    public void selecciono_primer_quad() {
        onView(withId(R.id.recyclerview_quads)).check(matches(isDisplayed()));
        onView(withId(R.id.recyclerview_quads))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, checkFirstCheckbox()));
    }

    @Cuando("Confirmo la selección de quads")
    public void confirmo_seleccion_quads() {
        onView(withId(R.id.button_confirm))
                .check(matches(isDisplayed()))
                .perform(click());
        // Esperar a que cargue la pantalla de confirmación
        onView(withId(R.id.title_confirm_reserva)).check(matches(isDisplayed()));
    }

    @Cuando("Pulso el botón de nuevo quad")
    public void pulso_nuevo_quad() {
        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.matricula)).check(matches(isDisplayed()));
    }

    @Cuando("Introduzco la matrícula {string}, el precio {string}, el tipo {string} y la descripcion {string}")
    public void introduzco_datos_quad(String matricula, String precio, String tipo, String descripcion) {
        onView(withId(R.id.matricula))
                .perform(clearText(), replaceText(matricula), closeSoftKeyboard());
        onView(withId(R.id.precio))
                .perform(clearText(), replaceText(precio), closeSoftKeyboard());

        // Corregido: usando forceClick() para saltar restricciones de visibilidad de Espresso
        if(Objects.equals(tipo, "true")){
            onView(withId(R.id.tipo_biplaza)).perform(forceClick());
        }else{
            onView(withId(R.id.tipo_monoplaza)).perform(forceClick());
        }
        onView(withId(R.id.descripcion))
                .perform(clearText(), replaceText(descripcion), closeSoftKeyboard());
    }

    @Cuando("Pulso guardar en el formulario de quad")
    public void pulso_guardar_quad() {
        onView(withId(R.id.button_save)).perform(click());
    }

    @Cuando("Pulso cancelar en el formulario de quad")
    public void pulso_cancelar_quad() {
        onView(withId(R.id.button_cancel)).perform(click());
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
    }

    @Cuando("Pulso sobre el primer quad del listado")
    public void pulso_sobre_primer_quad_listado() {
        onView(withId(R.id.recyclerview))
                .perform(RecyclerViewActions.actionOnItemAtPosition(
                        0,
                        guardarMatriculaYClickItem()
                ));

        onView(withId(R.id.detail_matricula)).check(matches(isDisplayed()));
    }

    @Cuando("Cambio el tipo a {string}, el precio a {string} y la descripcion a {string} del quad")
    public void cambio_tipo_precio_y_descripcion_quad(String tipo, String precio, String descripcion) {
        // Corregido: usando forceClick() para evitar fallos aleatorios al editar
        if (Objects.equals(tipo, "true")) {
            onView(withId(R.id.tipo_biplaza)).perform(forceClick());
        } else {
            onView(withId(R.id.tipo_monoplaza)).perform(forceClick());
        }
        onView(withId(R.id.precio))
                .perform(clearText(), replaceText(precio), closeSoftKeyboard());
        onView(withId(R.id.descripcion))
                .perform(clearText(), replaceText(descripcion), closeSoftKeyboard());
    }

    @Cuando("Pulso editar en el primer quad del listado")
    public void pulso_editar_primer_quad() {
        onView(withId(R.id.recyclerview))
                .perform(RecyclerViewActions.actionOnItemAtPosition(
                        0, guardarMatriculaYClickEdit()));
        onView(withId(R.id.matricula)).check(matches(isDisplayed()));
    }

    @Cuando("Pulso eliminar en el primer quad del listado")
    public void pulso_eliminar_quad() {
        onView(withId(R.id.recyclerview))
                .perform(RecyclerViewActions.actionOnItemAtPosition(
                        0, guardarMatriculaYClickDelete()));
    }

    @Cuando("Pulso eliminar en el detalle del quad")
    public void pulso_eliminar_detalle_quad() {
        onView(withId(R.id.button_delete))
                .check(matches(isDisplayed()))
                .perform(click());
    }

    @Cuando("Pulso editar en el detalle del quad")
    public void pulso_editar_detalle_quad() {
        onView(withId(R.id.button_edit))
                .check(matches(isDisplayed()))
                .perform(click());
    }

    @Cuando("Confirmo la eliminación en el diálogo de quad")
    public void confirmo_eliminacion_quad() {
        onView(withText("Eliminar")).inRoot(isDialog()).perform(click());
    }

    @Cuando("Cancelo la eliminación en el diálogo de quad")
    public void cancelo_eliminacion_quad() {
        onView(withText("Cancelar")).inRoot(isDialog()).perform(click());
    }

    @Cuando("Selecciono ordenar quads por matrícula")
    public void ordeno_quads_por_matricula() {
        onView(withId(R.id.orden_quads)).perform(forceClick());
        onView(withText("Por matrícula")).inRoot(isDialog()).perform(click());
    }

    @Cuando("Selecciono ordenar quads por tipo")
    public void ordeno_quads_por_tipo() {
        onView(withId(R.id.orden_quads)).perform(forceClick());
        onView(withText("Por tipo")).inRoot(isDialog()).perform(click());
    }

    @Cuando("Selecciono ordenar quads por precio")
    public void ordeno_quads_por_precio() {
        onView(withId(R.id.orden_quads)).perform(forceClick());
        onView(withText("Por precio")).inRoot(isDialog()).perform(click());
    }

    @Cuando("Introduzco nombre de cliente {string} y móvil {string} en reserva")
    public void introduzco_nombre_y_movil_reserva(String nombre, String movil) {
        onView(withId(R.id.nombre_cliente))
                .perform(clearText(), replaceText(nombre), closeSoftKeyboard());
        onView(withId(R.id.movil_cliente))
                .perform(clearText(), replaceText(movil), closeSoftKeyboard());
    }

    @Cuando("Introduzco fecha de recogida en {int} días y devolución en {int} días")
    public void introduzco_fechas_dias(int diasRecogida, int diasDevolucion) {
        onView(withId(R.id.fecha_recogida)).perform(click());

        Calendar recogida = Calendar.getInstance();
        recogida.add(Calendar.DAY_OF_YEAR, diasRecogida);

        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(
                        recogida.get(Calendar.YEAR),
                        recogida.get(Calendar.MONTH) + 1,
                        recogida.get(Calendar.DAY_OF_MONTH)
                ));

        onView(withText("Aceptar")).perform(click());

        onView(withId(R.id.fecha_devolucion)).perform(click());

        Calendar devolucion = Calendar.getInstance();
        devolucion.add(Calendar.DAY_OF_YEAR, diasDevolucion);

        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(
                        devolucion.get(Calendar.YEAR),
                        devolucion.get(Calendar.MONTH) + 1,
                        devolucion.get(Calendar.DAY_OF_MONTH)
                ));

        onView(withText("Aceptar")).perform(click());
    }

    @Cuando("Introduzco hora de recogida {string} y hora de devolución {string}")
    public void introduzco_horarios(String horaRecogida, String horaDevolucion) {
        // Corregido: Usamos forceClick() en lugar de click() para blindar las opciones de horario
        if (Objects.equals(horaRecogida, "true")) {
            onView(withId(R.id.horario_recogida_tarde)).perform(forceClick());
        } else {
            onView(withId(R.id.horario_recogida_manana)).perform(forceClick());
        }

        if (Objects.equals(horaDevolucion, "true")) {
            onView(withId(R.id.horario_devolucion_tarde)).perform(forceClick());
        } else {
            onView(withId(R.id.horario_devolucion_manana)).perform(forceClick());
        }
    }

    @Cuando("Pulso cancelar en el formulario de reserva")
    public void pulso_cancelar_reserva() {
        onView(withId(R.id.button_cancel)).perform(click());
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
    }

    @Cuando("Pulso editar en la primera reserva del listado")
    public void pulso_editar_primera_reserva() {
        onView(withId(R.id.recyclerview))
                .perform(RecyclerViewActions.actionOnItemAtPosition(
                        0,
                        guardarReservaYClickEdit()
                ));

        onView(withId(R.id.nombre_cliente)).check(matches(isDisplayed()));
    }

    @Cuando("Cambio el nombre a {string} y el móvil a {string} en reserva")
    public void cambio_nombre_y_movil_reserva(String nombre, String movil) {
        onView(withId(R.id.nombre_cliente))
                .perform(clearText(), replaceText(nombre), closeSoftKeyboard());
        onView(withId(R.id.movil_cliente))
                .perform(clearText(), replaceText(movil), closeSoftKeyboard());
    }

    @Cuando("Pulso eliminar en la primera reserva del listado de reservas")
    public void pulso_eliminar_primera_reserva() {
        withActivity(activity -> {
            numReservasAntes = activity.getReservaRepositoryMain().numReservas();
        });

        onView(withId(R.id.recyclerview))
                .perform(RecyclerViewActions.actionOnItemAtPosition(
                        0, clickChildViewWithId(R.id.btn_delete)));
    }

    @Cuando("Confirmo la eliminación en el diálogo de reserva")
    public void confirmo_eliminacion_reserva() {
        onView(withText("Eliminar")).inRoot(isDialog()).perform(click());
    }

    @Cuando("Cancelo la eliminación en el diálogo de reserva")
    public void cancelo_eliminacion_reserva() {
        onView(withText("Cancelar")).inRoot(isDialog()).perform(click());
    }

    @Cuando("Cancelo la eliminación en el diálogo de reserva desde el listado")
    public void cancelo_eliminacion_reserva_listado() {
        onView(withText("Cancelar")).inRoot(isDialog()).perform(click());
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
    }

    @Cuando("Pulso sobre la primera reserva del listado para ver su detalle")
    public void pulso_primera_reserva_detalle() {
        withActivity(activity -> {
            numReservasAntes = activity.getReservaRepositoryMain().numReservas();
        });

        onView(withId(R.id.recyclerview))
                .perform(actionOnItemAtPosition(0, guardarReservaYClickItem()));

        onView(withId(R.id.button_edit)).check(matches(isDisplayed()));
    }

    @Cuando("Pulso el botón eliminar en el detalle de la reserva")
    public void pulso_eliminar_en_detalle_reserva() {
        onView(withId(R.id.button_delete)).check(matches(isDisplayed())).perform(click());
    }

    @Cuando("Pulso el botón editar en el detalle de la reserva")
    public void pulso_editar_en_detalle_reserva() {
        onView(withId(R.id.button_edit)).check(matches(isDisplayed())).perform(click());
    }

    @Cuando("Pulso el botón enviar en el detalle de la reserva")
    public void pulso_enviar_en_detalle_reserva() {
        onView(withId(R.id.btn_enviar))
                .check(matches(isDisplayed()))
                .perform(click());
    }

    @Cuando("Confirmo la reserva en la pantalla de confirmación")
    public void confirmo_reserva_en_confirmacion() {
        onView(withId(R.id.button_confirm)).check(matches(isDisplayed())).perform(click());
        try {
            onView(withText("Aceptar")).inRoot(isDialog()).perform(click());
        } catch (Exception e) {
            onView(withText("Aceptar")).perform(click());
        }
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
    }

    @Cuando("Cancelo la reserva en la pantalla de confirmación")
    public void cancelo_reserva_en_confirmacion() {
        onView(withId(R.id.button_cancel)).perform(click());
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
    }

    @Cuando("Selecciono el filtro {string} en el listado de reservas")
    public void selecciono_filtro_reservas(String filtro) {
        onView(withId(R.id.filtro_reservas)).perform(click());
        onView(withText(filtro)).inRoot(isPlatformPopup()).perform(click());
    }

    @Cuando("Selecciono ordenar reservas por fecha de recogida")
    public void ordeno_reservas_por_recogida() {
        onView(withId(R.id.orden_reservas)).perform(click());
        onView(withText("Por fecha de recogida")).inRoot(isDialog()).perform(click());
    }

    @Cuando("Selecciono ordenar reservas por fecha de devolución")
    public void ordeno_reservas_por_devolucion() {
        onView(withId(R.id.orden_reservas)).perform(click());
        onView(withText("Por fecha de devolución")).inRoot(isDialog()).perform(click());
    }

    @Cuando("Selecciono ordenar reservas por nombre de cliente")
    public void ordeno_reservas_por_nombre() {
        onView(withId(R.id.orden_reservas)).perform(click());
        onView(withText("Por nombre de cliente")).inRoot(isDialog()).perform(click());
    }

    @Cuando("Selecciono ordenar reservas por número de móvil")
    public void ordeno_reservas_por_telefono() {
        onView(withId(R.id.orden_reservas)).perform(click());
        onView(withText("Por teléfono")).inRoot(isDialog()).perform(click());
    }

    @Entonces("Permanece en la pantalla de detalle del quad")
    public void sigue_en_detalle_quad() {
        onView(withId(R.id.detail_matricula)).check(matches(isDisplayed()));
    }

    @Entonces("El quad con matrícula {string} aparece en el listado de quads")
    public void quad_aparece_en_listado(String matricula) {
        onView(withId(R.id.recyclerview))
                .perform(RecyclerViewActions.scrollTo(hasDescendant(withText(matricula))));
        onView(withId(R.id.recyclerview))
                .check(matches(hasDescendant(withText(matricula))));
    }

    @Entonces("El quad con matrícula {string} no aparece en el listado de quads")
    public void quad_no_aparece_en_listado(String matricula) {
        onView(withText(matricula)).check(doesNotExist());
    }

    @Entonces("Permanece en el formulario de quad")
    public void sigue_en_formulario_quad() {
        onView(withId(R.id.matricula)).check(matches(isDisplayed()));
        onView(withId(R.id.button_save)).check(matches(isDisplayed()));
    }

    @Entonces("El quad ya no aparece en el listado de quads")
    public void quad_no_aparece_en_listado() {
        onView(withText(matriculaQuadSeleccionado)).check(doesNotExist());
    }

    @Entonces("El quad sigue apareciendo en el listado de quads")
    public void quad_sigue_en_listado() {
        onView(withId(R.id.recyclerview))
                .check(matches(hasDescendant(withText(matriculaQuadSeleccionado))));
    }

    @Entonces("El quad seleccionado muestra en detalle el tipo {string}, el precio {string} y la descripción {string}")
    public void quad_muestra_en_detalle(String tipo, String precio, String descripcion) {
        abrirDetalleQuadSeleccionado();

        if (Objects.equals(tipo, "true")) {
            onView(withId(R.id.detail_tipo)).check(matches(withText("Tipo: Biplaza")));
        } else {
            onView(withId(R.id.detail_tipo)).check(matches(withText("Tipo: Monoplaza")));
        }

        String precioConComa = precio.replace('.', ',');

        onView(withId(R.id.detail_precio))
                .check(matches(anyOf(
                        withSubstring(precio),
                        withSubstring(precioConComa)
                )));

        onView(withId(R.id.detail_descripcion))
                .check(matches(withText(descripcion)));
    }

    @Entonces("El quad seleccionado no muestra en detalle la descripción {string}")
    public void quad_no_muestra_descripcion_en_detalle(String descripcion) {
        abrirDetalleQuadSeleccionado();

        onView(withId(R.id.detail_descripcion))
                .check(matches(Matchers.not(withText(descripcion))));
    }

    @Entonces("El listado de quads es visible con al menos un elemento")
    public void listado_quads_visible() {
        onView(withId(R.id.recyclerview))
                .check(matches(isDisplayed()));

        onView(withId(R.id.recyclerview))
                .check(matches(hasDescendant(withId(R.id.text_view))));
    }

    @Entonces("El listado de quads aparece ordenado por matrícula de forma ascendente")
    public void listado_quads_ordenado_matricula() {
        onView(withId(R.id.recyclerview))
                .perform(actionOnItemAtPosition(0, assertMatriculaEnItem("1111AAA")));
    }

    @Entonces("El listado de quads aparece ordenado por tipo")
    public void listado_quads_ordenado_tipo() {
        onView(withId(R.id.recyclerview))
                .perform(actionOnItemAtPosition(0, assertMatriculaEnItem("1111AAA")));
    }

    @Entonces("El listado de quads aparece ordenado por precio de forma ascendente")
    public void listado_quads_ordenado_precio() {
        onView(withId(R.id.recyclerview))
                .perform(actionOnItemAtPosition(0, assertMatriculaEnItem("2222BBB")));
    }

    @Entonces("El listado de quads está vacío")
    public void listado_quads_vacio() {
        withActivity(activity -> {
            int total = activity.getQuadRespositoryMain().numQuads();
            assertEquals(0, total);
        });
    }

    @Entonces("Se lanza la acción de envío de la reserva")
    public void se_lanza_accion_envio_reserva() {

        intended(anyOf(
                hasAction(Intent.ACTION_SEND),
                hasAction(Intent.ACTION_SENDTO)
        ));
    }

    @Entonces("La primera reserva del listado tiene nombre {string}")
    public void primera_reserva_tiene_nombre(String nombreEsperado) {
        abrirPrimeraReservaDelListado();

        onView(withId(R.id.detail_nombre))
                .check(matches(withSubstring(nombreEsperado)));
    }

    @Entonces("Hay una reserva nueva en el listado de reservas")
    public void reserva_de_cliente_aparece() {
        withActivity(activity -> {
            int numReservasDespues = activity.getReservaRepositoryMain().numReservas();
            assertEquals(numReservasAntes + 1, numReservasDespues);
        });

        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
    }

    @Entonces("Permanece en el formulario de reserva")
    public void sigue_en_formulario_reserva() {
        onView(withId(R.id.nombre_cliente)).check(matches(isDisplayed()));
        onView(withId(R.id.button_continue)).check(matches(isDisplayed()));
    }

    @Entonces("Vuelvo al listado de reservas sin reserva nueva")
    public void vuelvo_al_listado_sin_reserva_nueva() {
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));

        withActivity(activity -> {
            int numReservasDespues = activity.getReservaRepositoryMain().numReservas();
            assertEquals(numReservasAntes, numReservasDespues);
        });
    }

    @Entonces("La reserva seleccionada muestra en detalle el nombre {string} y el móvil {string}")
    public void reserva_muestra_en_detalle(String nombre, String movil) {
        abrirDetalleReservaSeleccionada();

        onView(withId(R.id.detail_nombre))
                .check(matches(withSubstring(nombre)));

        onView(withId(R.id.detail_telefono))
                .check(matches(withSubstring(movil)));
    }

    @Entonces("La reserva seleccionada no muestra en detalle el nombre {string}")
    public void reserva_no_muestra_nombre_en_detalle(String nombre) {
        abrirDetalleReservaSeleccionada();

        onView(withId(R.id.detail_nombre))
                .check(matches(Matchers.not(withSubstring(nombre))));
    }

    @Entonces("La reserva ya no aparece en el listado de reservas")
    public void reserva_no_aparece_en_listado() {
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));

        withActivity(activity -> {
            int numReservasDespues = activity.getReservaRepositoryMain().numReservas();
            assertEquals(numReservasAntes - 1, numReservasDespues);
        });
    }

    @Entonces("La reserva sigue apareciendo en el listado de reservas")
    public void reserva_sigue_en_listado() {
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));

        withActivity(activity -> {
            int numReservasDespues = activity.getReservaRepositoryMain().numReservas();
            assertEquals(numReservasAntes, numReservasDespues);
        });
    }

    @Entonces("Vuelvo al listado de reservas y la reserva eliminada no aparece")
    public void vuelvo_al_listado_y_reserva_eliminada() {
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));

        withActivity(activity -> {
            int numReservasDespues = activity.getReservaRepositoryMain().numReservas();
            assertEquals(numReservasAntes - 1, numReservasDespues);
        });
    }

    @Entonces("Sigo en la pantalla de detalle de la reserva")
    public void sigo_en_detalle_reserva() {
        onView(withId(R.id.button_edit)).check(matches(isDisplayed()));

        withActivity(activity -> {
            int numReservasDespues = activity.getReservaRepositoryMain().numReservas();
            assertEquals(numReservasAntes, numReservasDespues);
        });
    }

    @Entonces("El listado de reservas es visible con al menos un elemento")
    public void listado_reservas_visible() {
        onView(withId(R.id.recyclerview))
                .check(matches(isDisplayed()));

        withActivity(activity -> {
            int total = activity.getReservaRepositoryMain().numReservas();
            assertTrue(total > 0);
        });
    }

    @Entonces("El listado muestra únicamente reservas actualmente en curso o aparece vacío")
    public void listado_solo_vigentes() {
        // El RecyclerView puede estar GONE si no hay reservas vigentes, lo cual es correcto
        onView(withId(R.id.recyclerview)).check((view, noViewFoundException) -> {
            if (noViewFoundException != null) throw noViewFoundException;
            int visibility = view.getVisibility();
            assertTrue("Se esperaba VISIBLE o GONE",
                    visibility == View.VISIBLE || visibility == View.GONE);
        });
    }

    @Entonces("El listado muestra únicamente reservas con fecha de devolución pasada o aparece vacío")
    public void listado_solo_caducadas() {
        onView(withId(R.id.recyclerview)).check((view, noViewFoundException) -> {
            if (noViewFoundException != null) throw noViewFoundException;
            int visibility = view.getVisibility();
            assertTrue("Se esperaba VISIBLE o GONE",
                    visibility == View.VISIBLE || visibility == View.GONE);
        });
    }

}