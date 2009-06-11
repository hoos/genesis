package com.uk.genesis.utils;

import java.io.*;
import org.apache.tools.ant.Task;

public class Cat extends Task {

    private String file;

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    private static void cat(InputStream in) throws IOException {
        PrintStream out = System.out;
        BufferedReader data = new BufferedReader(new InputStreamReader(in));
        String line = data.readLine();
        while (line != null) {
            out.println(line);
            line = data.readLine();
        }
    }

    public void execute() {
        try {
            if (file != null) {
                try {
                    InputStream in = new FileInputStream(file);
                    cat(in);
                    in.close();
                } catch (FileNotFoundException fnfEx) {
                    System.err.println("cat: " + file + ": file not found");
                }
            } else {
                cat(System.in);
            }
        } catch (IOException ioe) {
        }
    }
}
