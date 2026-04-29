package es.unizar.eina.g222_quads.ui.quads;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import es.unizar.eina.g222_quads.R;
import es.unizar.eina.g222_quads.database.Quad;

/**
 * ViewHolder que representa una cajita del RecyclerView.
 * Cada instancia de esta clase corresponde a una fila del listado de quads
 * (recyclerview_item.xml).
 * Se encarga de:
 * - Mostrar la matrícula del quad
 * - Exponer los botones de editar y eliminar
 */
public class QuadViewHolder extends RecyclerView.ViewHolder {

    /* ========= VISTAS DEL ITEM ========= */

    private final TextView txtMatricula;
    public final ImageView btnEdit;
    public final ImageView btnDelete;

    /**
     * Constructor privado.
     * Se invoca desde el método create().
     */
    private QuadViewHolder(@NonNull View itemView) {
        super(itemView);

        txtMatricula = itemView.findViewById(R.id.textView);
        btnEdit = itemView.findViewById(R.id.btn_edit);
        btnDelete = itemView.findViewById(R.id.btn_delete);
    }

    /**
     * Asocia los datos de un Quad con la vista.
     * En este caso SOLO se muestra la matrícula.
     *
     * @param quad objeto Quad a representar
     */
    public void bind(@NonNull Quad quad) {
        txtMatricula.setText(quad.getMatricula());
    }

    /**
     * Método de factoría para crear el ViewHolder.
     * Se llama desde el Adapter.
     *
     * @param parent ViewGroup padre (RecyclerView)
     * @return nueva instancia de QuadViewHolder
     */
    @NonNull
    public static QuadViewHolder create(@NonNull ViewGroup parent) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.recyclerview_item, parent, false);

        return new QuadViewHolder(view);
    }
}