package es.unizar.eina.G222_quads.ui.reservas;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import es.unizar.eina.G222_quads.R;
import es.unizar.eina.G222_quads.database.Reserva;
import es.unizar.eina.G222_quads.utils.DateUtils;

public class ReservaConfirm extends AppCompatActivity {

    private ReservaViewModel mReservaViewModel;
    private ReservaQuadCascosViewModel mReservaQuadCascosViewModel;

    private TextView tvCliente;
    private TextView tvFechas;
    private TextView tvPrecio;

    private Button btnConfirmar;
    private Button btnCancelar;

    private RecyclerView rvQuads;
    private ConfirmQuadsAdapter adapter;

    private Reserva reserva;
    private Map<String, Integer> cascosPorQuad = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserva_confirm);

        bindViews();
        setupRecycler();

        mReservaViewModel = new ViewModelProvider(this).get(ReservaViewModel.class);
        mReservaQuadCascosViewModel =
                new ViewModelProvider(this).get(ReservaQuadCascosViewModel.class);

        cargarDatos();
        mostrarDatos();
        pintarListaSinPrecios();
        calcularPrecio();

        btnConfirmar.setOnClickListener(v -> guardarReserva());
        btnCancelar.setOnClickListener(v -> finish());
    }

    private void bindViews() {
        tvCliente = findViewById(R.id.text_cliente);
        tvFechas = findViewById(R.id.text_fechas);
        tvPrecio = findViewById(R.id.text_precio_total);
        btnConfirmar = findViewById(R.id.button_confirm);
        btnCancelar = findViewById(R.id.button_cancel);

        rvQuads = findViewById(R.id.recyclerview_quads_resumen);
    }

    private void setupRecycler() {
        adapter = new ConfirmQuadsAdapter();
        rvQuads.setLayoutManager(new LinearLayoutManager(this));
        rvQuads.setAdapter(adapter);
    }

    private void cargarDatos() {
        Intent intent = getIntent();

        String nombre = intent.getStringExtra(ReservaModify.RESERVA_NOMBRE);
        String movil = intent.getStringExtra(ReservaModify.RESERVA_MOVIL);

        long fechaInicio = intent.getLongExtra(ReservaModify.RESERVA_FECHA_RECOGIDA, -1);
        long fechaFin = intent.getLongExtra(ReservaModify.RESERVA_FECHA_DEVOLUCION, -1);

        reserva = new Reserva(nombre, movil, fechaInicio, fechaFin);

        if (intent.hasExtra(ReservaModify.RESERVA_ID)) {
            reserva.setId(intent.getIntExtra(ReservaModify.RESERVA_ID, 0));
        }

        HashMap<String, Integer> sel =
                (HashMap<String, Integer>) intent.getSerializableExtra(ReservaModify.RESERVA_QUADS_CASCOS);

        if (sel != null) {
            cascosPorQuad.clear();
            cascosPorQuad.putAll(sel);
        }
    }

    private void mostrarDatos() {
        // Evita "null" si falta algo
        String nombre = reserva.getNombreCliente() == null ? "" : reserva.getNombreCliente();
        tvCliente.setText("Cliente: " + nombre);

        tvFechas.setText(DateUtils.toHumanRange(reserva.getFechaRecogida(), reserva.getFechaDevolucion()));
    }

    private void pintarListaSinPrecios() {
        List<ItemConfirm> items = new ArrayList<>();
        for (Map.Entry<String, Integer> e : cascosPorQuad.entrySet()) {
            items.add(new ItemConfirm(e.getKey(), 0.0, e.getValue())); // precio placeholder
        }
        adapter.submit(items);
    }

    private void calcularPrecio() {
        if (cascosPorQuad.isEmpty()) {
            tvPrecio.setText(String.format(Locale.getDefault(), "%.2f €", 0.0));
            return;
        }

        long dias = DateUtils.daysBetween(reserva.getFechaRecogida(), reserva.getFechaDevolucion());

        mReservaQuadCascosViewModel.getPreciosParaReservaAsync(
                reserva.getId(),
                cascosPorQuad,
                preciosPorMatricula -> {

                    double total = 0.0;
                    List<ItemConfirm> items = new ArrayList<>();

                    for (Map.Entry<String, Integer> e : cascosPorQuad.entrySet()) {
                        String matricula = e.getKey();
                        int cascos = e.getValue();

                        double precioDia = 0.0;
                        if (preciosPorMatricula != null && preciosPorMatricula.containsKey(matricula)) {
                            precioDia = preciosPorMatricula.get(matricula);
                        }

                        total += precioDia * dias;
                        items.add(new ItemConfirm(matricula, precioDia, cascos));
                    }

                    adapter.submit(items);
                    tvPrecio.setText(String.format(Locale.getDefault(), "%.2f €", total));
                }
        );
    }

    private void guardarReserva() {

        boolean esNueva = reserva.getId() == 0;

        if (esNueva) {
            mReservaViewModel.insertAndReturnIdAsync(reserva, id -> {
                reserva.setId((int) id);
                Log.d("ReservaConfirm", "Reserva guardada con id: " + id);

                mReservaQuadCascosViewModel.updateCascos(
                        reserva.getId(),
                        cascosPorQuad
                );

                mReservaViewModel.recalcularPrecio(
                        reserva.getId(),
                        reserva.getFechaRecogida(),
                        reserva.getFechaDevolucion()
                );

                mostrarDialogoConfirmacion();
            });

        } else {
            mReservaViewModel.update(reserva);

            mReservaQuadCascosViewModel.updateCascos(reserva.getId(), cascosPorQuad);

            mReservaViewModel.recalcularPrecio(
                    reserva.getId(),
                    reserva.getFechaRecogida(),
                    reserva.getFechaDevolucion()
            );
            mostrarDialogoConfirmacion();
        }
    }

    private void mostrarDialogoConfirmacion() {
        runOnUiThread(() ->
                new AlertDialog.Builder(this)
                        .setTitle("Reserva confirmada")
                        .setMessage("Reserva guardada correctamente.\nID: #" + reserva.getId())
                        .setPositiveButton("Aceptar", (d, w) -> {
                            setResult(RESULT_OK);
                            finish();
                        })
                        .setCancelable(false)
                        .show()
        );
    }


    /* =========================================================
       Adapter de solo lectura (3 columnas: matrícula, precio, cascos)
       ========================================================= */

    static class ItemConfirm {
        final String matricula;
        final double precioDia;
        final int cascos;

        ItemConfirm(String matricula, double precioDia, int cascos) {
            this.matricula = matricula;
            this.precioDia = precioDia;
            this.cascos = cascos;
        }
    }

    static class ConfirmQuadsAdapter extends RecyclerView.Adapter<ConfirmQuadsAdapter.VH> {

        private final List<ItemConfirm> data = new ArrayList<>();

        void submit(List<ItemConfirm> items) {
            data.clear();
            if (items != null) data.addAll(items);
            notifyDataSetChanged();
        }

        @Override
        public VH onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            android.view.View v = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_reserva_confirm_quad, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(VH h, int position) {
            ItemConfirm it = data.get(position);
            h.tvMatricula.setText(it.matricula);
            h.tvPrecio.setText(String.format(Locale.getDefault(), "%.2f €/día", it.precioDia));
            h.tvCascos.setText("Cascos: " + String.valueOf(it.cascos));
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        static class VH extends RecyclerView.ViewHolder {
            TextView tvMatricula, tvPrecio, tvCascos;

            VH(android.view.View itemView) {
                super(itemView);
                tvMatricula = itemView.findViewById(R.id.text_matricula);
                tvPrecio = itemView.findViewById(R.id.text_precio);
                tvCascos = itemView.findViewById(R.id.text_cascos);
            }
        }
    }
}
