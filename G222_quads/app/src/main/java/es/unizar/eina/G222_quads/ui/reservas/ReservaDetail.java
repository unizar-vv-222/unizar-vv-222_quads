package es.unizar.eina.G222_quads.ui.reservas;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import es.unizar.eina.G222_quads.R;
import es.unizar.eina.send.SendAbstraction;
import es.unizar.eina.send.SendAbstractionImpl;

public class ReservaDetail extends AppCompatActivity {

    private String nombre;
    private String telefono;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserva_detail);

        bindViews();
        loadData();
        setupButtons();
    }

    private void bindViews() {
        findViewById(R.id.button_edit).setOnClickListener(v -> editReserva());
        findViewById(R.id.button_delete).setOnClickListener(v -> confirmDelete());
        findViewById(R.id.btn_enviar).setOnClickListener(v -> {
            // Creamos la abstracción indicando el método a usar (SMS)
            SendAbstraction sendApp = new SendAbstractionImpl(this, "SMS");
            String mensaje = "Hola " + nombre + ", tu reserva con ID " + id + " ha sido confirmada.";
            sendApp.send(telefono, mensaje);
        });

    }

    private void loadData() {
        Bundle extras = getIntent().getExtras();
        if (extras == null) return;

        nombre   = extras.getString(ReservaModify.RESERVA_NOMBRE);
        telefono        = extras.getString(ReservaModify.RESERVA_MOVIL);
        id = extras.getInt(ReservaModify.RESERVA_ID);


        ((TextView) findViewById(R.id.detail_nombre)).setText(nombre);
        ((TextView) findViewById(R.id.detail_telefono)).setText(telefono);
    }

    private void setupButtons() {
        // ya enlazados en bindViews()
    }

    /* =========================================================
       EDITAR
       ========================================================= */

    private void editReserva() {
        Intent intent = new Intent(this, ReservaModify.class);
        intent.putExtra(ReservaModify.RESERVA_NOMBRE, nombre);
        intent.putExtra(ReservaModify.RESERVA_MOVIL, telefono);
        intent.putExtra(ReservaModify.RESERVA_ID, id);
        startActivity(intent);
    }

    /* =========================================================
       ELIMINAR
       ========================================================= */

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_delete_quad)
                .setMessage(R.string.confirm_delete_message)
                .setPositiveButton(R.string.delete, (dialog, which) -> deleteReserva())
                .setNegativeButton(R.string.button_cancel, null)
                .show();
    }

    private void deleteReserva() {
        Intent result = new Intent();
        result.putExtra(ReservaModify.RESERVA_ID, id);
        setResult(RESULT_OK, result);
        finish();
    }
}
