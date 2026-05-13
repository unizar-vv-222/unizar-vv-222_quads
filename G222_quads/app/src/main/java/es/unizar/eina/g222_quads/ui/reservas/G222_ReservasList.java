package es.unizar.eina.g222_quads.ui.reservas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import es.unizar.eina.g222_quads.R;
import es.unizar.eina.g222_quads.database.Reserva;
import es.unizar.eina.g222_quads.ui.BaseActivity;

import static androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult;

/**
 * Lista de reservas.
 * Permite crear, editar y eliminar reservas.
 */
public class G222_ReservasList extends BaseActivity {

    private ReservaViewModel mReservaViewModel;
    private ReservaListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_g222_reserva);

        MaterialAutoCompleteTextView filtro = findViewById(R.id.filtro_reservas);
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        FloatingActionButton fab = findViewById(R.id.fab);
        Button ordReservas = findViewById(R.id.orden_reservas);
        TextView emptyReservas = findViewById(R.id.text_empty_reservas);

        setupRecyclerView(recyclerView);

        mReservaViewModel = new ViewModelProvider(this).get(ReservaViewModel.class);
        mReservaViewModel.getReservasUi().observe(this, reservas -> {
            boolean empty = reservas == null || reservas.isEmpty();

            emptyReservas.setVisibility(empty ? View.VISIBLE : View.GONE);
            recyclerView.setVisibility(empty ? View.GONE : View.VISIBLE);

            mAdapter.submitList(reservas);
        });

        setupFiltro(filtro);

        fab.setOnClickListener(v -> createReserva());
        ordReservas.setOnClickListener(v -> showSortReservasDialog());

    }

    /* =========================
       INIT
       ========================= */
    private void setupRecyclerView(RecyclerView recyclerView) {

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

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);

    }

    private void setupFiltro(MaterialAutoCompleteTextView filtro) {

        String[] opciones = getResources().getStringArray(R.array.filtro_reservas_options);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.item_dropdown_reservas,
                opciones
        );

        filtro.setAdapter(adapter);

        // valor por defecto
        filtro.setText(opciones[0], false);

        // listener
        filtro.setOnItemClickListener((parent, view, position, id) -> {
            mReservaViewModel.setFiltro(position);
        });

        filtro.setOnClickListener(v -> {
            filtro.showDropDown();
        });

        // Impido escribir texto manualmente
        filtro.setKeyListener(null);

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
        intent.putExtra(ReservaModify.RESERVA_FECHA_RECOGIDA, reserva.getFechaRecogida());
        intent.putExtra(ReservaModify.RESERVA_HORA_RECOGIDA, reserva.getHoraRecogida());
        intent.putExtra(ReservaModify.RESERVA_FECHA_DEVOLUCION, reserva.getFechaDevolucion());
        intent.putExtra(ReservaModify.RESERVA_HORA_DEVOLUCION, reserva.getHoraDevolucion());
        intent.putExtra("precio_total", reserva.getPrecioTotal());
        startActivity(intent);

    }

    private final ActivityResultLauncher<Intent> mStartUpdateReserva =
            registerForActivityResult(
                    new StartActivityForResult(),
                    result -> {
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

        new MaterialAlertDialogBuilder(this)
                .setTitle("Ordenar reservas")
                .setItems(R.array.orden_reservas_options, (dialog, which) -> {
                    switch (which) {
                        case 0: // Por nombre de cliente
                            mReservaViewModel.setOrden(ReservaViewModel.ORDEN_NOMBRE);
                            break;
                        case 1: // Por teléfono
                            mReservaViewModel.setOrden(ReservaViewModel.ORDEN_TELEFONO);
                            break;
                        case 2: // Por fecha de recogida
                            mReservaViewModel.setOrden(ReservaViewModel.ORDEN_RECOGIDA);
                            break;
                        case 3: // Por fecha de devolución
                            mReservaViewModel.setOrden(ReservaViewModel.ORDEN_DEVOLUCION);
                            break;
                    }
                })
                .show();

    }

}