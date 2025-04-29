package org.playmore.common.util;


import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

/**
 * @author zhangpeng
 * @version 1.0
 * @date 2025-04-30 0:07
 * @description TODO
 */
public class TimeHelper {
    public final static long SECOND_MS = 1000L;
    public final static long MINUTE_MS = 60 * 1000L;
    public final static long DAY_MS = 24 * 60 * 60 * 1000L;
    public final static int MINUTE_S = 60;
    public final static int DAY_S = 24 * 60 * 60;
    public final static int HOUR_S = 60 * 60;
    public final static int HALF_HOUR_S = 30 * 60;
    public final static int HALF_HOUR_MS = 30 * 60 * 1000;
    public final static int WEEK_SECOND = 7 * 24 * 60 * 60;
    public static final ZoneId SYSTEM_ZONE = ZoneId.systemDefault();

    public static final int MINUTE = 60;

    public static int getCurrentSecond() {
        return (int) (System.currentTimeMillis() / SECOND_MS);
    }

    public static int getCurrentMinute() {
        return (int) (System.currentTimeMillis() / MINUTE_MS);
    }

    public static boolean isSameWeek(Date beginDate, Date endDate) {
        Calendar begin = Calendar.getInstance();
        begin.setTime(beginDate);
        Calendar end = Calendar.getInstance();
        end.setTime(endDate);

        // 换算beginDate的周一时间
        int beginDayOfWeek = begin.get(Calendar.DAY_OF_WEEK);
        if (beginDayOfWeek == 1) {
            begin.add(Calendar.DAY_OF_YEAR, -6);
        } else if (beginDayOfWeek > 2) {
            begin.add(Calendar.DAY_OF_YEAR, 2 - beginDayOfWeek);
        }

        // 换算endDate的周一时间
        int endDayOfWeek = end.get(Calendar.DAY_OF_WEEK);
        if (endDayOfWeek == 1) {
            end.add(Calendar.DAY_OF_YEAR, -6);
        } else if (endDayOfWeek > 2) {
            end.add(Calendar.DAY_OF_YEAR, 2 - endDayOfWeek);
        }
        return ((end.get(Calendar.YEAR) == begin.get(Calendar.YEAR)) && (end.get(Calendar.DAY_OF_YEAR) == begin.get(Calendar.DAY_OF_YEAR)));
    }


