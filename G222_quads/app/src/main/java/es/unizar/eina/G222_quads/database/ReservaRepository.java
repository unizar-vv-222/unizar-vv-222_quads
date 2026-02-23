package es.unizar.eina.G222_quads.database;


import static es.unizar.eina.G222_quads.database.Quad_Reserva_RoomDataBase.databaseWriteExecutor;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import es.unizar.eina.G222_quads.utils.IdCallback;


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

    /**
     * Constructor de ReservaRepository utilizando el contexto de la aplicación para instanciar la base de datos.
     */
    public ReservaRepository(Application application) {
        Quad_Reserva_RoomDataBase db = Quad_Reserva_RoomDataBase.getDatabase(application);
        mReservaDao = db.reservaDao();
        mAllReservas = mReservaDao.getReservasOrderByNombre();
        mReservaQuadCascosDao = db.reservaQuadCascosDao();
    }

    /**
     * Comprueba si un rango de fechas y horarios es válido.
     * @param fechaIniLong, horaIni, fechaFinLong, horaFin
     * @return true si la fecha es válida, false en caso contrario
     */
    private static boolean fechasValidas(long fechaIniLong, boolean horaIni,
                                         long fechaFinLong, boolean horaFin) {

        String fecha_ini = String.valueOf(fechaIniLong);
        if (!fecha_ini.matches("\\d{8}")) {
            return false;
        }

        String fecha_fin = String.valueOf(fechaFinLong);
        if (!fecha_fin.matches("\\d{8}")) {
            return false;
        }

        int dia_ini = Integer.parseInt(fecha_ini.substring(0, 2));
        int mes_ini = Integer.parseInt(fecha_ini.substring(2, 4));
        int anyo_ini = Integer.parseInt(fecha_ini.substring(4, 8));

        int dia_fin = Integer.parseInt(fecha_fin.substring(0, 2));
        int mes_fin = Integer.parseInt(fecha_fin.substring(2, 4));
        int anyo_fin = Integer.parseInt(fecha_fin.substring(4, 8));

        if (mes_ini < 1 || mes_ini > 12) return false;
        int[] diasMes = {31,28,31,30,31,30,31,31,30,31,30,31};
        boolean esBisiesto = (anyo_ini % 4 == 0 && anyo_ini % 100 != 0) || (anyo_ini % 400 == 0);
        if (esBisiesto && mes_ini == 2) diasMes[1] = 29;
        if (dia_ini < 1 || dia_ini > diasMes[mes_ini - 1]) return false;

        if (mes_fin < 1 || mes_fin > 12) return false;
        esBisiesto = (anyo_fin % 4 == 0 && anyo_fin % 100 != 0) || (anyo_fin % 400 == 0);
        if (esBisiesto && mes_fin == 2) diasMes[1] = 29;
        if (dia_fin < 1 || dia_fin > diasMes[mes_fin - 1]) return false;

        // Ambas fechas son válidas individuamente
        // Comprobación final -> fechaRecogida,horaRecogida <= fechaDevolución,horaDevolución

        if (anyo_ini > anyo_fin) {
            return false;
        } else if (anyo_ini < anyo_fin ) {
            return true;
        } else { // anyo_ini == anyo_fin
            if (mes_ini > mes_fin) {
                return false;
            } else if (mes_ini < mes_fin) {
                return true;
            } else { // mes_ini == mes_fin
                if (dia_ini > dia_fin) {
                    return false;
                } else if (dia_ini < dia_fin) {
                    return true;
                } else { // dia_ini == dia_fin
                    // true si se recoge por la mañana y se devuelve por la tarde
                    // en cualquier otro caso, false
                    return (!horaIni && horaFin);
                }
            }
        }

    }

    /**
     * Comprueba si una reserva es válida
     * @param r Reserva
     * @return true si la reserva es válida, false en caso contrario
     */
    private static boolean reservaValida(Reserva r) {
        if (r.getNombreCliente() == null || r.getNombreCliente().trim().isEmpty()) {
            return false;
        }
        if (r.getMovilCliente() == null || r.getMovilCliente().trim().isEmpty()) {
            return false;
        }
        if (!r.getMovilCliente().matches("\\d+")) {
            return false;
        }
        long fr = r.getFechaRecogida();
        boolean hr = r.getHoraRecogida();
        long fd = r.getFechaDevolucion();
        boolean hd = r.getHoraDevolucion();

        return fechasValidas(fr, hr, fd, hd);
    }

    /** Inserta una reserva nueva en la base de datos
     * @param reserva La reserva consta de: un nombre de cliente (reserva.getNombreCliente()) no nulo
     *                (reserva.getNombreCliente()!=null) y no vacío (reserva.getNombreCliente().length()>0);
     *                un móvil del cliente (reserva.getMovilCliente()) no nulo; una fecha de recogida
     *                (reserva.getFechaRecogida()) no nula; un horario de recogida
     *                (reserva.getHoraRecogida()) no nula; una fecha de devolución
     *                (reserva.getFechaDevolucion()) no nula; un horario de recogida
     *                (reserva.getHoraRecogida()) no nula; y un precio de la reserva (reserva.getPrecio())
     *                no nulo y no vacío.
     * @return Si la reserva se ha insertado correctamente, devuelve el id de la reserva que se ha creado. En caso
     *         contrario, devuelve -1 para indicar el fallo.
     */
    public long insert(Reserva reserva) {
        /* Para que la App funcione correctamente y no lance una excepción, la modificación de la
         * base de datos se debe lanzar en un hilo de ejecución separado
         * (databaseWriteExecutor.submit). Para poder sincronizar la recuperación del resultado
         * devuelto por la base de datos, se puede utilizar un Future.
         */
        if (reservaValida(reserva)) {
            Future<Long> future = databaseWriteExecutor.submit(
                    () -> mReservaDao.insert(reserva));
            try {
                return future.get(TIMEOUT, TimeUnit.MILLISECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException ex) {
                Log.d("ReservaRepository", ex.getClass().getSimpleName() + ex.getMessage());
                return -1;
            }
        }
        return 0;
    }

    /**
     * Inserta una reserva en la base de datos y devuelve su id a través de un callback.
     * @param reserva Reserva que se desea insertar
     * @param callback Callback que se ejecuta cuando se ha insertado la reserva
     */
    public void insertAndReturnIdAsync(Reserva reserva, IdCallback callback) {
        databaseWriteExecutor.execute(() -> {
            long id = mReservaDao.insert(reserva);
            callback.onInserted(id);
        });
    }

    /** Actualiza una reserva en la base de datos
     * @param reserva La reserva consta de: un nombre de cliente (reserva.getNombreCliente()) no nulo
     *                (reserva.getNombreCliente()!=null) y no vacío (reserva.getNombreCliente().length()>0);
     *                un móvil del cliente (reserva.getMovilCliente()) no nulo; una fecha de recogida
     *                (reserva.getFechaRecogida()) no nula; un horario de recogida
     *                (reserva.getHoraRecogida()) no nula; una fecha de devolución
     *                (reserva.getFechaDevolucion()) no nula; un horario de recogida
     *                (reserva.getHoraRecogida()) no nula; y un precio de la reserva (reserva.getPrecio())
     *                no nulo y no vacío.
     * @return Un valor entero con el número de filas modificadas: 1 si el id se corresponde con una reserva
     *         previamente insertada; 0 si no existe previamente una reserva con ese id, o hay algún problema
     *         con los atributos.
     */
    public int update(Reserva reserva) {
        if (reservaValida(reserva)) {
            Future<Integer> future = databaseWriteExecutor.submit(
                    () -> mReservaDao.update(reserva));
            try {
                return future.get(TIMEOUT, TimeUnit.MILLISECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException ex) {
                Log.d("ReservaRepository", ex.getClass().getSimpleName() + ex.getMessage());
                return -1;
            }
        }
        return 0;
    }


    /** Elimina una reserva en la base de datos.
     * @param reserva Objeto reserva cuyo atributo id (reserva.getId()) contiene la clave primaria de la reserva que se
     *             va a eliminar de la base de datos. Se debe cumplir: reserva.getId() > 0.
     * @return Un valor entero con el número de filas eliminadas: 1 si el id se corresponde con una reserva
     *         previamente insertada; 0 si no existe previamente una reserva con ese id o el id no es
     *         un valor aceptable.
     */
    public int delete(Reserva reserva) {
        Future<Integer> future = databaseWriteExecutor.submit(
                () -> mReservaDao.delete(reserva));
        try {
            return future.get(TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException ex) {
            Log.d("ReservaRepository", ex.getClass().getSimpleName() + ex.getMessage());
            return -1;
        }
    }

    /** Devuelve un objeto de tipo LiveData con todas las reservas.
     * Room ejecuta todas las consultas en un hilo separado.
     * El objeto LiveData notifica a los observadores cuando los datos cambian.
     */
    public LiveData<List<Reserva>> getAllReservas() {
        return mAllReservas;
    }

    /**
     * Elimina todas las reservas de la base de datos.
     */
    public void deleteAll(){
        databaseWriteExecutor.execute(()-> mReservaDao.deleteAll());
    }

    /**
     * Devuelve un objeto de tipo LiveData con todas las reservas
     * ordenadas por nombre del cliente.
     */
    public LiveData<List<Reserva>> getReservasOrderByNombre() {
        return mReservaDao.getReservasOrderByNombre();
    }

    /**
     * Devuelve un objeto de tipo LiveData con todas las reservas
     * ordenadas por telefono del cliente.
     */
    public LiveData<List<Reserva>> getReservasOrderByTelefono() {
        return mReservaDao.getReservasOrderByTelefono();
    }

    /**
     * Devuelve un objeto de tipo LiveData con todas las reservas
     * ordenadas por fecha y horario de recogida.
     * */
    public LiveData<List<Reserva>> getReservasOrderByRecogida() {
        return mReservaDao.getReservasOrderByRecogida();
    }

    /**
     * Devuelve un objeto de tipo LiveData con todas las reservas
     * ordenadas por fecha y horario de devolución.
     */
    public LiveData<List<Reserva>> getReservasOrderByDevolucion() {
        return mReservaDao.getReservasOrderByDevolucion();
    }

    /**
     * Recalcula el precio de una reserva
     * @param reservaId id de la reserva
     * @param fechaInicio fecha de inicio de la reserva
     * @param fechaFin fecha de fin de la reserva
     */
    public void recalcularPrecioReserva(int reservaId, long fechaInicio, long fechaFin) {

        databaseWriteExecutor.execute(() -> {

            long dias = TimeUnit.MILLISECONDS.toDays(fechaFin - fechaInicio) + 1;
            double precioDiario = mReservaQuadCascosDao.getPrecioDiarioReserva(reservaId);
            double total = dias * precioDiario;

            mReservaDao.updatePrecio(reservaId, total);
        });
    }


}
