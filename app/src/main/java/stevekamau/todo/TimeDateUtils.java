package stevekamau.todo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by steve on 10/15/17.
 */

public class TimeDateUtils {


    public static String getTodayDate(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date());
    }

    public static String formatDate(String dateTimeString, String currentFormat, String endFormat) {
        SimpleDateFormat format = new SimpleDateFormat(currentFormat, Locale.getDefault());
        Date newDate = null;
        try {
            newDate = format.parse(dateTimeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        format = new SimpleDateFormat(endFormat, Locale.getDefault());
        return format.format(newDate);
    }
}
