package com.github.bingoohuang.utils.lang;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;

public class CmdTest {
    @Test
    public void test1() {
        if (!Os.isWindows) {
            Cmd echo = new Cmd("sh", "-c", "echo \"abc\"");
            boolean succ = echo.syncExec(1200);

            String stdErr = echo.getStdErr();
            assertThat(succ).isTrue();
            assertThat(stdErr).isEmpty();

            int exitValue = echo.getExitValue();
            assertThat(exitValue).isEqualTo(0);

            String stdOut = echo.getStdOut();
            assertThat(stdOut).isEqualTo("abc\n");
        }
    }

    @Test
    public void test2() throws IOException {
        if (!Os.isWindows) {
            File abcOut = new File("abc.out");
            abcOut.delete();

            Cmd echo = new Cmd("sh", "-c", "echo \"abc\" > abc.out");
            boolean succ = echo.syncExec(1200);
            assertThat(succ).isTrue();

            String out = Files.asCharSource(abcOut, Charsets.UTF_8).read();
            assertThat(out).isEqualTo("abc\n");

            Cmd cat = new Cmd("sh", "-c", "cat < abc.out");
            succ = cat.syncExec(1000);
            assertThat(succ).isTrue();
            assertThat(cat.getStdOut()).isEqualTo("abc\n");
            abcOut.delete();
        }
    }
}
