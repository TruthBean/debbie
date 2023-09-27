package com.truthbean.debbie.time;

public interface ChineseDateTimeConstant {
    /**
     * 天干
     * the ten heavenly stems
     */
    String[] HEAVENLY_STEAMS = new String[]{
            // the first of the ten Heavenly Stems
            // 1H
            "甲",
            // the 2nd of the ten Heavenly Stems
            // 2H
            "乙",
            // the 3rd of the ten Heavenly Stems
            // 3H
            "丙",
            // the 4th of the ten Heavenly Stems
            // 4H
            "丁",
            // the 5th of the ten Heavenly Stems
            // 5H
            "戊",
            // the 6th of the ten Heavenly Stems
            // 6H
            "己",
            // the 7th of the ten Heavenly Stems
            // 7H
            "庚",
            // the 8th of the ten Heavenly Stems
            // 8H
            "辛",
            // the 9th of the ten Heavenly Stems
            // 9H
            "壬",
            // the last of the ten Heavenly Stems
            // 10H
            "癸"
    };

    /**
     * 地支
     * the twelve earthly branches
     */
    String[] EARTHLY_BRANCHES = new String[]{
            // 1E (the first of the twelve Earthly Branches.)
            "子",
            // 2E ( the 2nd of the twelve Earthly Branches.)
            "丑",
            // 3E (the 3rd of the twelve Earthly Branches.)
            "寅",
            // 4E (the 4th of the twelve Earthly Branches.)
            "卯",
            // 5E (the 5th of the twelve Earthly Branches.)
            "辰",
            // 6E (the 6th of the twelve Earthly Branches.)
            "巳",
            // 7E (the 7th of the twelve Earthly Branches.)
            "午",
            // 8E (the 8th of the twelve Earthly Branches.)
            "未",
            // 9E (the 9th of the twelve Earthly Branches.)
            "申",
            // 10E (the 10th of the twelve Earthly Branches.)
            "酉",
            // 11E (the 11th of the twelve Earthly Branches.)
            "戌",
            // 12E (the last of the twelve Earthly Branches.)
            "亥"
    };

    /**
     * sexagenaryCycle
     * the sexagenary cycle
     *
     */
    String[] SEXAGENARY_CYCLE = new String[]{
            // 1H-1E, 2H-2E, ... 10H-10E, 1H-11E, 2H-12E
            "甲子", "乙丑", "丙寅", "丁卯", "戊辰", "己巳", "庚午", "辛未", "壬申", "癸酉", "甲戌", "乙亥",
            // 3H-1E, 4H-2E, ... 10H-8E, 1H-9E, 2H-10E, 3H-11E, 4H-12E
            "丙子", "丁丑", "戊寅", "己卯", "庚辰", "辛巳", "壬午", "癸未", "甲申", "乙酉", "丙戌", "丁亥",
            // 5H-1E, 6H-2E, ... 10H-6E, 1H-7E, 2H-8E, ... 6H-12E
            "戊子", "己丑", "庚寅", "辛卯", "壬辰", "癸巳", "甲午", "乙未", "丙申", "丁酉", "戊戌", "己亥",
            // 7H-1E, 8H-2E, ... 10H-4E, 1H-5E, 2H-6E, ... 8H-12E
            "庚子", "辛丑", "壬寅", "癸卯", "甲辰", "乙巳", "丙午", "丁未", "戊申", "己酉", "庚戌", "辛亥",
            // 9H-1E, 10-2E, ... 10H-2E, 1H-3E, 2H-4E, ... 10H-12E
            "壬子", "癸丑", "甲寅", "乙卯", "丙辰", "丁巳", "戊午", "己未", "庚申", "辛酉", "壬戌", "癸亥"
    };

    /**
     * 生肖
     * the Chinese Zodiac
     * 1、Rat 鼠
     *
     * 2、OX 牛
     *
     * 3、Tiger 虎
     *
     * 4、Rabbit 兔
     *
     * 5、Dragon 龙
     *
     * 6、Snake 蛇
     *
     * 7、Horse 马
     *
     * 8、Sheep 羊
     *
     * 9、Monkey 猴
     *
     * 10、Rooster 鸡
     *
     * 11、Dog 狗
     *
     * 12、pig/boar 猪
     */
    String[] ZODIAC = new String[]{"鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊", "猴", "鸡", "狗", "猪"};
}
