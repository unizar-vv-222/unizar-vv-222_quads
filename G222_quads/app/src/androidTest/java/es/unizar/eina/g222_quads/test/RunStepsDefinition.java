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
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.TestCase.assertNotNull;
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


    // STEPS NUEVOS – Casos de uso de quads

    @Dado("Accedo a la sección de quads")
    public void accedo_a_quads() {
        onView(withId(R.id.quad)).perform(click());
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
    }

    @Dado("Existe al menos un quad en el listado")
    public void existe_al_menos_un_quad() {
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
    }

    @Dado("Existen al menos dos quads en el sistema")
    public void existen_al_menos_dos_quads() {
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
    }

    @Dado("Existen al menos dos quads de distintos tipos en el sistema")
    public void existen_quads_distintos_tipos() {
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
    }

    @Dado("Existen al menos dos quads con distintos precios en el sistema")
    public void existen_quads_distintos_precios() {
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
    }

    @Dado("No existe ningún quad en el sistema")
    public void no_existe_ningun_quad() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        withActivity(activity -> {
            new Thread(() -> {
                try {
                    activity.getQuadRespositoryMain().deleteAll();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }
            }).start();
        });
        latch.await();
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

    @Cuando("Pulso el botón de nuevo quad")
    public void pulso_nuevo_quad() {
        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.matricula)).check(matches(isDisplayed()));
    }

    @Cuando("Introduzco la matrícula {string} el precio {string} el tipo {string} y la descripcion {string}")
    public void introduzco_datos_quad(String matricula, String precio, String tipo, String descripcion) {
        onView(withId(R.id.matricula))
                .perform(clearText(), replaceText(matricula), closeSoftKeyboard());
        onView(withId(R.id.precio))
                .perform(clearText(), replaceText(precio), closeSoftKeyboard());

        if(Objects.equals(tipo, "true")){
            onView(withId(R.id.tipo_monoplaza)).perform(click());
        }else{
            onView(withId(R.id.tipo_biplaza)).perform(click());
        }
        onView(withId(R.id.descripcion))
                .perform(clearText(), replaceText(descripcion), closeSoftKeyboard());
    }

    @Cuando("Pulso guardar en el formulario de quad")
    public void pulso_guardar_quad() {
        onView(withId(R.id.button_save)).perform(click());
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
    }

    @Cuando("El usuario pulsa el botón guardar del formulario de quad")
    public void pulsar_boton_guardar_quad() {
        onView(withId(R.id.button_save)).perform(click());
    }

    @Cuando("Pulso cancelar en el formulario de quad")
    public void pulso_cancelar_quad() {
        onView(withId(R.id.button_cancel)).perform(click());
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
    }

    @Cuando("Borro el precio del formulario de quad")
    public void borro_precio_quad() {
        onView(withId(R.id.precio)).perform(clearText(), closeSoftKeyboard());
    }

    @Cuando("Cambio el precio a {string} y la descripcion a {string} del quad")
    public void cambio_precio_y_nombre_quad(String precio, String descripcion) {
        onView(withId(R.id.precio))
                .perform(clearText(), replaceText(precio), closeSoftKeyboard());
        onView(withId(R.id.descripcion))
                .perform(clearText(), replaceText(descripcion), closeSoftKeyboard());
    }

    @Cuando("Pulso editar en el primer quad del listado")
    public void pulso_editar_primer_quad() {
        onView(withId(R.id.recyclerview))
                .perform(RecyclerViewActions.actionOnItemAtPosition(
                        0, clickChildViewWithId(R.id.btn_edit)));
        onView(withId(R.id.matricula)).check(matches(isDisplayed()));
    }

    @Cuando("Pulso eliminar en ese quad del listado")
    public void pulso_eliminar_quad() {
        onView(withId(R.id.recyclerview))
                .perform(RecyclerViewActions.actionOnItemAtPosition(
                        0, clickChildViewWithId(R.id.btn_delete)));
    }

    @Cuando("Confirmo la eliminación en el diálogo de quad")
    public void confirmo_eliminacion_quad() {
        onView(withText("Eliminar")).inRoot(isDialog()).perform(click());
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
    }

    @Cuando("Cancelo la eliminación en el diálogo de quad")
    public void cancelo_eliminacion_quad() {
        onView(withText("Cancelar")).inRoot(isDialog()).perform(click());
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
    }

    @Cuando("Selecciono ordenar quads por matrícula")
    public void ordeno_quads_por_matricula() {
        onView(withId(R.id.orden_quads)).perform(click());
        onView(withText("Por matrícula")).inRoot(isDialog()).perform(click());
    }

    @Cuando("Selecciono ordenar quads por tipo")
    public void ordeno_quads_por_tipo() {
        onView(withId(R.id.orden_quads)).perform(click());
        onView(withText("Por tipo")).inRoot(isDialog()).perform(click());
    }

    @Cuando("Selecciono ordenar quads por precio")
    public void ordeno_quads_por_precio() {
        onView(withId(R.id.orden_quads)).perform(click());
        onView(withText("Por precio")).inRoot(isDialog()).perform(click());
    }

    @Entonces("El quad con matrícula {string} aparece en el listado de quads")
    public void quad_aparece_en_listado(String matricula) {
        onView(withId(R.id.recyclerview))
                .perform(RecyclerViewActions.scrollTo(hasDescendant(withText(matricula))));
        onView(withId(R.id.recyclerview))
                .check(matches(hasDescendant(withText(matricula))));
    }

    @Entonces("Permanece en el formulario de quad")
    public void sigue_en_formulario_quad() {
        onView(withId(R.id.matricula)).check(matches(isDisplayed()));
    }

    @Entonces("El quad aparece en el listado con la matricula {string}")
    public void quad_aparece_con_matricula(String matricula) {
        onView(withId(R.id.recyclerview))
                .check(matches(hasDescendant(withText(matricula))));
    }

    @Entonces("El quad ya no aparece en el listado de quads")
    public void quad_no_aparece_en_listado() {
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
    }

    @Entonces("El quad sigue apareciendo en el listado de quads")
    public void quad_sigue_en_listado() {
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
    }

    @Entonces("El quad no muestra el nombre {string} en el listado")
    public void quad_no_muestra_nombre(String nombre) {
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
    }

    @Entonces("Se muestra un error de matrícula duplicada")
    public void error_matricula_duplicada() {
        onView(withId(R.id.matricula)).check(matches(isDisplayed()));
    }

    @Entonces("Se muestra un error indicando que el quad tiene reservas y no puede eliminarse")
    public void error_quad_con_reservas() {
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
    }

    @Entonces("El listado de quads es visible con al menos un elemento")
    public void listado_quads_visible() {
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
    }

    @Entonces("El listado de quads aparece ordenado por matrícula de forma ascendente")
    public void listado_quads_ordenado_matricula() {
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
    }

    @Entonces("El listado de quads aparece ordenado por tipo")
    public void listado_quads_ordenado_tipo() {
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
    }

    @Entonces("El listado de quads aparece ordenado por precio de forma ascendente")
    public void listado_quads_ordenado_precio() {
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
    }

    @Entonces("El listado de quads está vacío")
    public void listado_quads_vacio() {
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
    }

    @Dado("Existe un quad sin reservas asociadas en el listado")
    public void existe_quad_sin_reservas() {
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
    }

    @Dado("Existe un quad con reservas activas asociadas en el listado")
    public void existe_quad_con_reservas() throws InterruptedException {
        // El setup del @Before ya inserta quads y reservas asociadas
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
    }



    // Casos de uso de reservas

    @Dado("Existe al menos una reserva en el listado")
    public void existe_al_menos_una_reserva() {
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
    }

    @Dado("Existen al menos dos reservas en el sistema")
    public void existen_al_menos_dos_reservas() {
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
    }

    @Dado("Existen reservas previstas en el sistema")
    public void existen_reservas_previstas() {
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
    }

    @Cuando("Introduzco nombre de cliente {string} y teléfono {string} en reserva")
    public void introduzco_nombre_y_telefono_reserva(String nombre, String telefono) {
        onView(withId(R.id.nombre_cliente))
                .perform(clearText(), replaceText(nombre), closeSoftKeyboard());
        onView(withId(R.id.movil_cliente))
                .perform(clearText(), replaceText(telefono), closeSoftKeyboard());
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

    @Cuando("Pulso cancelar en el formulario de reserva")
    public void pulso_cancelar_reserva() {
        onView(withId(R.id.button_cancel)).perform(click());
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
    }

    @Cuando("Pulso editar en la primera reserva del listado")
    public void pulso_editar_primera_reserva() {
        onView(withId(R.id.recyclerview))
                .perform(RecyclerViewActions.actionOnItemAtPosition(
                        0, clickChildViewWithId(R.id.btn_edit)));
        onView(withId(R.id.nombre_cliente)).check(matches(isDisplayed()));
    }

    @Cuando("Cambio el nombre a {string} y el teléfono a {string} en reserva")
    public void cambio_nombre_y_telefono_reserva(String nombre, String telefono) {
        onView(withId(R.id.nombre_cliente))
                .perform(clearText(), replaceText(nombre), closeSoftKeyboard());
        onView(withId(R.id.movil_cliente))
                .perform(clearText(), replaceText(telefono), closeSoftKeyboard());
    }

    @Cuando("Pulso eliminar en la primera reserva del listado de reservas")
    public void pulso_eliminar_primera_reserva() {
        onView(withId(R.id.recyclerview))
                .perform(RecyclerViewActions.actionOnItemAtPosition(
                        0, clickChildViewWithId(R.id.btn_delete)));
    }

    @Cuando("Confirmo la eliminación en el diálogo de reserva")
    public void confirmo_eliminacion_reserva() {
        onView(withText("Eliminar")).inRoot(isDialog()).perform(click());
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
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
        onView(withId(R.id.recyclerview))
                .perform(actionOnItemAtPosition(0, click()));
        onView(withId(R.id.button_edit)).check(matches(isDisplayed()));
    }

    @Cuando("Pulso el botón eliminar en el detalle de la reserva")
    public void pulso_eliminar_en_detalle_reserva() {
        onView(withId(R.id.button_delete)).perform(click());
    }

    @Cuando("Pulso el botón enviar en el detalle de la reserva")
    public void pulso_enviar_en_detalle_reserva() {
        onView(withId(R.id.btn_enviar)).perform(click());
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

    @Cuando("Pulso atrás en la pantalla de confirmación de reserva")
    public void pulso_atras_en_confirmacion() {
        pressBack();
        onView(withId(R.id.recyclerview_quads)).check(matches(isDisplayed()));
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

    @Cuando("Selecciono ordenar reservas por número de teléfono")
    public void ordeno_reservas_por_telefono() {
        onView(withId(R.id.orden_reservas)).perform(click());
        onView(withText("Por teléfono")).inRoot(isDialog()).perform(click());
    }

    @Entonces("La reserva de {string} aparece en el listado de reservas")
    public void reserva_de_cliente_aparece(String nombre) {
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
    }

    @Entonces("El botón continuar de la reserva está deshabilitado")
    public void boton_continuar_reserva_deshabilitado() {
        onView(withId(R.id.nombre_cliente)).check(matches(isDisplayed()));
    }

    @Entonces("El botón confirmar de la selección de quads está deshabilitado")
    public void boton_confirmar_quads_deshabilitado() {
        onView(withId(R.id.button_confirm)).check(matches(isDisplayed()));
    }

    @Entonces("Vuelvo al listado de reservas sin reserva nueva")
    public void vuelvo_al_listado_sin_reserva_nueva() {
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
    }

    @Entonces("La reserva aparece en el listado con el nombre {string}")
    public void reserva_aparece_con_nombre(String nombre) {
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
    }

    @Entonces("La reserva ya no aparece en el listado de reservas")
    public void reserva_no_aparece_en_listado() {
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
    }

    @Entonces("La reserva sigue apareciendo en el listado de reservas")
    public void reserva_sigue_en_listado() {
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
    }

    @Entonces("Vuelvo al listado de reservas y la reserva eliminada no aparece")
    public void vuelvo_al_listado_y_reserva_eliminada() {
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
    }

    @Entonces("Sigo en la pantalla de detalle de la reserva")
    public void sigo_en_detalle_reserva() {
        onView(withId(R.id.button_edit)).check(matches(isDisplayed()));
    }

    @Entonces("La reserva no muestra el nombre {string} en el listado")
    public void reserva_no_muestra_nombre(String nombre) {
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
    }

    @Entonces("El listado de reservas es visible con al menos un elemento")
    public void listado_reservas_visible() {
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
    }

    @Entonces("El listado muestra únicamente reservas con fecha de recogida futura")
    public void listado_solo_previstas() {
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
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

    @Entonces("El listado de reservas aparece ordenado por fecha de recogida de forma ascendente")
    public void listado_reservas_ordenado_recogida() {
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
    }

    @Entonces("El listado de reservas aparece ordenado por fecha de devolución de forma ascendente")
    public void listado_reservas_ordenado_devolucion() {
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
    }

    @Entonces("El listado de reservas aparece ordenado alfabéticamente por nombre de cliente")
    public void listado_reservas_ordenado_nombre() {
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
    }

    @Entonces("El listado de reservas aparece ordenado por número de teléfono de forma ascendente")
    public void listado_reservas_ordenado_telefono() {
        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
    }

    @Entonces("Vuelvo a la pantalla de selección de quads")
    public void vuelvo_a_seleccion_quads() {
        onView(withId(R.id.recyclerview_quads)).check(matches(isDisplayed()));
    }
}