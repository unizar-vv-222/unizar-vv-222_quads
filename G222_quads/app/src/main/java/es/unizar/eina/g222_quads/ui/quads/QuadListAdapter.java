package es.unizar.eina.g222_quads.ui.quads;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import es.unizar.eina.g222_quads.database.Quad;

/**
 * Adaptador para mostrar la lista de quads en un RecyclerView.
 * Se encarga únicamente de:
 * - mostrar los datos del quad
 * - notificar acciones de usuario (editar / eliminar)
 * La lógica de negocio se delega a la Activity mediante un listener.
 */
public class QuadListAdapter extends ListAdapter<Quad, QuadViewHolder> {

    /**
     * Listener para acciones sobre un quad (editar / eliminar).
     * La Activity debe implementar esta interfaz.
     */
    public interface OnQuadActionListener {
        void onEdit(Quad quad);

        void onDelete(Quad quad);

        void onClick(Quad quad);
    }

    private final OnQuadActionListener listener;

    /**
     * Constructor del adaptador.
     *
     * @param diffCallback estrategia para comparar quads
     * @param listener     receptor de eventos de edición y borrado
     */
    public QuadListAdapter(@NonNull DiffUtil.ItemCallback<Quad> diffCallback,
                           @NonNull OnQuadActionListener listener) {
        super(diffCallback);
        this.listener = listener;
    }

    @Override
    public QuadViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return QuadViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull QuadViewHolder holder, int position) {

        Quad current = getItem(position);

        /* ========= BIND DE DATOS ========= */

        holder.bind(current);

        /* ========= ACCIONES ========= */

        holder.btnEdit.setOnClickListener(v ->
                listener.onEdit(current)
        );

        holder.btnDelete.setOnClickListener(v ->
                listener.onDelete(current)
        );

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(current);
            }
        });

    }

    /* =========================================================
       DiffUtil
       ========================================================= */

    public static class QuadDiff extends DiffUtil.ItemCallback<Quad> {

        /**
         * Comprueba si dos objetos representan el mismo quad
         * (misma clave primaria).
         */
        @Override
        public boolean areItemsTheSame(@NonNull Quad oldItem,
                                       @NonNull Quad newItem) {
            return oldItem.getMatricula().equals(newItem.getMatricula());
        }

        /**
         * Comprueba si el contenido visual del quad ha cambiado.
         * Si devuelve false, el item se redibuja.
         */
        @Override
        public boolean areContentsTheSame(@NonNull Quad oldItem,
                                          @NonNull Quad newItem) {

            return oldItem.getMatricula().equals(newItem.getMatricula())
                    && oldItem.getTipo() == newItem.getTipo()
                    && oldItem.getPrecio() == newItem.getPrecio()
                    && oldItem.getDescripcion().equals(newItem.getDescripcion());
        }
    }
}
