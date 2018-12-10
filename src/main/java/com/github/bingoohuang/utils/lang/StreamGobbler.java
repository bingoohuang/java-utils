package com.github.bingoohuang.utils.lang;


import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamGobbler extends Thread {
    public enum TYPE {STDOUT, STDERR}

    private Logger log;

    private final static String separator = System.getProperty("line.separator");
    private InputStream is;
    private TYPE type;
    private String commandLine;
    private String output = "";

    public StreamGobbler(Logger log, String commandLine, InputStream is, TYPE type) {
        this.log = log;
        this.commandLine = commandLine;
        this.is = is;
        this.type = type;
    }

    public String getOutput() {
        return output;
    }

    @Override
    public void run() {
        InputStreamReader isr = null;
        BufferedReader br = null;
        StringBuilder outputStr = new StringBuilder();
        try {
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);

            String line = null;
            while ((line = br.readLine()) != null) {
                outputStr.append(line).append(separator);

                if (type == TYPE.STDOUT)
                    log.info("{}", line);
                else
                    log.warn("{}", line);
            }

            output = outputStr.toString();
        } catch (IOException e) {
            log.error("{} ioexception {}", commandLine, e.getMessage());
        } finally {
            Closer.closeQuietly(isr);
            Closer.closeQuietly(br);
        }
    }
}
