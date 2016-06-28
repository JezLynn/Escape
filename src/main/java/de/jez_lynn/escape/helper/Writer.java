package de.jez_lynn.escape.helper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Copyright 2016
 * Created on   : 24.06.2016
 * Author       : Michael Schlittenbauer
 */
public class Writer {
    private String path;

    public Writer(String p, int soldier){
        path = p;
        write(soldier);
    }

    private void write(int n){
        BufferedWriter br = null;
        try {
            br = new BufferedWriter(new FileWriter(new File(path)));
            br.write(n);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null)
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }
}
