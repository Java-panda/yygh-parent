package com.atguigu.yygh.common.utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

import java.util.Date;

public class DateTimeUtil {
    public static void main(String[] args) {
        System.out.println(getDayOfWeekByDate(new Date()));
    }
    public static String getDayOfWeekByDate(Date date){
        DateTime dateTime = new DateTime(date);
        int index = dateTime.dayOfWeek().get();
        switch (index){
            case 1:
                return "星期一";
            case 2:
                return "星期二";
            case 3:
                return "星期三";
            case 4:
                return "星期四";
            case 5:
                return "星期五";
            case 6:
                return "星期六";
            case 7:
                return "星期日";
            default:
                return null;
        }
    }
}
