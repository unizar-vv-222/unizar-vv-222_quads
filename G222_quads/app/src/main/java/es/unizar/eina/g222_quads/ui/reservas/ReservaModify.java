package es.unizar.eina.g222_quads.ui.reservas;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import es.unizar.eina.g222_quads.R;
import es.unizar.eina.g222_quads.ui.BaseActivity;
import es.unizar.eina.g222_quads.utils.DateUtils;

/**
 * Activity para CREAR o EDITAR los datos básicos de una reserva.
 */
public class ReservaModify extends BaseActivity {

    /* =========================
       EXTRAS
       ========================= */

    public static final String RESERVA_ID = "reserva_id";
    public static final String RESERVA_NOMBRE = "nombre_cliente";
    public static final String RESERVA_MOVIL = "movil_cliente";
    public static final String RESERVA_FECHA_RECOGIDA = "fecha_recogida";
    public static final String RESERVA_HORA_RECOGIDA = "hora_recogida";
    public static final String RESERVA_FECHA_DEVOLUCION = "fecha_devolucion";
    public static final String RESERVA_HORA_DEVOLUCION = "hora_devolucion";
    public static final String FECHAS_MODIFICADAS = "fechas_modificadas";
    public static final String RESERVA_QUADS_CASCOS = "reserva_quads_cascos";

    /* =========================
       VISTAS
       ========================= */

    private EditText mNombre;
    private EditText mMovil;
    private TextView mFechaRecogida;
    private RadioGroup mHoraRecogida;
    private TextView mFechaDevolucion;
    private RadioGroup mHoraDevolucion;
    private Button mContinueButton;
    private Button mCancelButton;
    private TextView mTitle;
    private TextView mReservaId;
    private View mLayoutReservaId;

    /* =========================
       ESTADO
       ========================= */

    private boolean isEditMode = false;
    private int reservaId = -1;

    private long fechaInicioMillis = -1;
    private long fechaFinMillis = -1;

    private long fechaInicioOriginal = -1;
    private long fechaFinOriginal = -1;

    private boolean horaInicio = false;
    private boolean horaFin = false;

    private boolean horaInicioOriginal = false;
    private boolean horaFinOriginal = false;


