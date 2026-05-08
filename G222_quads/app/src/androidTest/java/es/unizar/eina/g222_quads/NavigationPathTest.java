package es.unizar.eina.g222_quads;

import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import es.unizar.eina.g222_quads.database.Quad;
import es.unizar.eina.g222_quads.database.QuadRepository;
import es.unizar.eina.g222_quads.database.Reserva;
import es.unizar.eina.g222_quads.database.ReservaRepository;
import es.unizar.eina.g222_quads.ui.quads.G222_quads;
import es.unizar.eina.g222_quads.ui.quads.*;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.app.Activity;

import java.util.Calendar;

/**
 * Tests de caminos – Cobertura de PARES de aristas (profundidad 2) para G222.
 *
 * Grafo: 36 aristas.
 *   quadSaved  (12): quad_form → quad       [fusión quadCreated + quadEdited]
 *   reservaSaved (34): confirm → reserva    [fusión reservaSaved anterior]
 *
 * Total: 242 @Test, uno por par de aristas consecutivo posible.
 *
 * Aristas por nodo:
 *   main           1,2
 *   quad           3,4,5,6,7,8
 *   quad_detail    9,10,11
 *   quad_form      12,13,14
 *   reserva        15,16,17,18,19,20,21,22,23,24
 *   reserva_detail 25,26,27,28
 *   reserva_form   29,30,31
 *   select_quads   32,33
 *   confirm        34,35,36
 */
@RunWith(AndroidJUnit4.class)
public class NavigationPathTest {

    @Rule
    public ActivityScenarioRule<G222_quads> activityRule =
            new ActivityScenarioRule<>(G222_quads.class);


    @Before
    public void setup() {
        activityRule.getScenario().onActivity(activity -> {
            // Insertar al menos 1 quad
            QuadRepository quadRepo = activity.getQuadRespositoryMain();
            try {
                quadRepo.deleteAll().get();
                quadRepo.insert(new Quad("1234TST", true, 10.0, "Test quad")).get();
            } catch (Exception e) { throw new RuntimeException(e); }

            // Insertar al menos 1 reserva
            ReservaRepository reservaRepo = activity.getReservaRepositoryMain();
            try {
                reservaRepo.deleteAll().get();
                Calendar c = Calendar.getInstance();
                c.add(Calendar.YEAR, 1);
                long future1 = c.getTimeInMillis();
                c.add(Calendar.DAY_OF_YEAR, 1);
                long future2 = c.getTimeInMillis();
                reservaRepo.insert(new Reserva("Test", "600000001", future1, false, future2, false));
            } catch (Exception e) { throw new RuntimeException(e); }
        });
    }
    /**
     * Ejecuta la acción Espresso de cada arista.
     * Ajusta los R.id.* a los nombres reales de tus vistas.
     */
    private void executeEdge(int edgeId) {
        switch (edgeId) {
            // ── main ─────────────────────────────────────────────
            case 1:  // main --quadList--> quad
                onView(withId(R.id.quad)).perform(click());
                break;
            case 2:  // main --reservaList--> reserva
                onView(withId(R.id.reserva)).perform(click());
                break;

            // ── quad ─────────────────────────────────────────────
            case 3:  // quad --quadDetail--> quad_detail
                onView(withId(R.id.recyclerview)).perform(
                        androidx.test.espresso.contrib.RecyclerViewActions
                                .actionOnItemAtPosition(0, click()));
                break;
            case 4:  // quad --createQuad--> quad_form
                onView(withId(R.id.fab)).perform(click());
                break;
            case 5:  // quad --goBack--> main
                pressBack();
                break;
            case 6:  // quad --ordenarPorMatricula--> quad
                onView(withId(R.id.orden_quads)).perform(click());
                onView(withText("Por matrícula")).perform(click());
                break;
            case 7:  // quad --ordenarPorPrecio--> quad
                onView(withId(R.id.orden_quads)).perform(click());
                onView(withText("Por precio")).perform(click());
                break;
            case 8:  // quad --ordenarPorTipo--> quad
                onView(withId(R.id.orden_quads)).perform(click());
                onView(withText("Por tipo")).perform(click());
                break;

            // ── quad_detail ──────────────────────────────────────
            case 9:  // quad_detail --goBack--> quad
                pressBack();
                break;
            case 10: // quad_detail --deleteQuad--> quad
                onView(withId(R.id.btn_delete)).perform(click());
                onView(withText("Eliminar")).perform(click());
                break;
            case 11: // quad_detail --editQuad--> quad_form
                onView(withId(R.id.btn_edit)).perform(click());
                break;

            // ── quad_form ────────────────────────────────────────
            case 12: // quad_form --quadSaved--> quad  (crear o editar)
                onView(withId(R.id.button_save)).perform(click());
                break;
            case 13: // quad_form --goBack--> quad
                pressBack();
                break;
            case 14: // quad_form --cancel--> quad
                onView(withId(R.id.button_cancel)).perform(click());
                break;

            // ── reserva ──────────────────────────────────────────
            case 15: // reserva --reservaDetail--> reserva_detail
                onView(withId(R.id.recyclerview)).perform(  // ← el ID real tuyo
                        androidx.test.espresso.contrib.RecyclerViewActions
                                .actionOnItemAtPosition(0, click()));
                break;
            case 16: // reserva --createReserva--> reserva_form
                onView(withId(R.id.fab)).perform(click());
                break;
            case 17: // reserva --goBack--> main
                pressBack();
                break;
            case 18: // reserva --filtrarVigentes--> reserva
                onView(withId(R.id.filtro_reservas)).perform(click());
                onView(withText("Reservas vigentes")).perform(click());
                break;
            case 19: // reserva --filtrarFuturas--> reserva
                onView(withId(R.id.filtro_reservas)).perform(click());
                onView(withText("Reservas previstas")).perform(click());
                break;
            case 20: // reserva --filtrarCaducadas--> reserva
                onView(withId(R.id.filtro_reservas)).perform(click());
                onView(withText("Reservas caducadas")).perform(click());
                break;
            case 21: // reserva --ordenarPorNombre--> reserva
                onView(withId(R.id.orden_reservas)).perform(click());
                onView(withText("Por nombre de cliente")).perform(click());
                break;
            case 22: // reserva --ordenarPorTelefono--> reserva
                onView(withId(R.id.orden_reservas)).perform(click());
                onView(withText("Por teléfono")).perform(click());
                break;
            case 23: // reserva --ordenarPorFechaRecogida--> reserva
                onView(withId(R.id.orden_reservas)).perform(click());
                onView(withText("Por fecha de recogida")).perform(click());
                break;
            case 24: // reserva --ordenarPorFechaDevolucion--> reserva
                onView(withId(R.id.orden_reservas)).perform(click());
                onView(withText("Por fecha de devolución")).perform(click());
                break;

            // ── reserva_detail ───────────────────────────────────
            case 25: // reserva_detail --goBack--> reserva
                pressBack();
                break;
            case 26: // reserva_detail --deleteReserva--> reserva
                onView(withId(R.id.btn_delete)).perform(click());
                // onView(withText("Aceptar")).perform(click());
                break;
            case 27: // reserva_detail --editReserva--> reserva_form
                onView(withId(R.id.btn_edit)).perform(click());
                break;
            case 28: // reserva_detail --sendDetails--> external
                onView(withId(R.id.btn_enviar)).perform(click());
                // Intents.intended(hasAction(Intent.ACTION_SEND));
                break;

            // ── reserva_form ─────────────────────────────────────
            case 29: // reserva_form --goBack--> reserva
                pressBack();
                break;
            case 30: // reserva_form --cancel--> reserva
                onView(withId(R.id.button_cancel)).perform(click());
                break;
            case 31: // reserva_form --next--> select_quads
                onView(withId(R.id.button_continue)).perform(click());
                break;

            // ── select_quads ─────────────────────────────────────
            case 32: // select_quads --goBack--> reserva
                pressBack();
                break;
            case 33: // select_quads --next--> confirm
                onView(withId(R.id.button_continue)).perform(click());
                break;

            // ── confirm ──────────────────────────────────────────
            case 34: // confirm --reservaSaved--> reserva  (crear o editar)
                onView(withId(R.id.button_confirm)).perform(click());
                break;
            case 35: // confirm --cancel--> reserva
                onView(withId(R.id.button_cancel)).perform(click());
                break;
            case 36: // confirm --goBack--> reserva
                pressBack();
                break;

            default:
                throw new IllegalArgumentException("Arista desconocida: " + edgeId);
        }
    }

