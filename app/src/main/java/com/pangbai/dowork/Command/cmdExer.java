package com.pangbai.dowork.Command;

import com.pangbai.dowork.tool.Init;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class cmdExer {
    public static Process process = null;
    public static String lastLine = null;

    public static int execute(String command, boolean su) {
        return execute(command, su, true);
    }

    public static int execute(String command, boolean su, boolean wait) {
        BufferedReader reader = null;
        String shell;
        if (su)
            shell = "su";
        else
            shell = "sh";

        ProcessBuilder processBuilder = new ProcessBuilder();
        Map<String, String> environment = processBuilder.environment();

        // 设置环境变量
        environment.put("LD_LIBRARY_PATH", Init.filesDirPath + "/usr/lib");
        processBuilder.command(shell, "-c", command);

        try {


            process = processBuilder.start();
            if (!wait)
                return 0;
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                lastLine = line;
            }

            int exitCode = process.waitFor();
            return exitCode;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return -1;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (process != null && wait) {
                process.destroy();
            }
        }
    }


}