package es.unizar.eina.G222_quads.database;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import es.unizar.eina.G222_quads.utils.DateUtils;

@Database(entities = {Quad.class, Reserva.class, ReservaQuadCascos.class}, version = 7, exportSchema = false)
public abstract class Quad_Reserva_RoomDataBase extends RoomDatabase {

    public abstract QuadDao quadDao();
    public abstract ReservaDao reservaDao();
    public abstract ReservaQuadCascosDao reservaQuadCascosDao();

    private static volatile Quad_Reserva_RoomDataBase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;

    // Executor compartido para ambas tablas
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static Quad_Reserva_RoomDataBase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (Quad_Reserva_RoomDataBase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    Quad_Reserva_RoomDataBase.class, "quad_reserva_database")
                            .fallbackToDestructiveMigration()
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriteExecutor.execute(() -> {

                // Población inicial de Quads
                QuadDao qDao = INSTANCE.quadDao();
                qDao.insert(new Quad("0000XXX", false, 5.0, "Quad básico"));
                qDao.insert(new Quad("1111YYY", true, 10.0, "Quad avanzado"));

                // Población inicial de Reservas
                ReservaDao rDao = INSTANCE.reservaDao();

                long fechaRecogida = DateUtils.dateToMillis(2023, 10, 1);
                long fechaDevolucion = DateUtils.dateToMillis(2023, 10, 5);

                rDao.insert(new Reserva("Cliente Ejemplo", "600123456",
                        fechaRecogida, false, fechaDevolucion, false));
                ReservaQuadCascosDao rqDao = INSTANCE.reservaQuadCascosDao();
                rqDao.insert(new ReservaQuadCascos(1, "0000XXX", 1, 75.50));
            });
        }
    };
}