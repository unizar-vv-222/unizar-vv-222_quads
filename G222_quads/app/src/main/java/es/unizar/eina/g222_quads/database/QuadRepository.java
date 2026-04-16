package es.unizar.eina.g222_quads.database;

import static es.unizar.eina.g222_quads.database.Quad_Reserva_RoomDataBase.databaseWriteExecutor;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import es.unizar.eina.g222_quads.utils.DateUtils;

/**
 * Repositorio que gestiona el acceso a los datos de Quad.
 * Actúa como única puerta de entrada a Room desde la UI/ViewModel.
 */
public class QuadRepository {

    private final QuadDao mQuadDao;
    private final LiveData<List<Quad>> mAllQuads;

    /**
     * Constructor del repositorio.
     * Inicializa la base de datos y el DAO.
     */
    public QuadRepository(Application application) {
        Quad_Reserva_RoomDataBase db =
                Quad_Reserva_RoomDataBase.getDatabase(application);
        mQuadDao = db.quadDao();
        mAllQuads = mQuadDao.getQuadsOrderByMatricula();
    }

    public LiveData<List<Quad>> getQuadsOrderByMatricula() {
        return mQuadDao.getQuadsOrderByMatricula();
    }

    public LiveData<List<Quad>> getQuadsOrderByTipo() {
        return mQuadDao.getQuadsOrderByTipo();
    }

    public LiveData<List<Quad>> getQuadsOrderByPrecio() {
        return mQuadDao.getQuadsOrderByPrecio();
    }

    /**
     * Devuelve la lista observable de todos los quads.
     * Room se encarga de ejecutar la consulta en background.
     */
    public LiveData<List<Quad>> getAllQuads() {
        return mAllQuads;
    }

    private static boolean esQuadValido(Quad q) {
        if (q.getMatricula() == null) return false;
        if (!q.getMatricula().matches("^[0-9]{4}[A-Z]{3}$")) {
            return false;
        }
        if (q.getTipo() == null) return false;
        if (q.getPrecio() == null || q.getPrecio() <= 0) return false;
        if (q.getDescripcion() == null || q.getDescripcion().isEmpty()) return false;

        return true;
    }

    /**
     * Inserta un nuevo quad en la base de datos.
     */
    public void insert(Quad quad) {
        if (esQuadValido(quad)) {
            databaseWriteExecutor.execute(() ->
                    mQuadDao.insert(quad)
            );
        } else {
            throw new IllegalArgumentException("Quad inválido");
        }
    }

    /**
     * Actualiza un quad existente.
     */
    public void update(Quad quad) {
        if (esQuadValido(quad)) {
            databaseWriteExecutor.execute(() ->
                    mQuadDao.update(quad)
            );
        } else {
            throw new IllegalArgumentException("Quad inválido");
        }
    }

    /**
     * Elimina un quad por su matrícula (clave primaria).
     */
    public void deleteByMatricula(String matricula) {
        databaseWriteExecutor.execute(() ->
                mQuadDao.deleteByMatricula(matricula)
        );
    }

    /**
     * Elimina todos los quads de la base de datos.
     */
    public void deleteAll() {
        databaseWriteExecutor.execute(() ->
                mQuadDao.deleteAll()
        );
    }

    public LiveData<List<Quad>> getAvailableQuads(long fechaInicio, boolean horaInicio,
                                                  long fechaFin, boolean horaFin) {

        long recogidaComparable = DateUtils.obtenerInicioHorario(fechaInicio, horaInicio);
        long devolucionComparable = DateUtils.obtenerFinHorario(fechaFin, horaFin);

        return mQuadDao.getAvailableQuads(recogidaComparable, devolucionComparable);
    }

    public LiveData<List<Quad>> getAvailableQuadsExcludingReserva(long fechaInicio, boolean horaInicio,
                                                                  long fechaFin, boolean horaFin,
                                                                  int reservaId) {

        long recogidaComparable = DateUtils.obtenerInicioHorario(fechaInicio, horaInicio);
        long devolucionComparable = DateUtils.obtenerFinHorario(fechaFin, horaFin);

        return mQuadDao.getAvailableQuadsExcludingReserva(
                recogidaComparable,
                devolucionComparable,
                reservaId
        );
    }

    public Quad getQuadByMatriculaSync(String matricula) {
        try {
            return databaseWriteExecutor.submit(
                    () -> mQuadDao.getQuadByMatricula(matricula)
            ).get();
        } catch (Exception e) {
            return null;
        }
    }


}
