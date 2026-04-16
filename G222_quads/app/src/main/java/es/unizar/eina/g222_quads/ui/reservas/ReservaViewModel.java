package es.unizar.eina.g222_quads.ui.reservas;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.util.ArrayList;
import java.util.List;

import es.unizar.eina.g222_quads.database.Reserva;
import es.unizar.eina.g222_quads.database.ReservaRepository;
import es.unizar.eina.g222_quads.utils.DateUtils;

/**
 * ViewModel de reservas.
 * Mantiene el estado de orden y filtro y expone una única lista
 * observable para la UI.
 * Actúa como intermediario entre la UI y el repositorio.
 */
public class ReservaViewModel extends AndroidViewModel {

    // TIPOS DE ORDEN
    public static final int ORDEN_NOMBRE = 0;
    public static final int ORDEN_TELEFONO = 1;
    public static final int ORDEN_RECOGIDA = 2;
    public static final int ORDEN_DEVOLUCION = 3;

    // TIPOS DE FILTRO
    public static final int FILTRO_TODAS = 0;
    public static final int FILTRO_PREVISTAS = 1;
    public static final int FILTRO_VIGENTES = 2;
    public static final int FILTRO_CADUCADAS = 3;

    // ATRIBUTOS
    private final ReservaRepository mRepository;

    // Lista final que observará la UI
    private final MediatorLiveData<List<Reserva>> reservasUi = new MediatorLiveData<>();

    // Fuente actual obtenida de BD según el orden
    private LiveData<List<Reserva>> reservasOrdenadasSource;

    // Última lista obtenida de BD
    private List<Reserva> reservasBase = new ArrayList<>();

    private int ordenActual = ORDEN_NOMBRE;
    private int filtroActual = FILTRO_TODAS;

    /**
     * Constructor de ReservaViewModel
     *
     * @param application contexto de aplicación necesario para inicializar la base de datos
     */
    public ReservaViewModel(@NonNull Application application) {

        super(application);
        mRepository = new ReservaRepository(application);
        cambiarSourceOrdenada();

    }

    /* =========================
       GETTERS DE ESTADO
       ========================= */

    public LiveData<List<Reserva>> getReservasUi() {
        return reservasUi;
    }

    public int getOrdenActual() {
        return ordenActual;
    }

    public int getFiltroActual() {
        return filtroActual;
    }

    /* =========================
       CAMBIO DE ORDEN / FILTRO
       ========================= */

    /**
     * Cambia el orden actual y refresca la lista.
     * El filtro se mantiene.
     *
     * @param nuevoOrden nuevo orden de la lista
     */
    public void setOrden(int nuevoOrden) {

        if (ordenActual != nuevoOrden) {
            ordenActual = nuevoOrden;
            cambiarSourceOrdenada();
        }

    }

    /**
     * Cambia el filtro actual y refresca la lista.
     * El orden se mantiene.
     *
     * @param nuevoFiltro nuevo filtro de la lista
     */
    public void setFiltro(int nuevoFiltro) {

        if (filtroActual != nuevoFiltro) {
            filtroActual = nuevoFiltro;
            publicarListaFiltrada();
        }

    }

    /* =========================
       FUENTE ORDENADA DESDE BD
       ========================= */

    /**
     * Cambia la fuente observada de BD según el orden actual.
     * Cada vez que llega una nueva lista desde BD, se reaplica el filtro.
     */
    private void cambiarSourceOrdenada() {

        if (reservasOrdenadasSource != null) {
            reservasUi.removeSource(reservasOrdenadasSource);
        }

        reservasOrdenadasSource = getSourceByOrden(ordenActual);

        reservasUi.addSource(reservasOrdenadasSource, reservas -> {
            reservasBase = (reservas != null) ? new ArrayList<>(reservas) : new ArrayList<>();
            publicarListaFiltrada();
        });

    }

    /**
     * Devuelve la consulta LiveData correspondiente al orden seleccionado.
     *
     * @param orden Orden seleccionado
     * @return consulta LiveData
     */
    private LiveData<List<Reserva>> getSourceByOrden(int orden) {

        switch (orden) {

            case ORDEN_TELEFONO:
                return mRepository.getReservasOrderByTelefono();

            case ORDEN_RECOGIDA:
                return mRepository.getReservasOrderByRecogida();

            case ORDEN_DEVOLUCION:
                return mRepository.getReservasOrderByDevolucion();

            case ORDEN_NOMBRE:
            default:
                return mRepository.getReservasOrderByNombre();

        }

    }

    /* =========================
       FILTRADO EN MEMORIA
       ========================= */

    /**
     * Publica en reservasUi la lista base ordenada, tras aplicar el filtro actual.
     */
    private void publicarListaFiltrada() {

        List<Reserva> resultado = new ArrayList<>();
        long ahora = System.currentTimeMillis();

        for (Reserva r : reservasBase) {

            if (cumpleFiltro(r, ahora)) {
                resultado.add(r);
            }

        }

        reservasUi.setValue(resultado);

    }

    /**
     * Comprueba
     */
    private boolean cumpleFiltro(Reserva reserva, long ahoraMillis) {

        long ahoraComparable = DateUtils.obtenerHorarioActual(ahoraMillis);

        switch (filtroActual) {

            case FILTRO_PREVISTAS:
                return reserva.getRecogidaComparable() > ahoraComparable;

            case FILTRO_VIGENTES:
                return reserva.getRecogidaComparable() <= ahoraComparable
                        && reserva.getDevolucionComparable() > ahoraComparable;

            case FILTRO_CADUCADAS:
                return reserva.getDevolucionComparable() <= ahoraComparable;

            case FILTRO_TODAS:
            default:
                return true;

        }

    }

     /* =========================
       CRUD RESERVA
       ========================= */

    /**
     * Inserta un nuevo quad en la base de datos
     */
    public void insert(Reserva reserva) {
        mRepository.insert(reserva);
    }

    /**
     * Actualiza un quad existente en la base de datos
     */
    public void update(Reserva reserva) {
        mRepository.update(reserva);
    }

    /**
     * Elimina un quad de la base de datos
     */
    public void delete(Reserva reserva) {
        mRepository.delete(reserva);
    }

    public void recalcularPrecio(int reservaId, long fechaInicio, boolean horaInicio,
                                 long fechaFin, boolean horaFin) {
        mRepository.recalcularPrecioReserva(reservaId, fechaInicio, horaInicio, fechaFin, horaFin);
    }

    public void insertAndReturnIdAsync(Reserva reserva, es.unizar.eina.g222_quads.utils.IdCallback callback) {
        mRepository.insertAndReturnIdAsync(reserva, callback);
    }

}
