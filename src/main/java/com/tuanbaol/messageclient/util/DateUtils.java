package com.tuanbaol.messageclient.util;


import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * @author chenzesheng
 * @version 1.0
 * @since 2019.06.27
 */
public class DateUtils {
    public final static String YMDHMS = "ymdhms";
    public final static String YMDHM = "ymdhm";
    public final static String YMDH = "ymdh";
    public final static String YMD = "ymd";
    public final static String YM = "ym";
    public final static String Y = "y";

    private DateUtils() {
    }

    private static ThreadLocal<DateFormat> threadLocal =
            ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HHmmss"));

    private static ThreadLocal<Map<String, DateFormat>> threadLocalV2 =
            ThreadLocal.withInitial(() -> {
                Map<String, DateFormat> map = new HashMap<>();
                map.put(YMDHMS, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
                map.put(YMDHM, new SimpleDateFormat("yyyy-MM-dd HH:mm"));
                map.put(YMD, new SimpleDateFormat("yyyy-MM-dd"));
                return map;
            });

    public static long date2MillSecond(Date date) {
        try {
            SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dateTimeFormatter.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            String dateStr = dateTimeFormatter.format(date);
            Date converted = dateTimeFormatter.parse(dateStr);
            ZonedDateTime zonedDateTime = converted.toInstant().atZone(ZoneId.of("Asia/Shanghai"));
            return zonedDateTime.toInstant().toEpochMilli();
        } catch (Exception e) {
            return new Date().toInstant().toEpochMilli();
        }
    }

    public static String formatDate2ReadableStr(Date date) {
        return threadLocal.get().format(date);
    }

    public static String formatDateYmdhms(Date date) {
        return threadLocalV2.get().get(YMDHMS).format(date);
    }
    public static String formatDateYmdhm(Date date) {
        return threadLocalV2.get().get(YMDHM).format(date);
    }
    public static String formatDateYmd(Date date) {
        return threadLocalV2.get().get(YMD).format(date);
    }

    public static String formatNowYmdhms() {
        return formatDateYmdhms(new Date());
    }
    public static String formatNowYmdhm() {
        return formatDateYmdhm(new Date());
    }
    public static String formatNowYmd() {
        return formatDateYmd(new Date());
    }


    private static String TIME_ZONE = "GMT+8";

    public static void main(String[] args) {
//        showDateTime(Instant.now().toEpochMilli());

        long time = Instant.now().toEpochMilli();
//        long a1 = dayStart(time);
//        long a2 = dayEnd(time);
//        long a3 = weekStart(time);
//        long a4 = weekEnd(time);
//        long a5 = monthStart(time);
//        long a6 = monthEnd(time);
//        long a7 = yearStart(time);
//        long a8 = yearEnd(time);
//
//        System.out.println("currentTime  " + time);
//        System.out.println("currentTime  " + converMilli2TimeStr(time));
//        System.out.println("dayStart  " + a1);
//        System.out.println("dayStart  " + converMilli2TimeStr(a1));
//        System.out.println("dayEnd  " + a2);
//        System.out.println("dayEnd  " + converMilli2TimeStr(a2));
//        System.out.println("weekStart  " + a3);
//        System.out.println("weekStart  " + converMilli2TimeStr(a3));
//        System.out.println("weekEnd  " + a4);
//        System.out.println("weekEnd  " + converMilli2TimeStr(a4));
//        System.out.println("monthStart  " + a5);
//        System.out.println("monthStart  " + converMilli2TimeStr(a5));
//        System.out.println("monthEnd  " + a6);
//        System.out.println("monthEnd  " + converMilli2TimeStr(a6));
//        System.out.println("yearStart  " + a7);
//        System.out.println("yearStart  " + converMilli2TimeStr(a7));
//        System.out.println("yearEnd  " + a8);
//        System.out.println("yearEnd  " + converMilli2TimeStr(a8));
//        String aa = converMilli2TimeStr(time, "yyyy-MM-dd HH:mm:ss.SSS");
    }


//    public static void showDateTime(long time) {
//        Instant instant = Instant.ofEpochMilli(time);
//        LocalDateTime ldt =  LocalDateTime.ofInstant(instant, ZoneId.of("GMT+8"));
//        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        System.out.println("当前时间 " + ldt.format(dtf));
//
////        ldt = LocalDateTime.now();
//
//        // 当前天所在的月的月初
//        ldt = LocalDateTime.of(ldt.getYear(), ldt.getMonth(), 1, 0, 0,0);
//        System.out.println( "当前天所在的月的月初 " + ldt.format(dtf));
//        // 当前天所在的月的月末
//        ldt = LocalDateTime.of(ldt.getYear(), ldt.getMonth(), ldt.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth(), 23, 59,59);
//        System.out.println("前天所在的月的月末 " + ldt.format(dtf));
//
//        // 当前天的开始
//        ldt = LocalDateTime.of(ldt.getYear(), ldt.getMonth(), ldt.getDayOfMonth(), 0, 0, 0);
//        System.out.println("当前天的开始 " + ldt.format(dtf));
//        // 当前天的结束
//        ldt = LocalDateTime.of(ldt.getYear(), ldt.getMonth(), ldt.getDayOfMonth(), 23, 59, 59);
//        System.out.println("当前天的结束 " + ldt.format(dtf));
//
////        ldt =  LocalDateTime.of(2019, 9, 13, 12, 12,12);
//        // 当前天所在周的开始
////        System.out.println(ldt.getDayOfMonth());
////        System.out.println(ldt.getDayOfWeek().getValue());
//        ldt = ldt.minusDays((long) ldt.getDayOfWeek().getValue() - 1);
//        ldt = LocalDateTime.of(ldt.getYear(), ldt.getMonth(), ldt.getDayOfMonth(), 0,0,0);
//        System.out.println("当前天所在周的开始 " + ldt.format(dtf));
//        // 当前天所在的周结束
//        ldt = ldt.plusDays(6);
//        ldt = LocalDateTime.of(ldt.getYear(), ldt.getMonth(), ldt.getDayOfMonth(), 23,59,59);
//        System.out.println("当前天所在的周结束 " + ldt.format(dtf));
//
//        ldt = LocalDateTime.of(ldt.getYear(), 1, 1, 0,0,0);
//        System.out.println("当前天所在年开始 " + ldt.format(dtf));
//
//        ldt = LocalDateTime.of(ldt.getYear(), 12, 31, 23,59,59);
//        System.out.println("当前天所在年结束 " + ldt.format(dtf));
//
//    }

    /**
     * 一天的开始.
     *
     * @param timeStamp 时间戳
     * @return long
     */
    public static long dayStart(long timeStamp) {
        Instant instant = Instant.ofEpochMilli(timeStamp);
        LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZoneId.of(TIME_ZONE));
        ldt = LocalDateTime.of(ldt.getYear(), ldt.getMonth(), ldt.getDayOfMonth(), 0, 0, 0);
        return convertMilli(ldt);
    }

