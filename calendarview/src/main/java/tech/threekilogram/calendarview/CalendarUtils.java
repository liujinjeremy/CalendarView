package tech.threekilogram.calendarview;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Liujin 2019/2/22:15:57:27
 */
public class CalendarUtils {

      private static Calendar sCalendar = Calendar.getInstance();

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

      public static String getYearMonthFormat ( Date date ) {

            sCalendar.setTime( date );
            int year = sCalendar.get( Calendar.YEAR );
            int month = sCalendar.get( Calendar.MONTH );
            return String.format( "%d/%d", year, month + 1 );
      }

      public static Date getMonthByStep ( Date date, int offset ) {

            sCalendar.setTime( date );
            sCalendar.add( Calendar.MONTH, offset );
            return sCalendar.getTime();
      }

      public static void main ( String[] args ) {

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat format = new SimpleDateFormat( "yyyy年MM月dd日 HH:mm:ss" );
            calendar.setTime( new Date() );

            for( int i = 0; i < 12; i++ ) {
                  Date time = calendar.getTime();
                  System.out.println( monthDayCount( time ) );
                  System.out.println( format.format( time ) );
                  calendar.add( Calendar.MONTH, 1 );
            }
      }
}
