package com.metrobot;

import java.awt.Point;

public class GameWindow {
    private final String name;
    private final Point topLeftCorner;

    public GameWindow(String name, Point topLeftCorner) {
        this.name = name;
        this.topLeftCorner = topLeftCorner;
    }

    public String getName() {
        return name;
    }

    public Point getTopLeftCorner() {
        return topLeftCorner;
    }

    @Override
    public String toString() {
        return "GameWindow{name='" + name + "', topLeft=" + topLeftCorner + '}';
    }
}
