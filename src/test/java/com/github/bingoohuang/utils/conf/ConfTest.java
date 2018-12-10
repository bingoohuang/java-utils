package com.github.bingoohuang.utils.conf;

import com.github.bingoohuang.utils.file.Pathx;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import lombok.SneakyThrows;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n3r.diamond.client.impl.MockDiamondServer;

import java.io.File;

import static com.google.common.truth.Truth.assertThat;

public class ConfTest {
    @Conf
    @Conf.Source({"diamond:my^default", "classpath:conf.properties"})
    public interface MyConf {
        String domain();

        @Conf.Key("isTest")
        boolean testMode();

        @Conf.Key("isTest")
        Boolean testMode2();

        @Conf.DefaultValue("abc")
        String secret();
    }


    @BeforeClass
    public static void beforeClass() {
        MockDiamondServer.setUpMockServer();
    }

    @AfterClass
    public static void afterClass() {
        MockDiamondServer.tearDownMockServer();
    }

    @Test
    public void test() {
        MyConf conf = ConfFactory.create(MyConf.class);

        MockDiamondServer.setConfigInfo("my", "default", "domain=www.bingoohuang.com");

        assertThat(conf.domain()).isEqualTo("www.bingoohuang.com");
        assertThat(conf.testMode()).isTrue();
        assertThat(conf.testMode2()).isEqualTo(Boolean.TRUE);
        assertThat(conf.secret()).isEqualTo("abc");
    }


    @Conf
    public interface MyConf2 {
        String domain();

        String loginUrl();
    }


    @Test @SneakyThrows
    public void test2() {
        MyConf2 conf = ConfFactory.create(MyConf2.class);

        String confPathHome = Pathx.expandUserHome("~/.appconf/");
        new File(confPathHome).mkdirs();
        File file = new File(confPathHome + "MyConf2.properties");
        file.deleteOnExit();

        Files.asCharSink(file, Charsets.UTF_8).write("domain=xyz");
        assertThat(conf.domain()).isEqualTo("xyz");


        String p = Pathx.userDir + "/conf/";
        new File(p).mkdirs();
        File f = new File(p + "MyConf2.properties");
        f.deleteOnExit();

        Files.asCharSink(f, Charsets.UTF_8).write("loginUrl=http://www.baidu.com");
        assertThat(conf.loginUrl()).isEqualTo("http://www.baidu.com");
    }
}
