package es.unizar.eina.g222_quads.database;

import static es.unizar.eina.g222_quads.database.Quad_Reserva_RoomDataBase.databaseWriteExecutor;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * Repositorio para gestionar la relación Reserva–Quad.
 * Encapsula el acceso a ReservaQuadDao.
 */
public class ReservaQuadCascosRepository {

    private final ReservaQuadCascosDao mReservaQuadCascosDao;
    private final QuadDao mQuadDao;

    public ReservaQuadCascosRepository(Application application) {
        Quad_Reserva_RoomDataBase db =
                Quad_Reserva_RoomDataBase.getDatabase(application);
        mReservaQuadCascosDao = db.reservaQuadCascosDao();
        mQuadDao = db.quadDao();
    }

    public LiveData<Map<String, Integer>> getByReserva(int reservaId) {
        return Transformations.map(
                mReservaQuadCascosDao.getByReservaLive(reservaId),
                lista -> {
                    Map<String, Integer> map = new HashMap<>();
                    for (ReservaQuadCascos rqc : lista) {
                        map.put(rqc.getMatriculaQuad(), rqc.getNumCascos());
                    }
                    return map;
                }
        );
    }

    /**
     * Actualiza los cascos de los quads de una reserva.
     * Usa submit().get() internamente para bloquear hasta que la BD termine,
     * garantizando que el resultado es correcto cuando se devuelve.
     *
     * @param reservaId id de la reserva
     * @param nuevos    nuevos cascos por quad
     * @return true si se han realizado cambios, false en caso contrario
     */
    public boolean updateCascos(int reservaId, Map<String, Integer> nuevos) {
        try {
            return databaseWriteExecutor.submit(() -> {

                boolean cambios = false;

                List<ReservaQuadCascos> actualesList =
                        mReservaQuadCascosDao.getByReservaSync(reservaId);

                Map<String, ReservaQuadCascos> actuales = new HashMap<>();
                for (ReservaQuadCascos rqc : actualesList) {
                    actuales.put(rqc.getMatriculaQuad(), rqc);
                }

                for (Map.Entry<String, Integer> entry : nuevos.entrySet()) {
                    String matricula = entry.getKey();
                    int nuevosCascos = entry.getValue();

                    if (!actuales.containsKey(matricula)) {
                        mReservaQuadCascosDao.insert(
                                new ReservaQuadCascos(reservaId, matricula,
                                        nuevosCascos, getPrecioActual(matricula)));
                        cambios = true;
                    } else {
                        ReservaQuadCascos actual = actuales.get(matricula);
                        if (actual.getNumCascos() != nuevosCascos) {
                            mReservaQuadCascosDao.updateNumCascos(reservaId, matricula, nuevosCascos);
                        }
                    }
                }

                for (String matricula : actuales.keySet()) {
                    if (!nuevos.containsKey(matricula)) {
                        mReservaQuadCascosDao.delete(reservaId, matricula);
                        cambios = true;
                    }
                }

                return cambios;

            }).get(); // bloquea hasta que la operación de BD termina

        } catch (Exception e) {
            return false;
        }
    }

    private double getPrecioActual(String matricula) {
        Quad q = mQuadDao.getQuadByMatricula(matricula);
        return q == null ? -1 : q.getPrecio();
    }

    public Map<String, Double> getPreciosParaReserva(int reservaId, Map<String, Integer> seleccion) {
        Map<String, Double> precios = new HashMap<>();
        List<ReservaQuadCascos> existentes = mReservaQuadCascosDao.getByReservaSync(reservaId);

        Map<String, ReservaQuadCascos> congelados = new HashMap<>();
        for (ReservaQuadCascos rqc : existentes) {
            congelados.put(rqc.getMatriculaQuad(), rqc);
        }

        for (String matricula : seleccion.keySet()) {
            if (congelados.containsKey(matricula)) {
                precios.put(matricula, congelados.get(matricula).getPrecioOriginal());
            } else {
                precios.put(matricula, mQuadDao.getQuadByMatricula(matricula).getPrecio());
            }
        }
        return precios;
    }

    public void getPreciosParaReservaAsync(int reservaId,
                                           Map<String, Integer> seleccion,
                                           java.util.function.Consumer<Map<String, Double>> cb) {
        databaseWriteExecutor.execute(() -> {
            Map<String, Double> precios = getPreciosParaReserva(reservaId, seleccion);
            new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> cb.accept(precios));
        });
    }

    public Future<Integer> deleteAll() {
        return databaseWriteExecutor.submit(mReservaQuadCascosDao::deleteAll);
    }

}