package es.unizar.eina.g222_quads.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    // Duración de un bloque horario de reserva: mañana o tarde
    private static final long MEDIO_DIA_MILLIS = 12L * 60 * 60 * 1000;

    /* =====================================
       FORMATO HUMANO (UI)
       ===================================== */

    /**
     * Convierte una fecha en millis a formato legible para el usuario.
     *
     * @param millis fecha en milisegundos
     * @return fecha en formato legible: dd/mm/yyyy
     */
    public static String formatearFecha(long millis) {
        SimpleDateFormat sdf =
                new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(new Date(millis));
    }

    /**
     * Convierte una fecha + horario a formato legible para el usuario.
     *
     * @param millis  fecha en milisegundos
     * @param esTarde false = mañana, true = tarde
     * @return fecha en formato legible: dd/mm/yyyy (horario)
     */
    public static String formatearFechaHorario(long millis, boolean esTarde) {
        return formatearFecha(millis) + (esTarde ? " (tarde)" : " (mañana)");
    }

    /**
     * Convierte dos fechas a un rango legible para el usuario
     *
     * @param inicioMillis  fecha de inicio de una reserva en millis
     * @param inicioEsTarde horario (mañana/tarde) de inicio de una reserva
     * @param finMillis     fecha de fin de una reserva en millis
     * @param finEsTarde    horario (mañana/tarde) de fin de una reserva
     * @return rango en formato legible: Del dd/mm/aaaa (horario) al dd/mm/aaaa (horario)
     */
    public static String formatearRango(long inicioMillis, boolean inicioEsTarde,
                                        long finMillis, boolean finEsTarde) {
        return "Del " + formatearFechaHorario(inicioMillis, inicioEsTarde)
                + " al " + formatearFechaHorario(finMillis, finEsTarde);
    }

    /* =====================================
       UTILIDADES DE FECHA (LÓGICA)
       ===================================== */

    /**
     * Normaliza una fecha al inicio del día (00:00:00.000)
     *
     * @param millis fecha en millis
     * @return fecha normalizada al inicio del día
     */
    public static long inicioDelDia(long millis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millis);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    /**
     * Convierte fecha + horario a un instante comparable en millis.
     * false = mañana -> inicio del día
     * true = tarde -> inicio del día + 12h
     *
     * @param millis  fecha de una reserva en millis
     * @param esTarde horario (mañana/tarde) de una reserva
     * @return instante comparable en millis
     */
    public static long obtenerInicioHorario(long millis, boolean esTarde) {
        long inicioDia = inicioDelDia(millis);
        return inicioDia + (esTarde ? MEDIO_DIA_MILLIS : 0L);
    }

    /**
     * Devuelve el final del horario (exclusivo)
     * Para cálculos de solapamiento.
     *
     * @param millis  fecha de una reserva en millis
     * @param esTarde horario (mañana/tarde) de una reserva
     * @return instante en millis
     */
    public static long obtenerFinHorario(long millis, boolean esTarde) {
        return obtenerInicioHorario(millis, esTarde) + MEDIO_DIA_MILLIS;
    }

    /**
     * Devuelve la fecha y horario actual (mañana/tarde) en millis en función de la hora real
     *
     * @param ahoraMillis fecha actual en millis
     * @return instante en millis
     */
    public static long obtenerHorarioActual(long ahoraMillis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(ahoraMillis);

        boolean esTarde = c.get(Calendar.HOUR_OF_DAY) >= 12;
        return obtenerInicioHorario(ahoraMillis, esTarde);
    }

    /**
     * Calcula número de días entre dos fechas (inclusive).
     * mañana -> tarde = 0.5
     * mañana -> mañana día siguiente = 1.0
     *
     * @param fechaInicio   fecha de inicio en millis
     * @param inicioEsTarde horario de inicio (mañana/tarde)
     * @param fechaFin      fecha de fin en millis
     * @param finEsTarde    horario de fin (mañana/tarde)
     * @return número de días entre las dos fechas
     */
    public static double calcularDiasReserva(long fechaInicio, boolean inicioEsTarde,
                                             long fechaFin, boolean finEsTarde) {
        long ini = obtenerInicioHorario(fechaInicio, inicioEsTarde);
        long fin = obtenerInicioHorario(fechaFin, finEsTarde);
        long slots = (fin - ini) / MEDIO_DIA_MILLIS;
        return slots / 2.0;
    }

    /**
     * Comprueba si un rango de fechas es válido
     * (no permite mismo día y mismo horario)
     *
     * @param inicio        fecha de inicio de una reserva en millis
     * @param inicioEsTarde horario de inicio de una reserva
     * @param fin           fecha de fin de una reserva en millis
     * @param finEsTarde    horario de fin de una reserva
     * @return true si el rango es válido, false en caso contrario
     */
    public static boolean rangoValido(long inicio, boolean inicioEsTarde,
                                      long fin, boolean finEsTarde) {
        long _ini = obtenerInicioHorario(inicio, inicioEsTarde);
        long _fin = obtenerInicioHorario(fin, finEsTarde);
        return _fin > _ini;
    }

}