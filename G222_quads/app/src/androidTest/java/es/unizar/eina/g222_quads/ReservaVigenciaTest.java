package es.unizar.eina.g222_quads;

import static org.junit.Assert.assertEquals;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import es.unizar.eina.g222_quads.database.Reserva;
import es.unizar.eina.g222_quads.ui.reservas.ReservaViewModel;
import es.unizar.eina.g222_quads.ui.quads.G222_quads;

@RunWith(AndroidJUnit4.class)
public class ReservaVigenciaTest {

    @Rule
    public ActivityScenarioRule<G222_quads> scenarioRule =
            new ActivityScenarioRule<>(G222_quads.class);

    private ReservaViewModel viewModel;

    private long dateToMillis(int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(year, month - 1, day, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    // Fecha "ahora" fija y controlada por el test: 15 junio 2030
    private final long AHORA = dateToMillis(2030, 6, 15);

    // PREVISTA: recogida en el futuro
    private final long PREV_RECOGIDA   = dateToMillis(2030, 7, 1);
    private final long PREV_DEVOLUCION = dateToMillis(2030, 7, 5);

    // VIGENTE: recogida en el pasado, devolución en el futuro
    private final long VIG_RECOGIDA   = dateToMillis(2030, 6, 1);
    private final long VIG_DEVOLUCION = dateToMillis(2030, 6, 20);

    // CADUCADA: devolución en el pasado
    private final long CAD_RECOGIDA   = dateToMillis(2030, 5, 1);
    private final long CAD_DEVOLUCION = dateToMillis(2030, 5, 10);

    @Before
    public void setup() {
        scenarioRule.getScenario().onActivity(activity -> {
            // Obtenemos el ViewModel igual que la UI
            viewModel = new androidx.lifecycle.ViewModelProvider(activity)
                    .get(ReservaViewModel.class);
        });
    }

    @Test
    public void testFiltroPrevistas() {
        scenarioRule.getScenario().onActivity(activity -> {
            List<Reserva> lista = crearListaCompleta();

            viewModel.setFiltro(ReservaViewModel.FILTRO_PREVISTAS);
            List<Reserva> resultado = viewModel.obtenerListaFiltrada(lista, AHORA);

            assertEquals("Solo debe haber 1 reserva prevista", 1, resultado.size());
            assertEquals(PREV_RECOGIDA, resultado.get(0).getFechaRecogida());
        });
    }

    @Test
    public void testFiltroVigentes() {
        scenarioRule.getScenario().onActivity(activity -> {
            List<Reserva> lista = crearListaCompleta();

            viewModel.setFiltro(ReservaViewModel.FILTRO_VIGENTES);
            List<Reserva> resultado = viewModel.obtenerListaFiltrada(lista, AHORA);

            assertEquals("Solo debe haber 1 reserva vigente", 1, resultado.size());
            assertEquals(VIG_RECOGIDA, resultado.get(0).getFechaRecogida());
        });
    }

    @Test
    public void testFiltroCaducadas() {
        scenarioRule.getScenario().onActivity(activity -> {
            List<Reserva> lista = crearListaCompleta();

            viewModel.setFiltro(ReservaViewModel.FILTRO_CADUCADAS);
            List<Reserva> resultado = viewModel.obtenerListaFiltrada(lista, AHORA);

            assertEquals("Solo debe haber 1 reserva caducada", 1, resultado.size());
            assertEquals(CAD_RECOGIDA, resultado.get(0).getFechaRecogida());
        });
    }

    @Test
    public void testFiltroTodas() {
        scenarioRule.getScenario().onActivity(activity -> {
            List<Reserva> lista = crearListaCompleta();

            viewModel.setFiltro(ReservaViewModel.FILTRO_TODAS);
            List<Reserva> resultado = viewModel.obtenerListaFiltrada(lista, AHORA);

            assertEquals("Deben aparecer las 3 reservas", 3, resultado.size());
        });
    }

    private List<Reserva> crearListaCompleta() {
        List<Reserva> lista = new ArrayList<>();
        lista.add(new Reserva("Prevista",  "600000001", PREV_RECOGIDA, false, PREV_DEVOLUCION, false));
        lista.add(new Reserva("Vigente",   "600000002", VIG_RECOGIDA,  false, VIG_DEVOLUCION,  false));
        lista.add(new Reserva("Caducada",  "600000003", CAD_RECOGIDA,  false, CAD_DEVOLUCION,  false));
        return lista;
    }
}