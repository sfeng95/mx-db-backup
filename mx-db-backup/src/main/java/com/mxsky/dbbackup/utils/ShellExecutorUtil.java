package com.mxsky.dbbackup.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Create by johnsonmoon at 2018/10/31 10:38.
 */
public class ShellExecutorUtil {
    private static Logger logger = LoggerFactory.getLogger(ShellExecutorUtil.class);
    private static ExecutorService communicatorExecutor = Executors.newCachedThreadPool();

    /**
     * Execute shell command, return process exit value.
     * <p>
     * If {@code timeout} was set, {@link CommandTimeoutException} may be thrown if the command execution timeout.
     *
     * @param command       shell command.
     * @param directory     working directory where the command would be executed.
     * @param timeout       the max time to wait, time unit: ms. {@code null} means wait util the command execution was done.
     * @param communicators {@link Communicator} communication objects to receive message line from the process, with other operations as well.
     * @return the process exit value
     */
    public static int execute(String[] command, String directory, Long timeout, final Communicator... communicators) throws CommandTimeoutException {
        final ProcessBuilder processBuilder = new ProcessBuilder(command);
        if (directory != null) {
            File workDir = new File(directory);
            if (workDir.exists() && workDir.isDirectory()) {
                processBuilder.directory(workDir);
            }
        }
        processBuilder.redirectErrorStream(true);
        int status = -1;
        try {
            final Process process = processBuilder.start();
            if (communicators != null && communicators.length > 0) {
                communicatorExecutor.submit(() -> {
                    BufferedReader reader = null;
                    try {
                        InputStream inputStream = process.getInputStream();
                        if (inputStream == null) {
                            return;
                        }
                        reader = new BufferedReader(new InputStreamReader(inputStream));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            for (Communicator communicator : communicators) {
                                communicator.onMessage(line, process);
                            }
                        }
                    } catch (Exception e) {
                        logger.warn(e.getMessage(), e);
                    } finally {
                        if (reader != null) {
                            try {
                                reader.close();
                            } catch (Exception e) {
                                logger.warn(e.getMessage(), e);
                            }
                        }
                    }
                });
            }
            if (timeout == null || timeout <= 0) {
                status = process.waitFor();
            } else {
                if (!process.waitFor(timeout, TimeUnit.MILLISECONDS)) {
                    throw new CommandTimeoutException(String.format("Command execute timeout, timeout: %s, command: %s", timeout, command));
                } else {
                    status = process.exitValue();
                }
            }
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            if (e instanceof CommandTimeoutException) {
                throw (CommandTimeoutException) e;
            }
        }
        return status;
    }

    public static class CommandTimeoutException extends Exception {
        public CommandTimeoutException(String message) {
            super(message);
        }
    }

    /**
     * Shell command communication object during process executing.
     */
    public interface Communicator {
        /**
         * Received a line output message from the process.
         *
         * @param message a line output message from the process
         * @param process the process which the message from
         */
        void onMessage(String message, Process process) throws IOException;
    }
}