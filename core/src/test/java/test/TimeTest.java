package test;

import com.truthbean.Console;
import test.bean.Bean001;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author TruthBean
 * @since 0.5.5
 * Created on 2022/03/17 16:45.
 */
public class TimeTest {

    public static void main(String[] args) throws Exception {
        // localTime();
        // time();
        testMap();
    }

    public static void testMap() {
        Bean001 bean001 = new Bean001();
        bean001.setName("001");
        Map<Bean001, Object> map = new HashMap<>();
        map.put(bean001, new Object());
        System.out.println(map.get(bean001));
        bean001.setName("123");
        System.out.println(map.get(bean001));
    }

    public static void localTime() {
        LocalTime now = LocalTime.now();
        Console.info(now.toString());
        LocalTime begin = LocalTime.of(0, 0, 0);
        Console.info(begin.toString());
        LocalTime end = LocalTime.of(23, 59, 59);
        Console.info(end.toString());
        if (now.isAfter(begin) && now.isBefore(end)) {
            Console.info("是时候了");
        }
    }

    public static void time() throws ParseException {
        Time now = new Time(System.currentTimeMillis());
        Console.info(now.toString());
        Time begin = Time.valueOf(LocalTime.of(0, 0, 0));
        Time begin2 = Time.valueOf(LocalTime.of(0, 0, 0));
        Console.info(String.valueOf(begin2.equals(begin)));
        Console.info(begin.toString());
        Time end = Time.valueOf(LocalTime.of(23, 59, 59));
        Console.info(end.toString());
        if (now.after(begin) && now.before(end)) {
            Console.info("是时候了");
        }

        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        format.setLenient(true);
        Date date = format.parse("24:00:00");
        Time t = new Time(date.getTime());
        Console.info(t.toString());
        Console.info(String.valueOf(t.equals(begin)));
        Console.info(String.valueOf(t.getTime()));
        Console.info(String.valueOf(begin.getTime()));
        Console.info(String.valueOf(t.getTime() == begin.getTime()));
    }
}
