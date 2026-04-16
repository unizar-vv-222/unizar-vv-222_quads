package es.unizar.eina.g222_quads.ui.reservas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.unizar.eina.g222_quads.R;
import es.unizar.eina.g222_quads.database.Quad;
import es.unizar.eina.g222_quads.ui.BaseActivity;
import es.unizar.eina.g222_quads.ui.quads.QuadViewModel;
import es.unizar.eina.g222_quads.utils.DateUtils;

public class ReservaSelectQuads extends BaseActivity {

    private QuadViewModel mQuadViewModel;
    private ReservaQuadCascosViewModel mReservaQuadViewModel;
    private ReservaSelectQuadsAdapter mAdapter;

    private long fechaInicio;
    private long fechaFin;
    private boolean horaInicio;
    private boolean horaFin;

    private boolean isEditMode = false;
    private boolean fechasModificadas = false;
    private int reservaId = -1;

    private final Map<String, Integer> cascosReserva = new HashMap<>();

    private RecyclerView mRecyclerView;
    private TextView mEmptyText;
    private Button mConfirmButton;

    /* =========================
       CICLO DE VIDA
       ========================= */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserva_select_quads);

        readExtras();
        bindViews();
        setupRecyclerView();
        setupViewModels();
        setupObservers();

        mConfirmButton.setOnClickListener(v -> goToConfirm());
    }

    /* =========================
       INIT
       ========================= */

    private void readExtras() {
        Intent i = getIntent();

        fechaInicio = i.getLongExtra(ReservaModify.RESERVA_FECHA_RECOGIDA, -1);
        fechaFin = i.getLongExtra(ReservaModify.RESERVA_FECHA_DEVOLUCION, -1);

        if (fechaInicio == -1 || fechaFin == -1) {
            Toast.makeText(this, "Fechas inválidas", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        fechasModificadas =
                i.getBooleanExtra(ReservaModify.FECHAS_MODIFICADAS, false);

        horaInicio = i.getBooleanExtra(ReservaModify.RESERVA_HORA_RECOGIDA, false);
        horaFin = i.getBooleanExtra(ReservaModify.RESERVA_HORA_DEVOLUCION, false);

        if (!DateUtils.rangoValido(fechaInicio, horaInicio, fechaFin, horaFin)) {
            Toast.makeText(this, "Fechas inválidas", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        isEditMode = i.hasExtra(ReservaModify.RESERVA_ID);
        if (isEditMode) {
            reservaId = i.getIntExtra(ReservaModify.RESERVA_ID, -1);
        }
    }

    private void bindViews() {
        mRecyclerView = findViewById(R.id.recyclerview_quads);
        mEmptyText = findViewById(R.id.text_no_quads);
        mConfirmButton = findViewById(R.id.button_confirm);
    }

    private void setupRecyclerView() {
        mAdapter = new ReservaSelectQuadsAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
    }

    private void setupViewModels() {
        mQuadViewModel = new ViewModelProvider(this).get(QuadViewModel.class);
        mReservaQuadViewModel =
                new ViewModelProvider(this).get(ReservaQuadCascosViewModel.class);
    }

    /* =========================
       OBSERVERS
       ========================= */

    private void setupObservers() {

        // si editamos y NO cambiaron fechas/horarios, precargamos la selección original
        if (isEditMode && !fechasModificadas) {
            mReservaQuadViewModel
                    .getByReserva(reservaId)
                    .observe(this, cascos -> {
                        cascosReserva.clear();
                        if (cascos != null) {
                            cascosReserva.putAll(cascos);
                        }
                        mAdapter.setInitialData(cascosReserva);
                    });
        } else {
            // si cambiaron fechas/horarios, NO se precarga nada
            cascosReserva.clear();
            mAdapter.setInitialData(null);
        }

        // cargar quads disponibles
        // - si estamos creando -> disponibilidad normal
        // - si estamos editando -> excluir la propia reserva del cálculo
        LiveData<List<Quad>> quadsLiveData;

        if (isEditMode) {
            quadsLiveData = mQuadViewModel.getAvailableQuadsExcludingReserva(
                    fechaInicio, horaInicio, fechaFin, horaFin, reservaId);
        } else {
            quadsLiveData = mQuadViewModel.getAvailableQuads(
                    fechaInicio, horaInicio, fechaFin, horaFin);
        }

        quadsLiveData.observe(this, quads -> {
            if (quads == null) {
                quads = new ArrayList<>();
            }

            // si editamos y NO cambiaron fechas/horarios, también
            // deben aparecer los quads seleccionados originalmente
            if (isEditMode && !fechasModificadas && !cascosReserva.isEmpty()) {
                for (String matricula : cascosReserva.keySet()) {
                    boolean existe = false;

                    for (Quad q : quads) {
                        if (q.getMatricula().equals(matricula)) {
                            existe = true;
                            break;
                        }
                    }
                    if (!existe) {
                        Quad antiguo =
                                mQuadViewModel.getQuadByMatriculaSync(matricula);
                        if (antiguo != null) {
                            quads.add(antiguo);
                        }
                    }
                }
            }

            // mostrar lista o mensaje vacío
            if (quads.isEmpty()) {
                mRecyclerView.setVisibility(View.GONE);
                mEmptyText.setVisibility(View.VISIBLE);
            } else {
                mRecyclerView.setVisibility(View.VISIBLE);
                mEmptyText.setVisibility(View.GONE);
                mAdapter.submitList(new ArrayList<>(quads));
            }
        });

    }


    /* =========================
       FLUJO
       ========================= */

    private void goToConfirm() {

        Map<String, Integer> seleccion = mAdapter.getCascosPorQuad();

        if (seleccion == null || seleccion.isEmpty()) {
            Toast.makeText(this,
                    "Selecciona al menos un quad",
                    Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent(this, ReservaConfirm.class);

        intent.putExtra(
                ReservaModify.RESERVA_QUADS_CASCOS,
                new HashMap<>(seleccion)
        );

        intent.putExtra(
                ReservaModify.RESERVA_FECHA_RECOGIDA,
                fechaInicio
        );
        intent.putExtra(
                ReservaModify.RESERVA_FECHA_DEVOLUCION,
                fechaFin
        );

        intent.putExtra(
                ReservaModify.FECHAS_MODIFICADAS,
                fechasModificadas
        );

        intent.putExtra(ReservaModify.RESERVA_HORA_RECOGIDA,
                horaInicio
        );

        intent.putExtra(ReservaModify.RESERVA_HORA_DEVOLUCION,
                horaFin
        );

        intent.putExtra(
                ReservaModify.RESERVA_NOMBRE,
                getIntent().getStringExtra(ReservaModify.RESERVA_NOMBRE)
        );

        intent.putExtra(
                ReservaModify.RESERVA_MOVIL,
                getIntent().getStringExtra(ReservaModify.RESERVA_MOVIL)
        );


        if (isEditMode) {
            intent.putExtra(ReservaModify.RESERVA_ID, reservaId);
        }

        startActivity(intent);
        finish();
    }
}
