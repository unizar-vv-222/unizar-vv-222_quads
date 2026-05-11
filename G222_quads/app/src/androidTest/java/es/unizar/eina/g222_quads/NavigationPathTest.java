package es.unizar.eina.g222_quads;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import es.unizar.eina.g222_quads.database.Quad;
import es.unizar.eina.g222_quads.database.QuadRepository;
import es.unizar.eina.g222_quads.database.Reserva;
import es.unizar.eina.g222_quads.database.ReservaRepository;
import es.unizar.eina.g222_quads.ui.quads.G222_quads;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;

import java.util.*;

@RunWith(AndroidJUnit4.class)
public class NavigationPathTest {

    private static final String TAG = "TEST_DEBUG";
    private boolean safeForDetailFlow = true;

    @Rule
    public ActivityScenarioRule<G222_quads> activityRule =
            new ActivityScenarioRule<>(G222_quads.class);

    // contador para matrículas únicas
    private int quadCounter = 1000;

    // ─────────────────────────────────────────────
    // EDGE DEFINITION
    // ─────────────────────────────────────────────
    private static class Edge {
        int id;
        String from;
        String to;

        Edge(int id, String from, String to) {
            this.id = id;
            this.from = from;
            this.to = to;
        }
    }

    // Clase auxiliar para que el BFS pueda rastrear el camino
    private static class State {
        String node;      // El nombre del nodo actual (ej. "quad")
        int lastEdge;     // El ID de la última arista que pulsamos
        List<Integer> path; // La lista de IDs de aristas que llevamos en este "vuelo"

        State(String n, int le, List<Integer> p) {
            this.node = n;
            this.lastEdge = le;
            this.path = p;
        }
    }
    private String currentNode = "main";
    private List<Edge> getEdges() {
        List<Edge> edges = new ArrayList<>();

        edges.add(new Edge(1,"main","quad"));
        edges.add(new Edge(2,"main","reserva"));

        edges.add(new Edge(3,"quad","quad_detail"));
        edges.add(new Edge(4,"quad","quad_form"));
        edges.add(new Edge(5,"quad","main"));
        edges.add(new Edge(6,"quad","quad"));
        edges.add(new Edge(7,"quad","quad"));
        edges.add(new Edge(8,"quad","quad"));

        edges.add(new Edge(11,"quad_detail","quad"));
        edges.add(new Edge(12,"quad_detail","quad"));
        edges.add(new Edge(13,"quad_detail","quad_form"));

        edges.add(new Edge(14,"quad_form","quad"));
        edges.add(new Edge(15,"quad_form","quad"));
        edges.add(new Edge(16,"quad_form","quad"));

        edges.add(new Edge(17,"reserva","reserva_detail"));
        edges.add(new Edge(18,"reserva","reserva_form"));
        edges.add(new Edge(19,"reserva","main"));
        edges.add(new Edge(20,"reserva","reserva"));
        edges.add(new Edge(21,"reserva","reserva"));
        edges.add(new Edge(22,"reserva","reserva"));
        edges.add(new Edge(23,"reserva","reserva"));
        edges.add(new Edge(24,"reserva","reserva"));
        edges.add(new Edge(25,"reserva","reserva"));
        edges.add(new Edge(26,"reserva","reserva"));
        edges.add(new Edge(27,"reserva","reserva"));

        edges.add(new Edge(30,"reserva_detail","reserva"));
        edges.add(new Edge(31,"reserva_detail","reserva"));
        edges.add(new Edge(32,"reserva_detail","reserva_form"));
        // edges.add(new Edge(33,"reserva_detail","reserva"));

        edges.add(new Edge(34,"reserva_form","reserva"));
        edges.add(new Edge(35,"reserva_form","reserva"));
        edges.add(new Edge(36,"reserva_form","select_quads"));

        edges.add(new Edge(37,"select_quads","reserva"));
        edges.add(new Edge(38,"select_quads","confirm"));

        edges.add(new Edge(39,"confirm","reserva"));
        edges.add(new Edge(40,"confirm","reserva"));
        edges.add(new Edge(41,"confirm","reserva"));

        return edges;
    }

