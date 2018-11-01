package cn.wizzer.app.web.modules.controllers.open.api.Datatime;

/**
 * Created by xl on 2017/7/15.
 */

import org.nutz.lang.Strings;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;

public class newDataTime {

    public static String getDateYMDHMS() {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//可以方便地修改日期格式
        return dateFormat.format(now);
    }

    public static String getDateYMD() {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");//可以方便地修改日期格式
        return dateFormat.format(now);
    }

    public static String getDateFormat(String fmt) {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(fmt);//可以方便地修改日期格式
        return dateFormat.format(now);
    }

    public static String formatDatatime(Date data) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//可以方便地修改日期格式
        return dateFormat.format(data);
    }

    /**
     * 当天的开始时间 00:00:00
     *
     * @return
     */
    //20180228zhf1030
    public static Date startOfToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 当天的结束时间 23:59:59
     */
    //20180228zhf1030
    public static Date endOfToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return  calendar.getTime();

    }

    /**
     * 获取本月开始时间
     * @return
     */
    //20180228zhf1030
    public static Date startOfThisMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH,1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取本月结束时间
     * @return
     */
    //20180228zhf1030
    public static Date endOfThisMonth(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return  calendar.getTime();

    }

    //将Date类型时间转化为Integer类型
    //20180228zhf1030
    public static Integer getIntegerByDate(Date date) {
        return Integer.parseInt(String.valueOf(date.getTime()).substring(0, 10));
    }

    public static String getCurrentDateStr(String pattern){
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return  sdf.format(date);
    }

    //SimpleDateFormat sdf
    public static SimpleDateFormat getSdfByPattern(String pattern){
        if(Strings.isBlank(pattern)){
            pattern = "yyyy-MM-dd HH:mm:ss";
        }
        return new SimpleDateFormat(pattern);
    }
}
