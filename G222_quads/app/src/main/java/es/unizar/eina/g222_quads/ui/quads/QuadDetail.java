package es.unizar.eina.g222_quads.ui.quads;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Locale;

import es.unizar.eina.g222_quads.R;
import es.unizar.eina.g222_quads.ui.BaseActivity;

public class QuadDetail extends BaseActivity {

    private String matricula;
    private boolean tipo;
    private double precio;
    private String descripcion;

    private TextView mDetailMatricula;
    private TextView mDetailTipo;
    private TextView mDetailPrecio;
    private TextView mDetailDescripcion;

    private MaterialButton mButtonEdit;
    private MaterialButton mButtonDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quad_detail);

        bindViews();
        readQuadFromIntent();
        showQuadData();
        setupListeners();
    }

    private void bindViews() {
        mDetailMatricula = findViewById(R.id.detail_matricula);
        mDetailTipo = findViewById(R.id.detail_tipo);
        mDetailPrecio = findViewById(R.id.detail_precio);
        mDetailDescripcion = findViewById(R.id.detail_descripcion);

        mButtonEdit = findViewById(R.id.button_edit);
        mButtonDelete = findViewById(R.id.button_delete);
    }

    private void readQuadFromIntent() {
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Toast.makeText(this,
                    "No se han podido cargar los datos del quad",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        matricula = extras.getString(QuadModify.QUAD_MATRICULA);
        tipo = extras.getBoolean(QuadModify.QUAD_TIPO);
        precio = extras.getDouble(QuadModify.QUAD_PRECIO);
        descripcion = extras.getString(QuadModify.QUAD_DESCRIPCION);

        if (matricula == null) {
            Toast.makeText(this,
                    "No se han podido cargar los datos del quad",
                    Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void showQuadData() {

        mDetailMatricula.setText("Quad " + matricula);

        mDetailTipo.setText("Tipo: " + (tipo ? "Biplaza" : "Monoplaza"));

        mDetailPrecio.setText(
                String.format(Locale.getDefault(), "Precio diario: %.2f €", precio)
        );

        if (descripcion == null || descripcion.trim().isEmpty()) {
            mDetailDescripcion.setText("Sin descripción.");
        } else {
            mDetailDescripcion.setText(descripcion);
        }

    }

    private void setupListeners() {
        mButtonEdit.setOnClickListener(v -> editQuad());
        mButtonDelete.setOnClickListener(v -> confirmDelete());
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
        mStartUpdateQuad.launch(intent);
    }

    private final ActivityResultLauncher<Intent> mStartUpdateQuad =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        // Cerramos esta pantalla para volver al listado
                        // y que el listado se refresque con LiveData
                        finish();
                    }
            );

    /* =========================================================
       ELIMINAR
       ========================================================= */
    private void confirmDelete() {
        new MaterialAlertDialogBuilder(this)
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
