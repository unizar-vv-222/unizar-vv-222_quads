package es.unizar.eina.g222_quads.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import es.unizar.eina.g222_quads.utils.DateUtils;

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

    @ColumnInfo(name = "recogidaComparable")
    private long recogidaComparable;

    @ColumnInfo(name = "fechaDevolucion")
    private long fechaDevolucion;

    @ColumnInfo(name = "horaDevolucion")
    private boolean horaDevolucion;

    @ColumnInfo(name = "devolucionComparable")
    private long devolucionComparable;

    @NonNull
    @ColumnInfo(name = "precioTotal")
    private double precioTotal;

    /**
     * Crea una reserva con todos sus campos.
     *
     * @param nombreCliente   Nombre del cliente que ha realizado la reserva.
     * @param movilCliente    Número móvil del cliente que ha realizado la reserva.
     * @param fechaRecogida   Fecha de recogida de los quads alquilados.
     * @param horaRecogida    Horario (mañana/tarde) de recogida de los quads alquilados.
     * @param fechaDevolucion Fecha de devolución de los quads alquilados.
     * @param horaDevolucion  Horario (mañana/tarde) de devolución de los quads alquilados.
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

        actualizarComparables();

        this.precioTotal = 0.0;
    }

    /**
     * Devuelve el id de la reserva
     */
    public int getId() {
        return this.id;
    }

    /**
     * Permite actualizar el id de la reserva
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Devuelve el nombre del cliente de la reserva
     */
    public String getNombreCliente() {
        return this.nombreCliente;
    }

    /**
     * Permite actualizar el nombre del cliente de la reserva
     */
    public void setNombreCliente(@NonNull String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    /**
     * Devuelve el móvil del cliente de la reserva
     */
    public String getMovilCliente() {
        return this.movilCliente;
    }

    /**
     * Permite actualizar el móvil del cliente de la reserva
     */
    public void setMovilCliente(@NonNull String movilCliente) {
        this.movilCliente = movilCliente;
    }

    /**
     * Devuelve la fecha de recogida de los quads reservados
     */
    public long getFechaRecogida() {
        return this.fechaRecogida;
    }

    /**
     * Permite actualizar la fecha de recogida de los quads reservados
     */
    public void setFechaRecogida(long fechaRecogida) {
        this.fechaRecogida = fechaRecogida;
    }

    /**
     * Devuelve el horario de recogida de los quads reservados
     */
    public boolean getHoraRecogida() {
        return this.horaRecogida;
    }

    /**
     * Permite actualizar el horario de recogida de los quads reservados
     */
    public void setHoraRecogida(boolean horaRecogida) {
        this.horaRecogida = horaRecogida;
    }

    /**
     * Devuelve la clave temporal de recogida
     */
    public long getRecogidaComparable() {
        return this.recogidaComparable;
    }

    /**
     * Permite actualizar la clave temporal de recogida
     */
    public void setRecogidaComparable(long recogidaComparable) {
        this.recogidaComparable = recogidaComparable;
    }

    /**
     * Devuelve la fecha de devolución de los quads reservados
     */
    public long getFechaDevolucion() {
        return this.fechaDevolucion;
    }

    /**
     * Permite actualizar la fecha de devolución de los quads reservados
     */
    public void setFechaDevolucion(long fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    /**
     * Devuelve el horario de devolución de los quads reservados
     */
    public boolean getHoraDevolucion() {
        return this.horaDevolucion;
    }

    /**
     * Permite actualizar el horario de devolución de los quads reservados
     */
    public void setHoraDevolucion(boolean horaDevolucion) {
        this.horaDevolucion = horaDevolucion;
    }

    /**
     * Devuelve la clave temporal comparable de devolución
     */
    public long getDevolucionComparable() {
        return this.devolucionComparable;
    }

    /**
     * Permite actualizar la clave temporal comparable de devolución
     */
    public void setDevolucionComparable(long devolucionComparable) {
        this.devolucionComparable = devolucionComparable;
    }

    /**
     * Actualiza los comparables de la reserva
     */
    public void actualizarComparables() {
        this.recogidaComparable = DateUtils.slotToMillis(fechaRecogida, horaRecogida);
        this.devolucionComparable = DateUtils.endExclusiveMillis(fechaDevolucion, horaDevolucion);
    }

    /**
     * Devuelve el precio de la reserva
     */
    public double getPrecioTotal() {
        return this.precioTotal;
    }

    /**
     * Permite actualizar el precio de la reserva
     */
    public void setPrecioTotal(double precioTotal) {
        this.precioTotal = precioTotal;
    }

}
