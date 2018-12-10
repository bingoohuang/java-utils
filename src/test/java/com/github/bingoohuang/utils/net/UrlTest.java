package com.github.bingoohuang.utils.net;

import com.google.common.io.Files;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.Test;

import java.io.File;


public class UrlTest {

    @Test @SneakyThrows
    public void download() {
        val res = Url.download("http://eas.zhaopin.com/CompanyPlatform/RedirectToReport.ashx?eid=fc10fc13ffbc4168a2e60e0155018d1e&eaid=12286145&spid=3577&type=5");
        System.out.println(res);

        File file = new File(res.getFileName());
        Files.asByteSink(file).write(res.getContent());
        file.delete();
    }
}