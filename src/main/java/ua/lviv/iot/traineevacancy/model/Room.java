package ua.lviv.iot.traineevacancy.model;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private List<int[]> coordinates;
    private int id;
    private String name;

    public Room() {
        this.coordinates = new ArrayList<>();
    }

    public List<int[]> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<int[]> coordinates) {
        this.coordinates = coordinates;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Room{" +
                "coordinates=" + coordinates +
                ", id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
