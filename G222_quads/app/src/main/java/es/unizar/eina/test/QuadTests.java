package es.unizar.eina.test;

import android.app.Application;
import android.util.Log;

import es.unizar.eina.g222_quads.database.Quad;
import es.unizar.eina.g222_quads.database.QuadRepository;
import es.unizar.eina.g222_quads.database.Quad_Reserva_RoomDataBase;


public class QuadTests {

    private Quad_Reserva_RoomDataBase db;
    private QuadRepository quadRepo;


    public QuadTests(Application app) {
        Quad_Reserva_RoomDataBase db =
                Quad_Reserva_RoomDataBase.getDatabase(app);
        this.quadRepo = new QuadRepository(app);
    }

    public void testLimpiar() {
        quadRepo.deleteAll();
    }
    public void testInsertQuad() {
        // Insert válido Monoplaza
        try {
            Quad q = new Quad("1234ABC", true, 65.0, "Rojo");
            quadRepo.insert(q);
            Log.d("TEST_QUAD", "Insert válido Monoplaza");

        } catch (Throwable t) {
            Log.d("TEST_QUAD", "(Insert Monoplaza debería funcionar: " + t.getMessage());
        }

        // Insert válido Biplaza
        try {
            Quad q = new Quad("1235ABC", false, 65.0, "Rojo");
            quadRepo.insert(q);
            Log.d("TEST_QUAD", "Insert válido Biplaza");
        } catch (Throwable t) {
            Log.d("TEST_QUAD", ("Insert Biplaza debería funcionar: " + t.getMessage()));
        }

        // Precio inválido
        try {
            Quad q = new Quad("1238ABC", false, -5.0, "Rojo");
            quadRepo.insert(q);
            Log.d("TEST_QUAD", "Insert precio inválido");
        } catch (Throwable t) {
            Log.d("TEST_QUAD", "Precio inválido correctamente rechazado");
        }

        // Descripción vacía
        try {
            Quad q = new Quad("1237ABC", false, 65.0, "");
            quadRepo.insert(q);
            Log.d("TEST_QUAD", "Insert descripción inválida");
        } catch (Throwable t) {
            Log.d("TEST_QUAD", "Descripción inválida correctamente rechazada");
        }

        // Matrícula inválida
        try {
            Quad q = new Quad("A24B", true, 65.0, "Rojo");
            quadRepo.insert(q);
        } catch (Throwable t) {
            Log.d("TEST_QUAD", "Matrícula inválida correctamente rechazada");
        }

        // Matrícula repetida
        try {
            Quad q1 = new Quad("4444ABC", false, 20.0, "Verde");
            quadRepo.insert(q1);

            Quad q2 = new Quad("4444ABC", true, 65.0, "Rojo");
            quadRepo.insert(q2);
        } catch (Throwable t) {
            Log.d("TEST_QUAD", "Matrícula repetida correctamente rechazada");
        }

        Log.d("TEST_QUAD", "Test completo de insert finalizado");
    }


    public void testUpdateQuad() {
        // Insertamos un quad inicial válido
        Quad q = new Quad("1234ABC", true, 65.0, "Rojo");
        quadRepo.insert(q);

        // Actualización Monoplaza -> Biplaza
        try {
            q.setTipo(false); // de monoplaza a biplaza
            quadRepo.update(q);

            q.setTipo(true); // de biplaza a monoplaza
            quadRepo.update(q);

            Log.d("TEST_QUAD", "Actualización de tipo OK");
        } catch (Throwable t) {
            Log.d("TEST_QUAD", ("Fallo al actualizar tipo: " + t.getMessage()));
        }

        // Intento de actualizar un quad que no existe
        try {
            Quad qNoExiste = new Quad("3333ABC", true, 65.0, "No existe");
            quadRepo.update(qNoExiste);
            Log.d("TEST_QUAD", "Actualización de quad inexistente correctamente no realizada");
        } catch (Throwable t) {
            Log.d("TEST_QUAD", ("Excepción inesperada al actualizar quad inexistente: " + t.getMessage()));
        }
        /*
        // Poner null como tipo (solo si tipo es Boolean)
        try {
            q.setTipo(null);
            quadRepo.update(q);
            Log.d("TEST_QUAD", "Actualizar tipo a null debería fallar");
        } catch (Throwable t) {
            Log.d("TEST_QUAD", "Tipo null correctamente rechazado");
        }
        */


        // Poner null como descripción
        try {
            q.setTipo(true); // volvemos a tipo válido
            q.setDescripcion(null);
            quadRepo.update(q);
        } catch (Throwable t) {
            Log.d("TEST_QUAD", "Descripción null correctamente rechazada");
        }

        // Poner precio negativo
        try {
            q.setDescripcion("Válida"); // volvemos a descripción válida
            q.setPrecio(-20.0);
            quadRepo.update(q);
        } catch (Throwable t) {
            Log.d("TEST_QUAD", "Precio negativo correctamente rechazado");
        }

        Log.d("TEST_QUAD", "Test completo de update finalizado");
    }


    public void testDeleteQuad() {
        // Insertamos un quad válido que luego vamos a borrar
        Quad q = new Quad("1234ABC", true, 65.0, "Para borrar");
        try {
            quadRepo.insert(q);

        } catch (Throwable t) {
            Log.d("TEST_QUAD", ("Excepción al insertar quad para borrar: " + t.getMessage()));
        }

        // Borrar quad que existe
        try {
            quadRepo.deleteByMatricula("1234ABC");
            Log.d("TEST_QUAD", "Delete de quad existente OK");
        } catch (Throwable t) {
            Log.d("TEST_QUAD", "Fallo al borrar quad existente: " + t.getMessage());
        }

        // Intentar borrar quad que no existe
        try {
            quadRepo.deleteByMatricula("3333ABC");
            Log.d("TEST_QUAD", "Delete de quad inexistente correctamente no realizado");
        } catch (Throwable t) {
            Log.d("TEST_QUAD", "Excepción inesperada al borrar quad inexistente: " + t.getMessage());
        }

        Log.d("TEST_QUAD", "Test completo de delete finalizado");
    }

    public void testVolumenQuads() {
        int num = 0;
        try {
            String matricula;
            while ( num < 101 ){
                // Matrícula: 4 dígitos + "AAA"
                 matricula = String.format("%04dAAA", num);

                Quad q = new Quad(
                        matricula,
                        true,             //Monoplaza
                        10.00,
                        "Quad volumen " + num      // Descripción
                );

                quadRepo.insert(q);

                num ++;
            }
            Log.d("TEST_QUAD", "Prueba de volumen completada correctamente");
        } catch (Throwable t) {
            Log.d("TEST_QUAD", "Fallo en prueba de volumen con volumen ("+ num +")");
        }
    }

    private String generarCadena(int longitud) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < longitud; i++) {
            sb.append('A');
        }
        return sb.toString();
    }

    public void testSobrecargaVolumenQuads() {
        int[] longitudes = {10, 50, 100, 500, 1000, 5000};

        for (int len : longitudes) {
            Log.d("TEST_QUAD", "Prueba de sobrecarga con long = " + len);
            Quad quad = new Quad("1111ABC",
                    true,
                    10.0,
                    generarCadena(len)
            );
            quadRepo.insert(quad);
        }
    }

}
