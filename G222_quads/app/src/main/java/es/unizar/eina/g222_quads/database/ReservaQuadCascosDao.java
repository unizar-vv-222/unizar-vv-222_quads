package es.unizar.eina.g222_quads.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

/**
 * Data Access Object (DAO) para acceder a la tabla ReservaQuadCascos
 * Define las operaciones básicas de inserción, actualización, borrado
 * y la consulta para obtener todos los quads seleccionados para una reserva.
 */
@Dao
public interface ReservaQuadCascosDao {

    /**
     * Inserta una relación de quad-reserva en la base de datos.
     * @param reservaQuadCascos relación de quad-reserva a insertar
     */
    @Insert()
    void insert(ReservaQuadCascos reservaQuadCascos);

    /**
     * Inserta una lista de relaciones de quad-reserva en la base de datos.
     * @param reservaQuadCascos lista de relaciones de quad-reserva a insertar
     */
    @Insert()
    void insertAll(List<ReservaQuadCascos> reservaQuadCascos);

    /**
     * Elimina las relaciones quad-reserva asociadas a una reserva de la base de datos.
     * @param reservaId id de la reserva
     */
    @Query("DELETE FROM reserva_quad_cascos WHERE reservaId = :reservaId")
    void deleteByReserva(int reservaId);

    /**
     * Elimina una relación de quad-reserva de la base de datos.
     * @param reservaId id de la reserva
     * @param matricula matrícula del quad
     */
    @Query("DELETE FROM reserva_quad_cascos WHERE reservaId = :reservaId AND matriculaQuad = :matricula")
    void delete(int reservaId, String matricula);

    /**
     * Recupera todas las relaciones quad-reserva asociadas a una reserva.
     * Para observar (UI).
     * @param reservaId id de la reserva
     * @return lista observable (LiveData) de relaciones quad-reserva
     */
    @Query("SELECT * FROM reserva_quad_cascos WHERE reservaId = :reservaId")
    LiveData<List<ReservaQuadCascos>> getByReservaLive(int reservaId);

    /**
     * Recupera todas las relaciones quad-reserva asociadas a una reserva.
     * Para lógica interna (background).
     * @param reservaId id de la reserva
     * @return lista de relaciones quad-reserva
     */
    @Query("SELECT * FROM reserva_quad_cascos WHERE reservaId = :reservaId")
    List<ReservaQuadCascos> getByReservaSync(int reservaId);

    /**
     * Actualiza el número de cascos de un quad en una reserva.
     * @param reservaId id de la reserva
     * @param matricula matrícula del quad
     * @param numCascos número de cascos a actualizar
     */
    @Query("UPDATE reserva_quad_cascos SET numCascos = :numCascos WHERE reservaId = :reservaId AND matriculaQuad = :matricula")
    void updateNumCascos(int reservaId, String matricula, int numCascos);

    /**
     * Calcula el precio diario de una reserva.
     * @param reservaId id de la reserva
     * @return precio diario de la reserva
     */
    @Query("SELECT SUM(precioOriginal) FROM reserva_quad_cascos WHERE reservaId = :reservaId")
    double getPrecioDiarioReserva(int reservaId);

}

