package ua.lviv.iot.traineevacancy.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.lviv.iot.traineevacancy.model.Room;
import ua.lviv.iot.traineevacancy.service.RoomService;

import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping("/room")
@AllArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RoomController {

    private final RoomService service;

    @PostMapping
    public ResponseEntity<Map<String, String>> addRoom(@RequestBody Room room) {
        return service.addRoom(room);
    }

    @GetMapping
    public ResponseEntity<Collection<Room>> getAllRooms() {
        return service.getAllRooms();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Room> deleteRoom(@PathVariable("id") int id){
        return service.deleteRoom(id);
    }


}