    private void runPath(int... edges) {
        for (int e : edges) executeEdge(e);
    }

    // =============================================================
    //  242 @Test — uno por par de aristas (e1, e2) consecutivo
    //  Nombre: testPair_<e1>_<e2>__<e1Action>__<e2Action>
    // =============================================================
    /** Test #001: par (1,3) — quadList → quad → quadDetail */
    @Test public void testPair_1_3__quadList__quadDetail() { runPath(1, 3); }

    /** Test #002: par (1,4) — quadList → quad → createQuad */
    @Test public void testPair_1_4__quadList__createQuad() { runPath(1, 4); }

    /** Test #003: par (1,5) — quadList → quad → goBack */
    @Test public void testPair_1_5__quadList__goBack() { runPath(1, 5); }

    /** Test #004: par (1,6) — quadList → quad → ordenarPorMatricula */
    @Test public void testPair_1_6__quadList__ordenarPorMatricula() { runPath(1, 6); }

    /** Test #005: par (1,7) — quadList → quad → ordenarPorPrecio */
    @Test public void testPair_1_7__quadList__ordenarPorPrecio() { runPath(1, 7); }

    /** Test #006: par (1,8) — quadList → quad → ordenarPorTipo */
    @Test public void testPair_1_8__quadList__ordenarPorTipo() { runPath(1, 8); }

    /** Test #007: par (2,15) — reservaList → reserva → reservaDetail */
    @Test public void testPair_2_15__reservaList__reservaDetail() { runPath(2, 15); }

    /** Test #008: par (2,16) — reservaList → reserva → createReserva */
    @Test public void testPair_2_16__reservaList__createReserva() { runPath(2, 16); }

    /** Test #009: par (2,17) — reservaList → reserva → goBack */
    @Test public void testPair_2_17__reservaList__goBack() { runPath(2, 17); }

    /** Test #010: par (2,18) — reservaList → reserva → filtrarVigentes */
    @Test public void testPair_2_18__reservaList__filtrarVigentes() { runPath(2, 18); }

    /** Test #011: par (2,19) — reservaList → reserva → filtrarFuturas */
    @Test public void testPair_2_19__reservaList__filtrarFuturas() { runPath(2, 19); }

    /** Test #012: par (2,20) — reservaList → reserva → filtrarCaducadas */
    @Test public void testPair_2_20__reservaList__filtrarCaducadas() { runPath(2, 20); }

    /** Test #013: par (2,21) — reservaList → reserva → ordenarPorNombre */
    @Test public void testPair_2_21__reservaList__ordenarPorNombre() { runPath(2, 21); }

    /** Test #014: par (2,22) — reservaList → reserva → ordenarPorTelefono */
    @Test public void testPair_2_22__reservaList__ordenarPorTelefono() { runPath(2, 22); }

    /** Test #015: par (2,23) — reservaList → reserva → ordenarPorFechaRecogida */
    @Test public void testPair_2_23__reservaList__ordenarPorFechaRecogida() { runPath(2, 23); }

    /** Test #016: par (2,24) — reservaList → reserva → ordenarPorFechaDevolucion */
    @Test public void testPair_2_24__reservaList__ordenarPorFechaDevolucion() { runPath(2, 24); }

    /** Test #017: par (3,9) — quadDetail → quad_detail → goBack */
    @Test public void testPair_3_9__quadDetail__goBack() { runPath(1, 3, 9); }

    /** Test #018: par (3,10) — quadDetail → quad_detail → deleteQuad */
    @Test public void testPair_3_10__quadDetail__deleteQuad() { runPath(1, 3, 10); }

    /** Test #019: par (3,11) — quadDetail → quad_detail → editQuad */
    @Test public void testPair_3_11__quadDetail__editQuad() { runPath(1, 3, 11); }

    /** Test #020: par (4,12) — createQuad → quad_form → quadSaved */
    @Test public void testPair_4_12__createQuad__quadSaved() { runPath(1, 4, 12); }

    /** Test #021: par (4,13) — createQuad → quad_form → goBack */
    @Test public void testPair_4_13__createQuad__goBack() { runPath(1, 4, 13); }

    /** Test #022: par (4,14) — createQuad → quad_form → cancel */
    @Test public void testPair_4_14__createQuad__cancel() { runPath(1, 4, 14); }

    /** Test #023: par (5,1) — goBack → main → quadList */
    @Test public void testPair_5_1__goBack__quadList() { runPath(1, 5, 1); }

    /** Test #024: par (5,2) — goBack → main → reservaList */
    @Test public void testPair_5_2__goBack__reservaList() { runPath(1, 5, 2); }

    /** Test #025: par (6,3) — ordenarPorMatricula → quad → quadDetail */
    @Test public void testPair_6_3__ordenarPorMatricula__quadDetail() { runPath(1, 6, 3); }

    /** Test #026: par (6,4) — ordenarPorMatricula → quad → createQuad */
    @Test public void testPair_6_4__ordenarPorMatricula__createQuad() { runPath(1, 6, 4); }

    /** Test #027: par (6,5) — ordenarPorMatricula → quad → goBack */
    @Test public void testPair_6_5__ordenarPorMatricula__goBack() { runPath(1, 6, 5); }

    /** Test #028: par (6,6) — ordenarPorMatricula → quad → ordenarPorMatricula */
    @Test public void testPair_6_6__ordenarPorMatricula__ordenarPorMatricula() { runPath(1, 6, 6); }

    /** Test #029: par (6,7) — ordenarPorMatricula → quad → ordenarPorPrecio */
    @Test public void testPair_6_7__ordenarPorMatricula__ordenarPorPrecio() { runPath(1, 6, 7); }

    /** Test #030: par (6,8) — ordenarPorMatricula → quad → ordenarPorTipo */
    @Test public void testPair_6_8__ordenarPorMatricula__ordenarPorTipo() { runPath(1, 6, 8); }

    /** Test #031: par (7,3) — ordenarPorPrecio → quad → quadDetail */
    @Test public void testPair_7_3__ordenarPorPrecio__quadDetail() { runPath(1, 7, 3); }

