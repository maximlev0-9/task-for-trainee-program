package ua.lviv.iot.traineevacancy.data;

import org.springframework.stereotype.Component;
import ua.lviv.iot.traineevacancy.model.Room;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RoomRepository {
    private final Map<Integer, Room> rooms = new HashMap<>();

    private final AtomicInteger serialNumber = new AtomicInteger(0);

    public void addRoom(Room room){
        room.setId(serialNumber.incrementAndGet());
        rooms.put(serialNumber.get(), room);
    }

    public Room getById(int id){
        return rooms.get(id);
    }

    public Collection<Room> getAllRooms(){
        return rooms.values();
    }
}
