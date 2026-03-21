package es.unizar.eina.test;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QuadTestRunner {

    private final Context context;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public QuadTestRunner(Context context) {
        this.context = context;
    }

    /** Ejecuta las pruebas unitarias (insert, update, delete) */
    public void runUnitTests(Application app) {
        Toast.makeText(context, "Inicio prueba ", Toast.LENGTH_SHORT).show();
        executor.execute(() -> {
            QuadTests tests = new QuadTests(app);

            tests.testInsertQuad();

            tests.testUpdateQuad();

            tests.testDeleteQuad();

            android.os.Handler handler =
                    new android.os.Handler(android.os.Looper.getMainLooper());

            handler.post(() ->
                    Toast.makeText(context,
                            "Pruebas unitarias COMPLETADAS",
                            Toast.LENGTH_LONG
                    ).show()
            );
        });

    }

    /** Ejecuta la prueba de volumen */
    public void runVolumeTest(Application app) {
        Toast.makeText(context, "Inicio prueba ", Toast.LENGTH_SHORT).show();
        executor.execute(() -> {
            try {
                QuadTests tests = new QuadTests(app);
                tests.testVolumenQuads();
            } catch (Throwable t) {
                Log.e("OVERLOAD_TEST", "Fallo en sobrecarga", t);
            }

            android.os.Handler handler =
                    new android.os.Handler(android.os.Looper.getMainLooper());

            handler.post(() ->
                    Toast.makeText(context,
                            "Prueba de volumen COMPLETADA",
                            Toast.LENGTH_LONG
                    ).show()
            );
        });


    }

    /** Ejecuta la prueba de sobrecarga */
    public void runOverloadTest(Application app ) {
        Toast.makeText(context, "Inicio prueba ", Toast.LENGTH_SHORT).show();
        executor.execute(() -> {
            QuadTests tests = new QuadTests(app);

            tests.testSobrecargaVolumenQuads();

            android.os.Handler handler =
                    new android.os.Handler(android.os.Looper.getMainLooper());

            handler.post(() ->
                    Toast.makeText(context,
                            "Pruebas de sobrecarga COMPLETADA",
                            Toast.LENGTH_LONG
                    ).show()
            );
        });

    }

    public void runLimpiar(Application app) {
        Toast.makeText(context, "Iniciando limpieza", Toast.LENGTH_SHORT).show();
        executor.execute(() -> {
            QuadTests tests = new QuadTests(app);
            tests.testLimpiar();
        });
        Toast.makeText(context, "Limpieza completada", Toast.LENGTH_SHORT).show();

    }
}
