package es.unizar.eina.G222_quads.ui.quads;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import es.unizar.eina.G222_quads.R;
import es.unizar.eina.test.QuadTestRunner;

public class TestQuadActivity extends AppCompatActivity {

    private QuadTestRunner testRunner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_quad);

        // Inicializamos el runner
        testRunner = new QuadTestRunner(this);

        // Botón de pruebas unitarias
        Button btnUnitarias = findViewById(R.id.prueba_unitaria_quads);
        btnUnitarias.setOnClickListener(v -> testRunner.runUnitTests(getApplication()));

        // Botón de pruebas de volumen
        Button btnVolumen = findViewById(R.id.prueba_volumen_quads);
        btnVolumen.setOnClickListener(v -> testRunner.runVolumeTest(getApplication()));

        // Botón de pruebas de sobrecarga
        Button btnSobrecarga = findViewById(R.id.prueba_sobrecarga_quads);
        btnSobrecarga.setOnClickListener(v -> testRunner.runOverloadTest(getApplication()));

        Button btnLimpiar = findViewById((R.id.limpiar_quad));
        btnLimpiar.setOnClickListener(v -> testRunner.runLimpiar(getApplication()));

    }
}
