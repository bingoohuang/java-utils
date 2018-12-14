package com.github.bingoohuang.utils.lang;

import lombok.Data;
import lombok.val;
import org.joda.time.DateTime;
import org.jooq.lambda.Seq;
import org.junit.Test;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;


public class ComparesTest {
    @Data
    public static class Bean implements Comparable<Bean> {
        private String state; // 未使用/已使用
        private String time; // 日期
        private int num; // 数量
        private boolean enabled;// 是否已激活

        @Override public int compareTo(Bean o) {
            val today = DateTime.now().toString("yyyy年MM月dd日");
            val effective = time.compareTo(today) >= 0;
            val otherEffective = o.time.compareTo(today) >= 0;
            return new Compares()
                    .compare(effective, otherEffective, true, false)
                    .compare(state, o.state, "未使用", "已使用")
                    .compare(time, o.time)
                    .compare(num, o.num)
                    .compare(enabled, o.enabled, true, false)
                    .go();
        }


    }

    @Test
    public void compare() {
        List<Bean> beanList = new ArrayList<>();
        SecureRandom r = new SecureRandom();

        for (int i = 0; i < 20; ++i) {
            Bean bean = new Bean();
            beanList.add(bean);

            bean.setState(r.nextBoolean() ? "未使用" : "已使用");
            bean.setTime(DateTime.now().minusDays(5).plusDays(r.nextInt(10)).toString("yyyy年MM月dd日"));
            bean.setNum(r.nextInt(10));
            bean.setEnabled(r.nextBoolean());
        }

        for (val bean : Seq.seq(beanList).sorted().toList()) {
            System.out.println(bean);
        }
    }
}