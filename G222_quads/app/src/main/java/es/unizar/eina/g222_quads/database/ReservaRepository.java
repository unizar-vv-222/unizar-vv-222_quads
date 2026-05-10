package es.unizar.eina.g222_quads.database;

import static es.unizar.eina.g222_quads.database.Quad_Reserva_RoomDataBase.databaseWriteExecutor;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import es.unizar.eina.g222_quads.utils.DateUtils;
import es.unizar.eina.g222_quads.utils.IdCallback;

/**
 * Clase que gestiona el acceso la fuente de datos.
 * Interacciona con la base de datos a través de las clases ReservaRoomDatabase y ReservaDao.
 *
 * @author G222
 */
public class ReservaRepository {

    private final ReservaDao mReservaDao;
    private final LiveData<List<Reserva>> mAllReservas;
    private final ReservaQuadCascosDao mReservaQuadCascosDao;

    private final long TIMEOUT = 15000;

    public ReservaRepository(Application application) {
        Quad_Reserva_RoomDataBase db = Quad_Reserva_RoomDataBase.getDatabase(application);
        mReservaDao = db.reservaDao();
        mAllReservas = mReservaDao.getReservasOrderByNombre();
        mReservaQuadCascosDao = db.reservaQuadCascosDao();
    }

    private static boolean fechasValidas(long fechaIni, boolean horaIni,
                                         long fechaFin, boolean horaFin) {
        if (fechaIni < 0 || fechaFin < 0) return false;
        long hoy = DateUtils.inicioDelDia(System.currentTimeMillis());
        if (fechaIni < hoy) return false;
        return DateUtils.rangoValido(fechaIni, horaIni, fechaFin, horaFin);
    }

    private static boolean reservaValida(Reserva r) {
        if (r == null) return false;
        if (r.getNombreCliente() == null || r.getNombreCliente().trim().isEmpty()) return false;
        if (r.getMovilCliente() == null || r.getMovilCliente().trim().isEmpty()) return false;
        if (!r.getMovilCliente().matches("\\d+")) return false;
        return fechasValidas(r.getFechaRecogida(), r.getHoraRecogida(),
                r.getFechaDevolucion(), r.getHoraDevolucion());
    }

    public long insert(Reserva reserva) {
        if (reservaValida(reserva)) {
            reserva.actualizarComparables();
            Future<Long> future = databaseWriteExecutor.submit(() -> mReservaDao.insert(reserva));
            try {
                return future.get(TIMEOUT, TimeUnit.MILLISECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException ex) {
                Log.d("ReservaRepository", ex.getClass().getSimpleName() + ex.getMessage());
                return -1;
            }
        }
        return -1;
    }

    public void insertAndReturnIdAsync(Reserva reserva, IdCallback callback) {
        if (reservaValida(reserva)) {
            reserva.actualizarComparables();
            databaseWriteExecutor.execute(() -> {
                long id = mReservaDao.insert(reserva);
                callback.onInserted(id);
            });
        }
    }

    public int update(Reserva reserva) {
        if (reservaValida(reserva)) {
            reserva.actualizarComparables();
            Future<Integer> future = databaseWriteExecutor.submit(() -> mReservaDao.update(reserva));
            try {
                return future.get(TIMEOUT, TimeUnit.MILLISECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException ex) {
                Log.d("ReservaRepository", ex.getClass().getSimpleName() + ex.getMessage());
                return -1;
            }
        }
        return 0;
    }

    public int delete(Reserva reserva) {
        if (reserva == null || reserva.getId() <= 0) return 0;
        Future<Integer> future = databaseWriteExecutor.submit(() -> mReservaDao.delete(reserva));
        try {
            return future.get(TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException ex) {
            Log.d("ReservaRepository", ex.getClass().getSimpleName() + ex.getMessage());
            return -1;
        }
    }

    public Reserva getReservaByIdSync(int id) {
        try {
            return databaseWriteExecutor.submit(() -> mReservaDao.getReserva(id)).get();
        } catch (Exception e) {
            return null;
        }
    }

    public LiveData<List<Reserva>> getAllReservas() { return mAllReservas; }

    public Future<Integer> deleteAll() {
        return databaseWriteExecutor.submit(mReservaDao::deleteAll);
    }

    public LiveData<List<Reserva>> getReservasOrderByNombre() {
        return mReservaDao.getReservasOrderByNombre();
    }

    public LiveData<List<Reserva>> getReservasOrderByTelefono() {
        return mReservaDao.getReservasOrderByTelefono();
    }

    public LiveData<List<Reserva>> getReservasOrderByRecogida() {
        return mReservaDao.getReservasOrderByRecogida();
    }

    public LiveData<List<Reserva>> getReservasOrderByDevolucion() {
        return mReservaDao.getReservasOrderByDevolucion();
    }

    /**
     * Recalcula el precio de una reserva y lo persiste en la BD.
     * Bloquea internamente con submit().get() para garantizar que la escritura
     * ha terminado antes de volver, sin exponer Future en la firma.
     *
     * @param reservaId   id de la reserva
     * @param fechaInicio fecha de inicio de la reserva
     * @param horaInicio  horario de inicio de la reserva
     * @param fechaFin    fecha de fin de la reserva
     * @param horaFin     horario de fin de la reserva
     */
    public void recalcularPrecioReserva(int reservaId, long fechaInicio, boolean horaInicio,
                                        long fechaFin, boolean horaFin) {
        try {
            databaseWriteExecutor.submit(() -> {
                double dias = DateUtils.calcularDiasReserva(fechaInicio, horaInicio, fechaFin, horaFin);
                double precioDiario = mReservaQuadCascosDao.getPrecioDiarioReserva(reservaId);
                double total = dias * precioDiario;
                mReservaDao.updatePrecio(reservaId, total);
            }).get(); // bloquea hasta que la escritura termina
        } catch (Exception e) {
            Log.d("ReservaRepository", "recalcularPrecioReserva: " + e.getMessage());
        }
    }

}