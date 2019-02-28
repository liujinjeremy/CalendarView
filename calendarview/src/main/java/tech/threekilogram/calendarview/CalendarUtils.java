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

      public static int getDayCountOfMonth ( Date date ) {

            sCalendar.setTime( date );
            sCalendar.add( Calendar.MONTH, 1 );
            sCalendar.set( Calendar.DAY_OF_MONTH, 1 );
            sCalendar.add( Calendar.DAY_OF_MONTH, -1 );
            return sCalendar.get( Calendar.DAY_OF_MONTH );
      }

      public static int getDayOfWeekAtMonthFirstDay ( Date date ) {

            sCalendar.setTime( getFirstDayOfMonth( date ) );
            return sCalendar.get( Calendar.DAY_OF_WEEK );
      }

      public static Date getFirstDayOfMonth ( Date date ) {

            sCalendar.setTime( date );
            sCalendar.set( Calendar.DAY_OF_MONTH, 1 );
            return sCalendar.getTime();
      }

      public static Date getDateByAddDay ( Date date, int offset ) {

            sCalendar.setTime( date );
            sCalendar.add( Calendar.DAY_OF_MONTH, offset );
            return sCalendar.getTime();
      }

      public static Date getDateByAddWeek ( Date date, int offset ) {

            sCalendar.setTime( date );
            sCalendar.add( Calendar.WEEK_OF_YEAR, offset );
            return sCalendar.getTime();
      }

      public static Date getDateByAddMonth ( Date date, int offset ) {

            sCalendar.setTime( date );
            sCalendar.add( Calendar.MONTH, offset );
            return sCalendar.getTime();
      }

      public static int getDayOfMonth ( Date date ) {

            sCalendar.setTime( date );
            return sCalendar.get( Calendar.DAY_OF_MONTH );
      }

      public static Date setDayOfMonth ( Date date, int dayOfMonth ) {

            if( dayOfMonth < 1 ) {
                  dayOfMonth = 1;
            }
            int countOfMonth = getDayCountOfMonth( date );
            if( dayOfMonth > countOfMonth ) {
                  dayOfMonth = countOfMonth;
            }
            sCalendar.setTime( date );
            sCalendar.set( Calendar.DAY_OF_MONTH, dayOfMonth );
            return sCalendar.getTime();
      }

      public static int getDayOfWeek ( Date date ) {

            sCalendar.setTime( date );
            return sCalendar.get( Calendar.DAY_OF_WEEK );
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

      public static void main ( String[] args ) {

            Date date = new Date();
            for( int i = 0; i < 5; i++ ) {
                  Date week = getDateByAddWeek( date, i );
                  System.out.println( getDateFormat( week ) );
            }
      }
}
