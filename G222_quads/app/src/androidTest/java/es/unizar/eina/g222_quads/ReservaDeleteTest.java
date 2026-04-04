package es.unizar.eina.g222_quads;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import es.unizar.eina.g222_quads.database.Reserva;
import es.unizar.eina.g222_quads.database.ReservaRepository;
import es.unizar.eina.g222_quads.ui.quads.G222_quads;

@RunWith(AndroidJUnit4.class)
public class ReservaDeleteTest {

    @Rule
    public ActivityScenarioRule<G222_quads> scenarioRule = new ActivityScenarioRule<>(G222_quads.class);

    @Test
    public void testDeleteReservaExistente() {
        scenarioRule.getScenario().onActivity(activity -> {
            ReservaRepository repo = activity.getReservaRepositoryMain();
            Reserva r = new Reserva("A BORRAR", "612458920", 1735689600000L, true, 1735776000000L, false);

            long resultado = repo.insert(r);

            // Asignar ID que ha devuelto la base de datos para vincular al objeto y que el borrados e haga sin problema
            r.setId((int) resultado);
            assertNotEquals("Debería devolver un ID válido (>0)", -1, resultado);

            int filasBorradas = repo.delete(r);
            assertEquals("Debería borrar exactamente 1 fila", 1, filasBorradas);
        });
    }

    @Test
    public void testDeleteReservaInexistente() {
        scenarioRule.getScenario().onActivity(activity -> {
            ReservaRepository repo = activity.getReservaRepositoryMain();
            Reserva r = new Reserva("No Existe", "612458920", 1735689600000L, false, 1735776000000L, false);

            int filasBorradas = repo.delete(r);
            assertEquals("Debería devolver 0 filas borradas", 0, filasBorradas);
        });
    }
}