    /**
     * 一天的结束.
     *
     * @param timeStamp 时间戳
     * @return long
     */
    public static long dayEnd(long timeStamp) {
        Instant instant = Instant.ofEpochMilli(timeStamp);
        LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZoneId.of(TIME_ZONE));
        ldt = LocalDateTime.of(ldt.getYear(), ldt.getMonth(), ldt.getDayOfMonth(), 23, 59, 59);
        return convertMilli(ldt);
    }

    /**
     * 一月的开始.
     *
     * @param timeStamp 时间戳
     * @return long
     */
    public static long monthStart(long timeStamp) {
        Instant instant = Instant.ofEpochMilli(timeStamp);
        LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZoneId.of(TIME_ZONE));
        ldt = LocalDateTime.of(ldt.getYear(), ldt.getMonth(), 1, 0, 0, 0);
        return convertMilli(ldt);
    }

    /**
     * 一月的结束.
     *
     * @param timeStamp 时间戳
     * @return long
     */
    public static long monthEnd(long timeStamp) {
        Instant instant = Instant.ofEpochMilli(timeStamp);
        LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZoneId.of(TIME_ZONE));
        ldt = LocalDateTime.of(ldt.getYear(), ldt.getMonth(), ldt.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth(), 23, 59, 59);
        return convertMilli(ldt);
    }

    /**
     * 一周的开始.
     *
     * @param timeStamp 时间戳
     * @return long
     */
    public static long weekStart(long timeStamp) {
        Instant instant = Instant.ofEpochMilli(timeStamp);
        LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZoneId.of(TIME_ZONE));
        ldt = ldt.minusDays((long) ldt.getDayOfWeek().getValue() - 1);
        ldt = LocalDateTime.of(ldt.getYear(), ldt.getMonth(), ldt.getDayOfMonth(), 0, 0, 0);
        return convertMilli(ldt);
    }

    /**
     * 一周的结束.
     *
     * @param timeStamp 时间戳
     * @return long
     */
    public static long weekEnd(long timeStamp) {
        Instant instant = Instant.ofEpochMilli(weekStart(timeStamp));
        LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZoneId.of(TIME_ZONE));
        ldt = ldt.plusDays(6);
        ldt = LocalDateTime.of(ldt.getYear(), ldt.getMonth(), ldt.getDayOfMonth(), 23, 59, 59);
        return convertMilli(ldt);
    }

    /**
     * 一年的开始.
     *
     * @param timeStamp 时间戳
     * @return long
     */
    public static long yearStart(long timeStamp) {
        Instant instant = Instant.ofEpochMilli(timeStamp);
        LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZoneId.of(TIME_ZONE));
        ldt = LocalDateTime.of(ldt.getYear(), 1, 1, 0, 0, 0);
        return convertMilli(ldt);
    }

    /**
     * 一年的结束.
     *
     * @param timeStamp 时间戳
     * @return long
     */
    public static long yearEnd(long timeStamp) {
        Instant instant = Instant.ofEpochMilli(timeStamp);
        LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZoneId.of(TIME_ZONE));
        ldt = LocalDateTime.of(ldt.getYear(), 12, 31, 23, 59, 59);
        return convertMilli(ldt);
    }

    /**
     * 按北京时区把LocalDateTime类型时间转换为时间毫秒值.
     *
     * @param localDateTime localDateTime
     * @return long
     */
    public static long convertMilli(LocalDateTime localDateTime) {
        return localDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }

    /**
     * 把时间戳转换为yyyy-MM-dd HH:mm:ss格式的时间字符串.
     *
     * @param timeStamp 时间戳
     * @return String
     */
    public static String converMilli2TimeStr(long timeStamp) {
        Instant instant = Instant.ofEpochMilli(timeStamp);
        LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZoneId.of("GMT+8"));
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return ldt.format(dtf);
    }

    /**
     * 把时间戳转换为yyyy-MM-dd HH:mm:ss格式的时间字符串.
     *
     * @param timeStamp 时间戳
     * @param format    时间格式
     * @return String
     */
    public static String converMilli2TimeStr(long timeStamp, String format) {
        Instant instant = Instant.ofEpochMilli(timeStamp);
        LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZoneId.of("GMT+8"));
        if (StringUtils.isEmpty(format)) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(format);
        return ldt.format(dtf);
    }


}
