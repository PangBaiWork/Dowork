package com.pangbai.dowork.Command;

import com.pangbai.dowork.tool.Init;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class cmdExer {
    public static Process process;
    public static String lastLine, result;

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
        for (String env : Init.envp) {
            String[] tmp = getByEnv(env);
            processBuilder.environment().put(tmp[0], tmp[1]);
        }
        processBuilder.redirectErrorStream(true);
      //  environment.put("LD_LIBRARY_PATH", Init.filesDirPath + "/usr/lib");
        processBuilder.command(shell, "-c", command);

        try {


            process = processBuilder.start();
            if (!wait)
                return 0;
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                result += line+"\n";
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

    public static void destroy() {
        process = null;
        result = null;
        lastLine = null;
    }
    public static String[] getByEnv(String env){

        return env.split("=",2);
    }

    public static String getLastLine() {
        return lastLine;
    }

}