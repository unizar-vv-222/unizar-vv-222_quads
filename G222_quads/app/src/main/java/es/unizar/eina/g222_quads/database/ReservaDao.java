package es.unizar.eina.g222_quads.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * Data Access Object (DAO) para acceder a la tabla Reserva.
 * Define las operaciones básicas de inserción, actualización, borrado
 * y la consulta para obtener todos las reservas ordenadas.
 */
@Dao
public interface ReservaDao {

    /**
     * Inserta una reserva en la base de datos.
     * Si ya existe la misma reserva se ignora y no se inserta.
     *
     * @param reserva reserva a insertar
     * @return id de fila insertada o -1 si se ha ignorado por conflicto
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Reserva reserva);

    /**
     * Actualiza los datos de una reserva existente.
     *
     * @param reserva reserva modificada
     * @return número de filas afectadas
     */
    @Update
    int update(Reserva reserva);

    /**
     * Elimina un reserva de la base de datos.
     *
     * @param reserva reserva a eliminar
     * @return número de filas afectadas
     */
    @Delete
    int delete(Reserva reserva);

    /**
     * Elimina todos las reservas de la tabla.
     */
    @Query("DELETE FROM reserva")
    int deleteAll();

    /**
     * Recupera el número de reservas de la tabla.
     */
    @Query("SELECT COUNT(*) FROM reserva")
    int getNumReservas();

    /**
     * Actualiza el precio de una reserva.
     *
     * @param reservaId id de la reserva
     * @param precio    precio a actualizar
     */
    @Query("UPDATE reserva SET precioTotal = :precio WHERE id = :reservaId ")
    void updatePrecio(int reservaId, double precio);

    /**
     * Recupera la reserva que conicida con el id pasado como parámetro.
     * @return reserva
     */
    @Query("SELECT * FROM reserva WHERE id = :reservaId ")
    Reserva getReserva(int reservaId);


    /**
     * Recupera todos las reservas ordenadas por nombreCliente.
     *
     * @return lista observable (LiveData) de reservas
     */
    @Query("SELECT * FROM reserva ORDER BY nombreCliente ASC")
    LiveData<List<Reserva>> getReservasOrderByNombre();

    /**
     * Recupera todos las reservas ordenadas por telefono.
     *
     * @return lista observable (LiveData) de reservas
     */
    @Query("SELECT * FROM reserva ORDER BY movilCliente ASC")
    LiveData<List<Reserva>> getReservasOrderByTelefono();

    /**
     * Recupera todos las reservas ordenadas por fecha de recogida,
     * teniendo en cuenta el horario.
     *
     * @return lista observable (LiveData) de reservas
     */
    @Query("SELECT * FROM reserva ORDER BY recogidaComparable ASC")
    LiveData<List<Reserva>> getReservasOrderByRecogida();

    /**
     * Recupera todos las reservas ordenadas por fecha de devolucion,
     * teniendo en cuenta el horario.
     *
     * @return lista observable (LiveData) de reservas
     */
    @Query("SELECT * FROM reserva ORDER BY devolucionComparable ASC")
    LiveData<List<Reserva>> getReservasOrderByDevolucion();

}

