package es.unizar.eina.G222_quads.ui.reservas;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import es.unizar.eina.G222_quads.R;
import es.unizar.eina.test.ReservaTestRunner;

public class TestReservaActivity extends AppCompatActivity {

    private ReservaTestRunner testRunner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_reserva);

        // Inicializamos el runner
        testRunner = new ReservaTestRunner(this);

        // Botón de pruebas unitarias
        Button btnUnitarias = findViewById(R.id.prueba_unitaria_reserva);
        btnUnitarias.setOnClickListener(v -> testRunner.runUnitTests(getApplication()));

        // Botón de pruebas de volumen
        Button btnVolumen = findViewById(R.id.prueba_volumen_reserva);
        btnVolumen.setOnClickListener(v -> testRunner.runVolumeTest(getApplication()));

        // Botón limpiar
        Button btnLimpiar = findViewById(R.id.limpiar_reserva);
        btnLimpiar.setOnClickListener(v -> testRunner.runLimpiar(getApplication()));

    }
}
