package es.unizar.eina.test;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReservaTestRunner {

    private final Context context;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public ReservaTestRunner(Context context) {
        this.context = context;
    }

    /** Ejecuta las pruebas unitarias (insert, update, delete) */
    public void runUnitTests(Application app) {
        Toast.makeText(context, "Iniciando pruebas unitarias", Toast.LENGTH_SHORT).show();

        executor.execute(() -> {
            ReservaTests tests = new ReservaTests(app);
            tests.testInsertReserva();
            tests.testDeleteReserva();
        });
        Toast.makeText(context, "Pruebas unitarias completadas", Toast.LENGTH_SHORT).show();

    }

    /** Ejecuta la prueba de volumen */
    public void runVolumeTest(Application app) {
        Toast.makeText(context, "Iniciando prueba de volumen", Toast.LENGTH_SHORT).show();
        executor.execute(() -> {
            ReservaTests tests = new ReservaTests(app);
            tests.testVolumenReserva();
        });
        Toast.makeText(context, "Prueba de volumen completada", Toast.LENGTH_SHORT).show();

    }

    public void runLimpiar(Application app) {
        Toast.makeText(context, "Iniciando limpieza", Toast.LENGTH_SHORT).show();
        executor.execute(() -> {
            ReservaTests tests = new ReservaTests(app);
            tests.testLimpiar();
        });
        Toast.makeText(context, "Limpieza completada", Toast.LENGTH_SHORT).show();

    }


}
