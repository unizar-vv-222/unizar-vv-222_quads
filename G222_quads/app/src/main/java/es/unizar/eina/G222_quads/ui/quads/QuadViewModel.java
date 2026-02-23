package es.unizar.eina.G222_quads.ui.quads;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import es.unizar.eina.G222_quads.database.Quad;
import es.unizar.eina.G222_quads.database.QuadRepository;

/**
 * ViewModel que expone la lista de quads y las operaciones CRUD a la interfaz de usuario.
 * Actúa como intermediario entre la UI y el repositorio.
 */
public class QuadViewModel extends AndroidViewModel {

    private final QuadRepository mRepository;
    private final LiveData<List<Quad>> mAllQuads;

    /**
     * Inicializa el ViewModel, el repositorio y la lista observable de quads.
     *
     * @param application contexto de aplicación necesario para inicializar la base de datos
     */
    public QuadViewModel(Application application) {
        super(application);
        mRepository = new QuadRepository(application);
        mAllQuads = mRepository.getAllQuads();
    }

    public LiveData<List<Quad>> getQuadsOrderByMatricula() {
        return mRepository.getQuadsOrderByMatricula();
    }

    public LiveData<List<Quad>> getQuadsOrderByTipo() {
        return mRepository.getQuadsOrderByTipo();
    }

    public LiveData<List<Quad>> getQuadsOrderByPrecio() {
        return mRepository.getQuadsOrderByPrecio();
    }

    /**
     * Devuelve la lista observable de todos los quads.
     */
    public LiveData<List<Quad>> getAllQuads() {
        return mAllQuads;
    }

    /** Inserta un nuevo quad */
    public void insert(Quad quad) {
        mRepository.insert(quad);
    }

    /** Actualiza un quad existente */
    public void update(Quad quad) {
        mRepository.update(quad);
    }

    /** Elimina el quad con matrícula "matricula" */
    public void deleteByMatricula(String matricula) {
        mRepository.deleteByMatricula(matricula);
    }

    public LiveData<List<Quad>> getAvailableQuads(long inicio, long fin) {
        return mRepository.getAvailableQuads(inicio, fin);
    }

    public Quad getQuadByMatriculaSync(String matricula) {
        return mRepository.getQuadByMatriculaSync(matricula);
    }

}
