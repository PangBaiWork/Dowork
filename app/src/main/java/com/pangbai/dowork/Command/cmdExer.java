package com.pangbai.dowork.Command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class cmdExer {
  private static Process process = null;
  public static String lastLine=null;
    public static int execute(String command,boolean su){
       return execute(command,su,true);
    }
    public static int execute(String command,boolean su,boolean wait) {
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
                lastLine=line;
            }
            if (!wait)
                return 0;
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
            if (process != null) {
                process.destroy();
            }
        }
    }



}