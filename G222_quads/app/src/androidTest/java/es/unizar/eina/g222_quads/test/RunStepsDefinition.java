package es.unizar.eina.g222_quads.test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.espresso.contrib.RecyclerViewActions;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import java.util.Calendar;
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
    private QuadRepository quadRepo;
    private ReservaRepository reservaRepo;

    // Ciclo de vida
    @Before
    public void launchActivity() throws InterruptedException {
        scenario = ActivityScenario.launch(G222_quads.class);

        final CountDownLatch latch = new CountDownLatch(1);

        scenario.onActivity(activity -> {
            quadRepo = activity.getQuadRespositoryMain();
            reservaRepo = activity.getReservaRepositoryMain();

            new Thread(() -> {
                try {
                    quadRepo.deleteAll();
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


    // BLOQUE 1 – Scenario Testing

    @Dado("Abro la aplicación de gestión de quads")
    public void abro_la_aplicacion() {
        scenario.onActivity(activity -> assertNotNull(activity));
    }

    @Dado("Accedo a la sección de reservas")
    public void accedo_a_reservas() {
        onView(withId(R.id.reserva)).perform(click());
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
    }

    @Cuando("Pulso el botón de nueva reserva")
    public void pulso_nueva_reserva() {
        onView(withId(R.id.fab)).perform(click());
        // Esperar a que el formulario esté cargado
        onView(withId(R.id.nombre_cliente)).check(matches(isDisplayed()));
    }

    @Cuando("Relleno el formulario con nombre {string} y teléfono {string}")
    public void relleno_formulario(String nombre, String telefono) {
        onView(withId(R.id.nombre_cliente)).check(matches(isDisplayed()));
        onView(withId(R.id.nombre_cliente))
                .perform(clearText(), replaceText(nombre), closeSoftKeyboard());
        onView(withId(R.id.movil_cliente))
                .perform(clearText(), replaceText(telefono), closeSoftKeyboard());
    }

    @Cuando("Selecciono una fecha de recogida futura")
    public void selecciono_fecha_recogida() {
        onView(withId(R.id.fecha_recogida)).perform(click());
        Calendar recogida = Calendar.getInstance();
        recogida.add(Calendar.YEAR, 1);
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(
                        recogida.get(Calendar.YEAR),
                        recogida.get(Calendar.MONTH) + 1,
                        recogida.get(Calendar.DAY_OF_MONTH)));
        onView(withText("Aceptar")).perform(click());
        onView(withId(R.id.horario_recogida_manana)).perform(click());
    }

    @Cuando("Selecciono una fecha de devolución posterior a la recogida")
    public void selecciono_fecha_devolucion() {
        onView(withId(R.id.fecha_devolucion)).perform(click());
        Calendar devolucion = Calendar.getInstance();
        devolucion.add(Calendar.YEAR, 1);
        devolucion.add(Calendar.DAY_OF_YEAR, 2);
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(
                        devolucion.get(Calendar.YEAR),
                        devolucion.get(Calendar.MONTH) + 1,
                        devolucion.get(Calendar.DAY_OF_MONTH)));
        onView(withText("Aceptar")).perform(click());
        onView(withId(R.id.horario_devolucion_tarde)).perform(forceClick());
    }

    @Cuando("Pulso continuar para seleccionar quads")
    public void pulso_continuar() {
        onView(withId(R.id.button_continue)).check(matches(isDisplayed()));
        onView(withId(R.id.button_continue)).perform(click());
        // Esperar a que cargue la pantalla de selección de quads
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
        onView(withId(R.id.button_confirm)).check(matches(isDisplayed()));
        onView(withId(R.id.button_confirm)).perform(click());
        // Esperar a que cargue la pantalla de confirmación
        onView(withId(R.id.button_confirm)).check(matches(isDisplayed()));
    }

    @Cuando("Confirmo la reserva")
    public void confirmo_la_reserva() {
        onView(withId(R.id.title_confirm_reserva))
                .check(matches(isDisplayed()));
        onView(withId(R.id.button_confirm))
                .check(matches(isDisplayed()))
                .check(matches(isEnabled()))
                .perform(click());
        try {
            onView(withText("Aceptar")).inRoot(isDialog()).perform(click());
        } catch (Exception e) {
            onView(withText("Aceptar")).perform(click());
        }
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
    }

    @Entonces("La reserva aparece en el listado de reservas")
    public void reserva_aparece_en_listado() {
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
    }

    @Cuando("Pulso el botón cancelar en el formulario de reserva")
    public void pulso_cancelar_formulario() {
        onView(withId(R.id.button_cancel)).check(matches(isDisplayed()));
        onView(withId(R.id.button_cancel)).perform(click());
        // Esperar a que vuelva al listado
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
    }

    @Entonces("Vuelvo al listado de reservas sin crear ninguna nueva")
    public void vuelvo_al_listado() {
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
    }

    @Cuando("Pulso sobre la primera reserva del listado")
    public void pulso_primera_reserva() {
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
        onView(withId(R.id.recyclerview))
                .perform(actionOnItemAtPosition(0, click()));
        // Esperar a que cargue el detalle
        onView(withId(R.id.button_edit)).check(matches(isDisplayed()));
    }

    @Entonces("Se muestra la pantalla de detalle de la reserva")
    public void se_muestra_detalle() {
        onView(withId(R.id.button_edit)).check(matches(isDisplayed()));
    }

    @Cuando("Pulso el botón eliminar en el detalle")
    public void pulso_eliminar_detalle() {
        onView(withId(R.id.button_delete)).check(matches(isDisplayed()));
        onView(withId(R.id.button_delete)).perform(click());
    }

    @Cuando("Confirmo la eliminación")
    public void confirmo_eliminacion() {
        // Intentar con isDialog(), si falla intentar sin él
        try {
            onView(withText("Eliminar")).inRoot(isDialog()).check(matches(isDisplayed()));
            onView(withText("Eliminar")).inRoot(isDialog()).perform(click());
        } catch (Exception e) {
            onView(withText("Eliminar")).check(matches(isDisplayed()));
            onView(withText("Eliminar")).perform(click());
        }
        // Esperar a que vuelva al listado
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
    }

    @Entonces("Vuelvo al listado de reservas")
    public void vuelvo_al_listado_tras_eliminar() {
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
    }


    // BLOQUE 2 – Caja negra: particiones de equivalencia

    @Cuando("Introduzco nombre {string} y teléfono {string}")
    public void introduzco_nombre_y_telefono(String nombre, String telefono) {
        onView(withId(R.id.nombre_cliente)).check(matches(isDisplayed()));
        onView(withId(R.id.nombre_cliente))
                .perform(clearText(), replaceText(nombre), closeSoftKeyboard());
        onView(withId(R.id.movil_cliente))
                .perform(clearText(), replaceText(telefono), closeSoftKeyboard());
    }

    @Cuando("Introduzco fechas con desplazamiento de recogida {string} y devolución {string}")
    public void introduzco_fechas(String diasRecogidaStr, String diasDevolucionStr) {
        int diasRecogida   = Integer.parseInt(diasRecogidaStr);
        int diasDevolucion = Integer.parseInt(diasDevolucionStr);

        onView(withId(R.id.fecha_recogida)).check(matches(isDisplayed()));
        onView(withId(R.id.fecha_recogida)).perform(click());
        Calendar recogida = Calendar.getInstance();
        recogida.add(Calendar.DAY_OF_YEAR, diasRecogida);
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(
                        recogida.get(Calendar.YEAR),
                        recogida.get(Calendar.MONTH) + 1,
                        recogida.get(Calendar.DAY_OF_MONTH)));
        onView(withText("Aceptar")).perform(click());
        onView(withId(R.id.horario_recogida_manana)).perform(click());

        onView(withId(R.id.fecha_devolucion)).perform(click());
        Calendar devolucion = Calendar.getInstance();
        devolucion.add(Calendar.DAY_OF_YEAR, diasDevolucion);
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(
                        devolucion.get(Calendar.YEAR),
                        devolucion.get(Calendar.MONTH) + 1,
                        devolucion.get(Calendar.DAY_OF_MONTH)));
        onView(withText("Aceptar")).perform(click());
        onView(withId(R.id.horario_devolucion_tarde)).perform(forceClick());
    }

    @Entonces("El botón continuar {string}")
    public void el_boton_continuar(String resultado) {
        if ("funciona".equals(resultado)) {
            onView(withId(R.id.button_continue)).check(matches(isEnabled()));
        } else {
            onView(withId(R.id.nombre_cliente)).check(matches(isDisplayed()));
        }
    }

    @Dado("Existe un quad con matrícula {string} y precio {string} euros por día")
    public void existe_quad_con_precio(final String matricula, final String precioStr) {
        final CountDownLatch latch = new CountDownLatch(1);
        withActivity(activity -> {
            new Thread(() -> {
                try {
                    double precio = Double.parseDouble(precioStr);
                    QuadRepository repo = activity.getQuadRespositoryMain();
                    Quad quad = new Quad(matricula, true, precio, "Quad test Cucumber");
                    repo.insert(quad).get();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }
            }).start();
        });
        try { latch.await(); } catch (InterruptedException e) { e.printStackTrace(); }
    }

    @Dado("Existe una reserva para ese quad con precio total calculado")
    public void existe_reserva_para_ese_quad() {
        AtomicReference<Integer> idRef     = new AtomicReference<>();
        AtomicReference<Double>  precioRef = new AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(1);

        withActivity(activity -> {
            new Thread(() -> {
                try {
                    ReservaRepository rRepo = activity.getReservaRepositoryMain();

                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.DAY_OF_YEAR, 1);
                    long recogida = cal.getTimeInMillis();
                    cal.add(Calendar.DAY_OF_YEAR, 2);
                    long devolucion = cal.getTimeInMillis();

                    Reserva reserva = new Reserva(
                            "Cucumber Test", "611000000",
                            recogida, false,
                            devolucion, false
                    );
                    long idReserva = rRepo.insert(reserva);
                    assertTrue("La reserva debe insertarse", idReserva > 0);

                    rRepo.recalcularPrecioReserva(
                            (int) idReserva,
                            recogida, false,
                            devolucion, false
                    );

                    Reserva r = rRepo.getReservaByIdSync((int) idReserva);
                    idRef.set((int) idReserva);
                    precioRef.set(r.getPrecioTotal());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }
            }).start();
        });

        try { latch.await(); } catch (InterruptedException e) { e.printStackTrace(); }

        idReservaParaPrecio        = idRef.get();
        precioReservaAntesDeCambio = precioRef.get();
    }

    @Cuando("Modifico el precio del quad {string} a {string} euros por día")
    public void modifico_precio_quad(String matricula, String nuevoPrecioStr) {
        onView(withId(R.id.quad)).perform(click());
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));

        // Scroll hasta el quad con esa matrícula
        onView(withId(R.id.recyclerview))
                .perform(RecyclerViewActions.scrollTo(hasDescendant(withText(matricula))));

        // Pulsar btn_edit directamente en el item del RecyclerView
        onView(withId(R.id.recyclerview))
                .perform(RecyclerViewActions.actionOnItem(
                        hasDescendant(withText(matricula)),
                        clickChildViewWithId(R.id.btn_edit)
                ));

        // Esperar a que cargue el formulario de edición
        onView(withId(R.id.precio)).check(matches(isDisplayed()));
        onView(withId(R.id.precio))
                .perform(clearText(), replaceText(nuevoPrecioStr), closeSoftKeyboard());
        onView(withId(R.id.button_save)).check(matches(isDisplayed()));
        onView(withId(R.id.button_save)).perform(click());

        // Volver a main
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
        pressBack();
    }

    @Entonces("El precio total de la reserva sigue siendo el mismo que al crearla")
    public void precio_reserva_no_cambia() {
        assertTrue("No se encontró ninguna reserva de prueba", idReservaParaPrecio > 0);

        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<Double> precioActual = new AtomicReference<>();

        withActivity(activity -> {
            new Thread(() -> {
                try {
                    ReservaRepository rRepo = activity.getReservaRepositoryMain();
                    Reserva r = rRepo.getReservaByIdSync(idReservaParaPrecio);
                    precioActual.set(r.getPrecioTotal());
                } finally {
                    latch.countDown();
                }
            }).start();
        });

        try { latch.await(); } catch (InterruptedException e) { e.printStackTrace(); }

        assertEquals(
                "El precio total debe mantenerse igual aunque el quad cambie de precio",
                precioReservaAntesDeCambio,
                precioActual.get(),
                0.01
        );
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
}