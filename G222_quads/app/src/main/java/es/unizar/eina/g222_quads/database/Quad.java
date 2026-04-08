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


    @ColumnInfo(name = "tipo")
    private boolean tipo;

    @ColumnInfo(name = "precio")
    private double precio;

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
    public Quad(@NonNull String matricula,  boolean tipo, double precio, @NonNull String descripcion) {
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
    public boolean getTipo(){
        return this.tipo;
    }

    /** Permite actualizar el tipo del quad */
    public void setTipo(boolean tipo) { this.tipo = tipo; }

    /** Devuelve el precio del quad */
    public double getPrecio(){
        return this.precio;
    }

    /** Permite actualizar el precio del quad */
    public void setPrecio(Double precio) { this.precio = precio; }

    /** Devuelve la descripción del quad */
    public String getDescripcion() { return this.descripcion; }

    /** Permite actualizar la descripción del quad */
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

}
