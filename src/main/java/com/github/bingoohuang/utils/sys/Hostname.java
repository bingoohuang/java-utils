package com.github.bingoohuang.utils.sys;

import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.apache.commons.lang3.StringUtils;

import java.net.InetAddress;
import java.util.Scanner;

@UtilityClass
public class Hostname {
    public static final String HOSTNAME = getHostname();

    private static String getHostname() {
        try {
            return StringUtils.trim(execReadToString("hostname"));
        } catch (Throwable ex) {
            // ignore
        }

        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Throwable ex) {
            // ignore
        }

        return "Unknown";
    }

    @SneakyThrows
    public static String execReadToString(String execCommand) {
        val proc = Runtime.getRuntime().exec(execCommand);
        @Cleanup val stream = proc.getInputStream();
        @Cleanup val scanner = new Scanner(stream).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }
}
