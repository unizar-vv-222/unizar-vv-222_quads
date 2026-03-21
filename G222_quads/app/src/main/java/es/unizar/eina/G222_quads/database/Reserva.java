package es.unizar.eina.G222_quads.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Entidad Room que representa una reserva.
 * Una reserva tiene un identificador único (clave primaria), un nombre de cliente, un móvil
 * de cliente, una fecha y horario (mañana/tarde) de recogida, una fecha y horario (mañana/tarde)
 * de devolución y un precio total.
 *
 * @author G222
 */@Entity(tableName = "reserva")
public class Reserva {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @NonNull
    @ColumnInfo(name = "nombreCliente")
    private String nombreCliente;

    @NonNull
    @ColumnInfo(name = "movilCliente")
    private String movilCliente;

    @ColumnInfo(name = "fechaRecogida")
    private long fechaRecogida;

    @ColumnInfo(name = "horaRecogida")
    private boolean horaRecogida;

    @ColumnInfo(name = "fechaDevolucion")
    private long fechaDevolucion;

    @ColumnInfo(name = "horaDevolucion")
    private boolean horaDevolucion;

    @NonNull
    @ColumnInfo(name = "precioTotal")
    private Double precioTotal;

    /**
     * Crea una reserva con todos sus campos.
     * @param nombreCliente Nombre del cliente que ha realizado la reserva.
     * @param movilCliente Número móvil del cliente que ha realizado la reserva.
     * @param fechaRecogida Fecha de recogida de los quads alquilados.
     * @param horaRecogida Horario (mañana/tarde) de recogida de los quads alquilados.
     * @param fechaDevolucion Fecha de devolución de los quads alquilados.
     * @param horaDevolucion Horario (mañana/tarde) de devolución de los quads alquilados.
     */
    public Reserva(@NonNull String nombreCliente, @NonNull String movilCliente,
                   long fechaRecogida, boolean horaRecogida,
                   long fechaDevolucion, boolean horaDevolucion) {

        this.nombreCliente = nombreCliente;
        this.movilCliente = movilCliente;
        this.fechaRecogida = fechaRecogida;
        this.horaRecogida = horaRecogida;
        this.fechaDevolucion = fechaDevolucion;
        this.horaDevolucion = horaDevolucion;
        this.precioTotal = 0.0;
    }

    /** Devuelve el id de la reserva */
    public int getId(){
        return this.id;
    }

    /** Permite actualizar el id de la reserva */
    public void setId(int id){
        this.id = id;
    }

    /** Devuelve el nombre del cliente de la reserva */
    public String getNombreCliente(){
        return this.nombreCliente;
    }

    /** Devuelve el móvil del cliente de la reserva */
    public String getMovilCliente(){
        return this.movilCliente;
    }

    /** Devuelve la fecha de recogida de los quads reservados */
    public long getFechaRecogida(){
        return this.fechaRecogida;
    }

    /** Devuelve el horario de recogida de los quads reservados */
    public boolean getHoraRecogida() { return this.horaRecogida; }

    /** Devuelve la fecha de devolución de los quads reservados */
    public long getFechaDevolucion() { return this.fechaDevolucion; }

    /** Devuelve el horario de devolución de los quads reservados */
    public boolean getHoraDevolucion() { return this.horaDevolucion; }

    /** Devuelve el precio de la reserva */
    public Double getPrecioTotal() { return this.precioTotal; }

    /** Permite actualizar el precio de la reserva */
    public void setPrecioTotal(Double precioTotal) { this.precioTotal = precioTotal; }

}
