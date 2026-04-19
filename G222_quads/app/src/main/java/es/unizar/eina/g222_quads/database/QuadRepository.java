package es.unizar.eina.g222_quads.database;

import static es.unizar.eina.g222_quads.database.Quad_Reserva_RoomDataBase.databaseWriteExecutor;

import android.app.Application;
import androidx.lifecycle.LiveData;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import es.unizar.eina.g222_quads.utils.DateUtils;

public class QuadRepository {

    private final QuadDao mQuadDao;
    private final LiveData<List<Quad>> mAllQuads;

    public QuadRepository(Application application) {
        Quad_Reserva_RoomDataBase db = Quad_Reserva_RoomDataBase.getDatabase(application);
        mQuadDao = db.quadDao();
        mAllQuads = mQuadDao.getQuadsOrderByMatricula();
    }

    // --- MÉTODOS DE CONSULTA LIVE DATA (Para la UI) ---

    public LiveData<List<Quad>> getQuadsOrderByMatricula() { return mQuadDao.getQuadsOrderByMatricula(); }
    public LiveData<List<Quad>> getQuadsOrderByTipo() { return mQuadDao.getQuadsOrderByTipo(); }
    public LiveData<List<Quad>> getQuadsOrderByPrecio() { return mQuadDao.getQuadsOrderByPrecio(); }
    public LiveData<List<Quad>> getAllQuads() { return mAllQuads; }

    // --- LÓGICA DE VALIDACIÓN ---

    private static boolean esQuadValido(Quad q) {
        if (q.getMatricula() == null) return false;
        if (!q.getMatricula().matches("^[0-9]{4}[A-Z]{3}$")) return false;
        if (q.getPrecio() <= 0) return false;
        if (q.getDescripcion() == null || q.getDescripcion().isEmpty()) return false;
        return true;
    }

    // --- MÉTODOS DE ESCRITURA (CORREGIDOS CON FUTURE) ---

    /**
     * Inserta un nuevo quad. Devuelve Future para poder sincronizar en tests.
     */
    public Future<Long> insert(Quad quad) {
        if (esQuadValido(quad)) {
            // Usamos submit en lugar de execute para devolver el Future
            return databaseWriteExecutor.submit(() -> mQuadDao.insert(quad));
        } else {
            throw new IllegalArgumentException("Quad inválido");
        }
    }

    /**
     * Actualiza un quad existente.
     * Lanza NoSuchElementException si no existe (Sincrónicamente).
     */
    public Future<Integer> update(Quad quad) {
        if (!esQuadValido(quad)) {
            throw new IllegalArgumentException("Datos del Quad inválidos");
        }

        // Comprobación de existencia síncrona (usa tu método Sync)
        Quad existe = getQuadByMatriculaSync(quad.getMatricula());
        if (existe == null) {
            throw new java.util.NoSuchElementException("La matrícula " + quad.getMatricula() + " no existe.");
        }

        return databaseWriteExecutor.submit(() -> mQuadDao.update(quad));
    }

    /**
     * Elimina por matrícula. Devuelve Future.
     */
    public Future<Integer> deleteByMatricula(String matricula) {
        return databaseWriteExecutor.submit(() -> mQuadDao.deleteByMatricula(matricula));
    }

    /**
     * Elimina todo. Devuelve Future.
     */
    public Future<Integer> deleteAll() {
        return databaseWriteExecutor.submit(mQuadDao::deleteAll);
    }


    // --- MÉTODOS SÍNCRONOS (Útiles para Tests y Lógica interna) ---

    public Quad getQuadByMatriculaSync(String matricula) {
        try {
            return databaseWriteExecutor.submit(() -> mQuadDao.getQuadByMatricula(matricula)).get();
        } catch (Exception e) {
            return null;
        }
    }

    public int numQuads() {
        try {
            return databaseWriteExecutor.submit(mQuadDao::getNumQuad).get();
        } catch (ExecutionException | InterruptedException e) {
            return -1;
        }
    }

    // --- OTROS MÉTODOS ---

    public LiveData<List<Quad>> getAvailableQuads(long fechaInicio, boolean horaInicio, long fechaFin, boolean horaFin) {
        long recogidaComparable = DateUtils.slotToMillis(fechaInicio, horaInicio);
        long devolucionComparable = DateUtils.endExclusiveMillis(fechaFin, horaFin);
        return mQuadDao.getAvailableQuads(recogidaComparable, devolucionComparable);
    }

    public LiveData<List<Quad>> getAvailableQuadsExcludingReserva(long fechaInicio, boolean horaInicio, long fechaFin, boolean horaFin, int reservaId) {
        long recogidaComparable = DateUtils.slotToMillis(fechaInicio, horaInicio);
        long devolucionComparable = DateUtils.endExclusiveMillis(fechaFin, horaFin);
        return mQuadDao.getAvailableQuadsExcludingReserva(recogidaComparable, devolucionComparable, reservaId);
    }
}