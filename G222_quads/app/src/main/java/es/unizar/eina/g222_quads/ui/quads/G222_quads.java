package es.unizar.eina.g222_quads.ui.quads;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.android.material.button.MaterialButton;

import es.unizar.eina.g222_quads.R;
import es.unizar.eina.g222_quads.database.QuadRepository;
import es.unizar.eina.g222_quads.database.ReservaRepository;
import es.unizar.eina.g222_quads.ui.BaseActivity;

import es.unizar.eina.g222_quads.ui.reservas.G222_ReservasList;
import es.unizar.eina.g222_quads.ui.reservas.ReservaViewModel;
import es.unizar.eina.g222_quads.ui.reservas.TestReservaActivity;

import androidx.lifecycle.ViewModelProvider;

/**
 * Actividad principal de la aplicación.
 * Muestra el menú principal con acceso a la gestión de Quads y Reservas.
 * Lanza las actividades correspondientes a cada módulo mediante botones.
 */

public class G222_quads extends BaseActivity {


    private QuadViewModel mQuadViewModel;
    private ReservaViewModel mReservaViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Carga el diseño de los botones
        setContentView(R.layout.activity_g222_main);

        // Configura el botón 'QUADS' para lanzar la gestión de Quads
        MaterialButton quadButton = findViewById(R.id.quad);
        quadButton.setOnClickListener(view -> {
            Intent intent = new Intent(G222_quads.this, G222_QuadsList.class);
            startActivity(intent);
        });

        // Configura el botón 'RESERVA' para lanzar la gestión de Reservas
        MaterialButton reservaButton = findViewById(R.id.reserva);
        reservaButton.setOnClickListener(view -> {
            Intent intent = new Intent(G222_quads.this, G222_ReservasList.class);
            startActivity(intent);
        });

        // Configura el botón 'TEST RESERVAS' para lanzar los test de Reservas
        Button test_reservaButton = findViewById(R.id.test_reservas);
        test_reservaButton.setOnClickListener(view -> {
            Intent intent = new Intent(G222_quads.this, TestReservaActivity.class);
            startActivity(intent);
        });

        // Configura el botón 'TEST QUADS' para lanzar los test de Quads
        Button test_quadButton = findViewById(R.id.test_quads);
        test_quadButton.setOnClickListener(view -> {
            Intent intent = new Intent(G222_quads.this, TestQuadActivity.class);
            startActivity(intent);
        });
    }



    public QuadRepository getQuadRespositoryMain(){
        mQuadViewModel = new ViewModelProvider(this).get(QuadViewModel.class);
        return mQuadViewModel.getQuadRepository();
    }

    public ReservaRepository getReservaRepositoryMain(){
        mReservaViewModel = new ViewModelProvider(this).get(ReservaViewModel.class);
        return mReservaViewModel.getReservaRepository();
    }

}
