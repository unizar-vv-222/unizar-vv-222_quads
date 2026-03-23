package es.unizar.eina.g222_quads.ui.quads;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import es.unizar.eina.g222_quads.R;

public class QuadDetail extends AppCompatActivity {

    private String matricula;
    private boolean tipo;
    private double precio;
    private String descripcion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quad_detail);

        bindViews();
        loadData();
        setupButtons();
    }

    private void bindViews() {
        findViewById(R.id.button_edit).setOnClickListener(v -> editQuad());
        findViewById(R.id.button_delete).setOnClickListener(v -> confirmDelete());
    }

    private void loadData() {
        Bundle extras = getIntent().getExtras();
        if (extras == null) return;

        matricula   = extras.getString(QuadModify.QUAD_MATRICULA);
        tipo        = extras.getBoolean(QuadModify.QUAD_TIPO);
        precio      = extras.getDouble(QuadModify.QUAD_PRECIO);
        descripcion = extras.getString(QuadModify.QUAD_DESCRIPCION);

        ((TextView) findViewById(R.id.detail_matricula)).setText(matricula);
        ((TextView) findViewById(R.id.detail_tipo))
                .setText(tipo ? "Monoplaza" : "Biplaza");
        ((TextView) findViewById(R.id.detail_precio))
                .setText("Precio: " + precio);
        ((TextView) findViewById(R.id.detail_descripcion))
                .setText(descripcion);
    }

    private void setupButtons() {
        // ya enlazados en bindViews()
    }

    /* =========================================================
       EDITAR
       ========================================================= */

    private void editQuad() {
        Intent intent = new Intent(this, QuadModify.class);
        intent.putExtra(QuadModify.QUAD_MATRICULA, matricula);
        intent.putExtra(QuadModify.QUAD_TIPO, tipo);
        intent.putExtra(QuadModify.QUAD_PRECIO, precio);
        intent.putExtra(QuadModify.QUAD_DESCRIPCION, descripcion);
        startActivity(intent);
    }

    /* =========================================================
       ELIMINAR
       ========================================================= */

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_delete_quad)
                .setMessage(R.string.confirm_delete_message)
                .setPositiveButton(R.string.delete, (dialog, which) -> deleteQuad())
                .setNegativeButton(R.string.button_cancel, null)
                .show();
    }

    private void deleteQuad() {
        Intent result = new Intent();
        result.putExtra(QuadModify.QUAD_MATRICULA, matricula);
        setResult(RESULT_OK, result);
        finish();
    }
}
