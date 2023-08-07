package com.pangbai.dowork.Command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class cmdExer {
  private static Process process = null;
    public static boolean execute(String command,boolean su) {
        BufferedReader reader = null;
        String shell;
        if (su)
            shell="su";
        else
            shell="sh";

        try {
            process = new ProcessBuilder()
                .command(shell, "-c", command)
                .redirectErrorStream(true)
                .start();

            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            
            e.printStackTrace();
            return false;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (process != null) {
                process.destroy();
            }
        }
    }



}