    /** Test #032: par (7,4) — ordenarPorPrecio → quad → createQuad */
    @Test public void testPair_7_4__ordenarPorPrecio__createQuad() { runPath(1, 7, 4); }

    /** Test #033: par (7,5) — ordenarPorPrecio → quad → goBack */
    @Test public void testPair_7_5__ordenarPorPrecio__goBack() { runPath(1, 7, 5); }

    /** Test #034: par (7,6) — ordenarPorPrecio → quad → ordenarPorMatricula */
    @Test public void testPair_7_6__ordenarPorPrecio__ordenarPorMatricula() { runPath(1, 7, 6); }

    /** Test #035: par (7,7) — ordenarPorPrecio → quad → ordenarPorPrecio */
    @Test public void testPair_7_7__ordenarPorPrecio__ordenarPorPrecio() { runPath(1, 7, 7); }

    /** Test #036: par (7,8) — ordenarPorPrecio → quad → ordenarPorTipo */
    @Test public void testPair_7_8__ordenarPorPrecio__ordenarPorTipo() { runPath(1, 7, 8); }

    /** Test #037: par (8,3) — ordenarPorTipo → quad → quadDetail */
    @Test public void testPair_8_3__ordenarPorTipo__quadDetail() { runPath(1, 8, 3); }

    /** Test #038: par (8,4) — ordenarPorTipo → quad → createQuad */
    @Test public void testPair_8_4__ordenarPorTipo__createQuad() { runPath(1, 8, 4); }

    /** Test #039: par (8,5) — ordenarPorTipo → quad → goBack */
    @Test public void testPair_8_5__ordenarPorTipo__goBack() { runPath(1, 8, 5); }

    /** Test #040: par (8,6) — ordenarPorTipo → quad → ordenarPorMatricula */
    @Test public void testPair_8_6__ordenarPorTipo__ordenarPorMatricula() { runPath(1, 8, 6); }

    /** Test #041: par (8,7) — ordenarPorTipo → quad → ordenarPorPrecio */
    @Test public void testPair_8_7__ordenarPorTipo__ordenarPorPrecio() { runPath(1, 8, 7); }

    /** Test #042: par (8,8) — ordenarPorTipo → quad → ordenarPorTipo */
    @Test public void testPair_8_8__ordenarPorTipo__ordenarPorTipo() { runPath(1, 8, 8); }

    /** Test #043: par (9,3) — goBack → quad → quadDetail */
    @Test public void testPair_9_3__goBack__quadDetail() { runPath(1, 3, 9, 3); }

    /** Test #044: par (9,4) — goBack → quad → createQuad */
    @Test public void testPair_9_4__goBack__createQuad() { runPath(1, 3, 9, 4); }

    /** Test #045: par (9,5) — goBack → quad → goBack */
    @Test public void testPair_9_5__goBack__goBack() { runPath(1, 3, 9, 5); }

    /** Test #046: par (9,6) — goBack → quad → ordenarPorMatricula */
    @Test public void testPair_9_6__goBack__ordenarPorMatricula() { runPath(1, 3, 9, 6); }

    /** Test #047: par (9,7) — goBack → quad → ordenarPorPrecio */
    @Test public void testPair_9_7__goBack__ordenarPorPrecio() { runPath(1, 3, 9, 7); }

    /** Test #048: par (9,8) — goBack → quad → ordenarPorTipo */
    @Test public void testPair_9_8__goBack__ordenarPorTipo() { runPath(1, 3, 9, 8); }

    /** Test #049: par (10,3) — deleteQuad → quad → quadDetail */
    @Test public void testPair_10_3__deleteQuad__quadDetail() { runPath(1, 3, 10, 3); }

    /** Test #050: par (10,4) — deleteQuad → quad → createQuad */
    @Test public void testPair_10_4__deleteQuad__createQuad() { runPath(1, 3, 10, 4); }

    /** Test #051: par (10,5) — deleteQuad → quad → goBack */
    @Test public void testPair_10_5__deleteQuad__goBack() { runPath(1, 3, 10, 5); }

    /** Test #052: par (10,6) — deleteQuad → quad → ordenarPorMatricula */
    @Test public void testPair_10_6__deleteQuad__ordenarPorMatricula() { runPath(1, 3, 10, 6); }

    /** Test #053: par (10,7) — deleteQuad → quad → ordenarPorPrecio */
    @Test public void testPair_10_7__deleteQuad__ordenarPorPrecio() { runPath(1, 3, 10, 7); }

    /** Test #054: par (10,8) — deleteQuad → quad → ordenarPorTipo */
    @Test public void testPair_10_8__deleteQuad__ordenarPorTipo() { runPath(1, 3, 10, 8); }

    /** Test #055: par (11,12) — editQuad → quad_form → quadSaved */
    @Test public void testPair_11_12__editQuad__quadSaved() { runPath(1, 3, 11, 12); }

    /** Test #056: par (11,13) — editQuad → quad_form → goBack */
    @Test public void testPair_11_13__editQuad__goBack() { runPath(1, 3, 11, 13); }

    /** Test #057: par (11,14) — editQuad → quad_form → cancel */
    @Test public void testPair_11_14__editQuad__cancel() { runPath(1, 3, 11, 14); }

    /** Test #058: par (12,3) — quadSaved → quad → quadDetail */
    @Test public void testPair_12_3__quadSaved__quadDetail() { runPath(1, 4, 12, 3); }

    /** Test #059: par (12,4) — quadSaved → quad → createQuad */
    @Test public void testPair_12_4__quadSaved__createQuad() { runPath(1, 4, 12, 4); }

    /** Test #060: par (12,5) — quadSaved → quad → goBack */
    @Test public void testPair_12_5__quadSaved__goBack() { runPath(1, 4, 12, 5); }

    /** Test #061: par (12,6) — quadSaved → quad → ordenarPorMatricula */
    @Test public void testPair_12_6__quadSaved__ordenarPorMatricula() { runPath(1, 4, 12, 6); }

    /** Test #062: par (12,7) — quadSaved → quad → ordenarPorPrecio */
    @Test public void testPair_12_7__quadSaved__ordenarPorPrecio() { runPath(1, 4, 12, 7); }

    /** Test #063: par (12,8) — quadSaved → quad → ordenarPorTipo */
    @Test public void testPair_12_8__quadSaved__ordenarPorTipo() { runPath(1, 4, 12, 8); }

    /** Test #064: par (13,3) — goBack → quad → quadDetail */
    @Test public void testPair_13_3__goBack__quadDetail() { runPath(1, 4, 13, 3); }

    /** Test #065: par (13,4) — goBack → quad → createQuad */
    @Test public void testPair_13_4__goBack__createQuad() { runPath(1, 4, 13, 4); }

    /** Test #066: par (13,5) — goBack → quad → goBack */
    @Test public void testPair_13_5__goBack__goBack() { runPath(1, 4, 13, 5); }

    /** Test #067: par (13,6) — goBack → quad → ordenarPorMatricula */
    @Test public void testPair_13_6__goBack__ordenarPorMatricula() { runPath(1, 4, 13, 6); }

    /** Test #068: par (13,7) — goBack → quad → ordenarPorPrecio */
    @Test public void testPair_13_7__goBack__ordenarPorPrecio() { runPath(1, 4, 13, 7); }

