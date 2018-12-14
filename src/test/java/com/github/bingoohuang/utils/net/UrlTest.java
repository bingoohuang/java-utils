package com.github.bingoohuang.utils.net;

import com.google.common.io.Files;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.Test;

import java.io.File;

import static com.google.common.truth.Truth.assertThat;


public class UrlTest {

    @Test @SneakyThrows
    public void download() {
        val res = Url.download("https://s3-us-west-2.amazonaws.com/s.cdpn.io/172905/test.pdf", "pdf");
        System.out.println(res);

        File file = new File(res.getFileName());
        file.delete();
        Files.asByteSink(file).write(res.getContent());

        assertThat(file.exists()).isTrue();

        file.delete();
    }

    @Test(expected = RuntimeException.class)
    public void fail() {
        Url.download("https://s3-us-west-2.amazonaws.com/s.cdpn.io/172905/123", "pdf");
    }
}