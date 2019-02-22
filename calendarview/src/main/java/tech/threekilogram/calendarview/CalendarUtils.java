package tech.threekilogram.calendarview;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Liujin 2019/2/22:15:57:27
 */
public class CalendarUtils {

      private static Calendar sCalendar = Calendar.getInstance();

      public static int monthDayCount ( int year, int month ) {

            sCalendar.set( year, month + 1, 1 );
            sCalendar.add( Calendar.DAY_OF_MONTH, -1 );
            return sCalendar.get( Calendar.DAY_OF_MONTH );
      }

      public static int dayOfWeek ( int year, int month, int day ) {

            sCalendar.set( year, month, day );
            return sCalendar.get( Calendar.DAY_OF_WEEK );
      }

      public static int getYear ( Date date ) {

            sCalendar.setTime( date );
            return sCalendar.get( Calendar.YEAR );
      }

      public static int getMonth ( Date date ) {

            sCalendar.setTime( date );
            return sCalendar.get( Calendar.MONTH );
      }

      public static Date getMonthByStep ( int year, int month, int offset ) {

            sCalendar.set( year, month, 1 );
            sCalendar.add( Calendar.MONTH, offset );
            return sCalendar.getTime();
      }
}
