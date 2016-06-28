package de.jez_lynn.escape.util;

/**
 * Copyright 2016
 * Created on   : 24.06.2016
 * Author       : Michael Schlittenbauer
 */
public class Edge {
    public int from, to, capacity, flow;
    public Edge(int f, int t, int c){
        from = f;
        to = t;
        capacity = c;
        flow = 0;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Edge){
            Edge other = (Edge) obj;
            return (other.from == this.from && other.to == this.to && other.capacity == this.capacity && other.flow == this.flow);
        }
        return false;
    }
}
