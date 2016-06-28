package de.jez_lynn.escape.util;

import java.util.LinkedList;

/**
 * Copyright 2016
 * Created on   : 24.06.2016
 * Author       : Michael Schlittenbauer
 */
public class Node {
    public LinkedList<Edge> edges = new LinkedList<>();
    public Edge lastEdge;
}