    /**
     * Genera una ruta de navegación optimizada que busca cubrir todos los pares de transiciones posibles (profundidad 2).
     * Utiliza un enfoque codicioso (greedy) con recuperación mediante búsqueda en anchura (BFS).
     */
    private List<Integer> buildSafePath(List<Edge> edges) {
        // Agrupamos las aristas por su nodo de origen para saber rápidamente qué botones podemos pulsar desde cada pantalla.
        Map<String, List<Edge>> outgoing = new HashMap<>();
        for (Edge e : edges) outgoing.computeIfAbsent(e.from, k -> new ArrayList<>()).add(e);


        // Aquí generamos todos los pares posibles (Arista A -> Arista B) que existen en el grafo.
        Set<String> remainingPairs = new HashSet<>();
        for (Edge e1 : edges) {
            for (Edge e2 : outgoing.getOrDefault(e1.to, new ArrayList<>())) {
                remainingPairs.add(e1.id + "->" + e2.id); // Guardamos el par como una cadena identificadora
            }
        }

        int totalPares = remainingPairs.size();
        List<Integer> finalPath = new ArrayList<>();
        String currentLoc = "main"; // Empezamos siempre en la pantalla principal
        int lastId = 0;


        // Continuamos mientras falten pares por cubrir o no superemos un límite de seguridad (control de bucles infinitos).
        while (!remainingPairs.isEmpty() && finalPath.size() < 800) {
            List<Edge> options = outgoing.getOrDefault(currentLoc, new ArrayList<>());

            // Buscamos si alguna de las aristas disponibles desde la pantalla actual completa un par pendiente
            Edge best = null;
            for (Edge candidate : options) {
                if (remainingPairs.contains(lastId + "->" + candidate.id)) {
                    // Si podemos cubrir un par quedándonos en la misma pantalla (reflexiva), se ejecuta ese con prioridad.
                    if (candidate.from.equals(candidate.to)) {
                        best = candidate;
                        break;
                    }
                    if (best == null) best = candidate; // Si no hay reflexiva, el primer par que sirva
                }
            }

            if (best != null) {
                // Si encontramos un paso directo, lo ejecutamos y lo eliminamos de la lista de pendientes.
                remainingPairs.remove(lastId + "->" + best.id);
                finalPath.add(best.id);
                lastId = best.id;
                currentLoc = best.to;
            } else {
                // Si desde donde estamos no hay ningún botón que complete un par pendiente,
                // usamos findPathToNearestPair para calcular el camino más corto hacia el par "insatisfecho" más cercano.
                List<Integer> recovery = findPathToNearestPair(currentLoc, lastId, outgoing, remainingPairs);

                if (recovery.isEmpty()) break; // Si no hay forma de llegar a más pares, terminamos.

                // Añadimos toda la ruta de recuperación a nuestro camino final.
                for (int id : recovery) {
                    finalPath.add(id);
                    // Durante el trayecto de recuperación, si "por casualidad" cubrimos otros pares, los tachamos.
                    remainingPairs.remove(lastId + "->" + id);
                    lastId = id;
                    // Actualizamos la ubicación actual buscando el destino de la arista ejecutada.
                    for(Edge e : edges) if(e.id == id) { currentLoc = e.to; break; }
                }
            }
        }

        // 4. REPORTE DE RESULTADOS
        Log.d(TAG, "============================================");
        Log.d(TAG, "PASOS TOTALES: " + finalPath.size());
        Log.d(TAG, "COBERTURA: " + ((totalPares - remainingPairs.size()) * 100 / totalPares) + "%");
        Log.d(TAG, "============================================");
        return finalPath;
    }

