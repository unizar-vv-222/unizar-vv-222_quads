package es.unizar.eina.g222_quads.ui.reservas;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import es.unizar.eina.g222_quads.R;
import es.unizar.eina.g222_quads.database.Reserva;

import static androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult;

/**
 * Lista de reservas.
 * Permite crear, editar y eliminar reservas.
 */
public class G222_ReservasList extends AppCompatActivity {

    private ReservaViewModel mReservaViewModel;
    private ReservaListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_g222_reserva);

        /* =========================
           RECYCLERVIEW
           ========================= */

        RecyclerView recyclerView = findViewById(R.id.recyclerview);

        mAdapter = new ReservaListAdapter(
                new ReservaListAdapter.ReservaDiff(),
                new ReservaListAdapter.OnReservaActionListener() {
                    @Override
                    public void onClick(Reserva reserva) {
                        openDetail(reserva);
                    }

                    @Override
                    public void onEdit(Reserva reserva) {
                        editReserva(reserva);
                    }

                    @Override
                    public void onDelete(Reserva reserva) {
                        showDeleteDialog(reserva);
                    }
                }
        );

        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        /* =========================
           VIEWMODEL
           ========================= */

        mReservaViewModel = new ViewModelProvider(this).get(ReservaViewModel.class);
        mReservaViewModel.getAllReservas().observe(this,
                reservas -> mAdapter.submitList(reservas)
        );

        /* =========================
           FAB – CREAR RESERVA
           ========================= */

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> createReserva());

        Button ordReservas = findViewById(R.id.ord_reservas);
        ordReservas.setOnClickListener(v -> showSortReservasDialog());
    }

    /* =========================
       CREAR RESERVA
       ========================= */

    private void createReserva() {
        mStartCreateReserva.launch(new Intent(this, ReservaModify.class));
    }

    private final ActivityResultLauncher<Intent> mStartCreateReserva =
            registerForActivityResult(
                    new StartActivityForResult(),
                    result -> {
                        // No hacemos nada:
                        // ReservaModify ya guarda y LiveData refresca la lista
                    }
            );

    /* =========================
       EDITAR RESERVA
       ========================= */

    private void editReserva(Reserva reserva) {
        Intent intent = new Intent(this, ReservaModify.class);
        intent.putExtra(ReservaModify.RESERVA_ID, reserva.getId());
        intent.putExtra(ReservaModify.RESERVA_NOMBRE, reserva.getNombreCliente());
        intent.putExtra(ReservaModify.RESERVA_MOVIL, reserva.getMovilCliente());
        intent.putExtra(ReservaModify.RESERVA_FECHA_RECOGIDA, reserva.getFechaRecogida());
        intent.putExtra(ReservaModify.RESERVA_HORA_RECOGIDA, reserva.getHoraRecogida());
        intent.putExtra(ReservaModify.RESERVA_FECHA_DEVOLUCION, reserva.getFechaDevolucion());
        intent.putExtra(ReservaModify.RESERVA_HORA_DEVOLUCION, reserva.getHoraDevolucion());
        mStartUpdateReserva.launch(intent);
    }


    private void openDetail(Reserva reserva) {
        Intent intent = new Intent(this, ReservaDetail.class);
        intent.putExtra(ReservaModify.RESERVA_ID, reserva.getId());
        intent.putExtra(ReservaModify.RESERVA_NOMBRE, reserva.getNombreCliente());
        intent.putExtra(ReservaModify.RESERVA_MOVIL, reserva.getMovilCliente());
        startActivity(intent);

    }
    private final ActivityResultLauncher<Intent> mStartUpdateReserva =
            registerForActivityResult(
                    new StartActivityForResult(),
                    result -> {
                        // No hacemos nada aquí
                        // La actualización ya está hecha en ReservaModify
                    }
            );

    /* =========================
       ELIMINAR RESERVA
       ========================= */

    private void showDeleteDialog(Reserva reserva) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.delete_reserva)
                .setMessage(R.string.confirm_delete_reserva)
                .setPositiveButton(
                        R.string.delete,
                        (dialog, which) -> mReservaViewModel.delete(reserva)
                )
                .setNegativeButton(R.string.button_cancel, null)
                .show();
    }

    /* =========================
       ORDENAR RESERVAS
       ========================= */
    private void showSortReservasDialog() {
        String[] options = {"Por nombre de cliente", "Por teléfono", "Por fecha de recogida", "Por fecha de devolución"};

        new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle("Ordenar reservas")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: // Por nombre de cliente
                            mReservaViewModel.getReservasOrderByNombre().observe(this, reservas -> mAdapter.submitList(reservas));
                            break;
                        case 1: // Por teléfono
                            mReservaViewModel.getReservasOrderByTelefono().observe(this, reservas -> mAdapter.submitList(reservas));
                            break;
                        case 2: // Por fecha de recogida
                            mReservaViewModel.getReservasOrderByFechaRecogida().observe(this, reservas -> mAdapter.submitList(reservas));
                            break;
                        case 3: // Por fecha de devolución
                            mReservaViewModel.getReservasOrderByFechaDevolucion().observe(this, reservas -> mAdapter.submitList(reservas));
                            break;
                    }
                })
                .show();
    }


}
