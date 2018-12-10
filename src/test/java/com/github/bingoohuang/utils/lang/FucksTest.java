package com.github.bingoohuang.utils.lang;

import org.junit.Test;

import java.sql.SQLException;

public class FucksTest {
    @Test(expected = SQLException.class)
    public void fuckCheckedException() {
        try {
            throw new SQLException();
        } catch (SQLException ex) {
            throw Fucks.fuck(ex);
        }
    }
}