    /** Test #069: par (13,8) — goBack → quad → ordenarPorTipo */
    @Test public void testPair_13_8__goBack__ordenarPorTipo() { runPath(1, 4, 13, 8); }

    /** Test #070: par (14,3) — cancel → quad → quadDetail */
    @Test public void testPair_14_3__cancel__quadDetail() { runPath(1, 4, 14, 3); }

    /** Test #071: par (14,4) — cancel → quad → createQuad */
    @Test public void testPair_14_4__cancel__createQuad() { runPath(1, 4, 14, 4); }

    /** Test #072: par (14,5) — cancel → quad → goBack */
    @Test public void testPair_14_5__cancel__goBack() { runPath(1, 4, 14, 5); }

    /** Test #073: par (14,6) — cancel → quad → ordenarPorMatricula */
    @Test public void testPair_14_6__cancel__ordenarPorMatricula() { runPath(1, 4, 14, 6); }

    /** Test #074: par (14,7) — cancel → quad → ordenarPorPrecio */
    @Test public void testPair_14_7__cancel__ordenarPorPrecio() { runPath(1, 4, 14, 7); }

    /** Test #075: par (14,8) — cancel → quad → ordenarPorTipo */
    @Test public void testPair_14_8__cancel__ordenarPorTipo() { runPath(1, 4, 14, 8); }

    /** Test #076: par (15,25) — reservaDetail → reserva_detail → goBack */
    @Test public void testPair_15_25__reservaDetail__goBack() { runPath(2, 15, 25); }

    /** Test #077: par (15,26) — reservaDetail → reserva_detail → deleteReserva */
    @Test public void testPair_15_26__reservaDetail__deleteReserva() { runPath(2, 15, 26); }

    /** Test #078: par (15,27) — reservaDetail → reserva_detail → editReserva */
    @Test public void testPair_15_27__reservaDetail__editReserva() { runPath(2, 15, 27); }

    /** Test #079: par (15,28) — reservaDetail → reserva_detail → sendDetails */
    @Test public void testPair_15_28__reservaDetail__sendDetails() { runPath(2, 15, 28); }

    /** Test #080: par (16,29) — createReserva → reserva_form → goBack */
    @Test public void testPair_16_29__createReserva__goBack() { runPath(2, 16, 29); }

    /** Test #081: par (16,30) — createReserva → reserva_form → cancel */
    @Test public void testPair_16_30__createReserva__cancel() { runPath(2, 16, 30); }

    /** Test #082: par (16,31) — createReserva → reserva_form → next */
    @Test public void testPair_16_31__createReserva__next() { runPath(2, 16, 31); }

    /** Test #083: par (17,1) — goBack → main → quadList */
    @Test public void testPair_17_1__goBack__quadList() { runPath(2, 17, 1); }

    /** Test #084: par (17,2) — goBack → main → reservaList */
    @Test public void testPair_17_2__goBack__reservaList() { runPath(2, 17, 2); }

    /** Test #085: par (18,15) — filtrarVigentes → reserva → reservaDetail */
    @Test public void testPair_18_15__filtrarVigentes__reservaDetail() { runPath(2, 18, 15); }

    /** Test #086: par (18,16) — filtrarVigentes → reserva → createReserva */
    @Test public void testPair_18_16__filtrarVigentes__createReserva() { runPath(2, 18, 16); }

    /** Test #087: par (18,17) — filtrarVigentes → reserva → goBack */
    @Test public void testPair_18_17__filtrarVigentes__goBack() { runPath(2, 18, 17); }

    /** Test #088: par (18,18) — filtrarVigentes → reserva → filtrarVigentes */
    @Test public void testPair_18_18__filtrarVigentes__filtrarVigentes() { runPath(2, 18, 18); }

    /** Test #089: par (18,19) — filtrarVigentes → reserva → filtrarFuturas */
    @Test public void testPair_18_19__filtrarVigentes__filtrarFuturas() { runPath(2, 18, 19); }

    /** Test #090: par (18,20) — filtrarVigentes → reserva → filtrarCaducadas */
    @Test public void testPair_18_20__filtrarVigentes__filtrarCaducadas() { runPath(2, 18, 20); }

    /** Test #091: par (18,21) — filtrarVigentes → reserva → ordenarPorNombre */
    @Test public void testPair_18_21__filtrarVigentes__ordenarPorNombre() { runPath(2, 18, 21); }

    /** Test #092: par (18,22) — filtrarVigentes → reserva → ordenarPorTelefono */
    @Test public void testPair_18_22__filtrarVigentes__ordenarPorTelefono() { runPath(2, 18, 22); }

    /** Test #093: par (18,23) — filtrarVigentes → reserva → ordenarPorFechaRecogida */
    @Test public void testPair_18_23__filtrarVigentes__ordenarPorFechaRecogida() { runPath(2, 18, 23); }

    /** Test #094: par (18,24) — filtrarVigentes → reserva → ordenarPorFechaDevolucion */
    @Test public void testPair_18_24__filtrarVigentes__ordenarPorFechaDevolucion() { runPath(2, 18, 24); }

    /** Test #095: par (19,15) — filtrarFuturas → reserva → reservaDetail */
    @Test public void testPair_19_15__filtrarFuturas__reservaDetail() { runPath(2, 19, 15); }

    /** Test #096: par (19,16) — filtrarFuturas → reserva → createReserva */
    @Test public void testPair_19_16__filtrarFuturas__createReserva() { runPath(2, 19, 16); }

    /** Test #097: par (19,17) — filtrarFuturas → reserva → goBack */
    @Test public void testPair_19_17__filtrarFuturas__goBack() { runPath(2, 19, 17); }

    /** Test #098: par (19,18) — filtrarFuturas → reserva → filtrarVigentes */
    @Test public void testPair_19_18__filtrarFuturas__filtrarVigentes() { runPath(2, 19, 18); }

    /** Test #099: par (19,19) — filtrarFuturas → reserva → filtrarFuturas */
    @Test public void testPair_19_19__filtrarFuturas__filtrarFuturas() { runPath(2, 19, 19); }

    /** Test #100: par (19,20) — filtrarFuturas → reserva → filtrarCaducadas */
    @Test public void testPair_19_20__filtrarFuturas__filtrarCaducadas() { runPath(2, 19, 20); }

    /** Test #101: par (19,21) — filtrarFuturas → reserva → ordenarPorNombre */
    @Test public void testPair_19_21__filtrarFuturas__ordenarPorNombre() { runPath(2, 19, 21); }

    /** Test #102: par (19,22) — filtrarFuturas → reserva → ordenarPorTelefono */
    @Test public void testPair_19_22__filtrarFuturas__ordenarPorTelefono() { runPath(2, 19, 22); }

    /** Test #103: par (19,23) — filtrarFuturas → reserva → ordenarPorFechaRecogida */
    @Test public void testPair_19_23__filtrarFuturas__ordenarPorFechaRecogida() { runPath(2, 19, 23); }

    /** Test #104: par (19,24) — filtrarFuturas → reserva → ordenarPorFechaDevolucion */
    @Test public void testPair_19_24__filtrarFuturas__ordenarPorFechaDevolucion() { runPath(2, 19, 24); }

    /** Test #105: par (20,15) — filtrarCaducadas → reserva → reservaDetail */
    @Test public void testPair_20_15__filtrarCaducadas__reservaDetail() { runPath(2, 20, 15); }

