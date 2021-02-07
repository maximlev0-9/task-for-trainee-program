package ua.lviv.iot.traineevacancy.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.lviv.iot.traineevacancy.model.Room;
import ua.lviv.iot.traineevacancy.service.RoomService;

import java.util.Collection;

@RestController
@RequestMapping("/room")
@AllArgsConstructor
public class RoomController {

    private final RoomService service;

    @PostMapping
    public ResponseEntity addRoom(@RequestBody Room room) {
        return service.addRoom(room);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Room> getById(@PathVariable("id") int id) {
        return service.getRoomById(id);
    }

    @GetMapping
    public ResponseEntity<Collection<Room>> getAllRooms() {
        return service.getAllRooms();
    }


}
