package com.github.bingoohuang.utils.lang;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Cmd {
    private Logger log = LoggerFactory.getLogger(Cmd.class);
    private ProcessBuilder processBuilder;
    private String commandLine;
    private Process process;
    private long startMillis;
    private boolean aliveFlag;
    private int exitValue = Integer.MIN_VALUE;

    private StreamGobbler stdoutStreamGobbler;
    private StreamGobbler stderrStreamGobbler;
    private long costMillis;

    public Cmd(String... command) {
        commandLine = StringUtils.join(command, ' ');
        processBuilder = new ProcessBuilder(command);
    }

    public boolean asyncExec() {
        exitValue = 0;
        costMillis = 0;
        aliveFlag = false;
        process = null;

        try {
            log.debug("start command line {}", commandLine);
            process = processBuilder.start();
            startStreamGobbler();

            startMillis = System.currentTimeMillis();
            aliveFlag = true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    public boolean syncExec(long timeoutMilis) {
        if (!asyncExec()) return false;

        while (detectProcessAlive()) {
            if (isTimeout(timeoutMilis)) kill();
            Threadx.sleepMillis(1000);
        }

        return getExitValue() == 0;
    }

    private void kill() {
        long cost = System.currentTimeMillis() - startMillis;
        costMillis = cost;
        log.warn("{} expired in {}s , kill it.", commandLine, cost / 1000.);
        process.destroy();
    }

    private boolean isTimeout(long timeoutMilis) {
        long cost = System.currentTimeMillis() - startMillis;
        return cost > timeoutMilis;
    }

    private void startStreamGobbler() {
        stdoutStreamGobbler = new StreamGobbler(log, commandLine,
                process.getInputStream(), StreamGobbler.TYPE.STDOUT);
        stderrStreamGobbler = new StreamGobbler(log, commandLine,
                process.getErrorStream(), StreamGobbler.TYPE.STDERR);

        stdoutStreamGobbler.start();
        stderrStreamGobbler.start();
    }

    public int getExitValue() {
        return exitValue;
    }

    public boolean detectProcessAlive() {
        if (!aliveFlag) return false;

        try {
            exitValue = process.exitValue();
            costMillis = System.currentTimeMillis() - startMillis;

            aliveFlag = false;
            log.info("{} exited with value {}, cost {} seconds",
                    commandLine, exitValue, costMillis / 1000.);

            return false;
        } catch (IllegalThreadStateException e) {
            return true;
        }
    }

    public String getStdOut() {
        return stdoutStreamGobbler.getOutput();
    }

    public String getStdErr() {
        return stderrStreamGobbler.getOutput();
    }
}