    /** Test #106: par (20,16) — filtrarCaducadas → reserva → createReserva */
    @Test public void testPair_20_16__filtrarCaducadas__createReserva() { runPath(2, 20, 16); }

    /** Test #107: par (20,17) — filtrarCaducadas → reserva → goBack */
    @Test public void testPair_20_17__filtrarCaducadas__goBack() { runPath(2, 20, 17); }

    /** Test #108: par (20,18) — filtrarCaducadas → reserva → filtrarVigentes */
    @Test public void testPair_20_18__filtrarCaducadas__filtrarVigentes() { runPath(2, 20, 18); }

    /** Test #109: par (20,19) — filtrarCaducadas → reserva → filtrarFuturas */
    @Test public void testPair_20_19__filtrarCaducadas__filtrarFuturas() { runPath(2, 20, 19); }

    /** Test #110: par (20,20) — filtrarCaducadas → reserva → filtrarCaducadas */
    @Test public void testPair_20_20__filtrarCaducadas__filtrarCaducadas() { runPath(2, 20, 20); }

    /** Test #111: par (20,21) — filtrarCaducadas → reserva → ordenarPorNombre */
    @Test public void testPair_20_21__filtrarCaducadas__ordenarPorNombre() { runPath(2, 20, 21); }

    /** Test #112: par (20,22) — filtrarCaducadas → reserva → ordenarPorTelefono */
    @Test public void testPair_20_22__filtrarCaducadas__ordenarPorTelefono() { runPath(2, 20, 22); }

    /** Test #113: par (20,23) — filtrarCaducadas → reserva → ordenarPorFechaRecogida */
    @Test public void testPair_20_23__filtrarCaducadas__ordenarPorFechaRecogida() { runPath(2, 20, 23); }

    /** Test #114: par (20,24) — filtrarCaducadas → reserva → ordenarPorFechaDevolucion */
    @Test public void testPair_20_24__filtrarCaducadas__ordenarPorFechaDevolucion() { runPath(2, 20, 24); }

    /** Test #115: par (21,15) — ordenarPorNombre → reserva → reservaDetail */
    @Test public void testPair_21_15__ordenarPorNombre__reservaDetail() { runPath(2, 21, 15); }

    /** Test #116: par (21,16) — ordenarPorNombre → reserva → createReserva */
    @Test public void testPair_21_16__ordenarPorNombre__createReserva() { runPath(2, 21, 16); }

    /** Test #117: par (21,17) — ordenarPorNombre → reserva → goBack */
    @Test public void testPair_21_17__ordenarPorNombre__goBack() { runPath(2, 21, 17); }

    /** Test #118: par (21,18) — ordenarPorNombre → reserva → filtrarVigentes */
    @Test public void testPair_21_18__ordenarPorNombre__filtrarVigentes() { runPath(2, 21, 18); }

    /** Test #119: par (21,19) — ordenarPorNombre → reserva → filtrarFuturas */
    @Test public void testPair_21_19__ordenarPorNombre__filtrarFuturas() { runPath(2, 21, 19); }

    /** Test #120: par (21,20) — ordenarPorNombre → reserva → filtrarCaducadas */
    @Test public void testPair_21_20__ordenarPorNombre__filtrarCaducadas() { runPath(2, 21, 20); }

    /** Test #121: par (21,21) — ordenarPorNombre → reserva → ordenarPorNombre */
    @Test public void testPair_21_21__ordenarPorNombre__ordenarPorNombre() { runPath(2, 21, 21); }

    /** Test #122: par (21,22) — ordenarPorNombre → reserva → ordenarPorTelefono */
    @Test public void testPair_21_22__ordenarPorNombre__ordenarPorTelefono() { runPath(2, 21, 22); }

    /** Test #123: par (21,23) — ordenarPorNombre → reserva → ordenarPorFechaRecogida */
    @Test public void testPair_21_23__ordenarPorNombre__ordenarPorFechaRecogida() { runPath(2, 21, 23); }

    /** Test #124: par (21,24) — ordenarPorNombre → reserva → ordenarPorFechaDevolucion */
    @Test public void testPair_21_24__ordenarPorNombre__ordenarPorFechaDevolucion() { runPath(2, 21, 24); }

    /** Test #125: par (22,15) — ordenarPorTelefono → reserva → reservaDetail */
    @Test public void testPair_22_15__ordenarPorTelefono__reservaDetail() { runPath(2, 22, 15); }

    /** Test #126: par (22,16) — ordenarPorTelefono → reserva → createReserva */
    @Test public void testPair_22_16__ordenarPorTelefono__createReserva() { runPath(2, 22, 16); }

    /** Test #127: par (22,17) — ordenarPorTelefono → reserva → goBack */
    @Test public void testPair_22_17__ordenarPorTelefono__goBack() { runPath(2, 22, 17); }

    /** Test #128: par (22,18) — ordenarPorTelefono → reserva → filtrarVigentes */
    @Test public void testPair_22_18__ordenarPorTelefono__filtrarVigentes() { runPath(2, 22, 18); }

    /** Test #129: par (22,19) — ordenarPorTelefono → reserva → filtrarFuturas */
    @Test public void testPair_22_19__ordenarPorTelefono__filtrarFuturas() { runPath(2, 22, 19); }

    /** Test #130: par (22,20) — ordenarPorTelefono → reserva → filtrarCaducadas */
    @Test public void testPair_22_20__ordenarPorTelefono__filtrarCaducadas() { runPath(2, 22, 20); }

    /** Test #131: par (22,21) — ordenarPorTelefono → reserva → ordenarPorNombre */
    @Test public void testPair_22_21__ordenarPorTelefono__ordenarPorNombre() { runPath(2, 22, 21); }

    /** Test #132: par (22,22) — ordenarPorTelefono → reserva → ordenarPorTelefono */
    @Test public void testPair_22_22__ordenarPorTelefono__ordenarPorTelefono() { runPath(2, 22, 22); }

    /** Test #133: par (22,23) — ordenarPorTelefono → reserva → ordenarPorFechaRecogida */
    @Test public void testPair_22_23__ordenarPorTelefono__ordenarPorFechaRecogida() { runPath(2, 22, 23); }

    /** Test #134: par (22,24) — ordenarPorTelefono → reserva → ordenarPorFechaDevolucion */
    @Test public void testPair_22_24__ordenarPorTelefono__ordenarPorFechaDevolucion() { runPath(2, 22, 24); }

    /** Test #135: par (23,15) — ordenarPorFechaRecogida → reserva → reservaDetail */
    @Test public void testPair_23_15__ordenarPorFechaRecogida__reservaDetail() { runPath(2, 23, 15); }

    /** Test #136: par (23,16) — ordenarPorFechaRecogida → reserva → createReserva */
    @Test public void testPair_23_16__ordenarPorFechaRecogida__createReserva() { runPath(2, 23, 16); }

    /** Test #137: par (23,17) — ordenarPorFechaRecogida → reserva → goBack */
    @Test public void testPair_23_17__ordenarPorFechaRecogida__goBack() { runPath(2, 23, 17); }

    /** Test #138: par (23,18) — ordenarPorFechaRecogida → reserva → filtrarVigentes */
    @Test public void testPair_23_18__ordenarPorFechaRecogida__filtrarVigentes() { runPath(2, 23, 18); }

