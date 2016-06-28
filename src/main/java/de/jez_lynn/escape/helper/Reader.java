package de.jez_lynn.escape.helper;

import com.sun.javafx.geom.Vec2d;
import de.jez_lynn.escape.Escape;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Copyright 2016
 * Created on   : 24.06.2016
 * Author       : Michael Schlittenbauer
 */
public class Reader {
    private String path;
    private int l, w, n;
    private List<Vec2d> positions = new LinkedList<>();

    public Reader(String p) {
        path = p;
        init();
        System.out.println("Length: " + l);
        System.out.println("Width: " + w);
        System.out.println("Soldiers: " + n);
        for(int i = 0; i < positions.size(); i++){
            System.out.println("Soldier " + (i + 1) + " at: " + positions.get(i));
        }
    }

    private void init() {
        System.out.println();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(Escape.class.getResourceAsStream(path)));
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null && !line.isEmpty()) {
                if (firstLine) {
                    populateData(line);
                    firstLine = !firstLine;
                } else {
                    populatePositions(line);
                }
            }
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

    private void populateData(String line) {
        String[] result = line.split("\\s+");
        l = Integer.parseInt(result[0]);
        w = Integer.parseInt(result[1]);
        n = Integer.parseInt(result[2]);
    }

    private void populatePositions(String line) {
        String[] result = line.split("\\s+");
        Vec2d pos = new Vec2d(Double.parseDouble(result[0]), Double.parseDouble(result[1]));
        positions.add(pos);
    }

    public int getLenght() {
        return l;
    }

    public int getWidth() {
        return w;
    }

    public int getSoldiers() {
        return n;
    }

    public Vec2d getPosition(int i) {
        return positions.get(i-1);
    }
}
