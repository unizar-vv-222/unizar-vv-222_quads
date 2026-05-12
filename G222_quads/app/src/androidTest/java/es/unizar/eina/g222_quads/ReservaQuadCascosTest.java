package es.unizar.eina.g222_quads;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import es.unizar.eina.g222_quads.database.Quad;
import es.unizar.eina.g222_quads.database.QuadRepository;
import es.unizar.eina.g222_quads.database.Reserva;
import es.unizar.eina.g222_quads.database.ReservaQuadCascosRepository;
import es.unizar.eina.g222_quads.database.ReservaRepository;
import es.unizar.eina.g222_quads.ui.quads.G222_quads;

@RunWith(AndroidJUnit4.class)
public class ReservaQuadCascosTest {

    @Rule
    public ActivityScenarioRule<G222_quads> scenarioRule =
            new ActivityScenarioRule<>(G222_quads.class);

    // -----------------------------------------------------------------------
    // Setup
    // -----------------------------------------------------------------------
    @Before
    public void setup() {
        scenarioRule.getScenario().onActivity(activity -> {
            try {
                activity.getReservaQuadCascosRepositoryMain().deleteAll().get();
                activity.getReservaRepositoryMain().deleteAll().get();
                activity.getQuadRespositoryMain().deleteAll().get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    // -----------------------------------------------------------------------
    // LiveData helper
    // -----------------------------------------------------------------------
    private <T> T waitForLiveData(LiveData<T> liveData)
            throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        final Object[] data = new Object[1];
        androidx.lifecycle.Observer<T> observer =
                new androidx.lifecycle.Observer<T>() {
                    @Override
                    public void onChanged(T value) {
                        data[0] = value;
                        latch.countDown();
                        liveData.removeObserver(this);
                    }
                };
        new Handler(Looper.getMainLooper()).post(() ->
                liveData.observeForever(observer)
        );
        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new RuntimeException("Timeout esperando LiveData");
        }
        return (T) data[0];
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------
    private long inicioHoy() {
        long ahora = System.currentTimeMillis();
        return ahora - (ahora % (24 * 60 * 60 * 1000L));
    }
    private long insertarReserva(ReservaRepository repo,
                                 String nombre,
                                 String movil,
                                 long fechaIni,
                                 boolean horaIni,
                                 long fechaFin,
                                 boolean horaFin) throws Exception {
        Reserva r = new Reserva(
                nombre,
                movil,
                fechaIni,
                horaIni,
                fechaFin,
                horaFin
        );
        return repo.insert(r);
    }

    private boolean quadEstaDisponible(
            LiveData<List<Quad>> liveData,
            String matricula) throws InterruptedException {
        List<Quad> lista = waitForLiveData(liveData);
        return lista != null &&
                lista.stream()
                        .anyMatch(q ->
                                matricula.equals(q.getMatricula()));
    }

    // -----------------------------------------------------------------------
    // Validación cascos
    // -----------------------------------------------------------------------
    private boolean esCascoValido(
            boolean esMonoplaza,
            int numCascos) {
        if (numCascos < 0) {
            return false;
        }
        return esMonoplaza
                ? numCascos <= 1
                : numCascos <= 2;
    }

    // =======================================================================
    // FCÉ24 + RF12
    // =======================================================================

    @Test
    public void testCascos_Monoplaza_Cero_Valido() {
        assertTrue(esCascoValido(true, 0));
    }

    @Test
    public void testCascos_Monoplaza_Uno_Valido() {
        assertTrue(esCascoValido(true, 1));
    }

    @Test
    public void testCascos_Monoplaza_Dos_Invalido() {
        assertFalse(esCascoValido(true, 2));
    }

    @Test
    public void testCascos_Monoplaza_Negativo_Invalido() {
        assertFalse(esCascoValido(true, -1));
    }

    @Test
    public void testCascos_Biplaza_Cero_Valido() {
        assertTrue(esCascoValido(false, 0));
    }

    @Test
    public void testCascos_Biplaza_Uno_Valido() {
        assertTrue(esCascoValido(false, 1));
    }

    @Test
    public void testCascos_Biplaza_Dos_Valido() {
        assertTrue(esCascoValido(false, 2));
    }

    @Test
    public void testCascos_Biplaza_Tres_Invalido() {
        assertFalse(esCascoValido(false, 3));
    }

    // =======================================================================
    // RF12
    // =======================================================================

    @Test
    public void updateCascos_CascosValidos_Aceptado() {
        final boolean[] resultado = {false};
        scenarioRule.getScenario().onActivity(activity -> {
            try {
                QuadRepository quadRepo =
                        activity.getQuadRespositoryMain();
                ReservaRepository reservaRepo =
                        activity.getReservaRepositoryMain();
                ReservaQuadCascosRepository reservaQuadCascosRepo =
                        activity.getReservaQuadCascosRepositoryMain();
                quadRepo.insert(
                        new Quad(
                                "1111AAA",
                                true,
                                60.0,
                                "Monoplaza test"
                        )
                ).get();

                long hoy = inicioHoy();
                long id = insertarReserva(
                        reservaRepo,
                        "Ana García",
                        "612345678",
                        hoy + 2 * 86400000L,
                        true,
                        hoy + 3 * 86400000L,
                        false
                );

                Map<String, Integer> cascos = new HashMap<>();
                cascos.put("1111AAA", 1);
                resultado[0] =
                        reservaQuadCascosRepo.updateCascos((int) id, cascos);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        assertTrue(resultado[0]);
    }

    @Test
    public void cascosInvalidos_Rechazados_AntesDeUpdateCascos() {

        final boolean[] valido = {true};
        scenarioRule.getScenario().onActivity(activity -> {
            try {
                QuadRepository quadRepo =
                        activity.getQuadRespositoryMain();
                quadRepo.insert(
                        new Quad(
                                "1111AAA",
                                true,
                                60.0,
                                "Monoplaza test"
                        )
                ).get();
                Map<String, Integer> cascos = new HashMap<>();
                cascos.put("1111AAA", 2);
                valido[0] =
                        cascos.entrySet().stream().allMatch(e -> {
                            Quad quad =
                                    quadRepo.getQuadByMatriculaSync(
                                            e.getKey()
                                    );
                            return quad != null &&
                                    esCascoValido(
                                            quad.getTipo(),
                                            e.getValue()
                                    );
                        });
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        assertFalse(valido[0]);
    }

    // =======================================================================
    // RF11
    // =======================================================================

    @Test
    public void quadOcupado_NoDisponibleEnFechasSolapadas()
            throws Exception {
        final LiveData<List<Quad>>[] liveData = new LiveData[1];
        scenarioRule.getScenario().onActivity(activity -> {
            try {
                QuadRepository quadRepo =
                        activity.getQuadRespositoryMain();
                ReservaRepository reservaRepo =
                        activity.getReservaRepositoryMain();
                ReservaQuadCascosRepository reservaQuadCascosRepo =
                        activity.getReservaQuadCascosRepositoryMain();
                long hoy = inicioHoy();
                quadRepo.insert(
                        new Quad(
                                "2222BBB",
                                false,
                                80.0,
                                "Biplaza test"
                        )
                ).get();

                long id1 = insertarReserva(
                        reservaRepo,
                        "Cliente 1",
                        "698765432",
                        hoy + 2 * 86400000L,
                        true,
                        hoy + 3 * 86400000L,
                        false
                );

                Map<String, Integer> cascos = new HashMap<>();
                cascos.put("2222BBB", 2);
                reservaQuadCascosRepo.updateCascos((int) id1, cascos);
                long id2 = insertarReserva(
                        reservaRepo,
                        "Cliente 2",
                        "611111111",
                        hoy + 2 * 86400000L,
                        true,
                        hoy + 3 * 86400000L,
                        false
                );
                liveData[0] =
                        quadRepo.getAvailableQuadsExcludingReserva(
                                hoy + 2 * 86400000L,
                                true,
                                hoy + 3 * 86400000L,
                                false,
                                (int) id2
                        );

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        boolean disp = quadEstaDisponible(
                liveData[0],
                "2222BBB"
        );

        assertFalse(disp);
    }

    @Test
    public void quadOcupado_DisponibleEnFechasNoSolapadas()
            throws Exception {

        final LiveData<List<Quad>>[] liveData = new LiveData[1];

        scenarioRule.getScenario().onActivity(activity -> {

            try {
                QuadRepository quadRepo =
                        activity.getQuadRespositoryMain();
                ReservaRepository reservaRepo =
                        activity.getReservaRepositoryMain();
                ReservaQuadCascosRepository reservaQuadCascosRepo =
                        activity.getReservaQuadCascosRepositoryMain();
                long hoy = inicioHoy();
                quadRepo.insert(
                        new Quad(
                                "3333CCC",
                                true,
                                55.0,
                                "Monoplaza libre"
                        )
                ).get();
                long id1 = insertarReserva(
                        reservaRepo,
                        "Cliente 3",
                        "677777777",
                        hoy + 2 * 86400000L,
                        true,
                        hoy + 3 * 86400000L,
                        false
                );

                Map<String, Integer> cascos = new HashMap<>();
                cascos.put("3333CCC", 0);
                reservaQuadCascosRepo.updateCascos((int) id1, cascos);
                long id2 = insertarReserva(
                        reservaRepo,
                        "Cliente 4",
                        "633333333",
                        hoy + 5 * 86400000L,
                        true,
                        hoy + 6 * 86400000L,
                        false
                );

                liveData[0] =
                        quadRepo.getAvailableQuadsExcludingReserva(
                                hoy + 5 * 86400000L,
                                true,
                                hoy + 6 * 86400000L,
                                false,
                                (int) id2
                        );

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        boolean disp = quadEstaDisponible(
                liveData[0],
                "3333CCC"
        );

        assertTrue(disp);
    }

    @Test
    public void reservasConsecutivas_NoSonSolape()
            throws Exception {
        final LiveData<List<Quad>>[] liveData = new LiveData[1];
        scenarioRule.getScenario().onActivity(activity -> {
            try {
                QuadRepository quadRepo =
                        activity.getQuadRespositoryMain();
                ReservaRepository reservaRepo =
                        activity.getReservaRepositoryMain();
                ReservaQuadCascosRepository reservaQuadCascosRepo =
                        activity.getReservaQuadCascosRepositoryMain();
                long hoy = inicioHoy();
                quadRepo.insert(
                        new Quad(
                                "4444DDD",
                                false,
                                70.0,
                                "Biplaza consecutiva"
                        )
                ).get();
                long id1 = insertarReserva(
                        reservaRepo,
                        "Cliente 5",
                        "644444444",
                        hoy + 2 * 86400000L,
                        true,
                        hoy + 2 * 86400000L,
                        false
                );

                Map<String, Integer> cascos = new HashMap<>();
                cascos.put("4444DDD", 1);
                reservaQuadCascosRepo.updateCascos((int) id1, cascos);
                long id2 = insertarReserva(
                        reservaRepo,
                        "Cliente 6",
                        "655555555",
                        hoy + 2 * 86400000L,
                        false,
                        hoy + 3 * 86400000L,
                        false
                );

                liveData[0] =
                        quadRepo.getAvailableQuadsExcludingReserva(
                                hoy + 2 * 86400000L,
                                false,
                                hoy + 3 * 86400000L,
                                false,
                                (int) id2
                        );

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        boolean disp = quadEstaDisponible(
                liveData[0],
                "4444DDD"
        );

        assertTrue(disp);
    }

    @Test
    public void edicionPropiaReserva_QuadSigueDisponible()
            throws Exception {
        final LiveData<List<Quad>>[] liveData = new LiveData[1];
        scenarioRule.getScenario().onActivity(activity -> {
            try {
                QuadRepository quadRepo =
                        activity.getQuadRespositoryMain();
                ReservaRepository reservaRepo =
                        activity.getReservaRepositoryMain();
                ReservaQuadCascosRepository reservaQuadCascosRepo =
                        activity.getReservaQuadCascosRepositoryMain();
                long hoy = inicioHoy();
                quadRepo.insert(
                        new Quad(
                                "5555EEE",
                                true,
                                65.0,
                                "Monoplaza edicion"
                        )
                ).get();

                long id1 = insertarReserva(
                        reservaRepo,
                        "Cliente 7",
                        "666666666",
                        hoy + 2 * 86400000L,
                        true,
                        hoy + 4 * 86400000L,
                        false
                );

                Map<String, Integer> cascos = new HashMap<>();
                cascos.put("5555EEE", 1);
                reservaQuadCascosRepo.updateCascos((int) id1, cascos);
                liveData[0] =
                        quadRepo.getAvailableQuadsExcludingReserva(
                                hoy + 2 * 86400000L,
                                true,
                                hoy + 4 * 86400000L,
                                false,
                                (int) id1
                        );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        boolean disp = quadEstaDisponible(
                liveData[0],
                "5555EEE"
        );
        assertTrue(disp);
    }
}