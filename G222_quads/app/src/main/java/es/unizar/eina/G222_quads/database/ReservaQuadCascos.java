package es.unizar.eina.G222_quads.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(
        tableName = "reserva_quad_cascos",
        primaryKeys = {"reservaId", "matriculaQuad"},
        foreignKeys = {
                @ForeignKey(
                        entity = Reserva.class,
                        parentColumns = "id",
                        childColumns = "reservaId",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Quad.class,
                        parentColumns = "matricula",
                        childColumns = "matriculaQuad",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {
                @Index("reservaId"),
                @Index("matriculaQuad")
        }
)
public class ReservaQuadCascos {

    @ColumnInfo(name = "reservaId")
    private int reservaId;

    @NonNull
    @ColumnInfo(name = "matriculaQuad")
    private String matriculaQuad;

    @ColumnInfo(name="numCascos")
    private int numCascos;

    @NonNull
    @ColumnInfo(name="precioOriginal")
    private double precioOriginal;

    /**
     * Constructor de la clase ReservaQuadCascos
     * @param reservaId id de la reserva
     * @param matriculaQuad matricula del quad
     * @param numCascos numero de cascos
     * @param precioOriginal precio original del quad
     */
    public ReservaQuadCascos(int reservaId, @NonNull String matriculaQuad, int numCascos, @NonNull double precioOriginal) {
        this.reservaId = reservaId;
        this.matriculaQuad = matriculaQuad;
        this.numCascos = numCascos;
        this.precioOriginal = precioOriginal;
    }

    /**
     * Getter del id de la reserva
     * @return id de la reserva
     */
    public int getReservaId() {
        return reservaId;
    }

    /**
     * Getter de la matricula del quad
     * @return matricula del quad
     */
    @NonNull
    public String getMatriculaQuad() {
        return matriculaQuad;
    }

    /**
     * Getter del numero de cascos
     * @return numero de cascos
     */
    public int getNumCascos() { return numCascos; }

    /**
     * Getter del precio original del quad
     * @return precio original del quad
     */
    @NonNull
    public double getPrecioOriginal() { return precioOriginal; }

}
