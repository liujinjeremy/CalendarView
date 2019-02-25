package tech.threekilogram.calendarview;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author Liujin 2019/2/22:15:57:27
 */
public class CalendarUtils {

      private static final Calendar         sCalendar = Calendar.getInstance();
      private static final SimpleDateFormat sFormat   = new SimpleDateFormat( "yyyy年MM月dd日 HH:mm:ss" );

      public static int monthDayCount ( Date date ) {

            sCalendar.setTime( date );
            sCalendar.add( Calendar.MONTH, 1 );
            sCalendar.set( Calendar.DAY_OF_MONTH, 1 );
            sCalendar.add( Calendar.DAY_OF_MONTH, -1 );
            return sCalendar.get( Calendar.DAY_OF_MONTH );
      }

      public static int weekOfMonthFirstDay ( Date date ) {

            sCalendar.setTime( date );
            sCalendar.set( Calendar.DAY_OF_MONTH, 1 );
            return sCalendar.get( Calendar.DAY_OF_WEEK );
      }

      public static Date firstDayOfMonth ( Date date ) {

            sCalendar.setTime( date );
            sCalendar.set( Calendar.DAY_OF_MONTH, 1 );
            return sCalendar.getTime();
      }

      public static Date getDayByStep ( Date date, int offset ) {

            sCalendar.setTime( date );
            sCalendar.add( Calendar.DAY_OF_MONTH, offset );
            return sCalendar.getTime();
      }

      public static int getDayOfMonth ( Date date ) {

            sCalendar.setTime( date );
            return sCalendar.get( Calendar.DAY_OF_MONTH );
      }

      public static String getYearMonthFormat ( Date date ) {

            sCalendar.setTime( date );
            int year = sCalendar.get( Calendar.YEAR );
            int month = sCalendar.get( Calendar.MONTH );
            return String.format( Locale.CHINA, "%d/%d", year, month + 1 );
      }

      public static String getDateFormat ( Date date ) {

            return sFormat.format( date );
      }

      public static Date getMonthByStep ( Date date, int offset ) {

            sCalendar.setTime( date );
            sCalendar.add( Calendar.MONTH, offset );
            return sCalendar.getTime();
      }

      public static Date updateDayOfMonth ( Date date, int dayOfMonth ) {

            sCalendar.setTime( date );
            int count = monthDayCount( date );
            if( dayOfMonth <= count ) {
                  sCalendar.set( Calendar.DAY_OF_MONTH, dayOfMonth );
            } else {
                  sCalendar.set( Calendar.DAY_OF_MONTH, count );
            }

            return sCalendar.getTime();
      }

      public static void main ( String[] args ) {

            Date date = updateDayOfMonth( new Date(), 31 );
            System.out.println( getDateFormat( date ) );
      }
}
