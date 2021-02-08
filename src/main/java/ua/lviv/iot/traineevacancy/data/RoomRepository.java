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
        for (int i = 0; i < 5; i++) {
            Room value = new Room();
            value.setId(serialNumber.incrementAndGet());
            List<int[]> coordinates = new ArrayList<>();
            coordinates.add(new int[]{i*4+1, i*10+1});
            coordinates.add(new int[]{i*4+1, i*10+4});
            coordinates.add(new int[]{i*4+3, i*10+4});
            coordinates.add(new int[]{i*4+3, i*10+1});
            value.setCoordinates(coordinates);
            value.setName("New Room " + (i+1));
            rooms.put(value.getId(), value);
        }
    }

    public void addRoom(Room room) {
        room.setId(serialNumber.incrementAndGet());
        rooms.put(serialNumber.get(), room);
    }

    public Collection<Room> getAllRooms() {
        return rooms.values();
    }

    public Room deleteRoom(int id) {
        return rooms.remove(id);
    }

}
