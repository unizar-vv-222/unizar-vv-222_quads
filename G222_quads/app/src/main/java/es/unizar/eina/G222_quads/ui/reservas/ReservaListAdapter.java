package es.unizar.eina.G222_quads.ui.reservas;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import es.unizar.eina.G222_quads.database.Reserva;

/**
 * Adaptador para mostrar la lista de reservas en un RecyclerView.
 * Se encarga únicamente de:
 *  - mostrar los datos de la reserva
 *  - notificar acciones de usuario (editar / eliminar)
 * La lógica de negocio se delega a la Activity mediante un listener.
 */
public class ReservaListAdapter
        extends ListAdapter<Reserva, ReservaViewHolder> {

    /**
     * Listener para acciones sobre una reserva (editar / eliminar).
     * La Activity debe implementar esta interfaz.
     */
    public interface OnReservaActionListener {
        void onEdit(Reserva reserva);
        void onDelete(Reserva reserva);

        void onClick(Reserva reserva);
    }

    private final OnReservaActionListener listener;

    /**
     * Constructor del adaptador.
     *
     * @param diffCallback estrategia para comparar reservas
     * @param listener receptor de eventos de edición y borrado
     */
    public ReservaListAdapter(
            @NonNull DiffUtil.ItemCallback<Reserva> diffCallback,
            @NonNull OnReservaActionListener listener) {
        super(diffCallback);
        this.listener = listener;
    }

    @Override
    public ReservaViewHolder onCreateViewHolder(
            ViewGroup parent, int viewType) {
        return ReservaViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ReservaViewHolder holder, int position) {

        Reserva current = getItem(position);

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

    public static class ReservaDiff
            extends DiffUtil.ItemCallback<Reserva> {

        /**
         * Comprueba si dos objetos representan la misma reserva
         * (misma clave primaria).
         */
        @Override
        public boolean areItemsTheSame(
                @NonNull Reserva oldItem,
                @NonNull Reserva newItem) {

            return oldItem.getId() == newItem.getId();
        }

        /**
         * Comprueba si el contenido visual de la reserva ha cambiado.
         * Si devuelve false, el item se redibuja.
         */
        @Override
        public boolean areContentsTheSame(
                @NonNull Reserva oldItem,
                @NonNull Reserva newItem) {

            return oldItem.getNombreCliente()
                    .equals(newItem.getNombreCliente())
                    && oldItem.getMovilCliente()
                    .equals(newItem.getMovilCliente())
                    && oldItem.getFechaRecogida()
                    == newItem.getFechaRecogida()
                    && oldItem.getFechaDevolucion()
                    == newItem.getFechaDevolucion();
        }
    }
}
