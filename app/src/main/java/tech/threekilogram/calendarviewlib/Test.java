package tech.threekilogram.calendarviewlib;

import java.util.Calendar;

/**
 * @author Liujin 2019/2/20:17:02:58
 */
public class Test {

      public static void main ( String[] args ) {

            int i = CalendarUtils.monthDayCount( 2019, 2 );
            System.out.println( i );

            int i1 = CalendarUtils.dayOfWeek( 2019, 2, 1 );
            System.out.println( i1 );

            int i2 = CalendarUtils.dayOfWeek( 2019, 2, 28 );
            System.out.println( i2 );
      }

      public static class CalendarUtils {

            private static Calendar sCalendar = Calendar.getInstance();

            public static int monthDayCount ( int year, int month ) {

                  sCalendar.set( year, month, 1 );
                  sCalendar.add( Calendar.DAY_OF_MONTH, -1 );
                  return sCalendar.get( Calendar.DAY_OF_MONTH );
            }

            public static int dayOfWeek ( int year, int month, int day ) {

                  sCalendar.set( year, month, day );
                  return sCalendar.get( Calendar.DAY_OF_WEEK );
            }
      }
}
