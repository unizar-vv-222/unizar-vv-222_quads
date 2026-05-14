package es.unizar.eina.g222_quads.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * Data Access Object (DAO) para acceder a la tabla Quad.
 * Define las operaciones básicas de inserción, actualización, borrado
 * y la consulta para obtener todos los quads ordenados.
 */
@Dao
public interface QuadDao {

    /**
     * Inserta un quad en la base de datos.
     * Si ya existe la misma matrícula se ignora.
     *
     * @param quad quad a insertar
     * @return id de fila insertada o -1 si se ha ignorado por conflicto
     */
    @Insert
    long insert(Quad quad);

    /**
     * Actualiza los datos de un quad existente.
     *
     * @param quad quad modificado
     * @return número de filas afectadas
     */
    @Update
    int update(Quad quad);

    /**
     * Elimina un quad por su matrícula de la base de datos.
     *
     * @param matricula del quad a eliminar
     * @return número de filas afectadas
     */
    @Query("DELETE FROM quad WHERE matricula = :matricula")
    int deleteByMatricula(String matricula);

    @Query("DELETE FROM quad")
    int deleteAll();


    /**
     * Recupera todos los quads ordenados por matrícula.
     *
     * @return lista observable (LiveData) de quads
     */
    @Query("SELECT * FROM quad ORDER BY matricula ASC")
    LiveData<List<Quad>> getQuadsOrderByMatricula();

    // Ordenar por tipo
    @Query("SELECT * FROM quad ORDER BY tipo ASC")
    LiveData<List<Quad>> getQuadsOrderByTipo();

    // Ordenar por precio
    @Query("SELECT * FROM quad ORDER BY precio ASC")
    LiveData<List<Quad>> getQuadsOrderByPrecio();


    @Query("SELECT * FROM quad q WHERE q.matricula NOT IN (SELECT rq.matriculaQuad FROM reserva_quad_cascos rq INNER JOIN reserva r ON r.id = rq.reservaId WHERE r.recogidaComparable < :devolucionComparable AND r.devolucionComparable > :recogidaComparable )")
    LiveData<List<Quad>> getAvailableQuads(long recogidaComparable, long devolucionComparable);

    @Query("SELECT * FROM quad q WHERE q.matricula NOT IN (SELECT rq.matriculaQuad FROM reserva_quad_cascos rq INNER JOIN reserva r ON r.id = rq.reservaId WHERE r.id != :reservaId AND r.recogidaComparable < :devolucionComparable AND r.devolucionComparable > :recogidaComparable ) ")
    LiveData<List<Quad>> getAvailableQuadsExcludingReserva(long recogidaComparable, long devolucionComparable, int reservaId);

    @Query("SELECT * FROM quad WHERE matricula = :matricula LIMIT 1")
    Quad getQuadByMatricula(String matricula);


    //Pillar numero de quads
    @Query("SELECT COUNT(*) FROM quad ")
    int getNumQuad();

}

