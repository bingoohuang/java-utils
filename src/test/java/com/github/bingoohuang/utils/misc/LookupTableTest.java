package com.github.bingoohuang.utils.misc;

import org.junit.Assert;
import org.junit.Test;

import java.util.function.Supplier;

import static com.google.common.truth.Truth.assertThat;

public class LookupTableTest {

    @Test
    public void lookupTable() {
        Object[] table = {
                true, false, (Supplier) () -> "truefalse",
                true, true, "truetrue",
                false, true, "falsetrue",
                false, false, "falsefalse",
        };

        assertThat((String) LookupTable.lookupTable(table, 3, true, false)).isEqualTo("truefalse");
        assertThat((String) LookupTable.lookupTable(table, 3, true, true)).isEqualTo("truetrue");
        assertThat((String) LookupTable.lookupTable(table, 3, false, false)).isEqualTo("falsefalse");
        assertThat((String) LookupTable.lookupTable(table, 3, false, true)).isEqualTo("falsetrue");
    }

    @Test
    public void lookupTableNull() {
        String abc = null;
        Object[] table = {
                true, false, (Supplier) () -> abc.length(),
                true, true, "truetrue",
        };

        assertThat((String) LookupTable.lookupTable(table, 3, true, true)).isEqualTo("truetrue");
    }


    @Test(expected = RuntimeException.class)
    public void lookupTableFail() {
        Object[] table = {
                true, false, "truefalse",
        };

        LookupTable.lookupTable(table, 3, "fuck", false);

    }

    @Test
    public void lookupTableIllegalArgument() {
        Object[] table = {
                true, false, "truefalse",
        };

        try {
            LookupTable.lookupTable(table, 3, "fuck");
            Assert.fail();
        } catch (IllegalArgumentException ex) {
            assertThat(ex.getMessage()).isEqualTo("元素个数必须为2");
        }
    }
}