package es.unizar.eina.g222_quads.ui.reservas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import es.unizar.eina.g222_quads.R;
import es.unizar.eina.g222_quads.database.ReservaQuadCascos;

/**
 * Adapter SOLO lectura para confirmación de reserva.
 * Columnas: Matrícula | Precio | Cascos
 */
public class ReservaConfirmAdapter extends RecyclerView.Adapter<ReservaConfirmAdapter.ViewHolder> {

    private final List<ReservaQuadCascos> data = new ArrayList<>();

    public void setData(List<ReservaQuadCascos> lista) {
        data.clear();
        if (lista != null) {
            data.addAll(lista);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reserva_confirm_quad, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {
        ReservaQuadCascos rqc = data.get(position);

        holder.matricula.setText(rqc.getMatriculaQuad());

        holder.precio.setText(
                String.format("%.2f € / día", rqc.getPrecioOriginal())
        );

        holder.cascos.setText(
                String.valueOf(rqc.getNumCascos())
        );
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView matricula;
        TextView precio;
        TextView cascos;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            matricula = itemView.findViewById(R.id.text_matricula);
            precio = itemView.findViewById(R.id.text_precio);
            cascos = itemView.findViewById(R.id.text_cascos);
        }
    }
}