    /** Test #139: par (23,19) — ordenarPorFechaRecogida → reserva → filtrarFuturas */
    @Test public void testPair_23_19__ordenarPorFechaRecogida__filtrarFuturas() { runPath(2, 23, 19); }

    /** Test #140: par (23,20) — ordenarPorFechaRecogida → reserva → filtrarCaducadas */
    @Test public void testPair_23_20__ordenarPorFechaRecogida__filtrarCaducadas() { runPath(2, 23, 20); }

    /** Test #141: par (23,21) — ordenarPorFechaRecogida → reserva → ordenarPorNombre */
    @Test public void testPair_23_21__ordenarPorFechaRecogida__ordenarPorNombre() { runPath(2, 23, 21); }

    /** Test #142: par (23,22) — ordenarPorFechaRecogida → reserva → ordenarPorTelefono */
    @Test public void testPair_23_22__ordenarPorFechaRecogida__ordenarPorTelefono() { runPath(2, 23, 22); }

    /** Test #143: par (23,23) — ordenarPorFechaRecogida → reserva → ordenarPorFechaRecogida */
    @Test public void testPair_23_23__ordenarPorFechaRecogida__ordenarPorFechaRecogida() { runPath(2, 23, 23); }

    /** Test #144: par (23,24) — ordenarPorFechaRecogida → reserva → ordenarPorFechaDevolucion */
    @Test public void testPair_23_24__ordenarPorFechaRecogida__ordenarPorFechaDevolucion() { runPath(2, 23, 24); }

    /** Test #145: par (24,15) — ordenarPorFechaDevolucion → reserva → reservaDetail */
    @Test public void testPair_24_15__ordenarPorFechaDevolucion__reservaDetail() { runPath(2, 24, 15); }

    /** Test #146: par (24,16) — ordenarPorFechaDevolucion → reserva → createReserva */
    @Test public void testPair_24_16__ordenarPorFechaDevolucion__createReserva() { runPath(2, 24, 16); }

    /** Test #147: par (24,17) — ordenarPorFechaDevolucion → reserva → goBack */
    @Test public void testPair_24_17__ordenarPorFechaDevolucion__goBack() { runPath(2, 24, 17); }

    /** Test #148: par (24,18) — ordenarPorFechaDevolucion → reserva → filtrarVigentes */
    @Test public void testPair_24_18__ordenarPorFechaDevolucion__filtrarVigentes() { runPath(2, 24, 18); }

    /** Test #149: par (24,19) — ordenarPorFechaDevolucion → reserva → filtrarFuturas */
    @Test public void testPair_24_19__ordenarPorFechaDevolucion__filtrarFuturas() { runPath(2, 24, 19); }

    /** Test #150: par (24,20) — ordenarPorFechaDevolucion → reserva → filtrarCaducadas */
    @Test public void testPair_24_20__ordenarPorFechaDevolucion__filtrarCaducadas() { runPath(2, 24, 20); }

    /** Test #151: par (24,21) — ordenarPorFechaDevolucion → reserva → ordenarPorNombre */
    @Test public void testPair_24_21__ordenarPorFechaDevolucion__ordenarPorNombre() { runPath(2, 24, 21); }

    /** Test #152: par (24,22) — ordenarPorFechaDevolucion → reserva → ordenarPorTelefono */
    @Test public void testPair_24_22__ordenarPorFechaDevolucion__ordenarPorTelefono() { runPath(2, 24, 22); }

    /** Test #153: par (24,23) — ordenarPorFechaDevolucion → reserva → ordenarPorFechaRecogida */
    @Test public void testPair_24_23__ordenarPorFechaDevolucion__ordenarPorFechaRecogida() { runPath(2, 24, 23); }

    /** Test #154: par (24,24) — ordenarPorFechaDevolucion → reserva → ordenarPorFechaDevolucion */
    @Test public void testPair_24_24__ordenarPorFechaDevolucion__ordenarPorFechaDevolucion() { runPath(2, 24, 24); }

    /** Test #155: par (25,15) — goBack → reserva → reservaDetail */
    @Test public void testPair_25_15__goBack__reservaDetail() { runPath(2, 15, 25, 15); }

    /** Test #156: par (25,16) — goBack → reserva → createReserva */
    @Test public void testPair_25_16__goBack__createReserva() { runPath(2, 15, 25, 16); }

    /** Test #157: par (25,17) — goBack → reserva → goBack */
    @Test public void testPair_25_17__goBack__goBack() { runPath(2, 15, 25, 17); }

    /** Test #158: par (25,18) — goBack → reserva → filtrarVigentes */
    @Test public void testPair_25_18__goBack__filtrarVigentes() { runPath(2, 15, 25, 18); }

    /** Test #159: par (25,19) — goBack → reserva → filtrarFuturas */
    @Test public void testPair_25_19__goBack__filtrarFuturas() { runPath(2, 15, 25, 19); }

    /** Test #160: par (25,20) — goBack → reserva → filtrarCaducadas */
    @Test public void testPair_25_20__goBack__filtrarCaducadas() { runPath(2, 15, 25, 20); }

    /** Test #161: par (25,21) — goBack → reserva → ordenarPorNombre */
    @Test public void testPair_25_21__goBack__ordenarPorNombre() { runPath(2, 15, 25, 21); }

    /** Test #162: par (25,22) — goBack → reserva → ordenarPorTelefono */
    @Test public void testPair_25_22__goBack__ordenarPorTelefono() { runPath(2, 15, 25, 22); }

    /** Test #163: par (25,23) — goBack → reserva → ordenarPorFechaRecogida */
    @Test public void testPair_25_23__goBack__ordenarPorFechaRecogida() { runPath(2, 15, 25, 23); }

    /** Test #164: par (25,24) — goBack → reserva → ordenarPorFechaDevolucion */
    @Test public void testPair_25_24__goBack__ordenarPorFechaDevolucion() { runPath(2, 15, 25, 24); }

    /** Test #165: par (26,15) — deleteReserva → reserva → reservaDetail */
    @Test public void testPair_26_15__deleteReserva__reservaDetail() { runPath(2, 15, 26, 15); }

    /** Test #166: par (26,16) — deleteReserva → reserva → createReserva */
    @Test public void testPair_26_16__deleteReserva__createReserva() { runPath(2, 15, 26, 16); }

    /** Test #167: par (26,17) — deleteReserva → reserva → goBack */
    @Test public void testPair_26_17__deleteReserva__goBack() { runPath(2, 15, 26, 17); }

    /** Test #168: par (26,18) — deleteReserva → reserva → filtrarVigentes */
    @Test public void testPair_26_18__deleteReserva__filtrarVigentes() { runPath(2, 15, 26, 18); }

    /** Test #169: par (26,19) — deleteReserva → reserva → filtrarFuturas */
    @Test public void testPair_26_19__deleteReserva__filtrarFuturas() { runPath(2, 15, 26, 19); }

    /** Test #170: par (26,20) — deleteReserva → reserva → filtrarCaducadas */
    @Test public void testPair_26_20__deleteReserva__filtrarCaducadas() { runPath(2, 15, 26, 20); }

    /** Test #171: par (26,21) — deleteReserva → reserva → ordenarPorNombre */
    @Test public void testPair_26_21__deleteReserva__ordenarPorNombre() { runPath(2, 15, 26, 21); }