    /**
     * Utiliza el algoritmo BFS (Breadth-First Search) para encontrar la secuencia de botones
     * más corta que nos lleve desde el estado actual a cualquier par de aristas pendiente.
     */
    private List<Integer> findPathToNearestPair(String startNode, int lastId, Map<String, List<Edge>> outgoing, Set<String> remaining) {
        Queue<State> queue = new LinkedList<>();
        queue.add(new State(startNode, lastId, new ArrayList<>()));
        Set<String> visited = new HashSet<>(); // Evita ciclos infinitos volviendo a estados ya explorados

        while (!queue.isEmpty()) {
            State current = queue.poll();

            // Poda: Si el camino de recuperación es muy largo (>5), lo ignoramos para evitar rutas absurdas.
            if (current.path.size() > 5) continue;

            for (Edge e : outgoing.getOrDefault(current.node, new ArrayList<>())) {
                // ¿Pulsar esta arista 'e' completa un par pendiente con la arista anterior?
                if (remaining.contains(current.lastEdge + "->" + e.id)) {
                    List<Integer> found = new ArrayList<>(current.path);
                    found.add(e.id);
                    return found; // ¡Éxito! Hemos encontrado el camino más corto al siguiente objetivo.
                }

                // Si no es un objetivo inmediato, seguimos explorando desde el nuevo nodo.
                // El estado se define por el nodo (pantalla) y la última arista pulsada.
                String stateKey = e.to + "_" + e.id;
                if (!visited.contains(stateKey)) {
                    visited.add(stateKey);
                    List<Integer> nextP = new ArrayList<>(current.path);
                    nextP.add(e.id);
                    queue.add(new State(e.to, e.id, nextP));
                }
            }
        }
        return new ArrayList<>(); // No se encontraron más pares alcanzables.
    }

    // ─────────────────────────────────────────────
    // SETUP (igual que el tuyo)
    // ─────────────────────────────────────────────
    @Before
    public void setup() throws Exception {
        final Throwable[] error = new Throwable[1];
        final java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);

        activityRule.getScenario().onActivity(activity -> {

            QuadRepository quadRepo = activity.getQuadRespositoryMain();
            ReservaRepository reservaRepo = activity.getReservaRepositoryMain();

            new Thread(() -> {
                try {
                    quadRepo.deleteAll();
                    reservaRepo.deleteAll();

                    for (int i = 1; i <= 30; i++) {
                        quadRepo.insert(new Quad(
                                String.format("%04dAAA", i),
                                true,
                                25.0,
                                "Quad " + i
                        )).get();
                    }

                    Calendar c = Calendar.getInstance();
                    long now = System.currentTimeMillis();

                    c.setTimeInMillis(now);
                    c.add(Calendar.DAY_OF_YEAR, 5);
                    long ini = c.getTimeInMillis();

                    c.add(Calendar.DAY_OF_YEAR, 2);
                    long fin = c.getTimeInMillis();


                    for (int i = 1; i <= 40; i++) {
                        reservaRepo.insert(new Reserva(
                                "Prevista " + i,
                                "600000004",
                                ini,
                                false,
                                fin,
                                false
                        ));
                    }


                } catch (Throwable t) {
                    error[0] = t;
                } finally {
                    latch.countDown();
                }
            }).start();
        });

        latch.await();

