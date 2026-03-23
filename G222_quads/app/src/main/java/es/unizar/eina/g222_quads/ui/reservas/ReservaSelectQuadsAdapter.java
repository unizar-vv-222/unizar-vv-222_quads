package es.unizar.eina.g222_quads.ui.reservas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.Map;

import es.unizar.eina.g222_quads.R;
import es.unizar.eina.g222_quads.database.Quad;

public class ReservaSelectQuadsAdapter
        extends ListAdapter<Quad, ReservaSelectQuadsAdapter.QuadViewHolder> {

    /** Mapa: matrícula → nº de cascos */
    private final Map<String, Integer> cascosPorQuad = new HashMap<>();

    public ReservaSelectQuadsAdapter() {
        super(new QuadDiff());
    }

    @NonNull
    @Override
    public QuadViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType
    ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reserva_select_quad, parent, false);
        return new QuadViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull QuadViewHolder holder, int position
    ) {
        holder.bind(getItem(position));
    }

    /* =========================================================
       API PARA LA ACTIVITY
       ========================================================= */

    public void setInitialData(Map<String, Integer> initialData) {
        cascosPorQuad.clear();
        if (initialData != null) {
            cascosPorQuad.putAll(initialData);
        }
        notifyDataSetChanged();
    }

    public Map<String, Integer> getCascosPorQuad() {
        return new HashMap<>(cascosPorQuad);
    }

    /* =========================================================
       VIEW HOLDER
       ========================================================= */

    class QuadViewHolder extends RecyclerView.ViewHolder {

        private final TextView matricula;
        private final TextView tipo;
        private final CheckBox checkbox;

        private final LinearLayout helmetContainer;
        private final ImageButton btnPlus;
        private final ImageButton btnMinus;
        private final TextView tvCount;

        QuadViewHolder(View itemView) {
            super(itemView);

            matricula = itemView.findViewById(R.id.quad_matricula);
            tipo = itemView.findViewById(R.id.quad_tipo);
            checkbox = itemView.findViewById(R.id.quad_checkbox);

            helmetContainer = itemView.findViewById(R.id.helmet_container);
            btnPlus = itemView.findViewById(R.id.btn_plus);
            btnMinus = itemView.findViewById(R.id.btn_minus);
            tvCount = itemView.findViewById(R.id.tv_count);
        }

        void bind(Quad quad) {

            matricula.setText(quad.getMatricula());
            tipo.setText(quad.getTipo() ? "Biplaza" : "Monoplaza");

            int maxCascos = quad.getTipo() ? 2 : 1;
            int current = cascosPorQuad.getOrDefault(quad.getMatricula(), 0);

            checkbox.setOnCheckedChangeListener(null);

            boolean seleccionado = cascosPorQuad.containsKey(quad.getMatricula());

            checkbox.setChecked(seleccionado);
            helmetContainer.setAlpha(seleccionado ? 1f : 0f);
            helmetContainer.setEnabled(seleccionado);

            tvCount.setText(String.valueOf(current));

            checkbox.setOnCheckedChangeListener((button, checked) -> {
                if (checked) {
                    cascosPorQuad.put(quad.getMatricula(), 0);
                    tvCount.setText("0");
                    helmetContainer.animate().alpha(1f).setDuration(150).start();
                } else {
                    cascosPorQuad.remove(quad.getMatricula());
                    tvCount.setText("0");
                    helmetContainer.animate().alpha(0f).setDuration(150).start();
                }
            });

            btnPlus.setOnClickListener(v -> {
                int value = Integer.parseInt(tvCount.getText().toString());
                if (value < maxCascos && checkbox.isChecked()) {
                    value++;
                    tvCount.setText(String.valueOf(value));
                    cascosPorQuad.put(quad.getMatricula(), value);
                }
            });

            btnMinus.setOnClickListener(v -> {
                int value = Integer.parseInt(tvCount.getText().toString());
                if (value > 0 && checkbox.isChecked()) {
                    value--;
                    tvCount.setText(String.valueOf(value));
                    cascosPorQuad.put(quad.getMatricula(), value);
                }
            });
        }
    }

    /* =========================================================
       DIFF
       ========================================================= */

    static class QuadDiff extends DiffUtil.ItemCallback<Quad> {

        @Override
        public boolean areItemsTheSame(
                @NonNull Quad oldItem, @NonNull Quad newItem
        ) {
            return oldItem.getMatricula().equals(newItem.getMatricula());
        }

        @Override
        public boolean areContentsTheSame(
                @NonNull Quad oldItem, @NonNull Quad newItem
        ) {
            return oldItem.getTipo().equals(newItem.getTipo());
        }
    }
}
