package es.unizar.eina.g222_quads.ui.reservas;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.util.ArrayList;
import java.util.List;

import es.unizar.eina.g222_quads.R;
import es.unizar.eina.g222_quads.database.Reserva;
import es.unizar.eina.g222_quads.database.ReservaRepository;

/**
 * ViewModel que expone la lista de quads y las operaciones CRUD al interfaz de usuario.
 * Actúa como intermediario entre la UI y el repositorio.
 */
public class ReservaViewModel extends AndroidViewModel {

    public static final int FILTRO_TODAS = 0;
    public static final int FILTRO_PREVISTAS = 1;
    public static final int FILTRO_VIGENTES = 2;
    public static final int FILTRO_CADUCADAS = 3;

    public static final int ORDEN_NOMBRE = 0;
    public static final int ORDEN_TELEFONO = 1;
    public static final int ORDEN_RECOGIDA = 2;
    public static final int ORDEN_DEVOLUCION = 3;

    private final ReservaRepository mRepository;

    private final MediatorLiveData<List<Reserva>> reservasUi = new MediatorLiveData<>();
    private LiveData<List<Reserva>> sourceActual;

    private List<Reserva> reservasBase = new ArrayList<>();

    private int filtroActual = FILTRO_TODAS;
    private int ordenActual = ORDEN_NOMBRE;

    /**
     * Crea el ViewModel e inicializa el repositorio y la LiveData con todos los quads.
     * @param application contexto de aplicación necesario para inicializar la base de datos
     */
    public ReservaViewModel(@NonNull Application application) {

        super(application);
        mRepository = new ReservaRepository(application);
        cambiarFiltro(FILTRO_TODAS);

    }

    /* =========================
       QUERIES
       ========================= */

    public LiveData<List<Reserva>> getReservasUi() {
        return reservasUi;
    }

    public void setFiltro(int nuevoFiltro) {

        if (filtroActual != nuevoFiltro) {
            filtroActual = nuevoFiltro;
            // al cambiar el filtro, reseteamos el orden
            ordenActual = ORDEN_NOMBRE;
            cambiarFiltro(filtroActual);
        }

    }

    public void setOrden(int nuevoOrden) {

        ordenActual = nuevoOrden;
        publicarLista();

    }

    public int getFiltroActual() {
        return filtroActual;
    }

    public int getOrdenActual() {
        return ordenActual;
    }

    private void cambiarFiltro(int filtro) {

        if (sourceActual != null) {
            reservasUi.removeSource(sourceActual);
        }

        sourceActual = obtenerReservasPorFiltro(filtro);

        reservasUi.addSource(sourceActual, reservas -> {
            reservasBase = reservas != null ? new ArrayList<>(reservas) : new ArrayList<>();
            publicarLista();
        });

    }

    private LiveData<List<Reserva>> obtenerReservasPorFiltro(int filtro) {

        switch(filtro) {

            case FILTRO_PREVISTAS:
                // TODO Obtener listado de reservas previstas del repositorio
                // return mRepository.getReservasPrevistas();

            case FILTRO_VIGENTES:
                // TODO Obtener listado de reservas vigentes del repositorio
                // return mRepository.getReservasVigentes();

            case FILTRO_CADUCADAS:
                // TODO Obtener listado de reservas caducadas del repositorio
                // return mRepository.getReservasCaducadas();

            case FILTRO_TODAS:
                return mRepository.getAllReservas();

            default:
                return mRepository.getAllReservas();

        }

    }

    private void publicarLista() {

        List<Reserva> resultado = new ArrayList<>(reservasBase);

        switch(ordenActual) {

            case ORDEN_TELEFONO:
                // TODO Ordenar por teléfono

            case ORDEN_RECOGIDA:
                // TODO Ordenar por fecha de recogida

            case ORDEN_DEVOLUCION:
                // TODO Ordenar por fecha de devolución

            case ORDEN_NOMBRE:
                // TODO Ordenar por nombre

            default:
                // TODO Ordenar por nombre

        }

        reservasUi.setValue(resultado);

    }

    /**
     * Devuelve la LiveData con todos los quads almacenados.
     * @return lista observable de quads
     */
    LiveData<List<Reserva>> getAllReservas() { return sourceActual; }

    public LiveData<List<Reserva>> getReservasOrderByNombre() {
        return mRepository.getReservasOrderByNombre();
    }

    public LiveData<List<Reserva>> getReservasOrderByTelefono() {
        return mRepository.getReservasOrderByTelefono();
    }

    public LiveData<List<Reserva>> getReservasOrderByFechaRecogida() {
        return mRepository.getReservasOrderByRecogida();
    }

    public LiveData<List<Reserva>> getReservasOrderByFechaDevolucion() {
        return mRepository.getReservasOrderByDevolucion();
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

    public void recalcularPrecio(int reservaId, long fechaInicio, boolean horaInicio,
                                 long fechaFin, boolean horaFin) {
        mRepository.recalcularPrecioReserva(reservaId, fechaInicio, horaInicio, fechaFin, horaFin);
    }

    public void insertAndReturnIdAsync(Reserva reserva, es.unizar.eina.g222_quads.utils.IdCallback callback) {
        mRepository.insertAndReturnIdAsync(reserva, callback);
    }

}
