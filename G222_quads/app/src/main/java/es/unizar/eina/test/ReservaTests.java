package es.unizar.eina.test;

import android.app.Application;
import android.util.Log;

import es.unizar.eina.G222_quads.database.Quad_Reserva_RoomDataBase;
import es.unizar.eina.G222_quads.database.Reserva;
import es.unizar.eina.G222_quads.database.ReservaRepository;

public class ReservaTests {
    private Quad_Reserva_RoomDataBase db;
    private ReservaRepository reservaRepo;

    public ReservaTests(Application app) {
        Quad_Reserva_RoomDataBase db =
                Quad_Reserva_RoomDataBase.getDatabase(app);
        this.reservaRepo = new ReservaRepository(app);
    }
    public void testLimpiar() {
        reservaRepo.deleteAll();
        Log.d("TEST_RESERVA", "Fin limpieza");

    }
    public void testInsertReserva() {
        // Insert válido
        try {
            Reserva q = new Reserva("CL_001", "612458920", 01012026, false, 03012026, false);
            reservaRepo.insert(q);
            Log.d("TEST_RESERVA", "Insert válido");

        } catch (Throwable t) {
            Log.d("TEST_RESERVA", "(Insert debería funcionar: " + t.getMessage());
        }

        // Insert con nombre vacia de cliente
        try {
            Reserva q = new Reserva("", "612458920", 10012026, false, 13012026, false);
            reservaRepo.insert(q);
            Log.d("TEST_RESERVA", "Insert nombre inválido");

        } catch (Throwable t) {
            Log.d("TEST_RESERVA", "Nombre inválido correctamente rechazado");
        }

        // Insert con número de teléfono vacio
        try {
            Reserva q = new Reserva("CL_002", "", 10012026, false,13012026, false);
            reservaRepo.insert(q);
            Log.d("TEST_RESERVA", "Insert inválido");

        } catch (Throwable t) {
            Log.d("TEST_RESERVA", "Teléfono inválido correctamente rechazado");
        }

        // Insert con número de teléfono inválido
        try {
            Reserva q = new Reserva("CL_003", "a5463b", 10012026, false, 13012026, false);
            reservaRepo.insert(q);
            Log.d("TEST_RESERVA", "Insert inválido");

        } catch (Throwable t) {
            Log.d("TEST_RESERVA", "Teléfono inválido correctamente rechazado");
        }

        // Insert con fecha de recogida invalida (formato)
        try {
            Reserva q = new Reserva("CL_004", "612458920", 10012026, false, 13022026, false);
            reservaRepo.insert(q);
            Log.d("TEST_RESERVA", "Insert inválido");

        } catch (Throwable t) {
            Log.d("TEST_RESERVA", "Fecha recogida inválido correctamente rechazado");
        }

        // Insert con fecha de recogida invalida
        try {
            Reserva q = new Reserva("CL_005", "612458920", 10002026, false, 13022026, false);
            reservaRepo.insert(q);
            Log.d("TEST_RESERVA", "Insert inválido");

        } catch (Throwable t) {
            Log.d("TEST_RESERVA", "Fecha recogiga inválido correctamente rechazado");
        }

        // Insert con fecha de recogida invalida (> fecha devolucion)
        try {
            Reserva q = new Reserva("CL_006", "612458920", 13012026, false, 10012026, false );
            reservaRepo.insert(q);
            Log.d("TEST_RESERVA", "Insert inválido");

        } catch (Throwable t) {
            Log.d("TEST_RESERVA", "Fecha recogiga inválido correctamente rechazado");
        }

        // Las pruebas de fecha de devolución serían equivalentes a las de recogida
    }

    public void testDeleteReserva() {
        try{
            Reserva q = new Reserva("DELETE_001", "612458920", 01012026, false, 03012026, false);
            reservaRepo.insert(q);
            reservaRepo.delete(q);
            Log.d("TEST_RESERVA", "Delete válido");

        }catch(Throwable t){
            Log.d("TEST_RESERVA", "(Delete debería funcionar: " + t.getMessage());

        }

        try{
            Reserva q = new Reserva("DELETE_001", "612458920", 01012026, false, 03012026, false);
            reservaRepo.delete(q);
            Log.d("TEST_RESERVA", "Delete inválido");

        }catch(Throwable t){
            Log.d("TEST_RESERVA", "Delete inválido correctamente rechazado");

        }
    }

    public void testVolumenReserva(){
        String prefijo = "CL_";
        int sufijo = 0;
        Reserva r = new Reserva(prefijo + sufijo, "612458920", 10012026, false, 13012026, false);
        reservaRepo.insert(r);
        sufijo ++;
        Log.d("TEST_RESERVA", "Inicio prueba de volumen con volumen ("+ sufijo +")");

        try {
            while (sufijo < 20001) {
                r = new Reserva(prefijo + sufijo, "612458920", 10012026, false, 13012026, false);
                reservaRepo.insert(r);
                sufijo ++;

            }
        }catch(Throwable t){
            Log.d("TEST_RESERVA", "Fallo en prueba de volumen con volumen ("+ sufijo +")");
        }

        Log.d("TEST_RESERVA", "Fin prueba de volumen con volumen ("+ sufijo +")");

    }


}
