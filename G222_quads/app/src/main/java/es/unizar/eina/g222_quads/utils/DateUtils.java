package es.unizar.eina.g222_quads.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    private static final long HALF_DAY_MILLIS = 12L * 60 * 60 * 1000;

    /* =====================================
       FORMATO HUMANO (UI)
       ===================================== */

    /**
     * Convierte millis a formato legible para el usuario
     * @param millis fecha de una reserva en millis
     * @return fecha en formato legible: dd/mm/aaaa
     */
    public static String toHumanDate(long millis) {
        SimpleDateFormat sdf =
                new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(new Date(millis));
    }

    /**
     * Convierte millis y horario a formato legible para el usuario
     * @param millis fecha de una reserva en millis
     * @param hora horario (mañana/tarde)
     * @return fecha en formato legible: dd/mm/aaaa (horario)
     */
    public static String toHumanDateTime(long millis, boolean hora) {
        String date = toHumanDate(millis);
        return date + (hora ? " (tarde)" : " (mañana)");
    }

    /**
     * Convierte dos fechas a un rango legible para el usuario
     * @param inicio fecha de inicio de una reserva en millis
     * @param inicioHorario horario de inicio de una reserva
     * @param fin fecha de fin de una reserva en millis
     * @param finHorario horario de fin de una reserva
     * @return rango en formato legible: Del dd/mm/aaaa (horario) al dd/mm/aaaa (horario)
     */
    public static String toHumanRange(long inicio, boolean inicioHorario,
                                      long fin, boolean finHorario) {
        return "Del " + toHumanDateTime(inicio, inicioHorario) + " al " + toHumanDateTime(fin, finHorario);
    }

    /* =====================================
       UTILIDADES DE FECHA (LÓGICA)
       ===================================== */

    /**
     * Normaliza una fecha a las 00:00:00.000
     * (para comparar días sin horas)
     */
    public static long normalizeToDay(long millis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millis);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    /**
     * Calcula número de días entre dos fechas (inclusive).
     */
    public static double daysBetween(long fechaInicio, boolean horaInicio,
                                   long fechaFin, boolean horaFin) {
        long ini = slotToMillis(fechaInicio, horaInicio);
        long fin = slotToMillis(fechaFin, horaFin);
        long slots = (fin - ini) / HALF_DAY_MILLIS;
        return slots / 2.0;
    }

    /**
     * Convierte día + horario a un instante comparable en millis
     * horario = mañana -> inicio del día
     * horario = tarde -> +12h
     * @param dayMillis fecha de una reserva en millis
     * @param horario horario (mañana/tarde) de una reserva
     * @return instante en millis
     */
    public static long slotToMillis(long dayMillis, boolean horario) {
        long day = normalizeToDay(dayMillis);
        return day + (horario ? HALF_DAY_MILLIS : 0L);
    }

    /**
     * Devuelve el final del horario (exclusivo)
     * Para cálculos de solaapamiento
     * @param dayMillis fecha de una reserva en millis
     * @param horario horario (mañana/tarde) de una reserva
     * @return instante en millis
     */
    public static long endExclusiveMillis(long dayMillis, boolean horario) {
        return slotToMillis(dayMillis, horario) + HALF_DAY_MILLIS;
    }

    /**
     * Comprueba si un rango de fechas es válido
     * @param inicio fecha de inicio de una reserva en millis
     * @param inicioHorario horario de inicio de una reserva
     * @param fin fecha de fin de una reserva en millis
     * @param finHorario horario de fin de una reserva
     * @return true si el rango es válido, false en caso contrario
     */
    public static boolean isRangeValid(long inicio, boolean inicioHorario,
                                       long fin, boolean finHorario) {
        long _ini = slotToMillis(inicio, inicioHorario);
        long _fin = slotToMillis(fin, finHorario);
        return _fin > _ini;
    }

    //Para no poder reservar en el pasado
    public static long getTodayStart() {
        return normalizeToDay(System.currentTimeMillis());
    }

}