    /** Test #172: par (26,22) — deleteReserva → reserva → ordenarPorTelefono */
    @Test public void testPair_26_22__deleteReserva__ordenarPorTelefono() { runPath(2, 15, 26, 22); }

    /** Test #173: par (26,23) — deleteReserva → reserva → ordenarPorFechaRecogida */
    @Test public void testPair_26_23__deleteReserva__ordenarPorFechaRecogida() { runPath(2, 15, 26, 23); }

    /** Test #174: par (26,24) — deleteReserva → reserva → ordenarPorFechaDevolucion */
    @Test public void testPair_26_24__deleteReserva__ordenarPorFechaDevolucion() { runPath(2, 15, 26, 24); }

    /** Test #175: par (27,29) — editReserva → reserva_form → goBack */
    @Test public void testPair_27_29__editReserva__goBack() { runPath(2, 15, 27, 29); }

    /** Test #176: par (27,30) — editReserva → reserva_form → cancel */
    @Test public void testPair_27_30__editReserva__cancel() { runPath(2, 15, 27, 30); }

    /** Test #177: par (27,31) — editReserva → reserva_form → next */
    @Test public void testPair_27_31__editReserva__next() { runPath(2, 15, 27, 31); }

    /** Test #178: par (29,15) — goBack → reserva → reservaDetail */
    @Test public void testPair_29_15__goBack__reservaDetail() { runPath(2, 16, 29, 15); }

    /** Test #179: par (29,16) — goBack → reserva → createReserva */
    @Test public void testPair_29_16__goBack__createReserva() { runPath(2, 16, 29, 16); }

    /** Test #180: par (29,17) — goBack → reserva → goBack */
    @Test public void testPair_29_17__goBack__goBack() { runPath(2, 16, 29, 17); }

    /** Test #181: par (29,18) — goBack → reserva → filtrarVigentes */
    @Test public void testPair_29_18__goBack__filtrarVigentes() { runPath(2, 16, 29, 18); }

    /** Test #182: par (29,19) — goBack → reserva → filtrarFuturas */
    @Test public void testPair_29_19__goBack__filtrarFuturas() { runPath(2, 16, 29, 19); }

    /** Test #183: par (29,20) — goBack → reserva → filtrarCaducadas */
    @Test public void testPair_29_20__goBack__filtrarCaducadas() { runPath(2, 16, 29, 20); }

    /** Test #184: par (29,21) — goBack → reserva → ordenarPorNombre */
    @Test public void testPair_29_21__goBack__ordenarPorNombre() { runPath(2, 16, 29, 21); }

    /** Test #185: par (29,22) — goBack → reserva → ordenarPorTelefono */
    @Test public void testPair_29_22__goBack__ordenarPorTelefono() { runPath(2, 16, 29, 22); }

    /** Test #186: par (29,23) — goBack → reserva → ordenarPorFechaRecogida */
    @Test public void testPair_29_23__goBack__ordenarPorFechaRecogida() { runPath(2, 16, 29, 23); }

    /** Test #187: par (29,24) — goBack → reserva → ordenarPorFechaDevolucion */
    @Test public void testPair_29_24__goBack__ordenarPorFechaDevolucion() { runPath(2, 16, 29, 24); }

    /** Test #188: par (30,15) — cancel → reserva → reservaDetail */
    @Test public void testPair_30_15__cancel__reservaDetail() { runPath(2, 16, 30, 15); }

    /** Test #189: par (30,16) — cancel → reserva → createReserva */
    @Test public void testPair_30_16__cancel__createReserva() { runPath(2, 16, 30, 16); }

    /** Test #190: par (30,17) — cancel → reserva → goBack */
    @Test public void testPair_30_17__cancel__goBack() { runPath(2, 16, 30, 17); }

    /** Test #191: par (30,18) — cancel → reserva → filtrarVigentes */
    @Test public void testPair_30_18__cancel__filtrarVigentes() { runPath(2, 16, 30, 18); }

    /** Test #192: par (30,19) — cancel → reserva → filtrarFuturas */
    @Test public void testPair_30_19__cancel__filtrarFuturas() { runPath(2, 16, 30, 19); }

    /** Test #193: par (30,20) — cancel → reserva → filtrarCaducadas */
    @Test public void testPair_30_20__cancel__filtrarCaducadas() { runPath(2, 16, 30, 20); }

    /** Test #194: par (30,21) — cancel → reserva → ordenarPorNombre */
    @Test public void testPair_30_21__cancel__ordenarPorNombre() { runPath(2, 16, 30, 21); }

    /** Test #195: par (30,22) — cancel → reserva → ordenarPorTelefono */
    @Test public void testPair_30_22__cancel__ordenarPorTelefono() { runPath(2, 16, 30, 22); }

    /** Test #196: par (30,23) — cancel → reserva → ordenarPorFechaRecogida */
    @Test public void testPair_30_23__cancel__ordenarPorFechaRecogida() { runPath(2, 16, 30, 23); }

    /** Test #197: par (30,24) — cancel → reserva → ordenarPorFechaDevolucion */
    @Test public void testPair_30_24__cancel__ordenarPorFechaDevolucion() { runPath(2, 16, 30, 24); }

    /** Test #198: par (31,32) — next → select_quads → goBack */
    @Test public void testPair_31_32__next__goBack() { runPath(2, 16, 31, 32); }

    /** Test #199: par (31,33) — next → select_quads → next */
    @Test public void testPair_31_33__next__next() { runPath(2, 16, 31, 33); }

    /** Test #200: par (32,15) — goBack → reserva → reservaDetail */
    @Test public void testPair_32_15__goBack__reservaDetail() { runPath(2, 16, 31, 32, 15); }

    /** Test #201: par (32,16) — goBack → reserva → createReserva */
    @Test public void testPair_32_16__goBack__createReserva() { runPath(2, 16, 31, 32, 16); }

    /** Test #202: par (32,17) — goBack → reserva → goBack */
    @Test public void testPair_32_17__goBack__goBack() { runPath(2, 16, 31, 32, 17); }

    /** Test #203: par (32,18) — goBack → reserva → filtrarVigentes */
    @Test public void testPair_32_18__goBack__filtrarVigentes() { runPath(2, 16, 31, 32, 18); }

    /** Test #204: par (32,19) — goBack → reserva → filtrarFuturas */
    @Test public void testPair_32_19__goBack__filtrarFuturas() { runPath(2, 16, 31, 32, 19); }

    /** Test #205: par (32,20) — goBack → reserva → filtrarCaducadas */
    @Test public void testPair_32_20__goBack__filtrarCaducadas() { runPath(2, 16, 31, 32, 20); }

    /** Test #206: par (32,21) — goBack → reserva → ordenarPorNombre */
    @Test public void testPair_32_21__goBack__ordenarPorNombre() { runPath(2, 16, 31, 32, 21); }

    /** Test #207: par (32,22) — goBack → reserva → ordenarPorTelefono */
    @Test public void testPair_32_22__goBack__ordenarPorTelefono() { runPath(2, 16, 31, 32, 22); }

    /** Test #208: par (32,23) — goBack → reserva → ordenarPorFechaRecogida */
    @Test public void testPair_32_23__goBack__ordenarPorFechaRecogida() { runPath(2, 16, 31, 32, 23); }

