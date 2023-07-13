package com.pangbai.dowork.Command;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class cmdExer {

    public static cmdResult execute(String command) {
        StringBuilder output = new StringBuilder();
        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            int exitCode = process.waitFor();
            return new cmdResult(exitCode == 0, output.toString());
        } catch (Exception e) {
            return new cmdResult(false, e.getLocalizedMessage());
        }
    }

    public static class cmdResult {
        private final boolean isSuccessful;
        private final String output;

        public cmdResult(boolean isSuccessful, String output) {
            this.isSuccessful = isSuccessful;
            this.output = output;
        }

        public boolean isSuccessful() {
            return isSuccessful;
        }

        public String getOutput() {
            return output;
        }
    }
}