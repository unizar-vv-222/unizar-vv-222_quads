package es.unizar.eina.g222_quads;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import java.util.Calendar;

import es.unizar.eina.g222_quads.database.Reserva;
import es.unizar.eina.g222_quads.database.ReservaRepository;
import es.unizar.eina.g222_quads.ui.quads.G222_quads;

@RunWith(AndroidJUnit4.class)
public class ReservaNombreClienteTest {

    @Rule
    public ActivityScenarioRule<G222_quads> scenarioRule =
            new ActivityScenarioRule<>(G222_quads.class);

    private final long FECHA_1 = dateToMillis(2030, 5, 1);
    private final long FECHA_2 = dateToMillis(2030, 5, 2);

    @Before
    public void setup() {
        scenarioRule.getScenario().onActivity(activity -> {
            try {
                activity.getReservaRepositoryMain().deleteAll().get();
            } catch (Exception e) { throw new RuntimeException(e); }
        });
    }

    // ── PARTICIÓN VÁLIDA 1: nombre alfabético simple ──────────
    @Test
    public void testNombreAlfabeticoSimple() {
        insertar("Juan", true);
    }

    // ── PARTICIÓN VÁLIDA 2: nombre con tildes y espacios ──────
    @Test
    public void testNombreConTildesYEspacios() {
        insertar("María José García", true);
    }

    // ── PARTICIÓN VÁLIDA 3: nombre con guión ──────────────────
    @Test
    public void testNombreConGuion() {
        insertar("Ana-Belén", true);
    }

    // ── PARTICIÓN INVÁLIDA 1: nombre vacío ────────────────────
    @Test
    public void testNombreVacio() {
        insertar("", false);
    }

    // ── PARTICIÓN INVÁLIDA 2: nombre nulo ─────────────────────
    @Test
    public void testNombreNulo() {
        scenarioRule.getScenario().onActivity(activity -> {
            ReservaRepository repo = activity.getReservaRepositoryMain();
            Reserva r = new Reserva(null, "600000001", FECHA_1, false, FECHA_2, false);
            long resultado = repo.insert(r);
            assertEquals("Nombre nulo debe rechazarse", -1, resultado);
        });
    }

    // ── PARTICIÓN INVÁLIDA 3: nombre solo espacios ────────────
    @Test
    public void testNombreSoloEspacios() {
        insertar("     ", false);
    }

    // ── PARTICIÓN INVÁLIDA 4: nombre con caracteres especiales─
    @Test
    public void testNombreConCaracteresEspeciales() {
        insertar("Cliente@#$%", false);
    }

    // ── PARTICIÓN INVÁLIDA 5: nombre con números ──────────────
    @Test
    public void testNombreConNumeros() {
        insertar("Cliente123", false);
    }

    // ── Helper ────────────────────────────────────────────────
    private void insertar(String nombre, boolean debeAceptarse) {
        scenarioRule.getScenario().onActivity(activity -> {
            ReservaRepository repo = activity.getReservaRepositoryMain();
            Reserva r = new Reserva(nombre, "600000001", FECHA_1, false, FECHA_2, false);
            long resultado = repo.insert(r);
            if (debeAceptarse) {
                assertNotEquals("Nombre '" + nombre + "' debería aceptarse", -1, resultado);
            } else {
                assertEquals("Nombre '" + nombre + "' debería rechazarse", -1, resultado);
            }
        });
    }

    private long dateToMillis(int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(year, month - 1, day, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }
}
