package es.unizar.eina.G222_quads.ui.reservas;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import es.unizar.eina.G222_quads.database.Reserva;
import es.unizar.eina.G222_quads.database.ReservaRepository;

/**
 * ViewModel que expone la lista de quads y las operaciones CRUD al interfaz de usuario.
 * Actúa como intermediario entre la UI y el repositorio.
 */
public class ReservaViewModel extends AndroidViewModel {

    private ReservaRepository mRepository;

    private final LiveData<List<Reserva>> mAllReservas;

    /**
     * Crea el ViewModel e inicializa el repositorio y la LiveData con todos los quads.
     * @param application contexto de aplicación necesario para inicializar la base de datos
     */
    public ReservaViewModel(Application application) {
        super(application);
        mRepository = new ReservaRepository(application);
        mAllReservas = mRepository.getAllReservas();
    }

    /* =========================
       QUERIES
       ========================= */

    /**
     * Devuelve la LiveData con todos los quads almacenados.
     * @return lista observable de quads
     */
    LiveData<List<Reserva>> getAllReservas() { return mAllReservas; }

    public LiveData<List<Reserva>> getReservasOrderByNombre() {
        return mRepository.getReservasOrderByNombre();
    }

    public LiveData<List<Reserva>> getReservasOrderByTelefono() {
        return mRepository.getReservasOrderByTelefono();
    }

    public LiveData<List<Reserva>> getReservasOrderByFechaRecogida() {
        return mRepository.getReservasOrderByFechaRecogida();
    }

    public LiveData<List<Reserva>> getReservasOrderByFechaDevolucion() {
        return mRepository.getReservasOrderByFechaDevolucion();
    }

     /* =========================
       CRUD RESERVA
       ========================= */

    /** Inserta un nuevo quad en la base de datos */
    public void insert(Reserva reserva) { mRepository.insert(reserva); }

    /** Actualiza un quad existente en la base de datos */
    public void update(Reserva reserva) { mRepository.update(reserva); }

    /** Elimina un quad de la base de datos */
    public void delete(Reserva reserva) { mRepository.delete(reserva); }

    public void recalcularPrecio(int reservaId, long fechaInicio, long fechaFin) {
        mRepository.recalcularPrecioReserva(reservaId, fechaInicio, fechaFin);
    }

    public void insertAndReturnIdAsync(Reserva reserva, es.unizar.eina.G222_quads.utils.IdCallback callback) {
        mRepository.insertAndReturnIdAsync(reserva, callback);
    }

}
