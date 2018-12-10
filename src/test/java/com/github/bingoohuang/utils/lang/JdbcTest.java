package com.github.bingoohuang.utils.lang;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.sql.SQLException;
import java.util.Properties;

import static com.google.common.truth.Truth.assertThat;

public class JdbcTest {
    @BeforeClass
    public static void beforeClass() {
        Properties properties = new Properties();
        properties.put("url", "jdbc:h2:~/jdbc");
        properties.put("user", "sa");
        properties.put("password", "");

        Jdbc.config(properties);
    }


    @Before
    public void before() {
        Jdbc.exec("drop table if exists person");
        Jdbc.exec("create table  person(name varchar(10), id_no varchar(100), credit_card varchar(100))");
    }


    @After
    public void after() {
        String userHome = System.getProperty("user.home");
        new File(userHome, "jdbc.mv.db").delete();
    }

    @Test
    public void simple() {
        Jdbc.exec("insert into person(name, id_no, credit_card) values(?, ?, ?)",
                "bingoo", "321421198312111234", "1111222233334444");

        Person person = Jdbc.exec(Person.class,
                "select name, id_no, credit_card from person where name = ?",
                "bingoo");

        assertThat(person).isEqualTo(
                new Person("bingoo", "321421198312111234", "1111222233334444"));
    }

    @Test(expected = SQLException.class)
    public void badTableName() {
        Jdbc.exec("insert into person111(name, id_no, credit_card) values(?, ?, ?)",
                "bingoo", "321421198312111234", "1111222233334444");
    }
}