    /* =========================
       CICLO DE VIDA
       ========================= */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserva_form);

        bindViews();
        determineMode();
        configureUiForMode();
        preloadDataIfEditing();

        mFechaRecogida.setOnClickListener(v -> pickDate(true));
        mFechaDevolucion.setOnClickListener(v -> pickDate(false));

        mContinueButton.setOnClickListener(v -> continueReserva());
        mCancelButton.setOnClickListener(v -> cancel());
    }

    /* =========================
       INIT
       ========================= */

    private void bindViews() {
        mNombre = findViewById(R.id.nombre_cliente);
        mMovil = findViewById(R.id.movil_cliente);
        mFechaRecogida = findViewById(R.id.fecha_recogida);
        mHoraRecogida = findViewById(R.id.horario_recogida);
        mFechaDevolucion = findViewById(R.id.fecha_devolucion);
        mHoraDevolucion = findViewById(R.id.horario_devolucion);
        mContinueButton = findViewById(R.id.button_continue);
        mCancelButton = findViewById(R.id.button_cancel);
        mTitle = findViewById(R.id.title_create_reserva);
        mLayoutReservaId = findViewById(R.id.layout_reserva_id);
        mReservaId = findViewById(R.id.text_reserva_id);
    }

    private void determineMode() {
        isEditMode = getIntent().hasExtra(RESERVA_ID);
        if (isEditMode) {
            reservaId = getIntent().getIntExtra(RESERVA_ID, -1);
        }
    }

    private void configureUiForMode() {
        if (isEditMode) {
            mTitle.setText(R.string.edit_reserva);
            mLayoutReservaId.setVisibility(View.VISIBLE);
            mReservaId.setText("#" + reservaId);
        } else {
            mTitle.setText(R.string.create_reserva);
            mLayoutReservaId.setVisibility(View.GONE);
        }
    }

    private void preloadDataIfEditing() {
        if (!isEditMode) return;

        Bundle extras = getIntent().getExtras();
        if (extras == null) return;

        mNombre.setText(extras.getString(RESERVA_NOMBRE));
        mMovil.setText(extras.getString(RESERVA_MOVIL));

        fechaInicioMillis = extras.getLong(RESERVA_FECHA_RECOGIDA);
        fechaFinMillis = extras.getLong(RESERVA_FECHA_DEVOLUCION);

        fechaInicioOriginal = fechaInicioMillis;
        fechaFinOriginal = fechaFinMillis;

        mFechaRecogida.setText(DateUtils.formatearFecha(fechaInicioMillis));
        mFechaDevolucion.setText(DateUtils.formatearFecha(fechaFinMillis));

        horaInicio = extras.getBoolean(RESERVA_HORA_RECOGIDA, false);
        horaFin = extras.getBoolean(RESERVA_HORA_DEVOLUCION, false);

        horaInicioOriginal = horaInicio;
        horaFinOriginal = horaFin;

        if (horaInicio) {
            mHoraRecogida.check(R.id.horario_recogida_tarde);
        } else {
            mHoraRecogida.check(R.id.horario_recogida_manana);
        }

        if (horaFin) {
            mHoraDevolucion.check(R.id.horario_devolucion_tarde);
        } else {
            mHoraDevolucion.check(R.id.horario_devolucion_manana);
        }
    }

    /* =========================
       FECHAS
       ========================= */

    private void pickDate(boolean inicio) {
        Calendar cal = Calendar.getInstance();

        new DatePickerDialog(
                this,
                (view, year, month, day) -> {
                    Calendar c = Calendar.getInstance();
                    c.set(year, month, day, 0, 0, 0);
                    c.set(Calendar.MILLISECOND, 0);

                    long selectedDay = DateUtils.inicioDelDia(c.getTimeInMillis());

                    if (inicio) {
                        fechaInicioMillis = selectedDay;
                        mFechaRecogida.setText(DateUtils.formatearFecha(fechaInicioMillis));
                    } else {
                        fechaFinMillis = selectedDay;
                        mFechaDevolucion.setText(DateUtils.formatearFecha(fechaFinMillis));
                    }
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    /* =========================
       FLUJO
       ========================= */

    private void continueReserva() {
        if (!isFormValid()) {
            return;
        }

        horaInicio = mHoraRecogida.getCheckedRadioButtonId() == R.id.horario_recogida_tarde;
        horaFin = mHoraDevolucion.getCheckedRadioButtonId() == R.id.horario_devolucion_tarde;

        boolean fechasModificadas =
                fechaInicioOriginal != -1 &&
                        (fechaInicioMillis != fechaInicioOriginal ||
                                fechaFinMillis != fechaFinOriginal ||
                                horaInicio != horaInicioOriginal ||
                                horaFin != horaFinOriginal);

        Intent intent = new Intent(this, ReservaSelectQuads.class);
        intent.putExtra(RESERVA_NOMBRE, mNombre.getText().toString().trim());
        intent.putExtra(RESERVA_MOVIL, mMovil.getText().toString().trim());
        intent.putExtra(RESERVA_FECHA_RECOGIDA, fechaInicioMillis);
        intent.putExtra(RESERVA_FECHA_DEVOLUCION, fechaFinMillis);
        intent.putExtra(FECHAS_MODIFICADAS, fechasModificadas);
        intent.putExtra(RESERVA_HORA_RECOGIDA, horaInicio);
        intent.putExtra(RESERVA_HORA_DEVOLUCION, horaFin);

        if (isEditMode) {
            intent.putExtra(RESERVA_ID, reservaId);
        }

        startActivity(intent);
        finish();
    }

    private void cancel() {
        setResult(RESULT_CANCELED);
        finish();
    }

    private boolean isFormValid() {
        if (TextUtils.isEmpty((mNombre.getText()))) {
            mNombre.setError("Introduce el nombre del cliente");
            return false;
        }
        if (TextUtils.isEmpty((mMovil.getText()))) {
            mMovil.setError("Introduce el móvil del cliente");
            return false;
        }
        if (fechaInicioMillis == -1) {
            mFechaRecogida.setError("Selecciona una fecha de recogida");
            return false;
        }
        if (mHoraRecogida.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Selecciona un horario de recogida", Toast.LENGTH_LONG).show();
            return false;
        }
        if (fechaFinMillis == -1) {
            mFechaDevolucion.setError("Selecciona una fecha de devolución");
            return false;
        }
        if (mHoraDevolucion.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Selecciona un horario de devolución", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!isDateTimeRangeValid()) {
            Toast.makeText(this, "Fechas/horarios inválidos", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private boolean isDateTimeRangeValid() {
        boolean inicioHorario = mHoraRecogida.getCheckedRadioButtonId() == R.id.horario_recogida_tarde;
        boolean finHorario = mHoraDevolucion.getCheckedRadioButtonId() == R.id.horario_devolucion_tarde;

        return DateUtils.rangoValido(fechaInicioMillis, inicioHorario, fechaFinMillis, finHorario);
    }
}
