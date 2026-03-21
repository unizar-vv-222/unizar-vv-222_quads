package es.unizar.eina.G222_quads.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    /* =====================================
       FORMATO HUMANO (UI)
       ===================================== */

    /**
     * Convierte millis a formato legible para el usuario.
     * Ejemplo: 12/03/2026
     */
    public static String toHumanDate(long millis) {
        SimpleDateFormat sdf =
                new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(new Date(millis));
    }

    public static String toHumanDateTime(long millis, boolean hora) {
        String date = toHumanDate(millis);
        return date + (hora ? " (tarde)" : " (mañana)");
    }

    /**
     * Convierte dos fechas a un rango legible.
     * Ejemplo: Del 12/03/2026 al 14/03/2026
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
     * (útil para comparar días sin horas)
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
    public static long daysBetween(long inicio, long fin) {
        long oneDay = 24L * 60 * 60 * 1000;
        return (normalizeToDay(fin) - normalizeToDay(inicio)) / oneDay + 1;
    }

    /**
     * Convierte día + horario a un instante comparable en millis
     * horario = mañana -> inicio del día
     * horario = tarde -> +12h
     * @param dayMillis
     * @param horario
     * @return instante en millis
     */
    public static long slotToMillis(long dayMillis, boolean horario) {
        long day = normalizeToDay(dayMillis);
        return day + (horario ? 12L * 60 * 60 * 1000 : 0L);
    }

    /**
     * Devuelve el final del horario (exclusivo)
     * Para cálculos de solaapamiento
     * @param dayMillis
     * @param horario
     * @return
     */
    public static long endExclusiveMillis(long dayMillis, boolean horario) {
        return slotToMillis(dayMillis, horario) + 12L * 60 * 60 * 1000;
    }

    public static boolean isRangeValid(long inicio, boolean inicioHorario,
                                       long fin, boolean finHorario) {
        long _ini = slotToMillis(inicio, inicioHorario);
        long _fin = slotToMillis(fin, finHorario);
        return _fin >= _ini;
    }
}
