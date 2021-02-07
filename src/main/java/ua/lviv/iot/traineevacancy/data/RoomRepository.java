package ua.lviv.iot.traineevacancy.data;

import org.springframework.stereotype.Component;
import ua.lviv.iot.traineevacancy.model.Room;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RoomRepository {
    private final Map<Integer, Room> rooms = new HashMap<>();

    private final AtomicInteger serialNumber = new AtomicInteger(0);

    {
        for (int i = 0; i < 3; i++) {

            Room value = new Room();
            value.setId(serialNumber.incrementAndGet());
            List<int[]> coordinates = new ArrayList<>();
            coordinates.add(new int[]{1, 1});
            coordinates.add(new int[]{1, 2});
            coordinates.add(new int[]{2, 2});
            coordinates.add(new int[]{2, 1});
            value.setCoordinates(coordinates);
            rooms.put(value.getId(), value);
        }
    }

    public void addRoom(Room room) {
        room.setId(serialNumber.incrementAndGet());
        rooms.put(serialNumber.get(), room);
    }

    public Room getById(int id) {
        return rooms.get(id);
    }

    public Collection<Room> getAllRooms() {
        return rooms.values();
    }
}
