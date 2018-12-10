package com.github.bingoohuang.utils.text.matcher;

import com.github.bingoohuang.utils.text.matcher.model.TextItem;
import com.github.bingoohuang.utils.text.matcher.model.TextTripperConfig;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.objenesis.ObjenesisStd;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 文本匹配器。从文本中抓取符合要求的内容。
 */
@RequiredArgsConstructor
public class TextMatcher {
    /*
    需要匹配的文本。
     */
    private final String text;

    /**
     * 查找单个行标签匹配文本。
     *
     * @param label 文本标签
     * @return 匹配文本
     */
    public String findLineLabelText(String label) {
        return findLineLabelText(label, TextMatcherOption.builder().build());
    }

    /**
     * 查找单个行标签匹配文本。
     *
     * @param label  文本标签
     * @param option 匹配选项
     * @return 匹配文本
     */
    public String findLineLabelText(String label, TextMatcherOption option) {
        val range = option.locateRange(text);

        int labelPos = text.indexOf(label, range.getLeft());
        if (labelPos < 0) return "";

        int fromIndex = labelPos + label.length();
        int endPos = text.indexOf("\n", fromIndex);
        if (endPos >= range.getRight()) endPos = range.getRight();

        val labelText = endPos < 0 ? text.substring(fromIndex) : text.substring(fromIndex, endPos);

        return option.strip(labelText);
    }

    /**
     * 在文本中搜索指定正则模式的文本。
     *
     * @param pattern      正则模式
     * @param patternApply 模式捕获应用器
     * @param option       匹配选项
     */
    public void searchPattern(String pattern, PatternApplyAware patternApply, TextMatcherOption option) {
        val range = option.locateRange(text);
        val rangedText = text.substring(range.getLeft(), range.getRight());

        val p = Pattern.compile(pattern);
        val m = p.matcher(rangedText);
        while (m.find()) {
            int groupCount = m.groupCount();
            val groups = new String[groupCount];
            for (int i = 0; i < groupCount; ++i) {
                groups[i] = m.group(i + 1);
            }

            patternApply.apply(groups);
        }
    }

    /**
     * 在文本中搜索指定正则模式的文本。
     *
     * @param pattern   正则模式
     * @param itemClass 匹配JavaBean类型
     * @param <T>       匹配JavaBean泛型
     * @return 匹配JavaBean列表
     */
    public <T extends PatternApplyAware> List<T> searchPattern(String pattern, Class<T> itemClass) {
        return searchPattern(pattern, itemClass, TextMatcherOption.builder().build());
    }

    /**
     * 在文本中搜索指定正则模式的文本。
     *
     * @param pattern   正则模式
     * @param itemClass 匹配JavaBean类型
     * @param <T>       匹配JavaBean泛型
     * @param option    匹配选项
     * @return 匹配JavaBean列表
     */
    public <T extends PatternApplyAware> List<T> searchPattern(String pattern, Class<T> itemClass, TextMatcherOption option) {
        val instantiator = new ObjenesisStd().getInstantiatorOf(itemClass);
        val items = Lists.<T>newArrayList();

        searchPattern(pattern, groups -> {
            T item = instantiator.newInstance();
            item.apply(groups);
            items.add(item);
        }, option);

        return items;
    }

    private static Pattern BlankPattern = Pattern.compile("\\s*(\\S+)");

    /**
     * 查找标签匹配文本。
     *
     * @param label  文本标签
     * @param option 匹配选项
     * @return 匹配文本
     */
    public String findLabelText(String label, TextMatcherOption option) {
        val range = option.locateRange(text);
        int pos = text.indexOf(label, range.getLeft());
        if (pos < 0) return "";
        if (pos >= range.getRight()) pos = range.getRight();

        val sub = text.substring(pos + label.length(), range.getRight());
        val striped = option.strip(sub);
        val matcher = BlankPattern.matcher(striped);
        return matcher.find() ? matcher.group(1) : "";
    }


    /**
     * 查找模式匹配的整个文本。
     *
     * @param pattern   正则模式
     * @param option    匹配选项
     * @param findIndex 第几个匹配
     * @return 匹配文本
     */
    public String findPatternText(String pattern, TextMatcherOption option, int findIndex) {
        return findPatternText(pattern, option, findIndex, 0);

    }

    /**
     * 查找模式匹配的第几个抓取文本。
     *
     * @param pattern    正则模式
     * @param option     匹配选项
     * @param findIndex  第几个匹配
     * @param groupIndex 匹配后抓取的分组值组索引
     * @return 匹配文本
     */
    public String findPatternText(String pattern, TextMatcherOption option, int findIndex, int groupIndex) {
        val range = option.locateRange(text);
        val sub = text.substring(range.getLeft(), range.getRight());
        val matcher = Pattern.compile(pattern).matcher(sub);
        for (int i = 0; i < findIndex; ++i) {
            matcher.find();
        }
        return matcher.find() ? matcher.group(groupIndex) : "";
    }

    /**
     * 根据文本提取配置，从文本中获取指定属性。
     *
     * @param config 文本提取配置
     * @return 文本对象
     */
    public List<TextItem> strip(TextTripperConfig config) {
        val pagedText = new HashMap<Integer, String>() {{
            put(0, text);
        }};
        return new ConfiguredStripper(pagedText, config).strip();
    }
}
