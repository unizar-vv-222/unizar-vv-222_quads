package es.unizar.eina.G222_quads.ui.quads;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Button;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import es.unizar.eina.G222_quads.R;
import es.unizar.eina.G222_quads.database.Quad;

import static androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult;

/**
 * Actividad principal.
 * Lista los quads existentes y permite crear, editar y eliminar.
 */
public class G222_QuadsList extends AppCompatActivity {

    private QuadViewModel mQuadViewModel;
    private QuadListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_g222_quads);

        /* =========================
           RECYCLERVIEW
           ========================= */

        RecyclerView recyclerView = findViewById(R.id.recyclerview);

        mAdapter = new QuadListAdapter(
                new QuadListAdapter.QuadDiff(),
                new QuadListAdapter.OnQuadActionListener() {

                    @Override
                    public void onClick(Quad quad) {
                        openDetail(quad);
                    }

                    @Override
                    public void onEdit(Quad quad) {
                        editQuad(quad);
                    }

                    @Override
                    public void onDelete(Quad quad) {
                        showDeleteDialog(quad.getMatricula());
                    }
                }
        );


        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        /* =========================
           VIEWMODEL
           ========================= */

        mQuadViewModel = new ViewModelProvider(this).get(QuadViewModel.class);
        mQuadViewModel.getAllQuads().observe(this, quads ->
                mAdapter.submitList(quads)
        );

        /* =========================
           FAB – CREAR QUAD
           ========================= */

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> createQuad());

        /* =========================
         ORD QUADS - FILTRO ORDENAR
        =========================== */

        Button ordQuads = findViewById(R.id.ord_quads);
        ordQuads.setOnClickListener(v -> showSortDialog());

    }

    /* =========================
       CREAR QUAD
       ========================= */

    private void createQuad() {
        mStartCreateQuad.launch(new Intent(this, QuadModify.class));
    }

    private final ActivityResultLauncher<Intent> mStartCreateQuad =
            registerForActivityResult(
                    new StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Bundle extras = result.getData().getExtras();
                            Quad quad = new Quad(
                                    extras.getString(QuadModify.QUAD_MATRICULA),
                                    extras.getBoolean(QuadModify.QUAD_TIPO),
                                    extras.getDouble(QuadModify.QUAD_PRECIO),
                                    extras.getString(QuadModify.QUAD_DESCRIPCION)
                            );
                            mQuadViewModel.insert(quad);
                        }
                    }
            );

    /* =========================
       EDITAR QUAD
       ========================= */

    private void editQuad(Quad quad) {
        Intent intent = new Intent(this, QuadModify.class);
        intent.putExtra(QuadModify.QUAD_MATRICULA, quad.getMatricula());
        intent.putExtra(QuadModify.QUAD_TIPO, quad.getTipo());
        intent.putExtra(QuadModify.QUAD_PRECIO, quad.getPrecio());
        intent.putExtra(QuadModify.QUAD_DESCRIPCION, quad.getDescripcion());
        mStartUpdateQuad.launch(intent);
    }

    private final ActivityResultLauncher<Intent> mStartUpdateQuad =
            registerForActivityResult(
                    new StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Bundle extras = result.getData().getExtras();
                            Quad updated = new Quad(
                                    extras.getString(QuadModify.QUAD_MATRICULA),
                                    extras.getBoolean(QuadModify.QUAD_TIPO),
                                    extras.getDouble(QuadModify.QUAD_PRECIO),
                                    extras.getString(QuadModify.QUAD_DESCRIPCION)
                            );
                            mQuadViewModel.update(updated);
                        }
                    }
            );


    /* =========================
       DIÁLOGO DELETE QUAD
       ========================= */

    private void showDeleteDialog(String quad) {

        new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle(R.string.delete_quad)
                .setMessage(
                        getString(R.string.delete_quad_message, quad)
                )
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    mQuadViewModel.deleteByMatricula(quad);
                })
                .setNegativeButton(R.string.button_cancel, null)
                .show();
    }

    private final ActivityResultLauncher<Intent> mStartDetail =
            registerForActivityResult(
                    new StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {

                            String matricula = result.getData().getStringExtra(QuadModify.QUAD_MATRICULA);

                            // Eliminación confirmada desde detalle
                            mQuadViewModel.deleteByMatricula(matricula);

                        }
                    }
            );

    private void openDetail(Quad quad) {
        Intent intent = new Intent(this, QuadDetail.class);
        intent.putExtra(QuadModify.QUAD_MATRICULA, quad.getMatricula());
        intent.putExtra(QuadModify.QUAD_TIPO, quad.getTipo());
        intent.putExtra(QuadModify.QUAD_PRECIO, quad.getPrecio());
        intent.putExtra(QuadModify.QUAD_DESCRIPCION, quad.getDescripcion());

        mStartDetail.launch(intent);
    }

    private void showSortDialog() {
        String[] options = {"Por matrícula", "Por tipo",  "Por precio"};

        new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle("Ordenar quads")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: // Por matricula
                            mQuadViewModel.getQuadsOrderByMatricula().observe(this, quads -> mAdapter.submitList(quads));
                            break;
                        case 1: // Por tipo
                            mQuadViewModel.getQuadsOrderByTipo().observe(this, quads -> mAdapter.submitList(quads));
                            break;
                        case 2: // Por fecha de entrega
                            mQuadViewModel.getQuadsOrderByPrecio().observe(this, quads -> mAdapter.submitList(quads));
                            break;
                    }
                })
                .show();
    }


}
