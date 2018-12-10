package com.github.bingoohuang.utils.lang;

import lombok.SneakyThrows;
import lombok.val;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;

public class NamePinyin {
    /**
     * 汉字转拼音，非汉字不转。
     *
     * @param name 需要转拼音的文字。
     * @return 全大写拼音。
     */
    @SneakyThrows
    public static String pinyin(String name) {
        if (name == null) {
            return "";
        }

        val defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

        val pinyin = new StringBuilder();
        char[] chars = name.toCharArray();
        for (int i = 0, ii = chars.length; i < ii; ++i) {
            char ch = chars[i];
            if (!isChinese(ch)) {
                pinyin.append(ch);
                continue;
            }

            val pys = PinyinHelper.toHanyuPinyinStringArray(ch, defaultFormat);
            if (pys != null && pys.length > 0) {
                if (pys.length > 1 && i == 0) { // 姓氏多音字处理
                    pinyin.append(surname(ch, pys));
                } else {
                    pinyin.append(pys[0]);
                }
            }
        }

        return pinyin.toString().toUpperCase();
    }

    private static final String[] SURNAMES = {
            "解XIE", "单SHAN", "盖GE", "乐YUE", "查ZHA", "曾ZENG", "缪MIAO", "朴PIAO", "区OU", "繁PO", "仇QIU",
            "行XING", "黑HE", "折SHE", "澹TAN"
    };

    // 姓氏多音字处理
    private static String surname(char ch, String[] chPy) {
        for (val surname : SURNAMES) {
            if (surname.charAt(0) == ch) {
                return surname.substring(1);
            }
        }

        return chPy[0];
    }

    /**
     * 判断一个字符是否是中文字符
     */
    private static boolean isChinese(char c) {
        return String.valueOf(c).matches("[\\u4E00-\\u9FA5]+");
    }
}
