package es.unizar.eina.g222_quads.ui.reservas;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.Map;

import es.unizar.eina.g222_quads.database.ReservaQuadCascosRepository;

/**
 * ViewModel para gestionar la relación Reserva–Quad.
 * Actúa como intermediario entre la UI y ReservaQuadRepository.
 *
 * @author G222
 */
public class ReservaQuadCascosViewModel extends AndroidViewModel {

    private final ReservaQuadCascosRepository mRepository;

    public ReservaQuadCascosViewModel(@NonNull Application application) {
        super(application);
        mRepository = new ReservaQuadCascosRepository(application);
    }

    public boolean updateCascos(int reservaId, Map<String, Integer> nuevos) {
        return mRepository.updateCascos(reservaId, nuevos);
    }

    /**
     * Devuelve las matrículas de los quads asociados a una reserva.
     * Se usa en modo EDICIÓN para marcar checkboxes.
     *
     * @param reservaId id de la reserva
     */
    public LiveData<Map<String, Integer>> getByReserva(int reservaId) {
        return mRepository.getByReserva(reservaId);
    }

    public void getPreciosParaReservaAsync(int reservaId,
                                           Map<String, Integer> seleccion,
                                           java.util.function.Consumer<Map<String, Double>> cb) {
        mRepository.getPreciosParaReservaAsync(reservaId, seleccion, cb);
    }

}