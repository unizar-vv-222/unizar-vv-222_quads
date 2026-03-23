package es.unizar.eina.g222_quads.ui.quads;

import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import es.unizar.eina.g222_quads.R;
import es.unizar.eina.g222_quads.ui.BaseActivity;

/**
 * Activity única para CREAR o EDITAR un quad.
 * El modo (crear / editar) se decide en función de los extras del Intent.
 */
public class QuadModify extends BaseActivity {

    /* =========================================================
       CLAVES DE RESULTADO (Intent)
       ========================================================= */

    public static final String QUAD_MATRICULA   = "matricula";
    public static final String QUAD_TIPO        = "tipo";
    public static final String QUAD_PRECIO      = "precio";
    public static final String QUAD_DESCRIPCION = "descripcion";

    /* =========================================================
       VISTAS
       ========================================================= */

    private EditText mRowMatricula;
    private RadioGroup mTipo;
    private EditText mPrecio;
    private EditText mDescripcionText;
    private Button mSaveButton;
    private Button mCancelButton;
    private TextView mTitle;

    /* =========================================================
       ESTADO
       ========================================================= */

    private boolean isEditMode;

    /* =========================================================
       CICLO DE VIDA
       ========================================================= */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quad_form);

        bindViews();

        determineMode();
        configureUiForMode();
        preloadDataIfEditing();

        mSaveButton.setOnClickListener(v -> saveQuad());
        mCancelButton.setOnClickListener(view -> cancel());
    }

    /* =========================================================
       INICIALIZACIÓN
       ========================================================= */

    private void bindViews() {
        mRowMatricula = findViewById(R.id.matricula);
        mTipo = findViewById(R.id.tipo);
        mPrecio = findViewById(R.id.precio);
        mDescripcionText = findViewById(R.id.descripcion);
        mSaveButton = findViewById(R.id.button_save);
        mCancelButton = findViewById(R.id.button_cancel);
        mTitle = findViewById(R.id.title_create_quad);
    }

    /**
     * Determina si estamos creando o editando un quad.
     * Si viene la matrícula en el Intent → estamos editando.
     */
    private void determineMode() {
        isEditMode = getIntent().hasExtra(QUAD_MATRICULA);
    }

    /**
     * Ajusta textos y comportamiento visual según el modo.
     */
    private void configureUiForMode() {
        if (isEditMode) {
            mTitle.setText(R.string.edit_quad);
            mSaveButton.setText(R.string.button_save);
            mRowMatricula.setEnabled(false); // PK no editable
            Typeface bold = ResourcesCompat.getFont(this, R.font.poppins_bold);
            mRowMatricula.setTypeface(bold);
        } else {
            mTitle.setText(R.string.create_quad);
            mSaveButton.setText(R.string.button_save);
            mRowMatricula.setEnabled(true);
        }
    }

    /**
     * Precarga los datos solo si estamos editando.
     */
    private void preloadDataIfEditing() {
        if (!isEditMode) return;

        Bundle extras = getIntent().getExtras();

        mRowMatricula.setText(extras.getString(QUAD_MATRICULA));

        boolean tipo = extras.getBoolean(QUAD_TIPO);
        mTipo.check(tipo ? R.id.tipo_monoplaza : R.id.tipo_biplaza);

        mPrecio.setText(String.valueOf(extras.getDouble(QUAD_PRECIO)));
        mDescripcionText.setText(extras.getString(QUAD_DESCRIPCION));
    }

    /* =========================================================
       GUARDAR / DEVOLVER RESULTADO
       ========================================================= */

    private void saveQuad() {

        if (!isFormValid()) {
            Toast.makeText(this, R.string.empty_not_saved, Toast.LENGTH_LONG).show();
            return;
        }

        String matricula   = mRowMatricula.getText().toString().trim();
        String descripcion = mDescripcionText.getText().toString().trim();
        double precio;
        try {
            precio      = parsePrecio(mPrecio.getText().toString().trim());
        } catch(NumberFormatException e) {
            return;
        }
        boolean tipo       = (mTipo.getCheckedRadioButtonId() == R.id.tipo_monoplaza);

        Intent replyIntent = new Intent();
        replyIntent.putExtra(QUAD_MATRICULA, matricula);
        replyIntent.putExtra(QUAD_TIPO, tipo);
        replyIntent.putExtra(QUAD_PRECIO, precio);
        replyIntent.putExtra(QUAD_DESCRIPCION, descripcion);

        setResult(RESULT_OK, replyIntent);
        finish();
    }

    /* =========================================================
       CANCELAR
       ========================================================= */

    private void cancel() {
        setResult(RESULT_CANCELED);
        finish();
    }

    /* =========================================================
       VALIDACIONES
       ========================================================= */

    private boolean isFormValid() {
        return !TextUtils.isEmpty(mRowMatricula.getText())
                && !TextUtils.isEmpty(mDescripcionText.getText())
                && !TextUtils.isEmpty(mPrecio.getText())
                && mTipo.getCheckedRadioButtonId() != -1;
    }

    private double parsePrecio(String raw) {
        try {
            double precio = Double.parseDouble(raw);
            if (precio <= 0) {
                Toast.makeText(this, "El precio debe ser mayor que 0", Toast.LENGTH_LONG).show();
                throw new NumberFormatException();
            }
            return precio;
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Precio inválido", Toast.LENGTH_LONG).show();
            throw e;
        }
    }
}
