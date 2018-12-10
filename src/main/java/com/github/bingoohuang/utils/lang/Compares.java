package com.github.bingoohuang.utils.lang;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.val;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;

public class Compares {
    private List<CompareItem> items = Lists.newArrayList();

    public Compares compare(Comparable a, Comparable b, Comparable... seqValues) {
        if (seqValues.length > 0) {
            items.add(new SequencedCompareItem(a, b, seqValues));
        } else {
            items.add(new DefaultCompareItem(a, b));
        }
        return this;
    }

    public Compares compareReversed(Comparable a, Comparable b, Comparable... seqValues) {
        return compare(b, a, seqValues);
    }

    public int go() {
        for (val item : items) {
            int compare = item.compare();
            if (compare != 0) return compare;
        }

        return 0;
    }


    private interface CompareItem {
        int compare();
    }

    private static class SequencedCompareItem extends DefaultCompareItem {
        private final Comparable[] seqValues;

        public SequencedCompareItem(Comparable a, Comparable b, Comparable[] seqValues) {
            super(a, b);
            this.seqValues = seqValues;
        }

        @Override public int compare() {
            int ia = ArrayUtils.indexOf(seqValues, a);
            int ib = ArrayUtils.indexOf(seqValues, b);

            return Integer.compare(ia, ib);
        }
    }

    @AllArgsConstructor
    private static class DefaultCompareItem implements CompareItem {
        final Comparable a;
        final Comparable b;

        @Override public int compare() {
            if (a == null && b == null) return 0;
            if (a == null && b != null) return -1;
            if (a != null && b == null) return 1;

            return a.compareTo(b);
        }
    }
}
