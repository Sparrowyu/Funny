package com.sortinghat.funny.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

    /**
     * @return 2020-10-01和现在差距的天数
     */
    public static int getDateFromNetToProgressDay(String incomeValue) {
        try {
            SimpleDateFormat incomeSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            Date date = incomeSimpleDateFormat.parse(incomeValue);
            if (date != null) {
                Calendar nowCal = Calendar.getInstance();
                nowCal.setTime(new Date());//获取当前的Calendar
                nowCal.set(Calendar.HOUR_OF_DAY, 0);
                nowCal.set(Calendar.MINUTE, 0);
                nowCal.set(Calendar.SECOND, 0);
                nowCal.set(Calendar.MILLISECOND, 0);

                Calendar cal = Calendar.getInstance();
                cal.setTime(date);//获取传入的Calendar
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                return (int) ((nowCal.getTime().getTime() - cal.getTime().getTime()) / (1000 * 60 * 60 * 24));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * @return 获得今天的日期转成 20201203 格式
     */
    public static String getTodayDateStringToServer() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(new Date());
    }

    public static String getMillToTime(long milliSecond) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        date.setTime(milliSecond);
        return simpleDateFormat.format(date);

    }

    public static String getMillToTimeDay(long milliSecond) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        date.setTime(milliSecond);
        return simpleDateFormat.format(date);

    }

    public static String getMillToCurrentTime(long milliSecond) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        date.setTime(milliSecond);
        return simpleDateFormat.format(date);

    }

}
