package es.unizar.eina.g222_quads.ui.reservas;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Locale;
import java.util.Map;

import es.unizar.eina.g222_quads.R;
import es.unizar.eina.g222_quads.database.Reserva;
import es.unizar.eina.g222_quads.ui.BaseActivity;
import es.unizar.eina.g222_quads.utils.DateUtils;
import es.unizar.eina.send.SendAbstraction;
import es.unizar.eina.send.SendAbstractionImpl;

public class ReservaDetail extends BaseActivity {

    private ReservaViewModel mReservaViewModel;
    private ReservaQuadCascosViewModel mReservaQuadCascosViewModel;

    private TextView mDetailId;
    private TextView mDetailNombre;
    private TextView mDetailMovil;
    private TextView mDetailRecogida;
    private TextView mDetailDevolucion;
    private TextView mDetailPrecio;
    private TextView mDetailNoQuads;
    private LinearLayout mDetailQuadsContainer;

    private MaterialButton mButtonDelete;
    private MaterialButton mButtonEdit;
    private MaterialButton mButtonEnviar;

    private Reserva reservaActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserva_detail);

        bindViews();
        setupViewModels();
        readReservaFromIntent();
        showReservaData();
        observeQuads();
        setupListeners();
    }

    private void bindViews() {

        mDetailId = findViewById(R.id.detail_id);
        mDetailNombre = findViewById(R.id.detail_nombre);
        mDetailMovil = findViewById(R.id.detail_telefono);
        mDetailRecogida = findViewById(R.id.detail_recogida);
        mDetailDevolucion = findViewById(R.id.detail_devolucion);
        mDetailPrecio = findViewById(R.id.detail_precio);
        mDetailNoQuads = findViewById(R.id.detail_no_quads);
        mDetailQuadsContainer = findViewById(R.id.detail_quads_container);

        mButtonDelete = findViewById(R.id.button_delete);
        mButtonEdit = findViewById(R.id.button_edit);
        mButtonEnviar = findViewById(R.id.btn_enviar);

    }


    private void setupViewModels() {

        mReservaViewModel = new ViewModelProvider(this).get(ReservaViewModel.class);
        mReservaQuadCascosViewModel = new ViewModelProvider(this).get(ReservaQuadCascosViewModel.class);

    }

    private void readReservaFromIntent() {

        Intent intent = getIntent();

        int id = intent.getIntExtra(ReservaModify.RESERVA_ID, -1);
        String nombre = intent.getStringExtra(ReservaModify.RESERVA_NOMBRE);
        String movil = intent.getStringExtra(ReservaModify.RESERVA_MOVIL);
        long fechaRecogida = intent.getLongExtra(ReservaModify.RESERVA_FECHA_RECOGIDA, -1);
        boolean horaRecogida = intent.getBooleanExtra(ReservaModify.RESERVA_HORA_RECOGIDA, false);
        long fechaDevolucion = intent.getLongExtra(ReservaModify.RESERVA_FECHA_DEVOLUCION, -1);
        boolean horaDevolucion = intent.getBooleanExtra(ReservaModify.RESERVA_HORA_DEVOLUCION, false);

        double precioTotal = intent.getDoubleExtra("precio_total", -1.0);

        if (id == -1 || nombre == null || movil == null
                || fechaRecogida == -1 || fechaDevolucion == -1) {
            Toast.makeText(this,
                    "No se han podido cargar los datos de la reserva",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        reservaActual = new Reserva(nombre, movil, fechaRecogida, horaRecogida, fechaDevolucion, horaDevolucion);
        reservaActual.setId(id);
        reservaActual.setPrecioTotal(precioTotal >= 0 ? precioTotal : 0.0);

    }

    private void showReservaData() {

        if (reservaActual == null) return;

        mDetailId.setText(String.format(Locale.getDefault(), "Reserva #%d", reservaActual.getId()));
        mDetailNombre.setText("Nombre: " + reservaActual.getNombreCliente());
        mDetailMovil.setText("Móvil: " + reservaActual.getMovilCliente());

        mDetailRecogida.setText(
                "Recogida: " + DateUtils.formatearFechaHorario(
                        reservaActual.getFechaRecogida(),
                        reservaActual.getHoraRecogida()
                )
        );

        mDetailDevolucion.setText(
                "Devolución: " + DateUtils.formatearFechaHorario(
                        reservaActual.getFechaDevolucion(),
                        reservaActual.getHoraDevolucion()
                )
        );

        mDetailPrecio.setText(
                String.format(Locale.getDefault(), "Precio total: %.2f €", reservaActual.getPrecioTotal())
        );

    }

    private void observeQuads() {

        if (reservaActual == null) return;

        mReservaQuadCascosViewModel
                .getByReserva(reservaActual.getId())
                .observe(this, quadsCascos -> showQuads(quadsCascos));

    }

    private void setupListeners() {

        mButtonDelete.setOnClickListener(v -> showDeleteDialog());
        mButtonEdit.setOnClickListener(v -> editReserva());
        mButtonEnviar.setOnClickListener(v -> enviarReserva());

    }

    private void showQuads(Map<String, Integer> quadsCascos) {

        mDetailQuadsContainer.removeAllViews();

        if (quadsCascos == null || quadsCascos.isEmpty()) {
            mDetailNoQuads.setVisibility(TextView.VISIBLE);
            return;
        }

        mDetailNoQuads.setVisibility(TextView.GONE);

        for (Map.Entry<String, Integer> entry : quadsCascos.entrySet()) {

            String matricula = entry.getKey();
            Integer numCascos = entry.getValue();

            TextView tv = new TextView(this);
            tv.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));

            tv.setText(String.format(Locale.getDefault(),
                    "- Quad %s (%d casco/s)", matricula, numCascos));
            tv.setTextSize(15f);
            tv.setTextColor(getResources().getColor(R.color.black, getTheme()));
            tv.setPadding(0, 8, 0, 8);
            tv.setFontFeatureSettings("normal");

            mDetailQuadsContainer.addView(tv);

        }

    }

    /* =========================================================
       ELIMINAR
       ========================================================= */
    private void showDeleteDialog() {

        if (reservaActual == null) return;

        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.delete_reserva)
                .setMessage(R.string.confirm_delete_reserva)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    mReservaViewModel.delete(reservaActual);
                    Toast.makeText(this, "Reserva eliminada", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton(R.string.button_cancel, null)
                .show();

    }

    /* =========================================================
       EDITAR
       ========================================================= */
    private void editReserva() {

        if (reservaActual == null) return;

        Intent intent = new Intent(this, ReservaModify.class);

        intent.putExtra(ReservaModify.RESERVA_ID, reservaActual.getId());
        intent.putExtra(ReservaModify.RESERVA_NOMBRE, reservaActual.getNombreCliente());
        intent.putExtra(ReservaModify.RESERVA_MOVIL, reservaActual.getMovilCliente());
        intent.putExtra(ReservaModify.RESERVA_FECHA_RECOGIDA, reservaActual.getFechaRecogida());
        intent.putExtra(ReservaModify.RESERVA_HORA_RECOGIDA, reservaActual.getHoraRecogida());
        intent.putExtra(ReservaModify.RESERVA_FECHA_DEVOLUCION, reservaActual.getFechaDevolucion());
        intent.putExtra(ReservaModify.RESERVA_HORA_DEVOLUCION, reservaActual.getHoraDevolucion());

        mStartUpdateReserva.launch(intent);
    }

    private final ActivityResultLauncher<Intent> mStartUpdateReserva =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        // La actualización ya está hecha en ReservaModify
                        finish();
                    }
            );

    private void enviarReserva() {

        if (reservaActual == null) return;

        // Creamos la abstracción indicando el método a usar (SMS)
        SendAbstraction sendApp = new SendAbstractionImpl(this, "SMS");
        String mensaje = "Hola " + reservaActual.getNombreCliente() + ", tu reserva con ID "
                + reservaActual.getId() + " ha sido confirmada.";
        sendApp.send(reservaActual.getMovilCliente(), mensaje);

    }

}

