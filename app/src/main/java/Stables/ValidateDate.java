package Stables;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidateDate {

    public boolean validate(String date1){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false);
        try {

            // if not valid, it will throw ParseException
            Date date = sdf.parse(date1);

            // current date after 24 months
            Calendar currentDateAfter3Months = Calendar.getInstance();
            currentDateAfter3Months.add(Calendar.MONTH, 24);

            // current date before 24 months
            Calendar currentDateBefore3Months = Calendar.getInstance();
            currentDateBefore3Months.add(Calendar.MONTH, -24);

            if (date.before(currentDateAfter3Months.getTime())
                    && date.after(currentDateBefore3Months.getTime())) {

                //ok everything is fine, date in range
                return true;

            } else {

                return false;

            }

        } catch (ParseException e) {

            e.printStackTrace();
            return false;
        }
    }
}
