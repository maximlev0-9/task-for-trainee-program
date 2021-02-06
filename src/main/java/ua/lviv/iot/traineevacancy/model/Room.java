package ua.lviv.iot.traineevacancy.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Room {
    private List<int[]> coordinates;
    private int id;

    public Room() {
        this.coordinates = new ArrayList<>();
    }

    public List<int[]> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<int[]> coordinates) {
        this.coordinates = coordinates;
    }

    public void addCoordinates(int[] newCoordinates) {
        this.coordinates.add(newCoordinates);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