        if (error[0] != null) throw new RuntimeException(error[0]);
    }

    // ─────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────
    private void fillQuadForm() {
        onView(withId(R.id.matricula))
                .check((view, noViewFoundException) -> {
                    android.widget.EditText et = (android.widget.EditText) view;
                    String actual = et.getText().toString();

                    if (actual.isEmpty()) {
                        et.setText(String.format("%04dTST", quadCounter++));
                    }
                });

        onView(withId(R.id.tipo_monoplaza)).perform(click());

        onView(withId(R.id.precio))
                .perform(clearText(), replaceText("15.0"));

        onView(withId(R.id.descripcion))
                .perform(clearText(), replaceText("Quad test"));
    }



    private void fillReservaForm() {

        onView(withId(R.id.nombre_cliente))
                .perform(clearText(), replaceText("Cliente Test"), closeSoftKeyboard());

        onView(withId(R.id.movil_cliente))
                .perform(clearText(), replaceText("600000099"), closeSoftKeyboard());

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

        onView(withId(R.id.horario_devolucion_tarde))
                .perform(new ViewAction() {

                    @Override
                    public Matcher<View> getConstraints() {
                        return isEnabled();
                    }

                    @Override
                    public String getDescription() {
                        return "force click";
                    }

                    @Override
                    public void perform(UiController uiController, View view) {
                        view.performClick();
                    }
                });
    }

    private void selectFirstQuad() {

        onView(withId(R.id.recyclerview_quads))
                .perform(
                        RecyclerViewActions.actionOnItemAtPosition(
                                0,
                                new ViewAction() {

                                    @Override
                                    public Matcher<View> getConstraints() {
                                        return isEnabled();
                                    }

                                    @Override
                                    public String getDescription() {
                                        return "select first quad";
                                    }

                                    @Override
                                    public void perform(
                                            UiController uiController,
                                            View view
                                    ) {

                                        CheckBox cb =
                                                view.findViewById(R.id.quad_checkbox);

                                        if (cb != null && !cb.isChecked()) {

                                            cb.performClick();
                                        }
                                    }
                                }
                        )
                );
    }

    public static ViewAction clickChildViewWithId(final int id) {

        return new ViewAction() {

            @Override
            public Matcher<View> getConstraints() {
                return isDisplayed();
            }

            @Override
            public String getDescription() {
                return "Click child";
            }

            @Override
            public void perform(UiController uiController, View view) {

                View v = view.findViewById(id);

                if (v != null) {

                    v.performClick();
                }
            }
        };
    }

    private void ensureSafeReservationState() {
        if (!safeForDetailFlow) {
            try {
                onView(withId(R.id.filtro_reservas)).perform(click());
                onView(withText("Todas las reservas"))
                        .inRoot(isPlatformPopup())
                        .perform(click());

                safeForDetailFlow = true;
            } catch (Exception ignored) {}
        }
    }

    // ─────────────────────────────────────────────
    // EXECUTOR (TU CÓDIGO ORIGINAL INTACTO)
    // ─────────────────────────────────────────────
    private void executeEdge(int edgeId) throws InterruptedException {
        Log.d(TAG, "executeEdge: " + edgeId + "  (nodo=" + currentNode + ")");
        switch (edgeId) {

            // ── main ─────────────────────────────────────────────
            case 1:  // main → quad
                onView(withId(R.id.quad)).perform(click());
                currentNode = "quad";
                break;
            case 2:  // main → reserva
                onView(withId(R.id.reserva)).perform(click());
                currentNode = "reserva";
                break;

            // ── quad ─────────────────────────────────────────────
            case 3:  // quad → quad_detail
                onView(withId(R.id.recyclerview)).perform(
                        RecyclerViewActions.actionOnItemAtPosition(0, click()));
                currentNode = "quad_detail";
                break;
            case 4:  // quad → quad_form (FAB)
                onView(withId(R.id.fab)).perform(click());
                currentNode = "quad_form";
                break;
            case 5:  // quad → main
                pressBack();
                currentNode = "main";
                break;
            case 6:  // quad → quad (ordenar matrícula)
                onView(withId(R.id.orden_quads)).perform(click());
                onView(withText("Por matrícula")).inRoot(isDialog())
                        .check(matches(isDisplayed())).perform(click());
                currentNode = "quad";
                break;
            case 7:  // quad → quad (ordenar precio)
                onView(withId(R.id.orden_quads)).perform(click());
                onView(withText("Por precio")).inRoot(isDialog())
                        .check(matches(isDisplayed())).perform(click());
                currentNode = "quad";
                break;
            case 8:  // quad → quad (ordenar tipo)
                onView(withId(R.id.orden_quads)).perform(click());
                onView(withText("Por tipo")).inRoot(isDialog())
                        .check(matches(isDisplayed())).perform(click());
                currentNode = "quad";
                break;
            case 9: // quad → quad (delete)
                onView(withId(R.id.recyclerview))
                        .perform(RecyclerViewActions.actionOnItemAtPosition(
                                0,
                                clickChildViewWithId(R.id.btn_delete)
                        ));
                break;
            case 10: // quad → quad_modify (edit)
                onView(withId(R.id.recyclerview))
                        .perform(RecyclerViewActions.actionOnItemAtPosition(
                                0,
                                clickChildViewWithId(R.id.btn_edit)
                        ));
                break;
            // ── quad_detail ──────────────────────────────────────
            case 11:  // quad_detail → quad (goBack)
                pressBack();
                currentNode = "quad";
                break;
            case 12:  // quad_detail → quad (delete)
                onView(withId(R.id.button_delete)).perform(click());
                onView(withText("Eliminar")).inRoot(isDialog()).perform(click());
                currentNode = "quad";
                break;
            case 13:  // quad_detail → quad_form (edit)
                onView(withId(R.id.button_edit)).perform(click());
                currentNode = "quad_form";
                break;

            // ── quad_form ────────────────────────────────────────
            case 14:  // quad_form → quad (save)
                fillQuadForm();
                onView(withId(R.id.button_save)).perform(click());
                currentNode = "quad";
                break;
            case 15:  // quad_form → quad (goBack)
                pressBack();
                currentNode = "quad";
                break;
            case 16:  // quad_form → quad (cancel)
                onView(withId(R.id.button_cancel)).perform(click());
                currentNode = "quad";
                break;

            // ── reserva ──────────────────────────────────────────
            case 17:  // reserva → reserva_detail
                ensureSafeReservationState();
                onView(withId(R.id.recyclerview)).perform(
                        RecyclerViewActions.actionOnItemAtPosition(0, click()));
                currentNode = "reserva_detail";
                break;
            case 18:  // reserva → reserva_form (FAB)
                onView(withId(R.id.fab)).perform(click());
                currentNode = "reserva_form";
                break;
            case 19:  // reserva → main
                pressBack();
                currentNode = "main";
                break;
            case 20:  // reserva → reserva (filtrar vigentes)
                onView(withId(R.id.filtro_reservas)).perform(click());
                onView(withText("Reservas vigentes")).inRoot(isPlatformPopup()).perform(click());
                safeForDetailFlow = false;
                currentNode = "reserva";
                break;
            case 21:  // reserva → reserva (filtrar previstas)
                onView(withId(R.id.filtro_reservas)).perform(click());
                onView(withText("Reservas previstas")).inRoot(isPlatformPopup()).perform(click());
                safeForDetailFlow = false;
                currentNode = "reserva";
                break;
            case 22:  // reserva → reserva (filtrar caducadas)
                onView(withId(R.id.filtro_reservas)).perform(click());
                onView(withText("Reservas caducadas")).inRoot(isPlatformPopup()).perform(click());
                safeForDetailFlow = false;
                currentNode = "reserva";
                break;
            case 23:  // reserva → reserva (filtrar todas)
                onView(withId(R.id.filtro_reservas)).perform(click());
                onView(withText("Todas las reservas")).inRoot(isPlatformPopup()).perform(click());
                safeForDetailFlow = true;
                currentNode = "reserva";
                break;
            case 24:  // reserva → reserva (ordenar nombre)
                onView(withId(R.id.orden_reservas)).perform(click());
                onView(withText("Por nombre de cliente")).inRoot(isDialog())
                        .check(matches(isDisplayed())).perform(click());
                currentNode = "reserva";
                break;
            case 25:  // reserva → reserva (ordenar teléfono)
                onView(withId(R.id.orden_reservas)).perform(click());
                onView(withText("Por teléfono")).inRoot(isDialog())
                        .check(matches(isDisplayed())).perform(click());
                currentNode = "reserva";
                break;
            case 26:  // reserva → reserva (ordenar recogida)
                onView(withId(R.id.orden_reservas)).perform(click());
                onView(withText("Por fecha de recogida")).inRoot(isDialog())
                        .check(matches(isDisplayed())).perform(click());
                currentNode = "reserva";
                break;
            case 27:  // reserva → reserva (ordenar devolución)
                onView(withId(R.id.orden_reservas)).perform(click());
                onView(withText("Por fecha de devolución")).inRoot(isDialog())
                        .check(matches(isDisplayed())).perform(click());
                currentNode = "reserva";
                break;
            case 28: // reserva → reserva (delete)
                ensureSafeReservationState();
                onView(withId(R.id.recyclerview))
                        .perform(RecyclerViewActions.actionOnItemAtPosition(
                                0,
                                clickChildViewWithId(R.id.btn_delete)
                        ));
                break;
            case 29: // reserva → reserva_modify (edit)
                ensureSafeReservationState();
                onView(withId(R.id.recyclerview))
                        .perform(RecyclerViewActions.actionOnItemAtPosition(
                                0,
                                clickChildViewWithId(R.id.btn_edit)
                        ));
                break;

            // ── reserva_detail ───────────────────────────────────
            case 30:  // reserva_detail → reserva (goBack)
                pressBack();
                currentNode = "reserva";
                break;
            case 31:  // reserva_detail → reserva (delete)
                onView(withId(R.id.button_delete)).perform(click());
                onView(withText("Eliminar")).inRoot(isDialog()).perform(click());
                currentNode = "reserva";
                break;
            case 32:  // reserva_detail → reserva_form (edit)
                onView(withId(R.id.button_edit)).perform(click());
                currentNode = "reserva_form";
                break;

            // ── reserva_form ─────────────────────────────────────
            case 34:  // reserva_form → reserva (goBack)
                pressBack();
                currentNode = "reserva";
                break;
            case 35:  // reserva_form → reserva (cancel)
                onView(withId(R.id.button_cancel)).perform(click());
                currentNode = "reserva";
                break;
            case 36:  // reserva_form → select_quads (continue)
                fillReservaForm();
                onView(withId(R.id.button_continue)).perform(click());
                currentNode = "select_quads";
                break;

            // ── select_quads ─────────────────────────────────────
            case 37:  // select_quads → reserva (goBack)
                pressBack();
                currentNode = "reserva";
                break;
            case 38:  // select_quads → confirm
                selectFirstQuad();
                onView(withId(R.id.button_confirm)).perform(click());
                currentNode = "confirm";
                break;

            // ── confirm ──────────────────────────────────────────
            case 39:  // confirm → reserva (save)
                onView(withId(R.id.button_confirm)).perform(click());
                onView(withText("Aceptar")).inRoot(isDialog()).perform(click());
                currentNode = "reserva";
                break;
            case 40:  // confirm → reserva (cancel)
                onView(withId(R.id.button_cancel)).perform(click());
                currentNode = "reserva";
                break;
            case 41:  // confirm → reserva (goBack)
                pressBack();
                currentNode = "reserva";
                break;

            default:
                throw new IllegalArgumentException("Arista desconocida: " + edgeId);
        }
    }

    // ─────────────────────────────────────────────
    // TEST
    // ─────────────────────────────────────────────
    @Test
    public void testContinuousCoverage() throws InterruptedException {

        List<Edge> edges = getEdges();
        List<Integer> path = buildSafePath(edges);

        int total = path.size();
        int i = 0;
        for (int edgeId : path) {
            i++;
            if (i % 10 == 0) Log.d(TAG, "Progreso: " + i + "/" + total);
            executeEdge(edgeId);
            boolean isScreenTransition = Arrays.asList(1,2,3,4,5,11,12,13,14,15,16,17,18,19,30,31,32,34,35,36,37,38,39,40,41).contains(edgeId);
            Thread.sleep(isScreenTransition ? 80 : 30);
        }
    }
}