    /** Test #209: par (32,24) — goBack → reserva → ordenarPorFechaDevolucion */
    @Test public void testPair_32_24__goBack__ordenarPorFechaDevolucion() { runPath(2, 16, 31, 32, 24); }

    /** Test #210: par (33,34) — next → confirm → reservaSaved */
    @Test public void testPair_33_34__next__reservaSaved() { runPath(2, 16, 31, 33, 34); }

    /** Test #211: par (33,35) — next → confirm → cancel */
    @Test public void testPair_33_35__next__cancel() { runPath(2, 16, 31, 33, 35); }

    /** Test #212: par (33,36) — next → confirm → goBack */
    @Test public void testPair_33_36__next__goBack() { runPath(2, 16, 31, 33, 36); }

    /** Test #213: par (34,15) — reservaSaved → reserva → reservaDetail */
    @Test public void testPair_34_15__reservaSaved__reservaDetail() { runPath(2, 16, 31, 33, 34, 15); }

    /** Test #214: par (34,16) — reservaSaved → reserva → createReserva */
    @Test public void testPair_34_16__reservaSaved__createReserva() { runPath(2, 16, 31, 33, 34, 16); }

    /** Test #215: par (34,17) — reservaSaved → reserva → goBack */
    @Test public void testPair_34_17__reservaSaved__goBack() { runPath(2, 16, 31, 33, 34, 17); }

    /** Test #216: par (34,18) — reservaSaved → reserva → filtrarVigentes */
    @Test public void testPair_34_18__reservaSaved__filtrarVigentes() { runPath(2, 16, 31, 33, 34, 18); }

    /** Test #217: par (34,19) — reservaSaved → reserva → filtrarFuturas */
    @Test public void testPair_34_19__reservaSaved__filtrarFuturas() { runPath(2, 16, 31, 33, 34, 19); }

    /** Test #218: par (34,20) — reservaSaved → reserva → filtrarCaducadas */
    @Test public void testPair_34_20__reservaSaved__filtrarCaducadas() { runPath(2, 16, 31, 33, 34, 20); }

    /** Test #219: par (34,21) — reservaSaved → reserva → ordenarPorNombre */
    @Test public void testPair_34_21__reservaSaved__ordenarPorNombre() { runPath(2, 16, 31, 33, 34, 21); }

    /** Test #220: par (34,22) — reservaSaved → reserva → ordenarPorTelefono */
    @Test public void testPair_34_22__reservaSaved__ordenarPorTelefono() { runPath(2, 16, 31, 33, 34, 22); }

    /** Test #221: par (34,23) — reservaSaved → reserva → ordenarPorFechaRecogida */
    @Test public void testPair_34_23__reservaSaved__ordenarPorFechaRecogida() { runPath(2, 16, 31, 33, 34, 23); }

    /** Test #222: par (34,24) — reservaSaved → reserva → ordenarPorFechaDevolucion */
    @Test public void testPair_34_24__reservaSaved__ordenarPorFechaDevolucion() { runPath(2, 16, 31, 33, 34, 24); }

    /** Test #223: par (35,15) — cancel → reserva → reservaDetail */
    @Test public void testPair_35_15__cancel__reservaDetail() { runPath(2, 16, 31, 33, 35, 15); }

    /** Test #224: par (35,16) — cancel → reserva → createReserva */
    @Test public void testPair_35_16__cancel__createReserva() { runPath(2, 16, 31, 33, 35, 16); }

    /** Test #225: par (35,17) — cancel → reserva → goBack */
    @Test public void testPair_35_17__cancel__goBack() { runPath(2, 16, 31, 33, 35, 17); }

    /** Test #226: par (35,18) — cancel → reserva → filtrarVigentes */
    @Test public void testPair_35_18__cancel__filtrarVigentes() { runPath(2, 16, 31, 33, 35, 18); }

    /** Test #227: par (35,19) — cancel → reserva → filtrarFuturas */
    @Test public void testPair_35_19__cancel__filtrarFuturas() { runPath(2, 16, 31, 33, 35, 19); }

    /** Test #228: par (35,20) — cancel → reserva → filtrarCaducadas */
    @Test public void testPair_35_20__cancel__filtrarCaducadas() { runPath(2, 16, 31, 33, 35, 20); }

    /** Test #229: par (35,21) — cancel → reserva → ordenarPorNombre */
    @Test public void testPair_35_21__cancel__ordenarPorNombre() { runPath(2, 16, 31, 33, 35, 21); }

    /** Test #230: par (35,22) — cancel → reserva → ordenarPorTelefono */
    @Test public void testPair_35_22__cancel__ordenarPorTelefono() { runPath(2, 16, 31, 33, 35, 22); }

    /** Test #231: par (35,23) — cancel → reserva → ordenarPorFechaRecogida */
    @Test public void testPair_35_23__cancel__ordenarPorFechaRecogida() { runPath(2, 16, 31, 33, 35, 23); }

    /** Test #232: par (35,24) — cancel → reserva → ordenarPorFechaDevolucion */
    @Test public void testPair_35_24__cancel__ordenarPorFechaDevolucion() { runPath(2, 16, 31, 33, 35, 24); }

    /** Test #233: par (36,15) — goBack → reserva → reservaDetail */
    @Test public void testPair_36_15__goBack__reservaDetail() { runPath(2, 16, 31, 33, 36, 15); }

    /** Test #234: par (36,16) — goBack → reserva → createReserva */
    @Test public void testPair_36_16__goBack__createReserva() { runPath(2, 16, 31, 33, 36, 16); }

    /** Test #235: par (36,17) — goBack → reserva → goBack */
    @Test public void testPair_36_17__goBack__goBack() { runPath(2, 16, 31, 33, 36, 17); }

    /** Test #236: par (36,18) — goBack → reserva → filtrarVigentes */
    @Test public void testPair_36_18__goBack__filtrarVigentes() { runPath(2, 16, 31, 33, 36, 18); }

    /** Test #237: par (36,19) — goBack → reserva → filtrarFuturas */
    @Test public void testPair_36_19__goBack__filtrarFuturas() { runPath(2, 16, 31, 33, 36, 19); }

    /** Test #238: par (36,20) — goBack → reserva → filtrarCaducadas */
    @Test public void testPair_36_20__goBack__filtrarCaducadas() { runPath(2, 16, 31, 33, 36, 20); }

    /** Test #239: par (36,21) — goBack → reserva → ordenarPorNombre */
    @Test public void testPair_36_21__goBack__ordenarPorNombre() { runPath(2, 16, 31, 33, 36, 21); }

    /** Test #240: par (36,22) — goBack → reserva → ordenarPorTelefono */
    @Test public void testPair_36_22__goBack__ordenarPorTelefono() { runPath(2, 16, 31, 33, 36, 22); }

    /** Test #241: par (36,23) — goBack → reserva → ordenarPorFechaRecogida */
    @Test public void testPair_36_23__goBack__ordenarPorFechaRecogida() { runPath(2, 16, 31, 33, 36, 23); }

    /** Test #242: par (36,24) — goBack → reserva → ordenarPorFechaDevolucion */
    @Test public void testPair_36_24__goBack__ordenarPorFechaDevolucion() { runPath(2, 16, 31, 33, 36, 24); }
}
