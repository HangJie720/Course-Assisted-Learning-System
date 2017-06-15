package com.ijustyce.fastandroiddev.baseLib.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yc on 2015/8/14.  常用正则表达式
 */
public class RegularUtils {

    public static boolean isMobilePhone(String s){

        if (s == null){
            return false;
        }if (s.startsWith("+86")){
            s = s.replace("+86", "");
        }

        Pattern p = Pattern
                .compile("^((13[0-9])|(15[^4,\\D])|(18[0-9])|(145)|(147)|(17[6-8]))\\d{8}$");
        Matcher m = p.matcher(s);
        return m.matches();
    }

    /**
     * 判断是否是图片，即判断扩展名是否为 .jpg, .jpeg, .png
     * @param file
     * @return
     */
    public static boolean isImage(String file){

        if (file == null){
            return false;
        }
        if (file.endsWith(".jpg") || file.endsWith(".jpeg") || file.endsWith(".png")){
            return true;
        }
        return false;
    }

    /**
     * 判断是否是身份证号，包括生日校验
     * @param cardNum   身份证号
     */
    public static boolean isCard(String cardNum){

        //定义判别用户身份证号的正则表达式（要么是15位，要么是18位，最后一位可以为字母）
        Pattern idNumPattern = Pattern.compile("(\\d{14}[0-9a-zA-Z])|(\\d{17}[0-9a-zA-Z])");
        Matcher idNumMatcher = idNumPattern.matcher(cardNum);
        if(idNumMatcher.matches()){
            System.out.println("您的出生年月日是：");
            Pattern birthDatePattern= Pattern.compile("\\d{6}(\\d{4})(\\d{2})(\\d{2}).*");//身份证上的前6位以及出生年月日
            Matcher birthDateMather= birthDatePattern.matcher(cardNum);
            if(birthDateMather.find()){
                String year = birthDateMather.group(1);
                String month = birthDateMather.group(2);
                String date = birthDateMather.group(3);
                System.out.println(year + "年 " + month + "月 " + date + "日");
                return true;
            }
        }else{
            System.out.println("您输入的并不是身份证号");
            return false;
        }
        return false;
    }

    /**
     * 判断是否为域名，类似：http://22.com 不能是  http://22.22/22,  暂不支持中文域名
     * @param string    一个url
     * @return  true or false
     */
    public static boolean isHost(String string){

        if (string == null){
            return false;
        }
        Pattern p = Pattern.compile("^((http://)|(https://))((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}"
                + "\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
        return p.matcher(string).matches();
    }

    /**
     * 判断是否为网址，类似：http://22.com/222 暂不支持中文域名
     * @param string    一个url
     * @return  true or false
     */
    public static boolean isUrl(String string){

        if (string == null){
            return false;
        }
        Pattern p = Pattern.compile("^((http://)|(https://))((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}"
                + "\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)(/)(.+)$");
        return p.matcher(string).matches();
    }

    /**
     * 判断是否是通用手机号，即：正常11位手机号、6位城市短号、通用固话号码
     * @param s String 字符串
     * @return
     */
    public static boolean isCommonPhone(String s){

        return isMobilePhone(s) || isFixedPhone(s) || isShortPhone(s);
    }

    public static boolean isShortPhone(String s){

        if (s == null){
            return false;
        }

        Pattern p = Pattern
                .compile("^([0-9]{6})$");
        Matcher m = p.matcher(s);
        return m.matches();
    }

    public static boolean isFixedPhone(String s){

        if (s == null){
            return false;
        }

        Pattern p = Pattern.compile("^([0-9]{3,5}(-)?)?([0-9]{7,8})((-)?[0-9]{1,4})?$");
        Matcher m = p.matcher(s);
        return m.matches();
    }

    public static boolean isEmail(String s){

        if (s == null){
            return false;
        }

        Pattern p = Pattern
                .compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}"
                        + "\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
        Matcher m = p.matcher(s);
        return m.matches();
    }

    public static String delStringBlank(String s){

        if (s == null){
            return null;
        }
        return s.replaceAll(" ", "");
    }
}
