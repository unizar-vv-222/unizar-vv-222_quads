package es.unizar.eina.g222_quads;

import static org.junit.Assert.assertEquals;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Before;

import java.util.Calendar;

import es.unizar.eina.g222_quads.database.Reserva;
import es.unizar.eina.g222_quads.database.ReservaRepository;
import es.unizar.eina.g222_quads.ui.quads.G222_quads;

@RunWith(AndroidJUnit4.class)
public class ReservaUpdateTest {

    @Rule
    public ActivityScenarioRule<G222_quads> scenarioRule = new ActivityScenarioRule<>(G222_quads.class);

    private long dateToMillis(int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(year, month - 1, day, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    private final long FECHA_INICIO = dateToMillis(2030, 1, 1);  // 1 ene 2030
    private final long FECHA_FIN = dateToMillis(2030, 1, 2);     // 2 ene 2030

    @Before
    public void setup() {
        scenarioRule.getScenario().onActivity(activity -> {
            try {
                activity.getReservaRepositoryMain().deleteAll().get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    public void testUpdateValidData() {
        scenarioRule.getScenario().onActivity(activity -> {
            ReservaRepository repo = activity.getReservaRepositoryMain();
            Reserva r = new Reserva("Original", "612458920", FECHA_INICIO, false, FECHA_FIN, false);

            long id = repo.insert(r);
            r.setId((int) id);

            // Modificamos el nombre
            r.setNombreCliente("Editado");
            int filasAfectadas = repo.update(r);

            assertEquals("Debería haber actualizado 1 fila", 1, filasAfectadas);

            // Verificamos que el cambio se ha guardado realmente
            Reserva recuperada = repo.getReservaByIdSync((int)id);
            assertEquals("Editado", recuperada.getNombreCliente());
        });
    }

    @Test
    public void testUpdateInvalidData() {
        scenarioRule.getScenario().onActivity(activity -> {
            ReservaRepository repo = activity.getReservaRepositoryMain();
            Reserva r = new Reserva("Valido", "612458920", FECHA_INICIO, false, FECHA_FIN, false);
            long id = repo.insert(r);
            r.setId((int) id);

            // Intentamos actualizar con un teléfono inválido (letras)
            r.setMovilCliente("ABC123");
            int resultado = repo.update(r);

            assertEquals("El repositorio debería rechazar la actualización (-1 o 0)", 0, resultado);

            // Verificamos que en la base de datos sigue el teléfono antiguo
            Reserva recuperada = repo.getReservaByIdSync((int)id);
            assertEquals("612458920", recuperada.getMovilCliente());
        });
    }
}