package es.unizar.eina.g222_quads.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

/**
 * Entidad Room que representa un quad disponible en la tienda.
 * Un quad tiene una matrícula (clave primaria), un tipo, un precio y una descripción.
 *
 * @author G222
 */@Entity(tableName = "quad")
public class Quad {
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "matricula")
    private String matricula;

    @NonNull
    @ColumnInfo(name = "tipo")
    private Boolean tipo;

    @NonNull
    @ColumnInfo(name = "precio")
    private Double precio;

    @NonNull
    @ColumnInfo(name = "descripcion")
    private String descripcion;

    /**
     * Crea un quad con todos sus campos.
     * @param matricula Matrícula del quad. Es la clave primaria.
     * @param tipo Tipo del quad (true/false según criterio del modelo).
     * @param precio Precio de alquiler del quad.
     * @param descripcion Descripción textual del quad.
     */
    public Quad(@NonNull String matricula, @NotNull Boolean tipo, @NonNull Double precio, @NonNull String descripcion) {
        this.matricula = matricula;
        this.tipo = tipo;
        this.precio = precio;
        this.descripcion = descripcion;
    }

    /** Devuelve la matrícula del quad */
    public String getMatricula(){
        return this.matricula;
    }

    /** Permite actualizar la matrícula del quad */
    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    /** Devuelve el tipo del quad */
    public Boolean getTipo(){
        return this.tipo;
    }

    /** Permite actualizar el tipo del quad */
    public void setTipo(Boolean tipo) { this.tipo = tipo; }

    /** Devuelve el precio del quad */
    public Double getPrecio(){
        return this.precio;
    }

    /** Permite actualizar el precio del quad */
    public void setPrecio(Double precio) { this.precio = precio; }

    /** Devuelve la descripción del quad */
    public String getDescripcion() { return this.descripcion; }

    /** Permite actualizar la descripción del quad */
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

}
