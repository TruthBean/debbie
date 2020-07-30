package com.truthbean.tools;

import com.truthbean.Logger;
import com.truthbean.logger.LoggerFactory;

import java.util.Random;
import java.util.regex.Pattern;

public class ChinesePhoneNumberHelper {
    /**
     * 移动号码段
     */
    private static final String[] mobile = new String[]{
            "1340", "1341", "1342", "1343", "1344", "1345", "1346", "1347", "1348",
            "135", "136", "137", "138", "139",
            "1440",
            "147", "148",
            "150", "151", "152", "157", "158", "159",
            "165",
            "1703", "1705", "1706",
            "172", "178",
            "182", "183", "184", "187", "188",
            "195", "197", "198"
    };

    /**
     * 联通号码段
     */
    public static final String[] unicom = new String[]{
            "130", "131", "132",
            "140", "145", "146",
            "155", "156",
            "166", "167",
            "1704", "1707", "1709", "171", "175", "176",
            "185", "186", "196"
    };

    /**
     * 电信号码段
     */
    public static final String[] telecom = new String[]{
            "133", "1349",
            "1410", "149",
            "153",
            "162",
            "1700", "1702", "173", "1740", "1741", "177",
            "180", "181", "189",
            "190", "191", "193", "199"
    };
    /**
     * 中国广电号段
     */
    public static final String[] broadcast = new String[]{
            "192"
    };

    /**
     * 验证手机号码
     * @param phoneNumber 电话号码
     * @return boolean
     */
    public static boolean checkCellphone(String phoneNumber) {
        if (phoneNumber.length() != 11)
            return false;
        for (String s : mobile) {
            if (phoneNumber.startsWith(s)) {
                LOGGER.info("中国移动");
                return true;
            }
        }
        for (String s : unicom) {
            if (phoneNumber.startsWith(s)) {
                LOGGER.info("中国联通");
                return true;
            }
        }
        for (String s : telecom) {
            if (phoneNumber.startsWith(s)) {
                LOGGER.info("中国电信");
                return true;
            }
        }
        for (String s : broadcast) {
            if (phoneNumber.startsWith(s)) {
                LOGGER.info("中国广电");
                return true;
            }
        }
        return false;
    }

    public static String getRandomPhoneNumber() {
        Random random = new Random();
        int type = random.nextInt(4);
        switch (type) {
            case 0: {
                // 中国移动
                return makeRandomPhoneNumber(mobile);
            }
            case 1:
                // 中国联通
                return makeRandomPhoneNumber(unicom);
            case 2:
                // 中国电信
                return makeRandomPhoneNumber(telecom);
            case 3:
                // 中国广电
                return makeRandomPhoneNumber(broadcast);
            default:
                return null;
        }
    }

    private static String makeRandomPhoneNumber(String[] phone) {
        final Random random = new Random();
        final StringBuilder result = new StringBuilder();
        int size = phone.length;
        var i = random.nextInt(size);
        var prefix = phone[i];
        result.append(prefix);
        var suffix = 11 - prefix.length();
        for (int j = 0; j < suffix; j++) {
            result.append(random.nextInt(10));
        }
        return result.toString();
    }

    public static void main(String[] args) {
        var s = "18272163739";
        System.out.println(checkCellphone(s));
        s = getRandomPhoneNumber();
        System.out.println(checkCellphone(s));
    }

    public static final Logger LOGGER = LoggerFactory.getLogger(ChinesePhoneNumberHelper.class);
}