    public static int getCurrentDay() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.YEAR) * 10000 + (c.get(Calendar.MONTH) + 1) * 100 + c.get(Calendar.DAY_OF_MONTH);
    }

    public static int getCurrentDay0() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.YEAR) * 1000000 + (c.get(Calendar.MONTH) + 1) * 10000 + c.get(Calendar.DAY_OF_MONTH) * 100;
    }

    public static int getCurrentWeek() {
        Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        return c.get(Calendar.YEAR) * 100 + c.get(Calendar.WEEK_OF_YEAR);
    }

    public static int getCurrentHour() {
        return getHour();
    }

    public static int getHour(int second) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(second * SECOND_MS));
        return c.get(Calendar.HOUR_OF_DAY);
    }

    public static boolean isMonday() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek == 2;
    }

    public static boolean isTuesday() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek == Calendar.TUESDAY;
    }

    public static boolean isDayOfWeek(int dayOfWeek) {
        Calendar calendar = Calendar.getInstance();
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
        return weekDay == dayOfWeek;
    }

    /**
     * 是否是星期五
     */
    public static boolean isFriday() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek == Calendar.FRIDAY;
    }

    /**
     * 返回今天是星期几，按中国习惯，星期一返回1，星期天返回7
     *
     * @return
     */
    public static int getCNDayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (weekDay == 0) {
            weekDay = 7;
        }
        return weekDay;
    }

    /**
     * 返回今天是星期几，按中国习惯，星期一返回1，星期天返回7
     *
     * @param date
     * @return
     */
    public static int getCNDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (weekDay == 0) {
            weekDay = 7;
        }
        return weekDay;
    }

    /**
     * 返回日历的星期, 按照日历习惯, 星期天返回1, 星期六返回7
     *
     * @param weekDay
     * @return
     */
    public static int getCalendarDayOfWeek(int weekDay) {
        if (weekDay == 7) {
            weekDay = 0;
        }
        return weekDay + 1;
    }

    /**
     * 返回中国的星期, 按照中国习惯, 星期天返回7, 星期一返回1
     *
     * @param weekDay 日历习惯
     * @return
     */
    public static int getCalendarDayOfChWeek(int weekDay) {
        weekDay = weekDay - 1;
        if (weekDay == 0) {
            weekDay = 7;
        }
        return weekDay;
    }

    /**
     * 基础星期转换成Num
     *
     * @param weekDay
     * @return
     */
    public static int getWeekEnConverNum(String weekDay) {
        int weekNum = 0;
        switch (weekDay) {
            case "MON":
                weekNum = 1;
                break;
            case "TUE":
                weekNum = 2;
                break;
            case "WED":
                weekNum = 3;
                break;
            case "THU":
                weekNum = 4;
                break;
            case "FRI":
                weekNum = 5;
                break;
            case "SAT":
                weekNum = 6;
                break;
            case "SUN":
                weekNum = 7;
                break;
        }
        return weekNum;
    }

    public static int getDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.YEAR) * 10000 + (c.get(Calendar.MONTH) + 1) * 100 + c.get(Calendar.DAY_OF_MONTH);
    }

    public static int getDay(long second) {
        return getDay(new Date(second * 1000L));
        // Calendar c = Calendar.getInstance();
        // c.setTimeInMillis(second * 1000);
        // int d = c.get(Calendar.YEAR) * 10000 + (c.get(Calendar.MONTH) + 1) *
        // 100 + c.get(Calendar.DAY_OF_MONTH);
        // return d;
    }

    public static Date getDate(long second) {
        return new Date(second * 1000L);
    }

    public static Date getDateByStamp(int sec) {
        return new Date(sec * 1000L);
    }

    public static int getMonthAndDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return (c.get(Calendar.MONTH) + 1) * 10000 + (c.get(Calendar.DAY_OF_MONTH)) * 100;
    }

    public static boolean isTimeSecond(int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        int h = calendar.get(Calendar.HOUR_OF_DAY);
        int m = calendar.get(Calendar.MINUTE);
        int s = calendar.get(Calendar.SECOND);
        return h == hour && m == minute && s == second;
    }

    public static boolean isTime(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        int h = calendar.get(Calendar.HOUR_OF_DAY);
        int m = calendar.get(Calendar.MINUTE);
        return h == hour && m == minute;
    }

    public static int getSecond(int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, 0);
        return (int) (calendar.getTimeInMillis() / 1000);
    }

    /**
     * 获取若干天后的，某时分秒的时间，从今天到明天算一天
     *
     * @param addDays
     * @param hour
     * @param minute
     * @param second
     * @return
     */
    public static int getSomeDayAfter(int addDays, int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + addDays);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, 0);
        return (int) (calendar.getTimeInMillis() / 1000);
    }

    /**
     * 获取某个时间点若干天后的，某时分秒的时间
     *
     * @param origin
     * @param addDays
     * @param hour
     * @param minute
     * @param second
     * @return
     */
    public static int getSomeDayAfter(Date origin, int addDays, int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(origin);
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + addDays);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, 0);
        return (int) (calendar.getTimeInMillis() / 1000);
    }

    public static int getHour() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public static int getMinute() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MINUTE);
    }

    /**
     * 获取下个小时的整点时间
     *
     * @return
     */
    public static int getNextHourTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 1);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return (int) (calendar.getTimeInMillis() / 1000);
    }

    /**
     * 获取离现在最近的整点时间，如果当前时间是整点，直接返回当前时间，否则返回下一个小时的整点时间
     *
     * @return
     */
    public static int getNearlyHourTime() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        if (second != 0 || minute != 0) {
            calendar.set(Calendar.HOUR_OF_DAY, hour + 1);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
        }
        calendar.set(Calendar.MILLISECOND, 0);
        return (int) (calendar.getTimeInMillis() / 1000);
    }

    /**
     * 周日20点15到周六19:30之间
     *
     * @return
     */
    public static boolean isThisWeekSaturday1930ToSunday2015() {
        Calendar c = Calendar.getInstance();

        // 判断今天是否周日
        c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY); // 周日
        c.set(Calendar.HOUR_OF_DAY, 20);
        c.set(Calendar.MINUTE, 15);
        c.set(Calendar.SECOND, 0);
        int s1 = (int) (c.getTime().getTime() / SECOND_MS);

        // 本周六19.30
        c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY); // 获取周六的
        c.set(Calendar.HOUR_OF_DAY, 19);
        c.set(Calendar.MINUTE, 30);
        c.set(Calendar.SECOND, 0);
        int s2 = (int) (c.getTime().getTime() / SECOND_MS);

        int cc = getCurrentSecond();

        return s1 <= cc && cc <= s2;
    }

    /**
     * 是否小于本周六19.30
     *
     * @return
     */
    public static boolean isLessThanThisWeekSaturday1930() {
        Calendar c = Calendar.getInstance();
        // 本周六19.30
        c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY); // 获取周六的
        c.set(Calendar.HOUR_OF_DAY, 19);
        c.set(Calendar.MINUTE, 30);
        c.set(Calendar.SECOND, 0);
        int s = (int) (c.getTime().getTime() / SECOND_MS);
        int cc = getCurrentSecond();
        return cc < s;
    }

    /**
     * 是否小于本周四20:00
     *
     * @return
     */
    public static boolean isLessThanThisWeekWednesday2000() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY); // 获取周四的
        c.set(Calendar.HOUR_OF_DAY, 20);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        int s = (int) (c.getTime().getTime() / SECOND_MS);
        int cc = getCurrentSecond();
        return cc < s;
    }

    /**
     * 当前时间是否大于今天的19:30
     *
     * @return
     */
    public static boolean isMoreThan1930() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 19);
        c.set(Calendar.MINUTE, 30);
        c.set(Calendar.SECOND, 0);
        int s = (int) (c.getTime().getTime() / SECOND_MS);
        int cc = getCurrentSecond();
        return cc > s;
    }

    /**
     * 获取该天的凌晨时刻（秒）
     *
     * @param currentSecond
     * @return
     */
    public static int getTodayZone(int currentSecond) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentSecond * 1000L);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return (int) (calendar.getTimeInMillis() / 1000);
    }

    public static int getTodayZone() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return (int) (calendar.getTimeInMillis() / 1000);
    }

    public static Date getTodayZoneDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getTomorrowZoneDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date time = calendar.getTime();
        time.setTime(time.getTime() + TimeHelper.DAY_MS);
        return time;
    }

    /**
     * 获取该天的最晚时刻（秒）
     *
     * @param currentSecond
     * @return
     */
    public static int getEndOfDay(int currentSecond) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(currentSecond * 1000L);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        return (int) (c.getTimeInMillis() / 1000);
    }

    public static int getTomorrowZone() {
        return getTodayZone() + DAY_S;
    }

    /**
     * 判断时间是否本周
     *
     * @param dayTime
     * @return
     */
    public static boolean isThisWeek(int dayTime) {
        Calendar c = Calendar.getInstance();

        // 判断是否周日
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

        if (dayOfWeek == Calendar.SUNDAY) {
            c.add(Calendar.WEEK_OF_YEAR, -1);
        }

        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // 获取本周一的
        int mondayTime = c.get(Calendar.YEAR) * 10000 + (c.get(Calendar.MONTH) + 1) * 100
                + c.get(Calendar.DAY_OF_MONTH);

        c.add(Calendar.WEEK_OF_YEAR, 1);
        c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY); // 获取本周日的
        int sundayTime = c.get(Calendar.YEAR) * 10000 + (c.get(Calendar.MONTH) + 1) * 100
                + c.get(Calendar.DAY_OF_MONTH);

        return dayTime >= mondayTime && dayTime <= sundayTime;
    }

    /**
     * 判断是否周日
     *
     * @return
     */
    public static boolean isSunDay() {
        Calendar c = Calendar.getInstance();
        // 判断是否周日
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek == Calendar.SUNDAY;
    }

    /**
     * 获取本周一
     *
     * @return 返回格式:yyyyMMdd
     */
    public static int getThisWeekMonday() {
        // 判断若是星期天的话,则需要-1周
        Calendar c = Calendar.getInstance();
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

        if (dayOfWeek == Calendar.SUNDAY) {
            c.add(Calendar.WEEK_OF_YEAR, -1);
        }

        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // 获取本周一的
        return c.get(Calendar.YEAR) * 10000 + (c.get(Calendar.MONTH) + 1) * 100 + c.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取下周一凌晨时间
     *
     * @return
     */
    public static Date getLastWeekMonday() {
        Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        int currentWeekOfYear = c.get(Calendar.WEEK_OF_YEAR);
        //如果月份是12月，且求出来的周数是第一周，说明该日期实质上是这一年的第53周，也是下一年的第一周
        if (c.get(Calendar.MONTH) >= Calendar.DECEMBER && currentWeekOfYear <= 1) {
            currentWeekOfYear += 52;
        }
        c.set(Calendar.WEEK_OF_YEAR, currentWeekOfYear + 1);
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // 获取下周一
        return c.getTime();
    }

    /**
     * 获取本周日
     *
     * @return 返回格式:yyyyMMdd
     */
    public static int getThisWeekSunday() {
        Calendar c = Calendar.getInstance();
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

        if (dayOfWeek == Calendar.SUNDAY) {
        } else {
            c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY); // 获取本周日的
            c.add(Calendar.WEEK_OF_YEAR, 1);
        }

        return c.get(Calendar.YEAR) * 10000 + (c.get(Calendar.MONTH) + 1) * 100 + c.get(Calendar.DAY_OF_MONTH);
    }

    public static Date getDateZeroTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * date的second秒以后
     *
     * @param date
     * @param second
     * @return
     */
    public static int afterSecondTime(Date date, int second) {
        return (int) (date.getTime() / SECOND_MS + second);
    }

    public static Date beforeSecondTime(Date date, int second) {
        return new Date(date.getTime() - second * 1000l);
    }

    public static Date beforeMinuteTime(Date date, int minute) {
        return new Date(date.getTime() - minute * MINUTE_MS);
    }

    public static Date beforeDayDate(Date date, int day) {
        return new Date(date.getTime() - day * DAY_MS);
    }

    public static Date afterDayDate(Date date, int day) {
        return new Date(date.getTime() + day * DAY_MS);
    }

    public static Date afterSecondTime1(Date date, int second) {
        return new Date(date.getTime() + second * SECOND_MS);
    }

    /**
     * 获取这个月的第几周
     *
     * @param date
     * @return
     */
    public static int getWeekOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY); // 周1位第一天
        calendar.setTime(date);
        return calendar.get(Calendar.WEEK_OF_MONTH);
    }

    /**
     * 获取某个时间的第几周时间
     *
     * @param date
     * @param week
     * @return
     */
    public static Date getDayOfWeekByDate(Date date, int week) {
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.DAY_OF_WEEK, getCalendarDayOfWeek(week));
        return cal.getTime();
    }

    /**
     * 获取前几天或后几天的时间
     *
     * @param date    原时间
     * @param addDays 天数
     * @param hour    小时
     * @param minute  分钟
     * @param second  秒
     * @return 变动后的Date
     */
    public static Date getSomeDayAfterOrBefore(Date date, int addDays, int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + addDays);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date secondToDate(int second) {
        return new Date(second * 1000L);
    }

    public static int dateToSecond(Date date) {
        return (int) (date.getTime() / 1000);
    }

    /**
     * 当前时间加上指定天数后转换成毫秒值返回
     *
     * @param time
     * @return
     */
    public static int getNextDaySecond(int time) {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        //+1今天的时间加一天
        calendar.add(Calendar.DAY_OF_MONTH, +time);
        date = calendar.getTime();
        return (int) (date.getTime() / 1000);
    }

    public static int getWeekByCron(String cronWeek) {////MON TUE WED THU FRI SAT SUN
        if ("MON".equals(cronWeek)) {
            return Calendar.MONDAY;
        } else if ("TUE".equals(cronWeek)) {
            return Calendar.TUESDAY;
        } else if ("WED".equals(cronWeek)) {
            return Calendar.WEDNESDAY;
        } else if ("THU".equals(cronWeek)) {
            return Calendar.THURSDAY;
        } else if ("FRI".equals(cronWeek)) {
            return Calendar.FRIDAY;
        } else if ("SAT".equals(cronWeek)) {
            return Calendar.SATURDAY;
        } else {
            return Calendar.SUNDAY;
        }
    }

    public static int getDayEndStamp() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 0);
        return (int) (c.getTimeInMillis() / SECOND_MS);
    }

    public static int getDayLostSecond() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        Date now = new Date();
        return (int) ((now.getTime() - c.getTimeInMillis()) / SECOND_MS);
    }

    /**
     * 与当前时间是否是同一天
     */
    public static boolean isSameDay(int second) {
        return getTodayZone(second) == getTodayZone(TimeHelper.getCurrentSecond());
    }

    /**
     * 获取下次该星期的凌晨时间（秒）
     * 如果当前星期等于目标星期  那么返回下周该星期的时间
     *
     * @param week 中国习惯
     * @return
     */
    public static int getAfterWeekTime(int week) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.setFirstDayOfWeek(Calendar.MONDAY);

        if (getCalendarDayOfChWeek(c.get(Calendar.DAY_OF_WEEK)) >= week) {
            int currentWeekOfYear = c.get(Calendar.WEEK_OF_YEAR);
            //如果月份是12月，且求出来的周数是第一周，说明该日期实质上是这一年的第53周，也是下一年的第一周
            if (c.get(Calendar.MONTH) >= Calendar.DECEMBER && currentWeekOfYear <= 1) {
                currentWeekOfYear += 52;
            }
            c.set(Calendar.WEEK_OF_YEAR, currentWeekOfYear + 1);
        }
        c.set(Calendar.DAY_OF_WEEK, getCalendarDayOfWeek(week));
        return dateToSecond(c.getTime());
    }

    /**
     * 获取以当前时间为准, x周之前或之后的指定周几、指定时分秒的时间, 周一为第一天
     *
     * @param weekOffSet 0为当前星期
     * @param dayOfWeek
     * @param hour
     * @param minute
     * @param second
     * @return
     */
    public static Date getWeekDayDate(int weekOffSet, int dayOfWeek, int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.add(Calendar.WEEK_OF_YEAR, weekOffSet);
        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek >= 7 ? 1 : dayOfWeek + 1);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取以当前时间为准, x月之前或之后的指定该月第几天、指定时分秒的时间, 如果超过了该月最大天数, 则取该月最后一天
     *
     * @param monthOffset 0为当月
     * @param dayOfMonth
     * @param hour
     * @param minute
     * @param second
     * @return
     */
    public static Date getMonthDayDate(int monthOffset, int dayOfMonth, int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, monthOffset);
        // 获取该月的最大天数
        int maxDayInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        // 如果指定的天数超过该月最大天数，取该月最后一天
        calendar.set(Calendar.DAY_OF_MONTH, Math.min(dayOfMonth, maxDayInMonth));
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
}
