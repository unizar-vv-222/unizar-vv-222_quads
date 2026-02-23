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

    /**
     * Convierte dos fechas a un rango legible.
     * Ejemplo: Del 12/03/2026 al 14/03/2026
     */
    public static String toHumanRange(long inicio, long fin) {
        return "Del " + toHumanDate(inicio) + " al " + toHumanDate(fin);
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
}
