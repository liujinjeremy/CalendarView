package tech.threekilogram.calendarviewlib;

/**
 * @author Liujin 2019/2/20:17:02:58
 */
public class Test {

      public static void main ( String[] args ) {

            WeekDay[] values = WeekDay.values();
            for( int i = 0; i < values.length; i++ ) {

                  System.out.println( values[ i ].toString() );
            }
      }

      public static enum WeekDay {

            SUNDAY( "Sunday" ),
            MONDAY( "Monday" ),
            TUESDAY( "Tuesday" ),
            WEDNESDAY( "Wednesday" ),
            THURSDAY( "Thursday" ),
            FRIDAY( "Friday" ),
            SATURDAY( "Saturday" );

            private static int sOffset = 1;

            private final String mDay;

            WeekDay ( String day ) {

                  mDay = day;
            }
      }
}
