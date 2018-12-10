package com.github.bingoohuang.utils.json;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonPathing {
    /**
     * JsonPath表达式。
     * 在使用之前，请先在http://jsonpath.com/上做好测试。
     *
     * @return JsonPath表达式。
     */
    String[] value();

    /**
     * 值映射。
     * 映射表达式举例：
     * 1. a,1,2 表示取值为a时映射为1，否则映射为2；
     * 2. a,1,b,2 表示取值为a时映射为1，取值为b时映射为2，其它不映射（使用原值）。
     *
     * @return 映射表达式。
     */
    String mapping() default "";

    /**
     * 提取用的正则表达式。
     * 例如从"/Date(631152000000+0000)/"中提取第一个数字串，使用\\d+即可。
     *
     * @return 捕获取值的正则。
     */
    String catchExpr() default "";

    /**
     * 提取结果有多条时，使用的过滤器。例如max,min等。
     *
     * @return 过滤器列表，英文逗号分隔。
     */
    String filter() default "";
}
