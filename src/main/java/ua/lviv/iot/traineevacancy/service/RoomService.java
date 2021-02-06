package ua.lviv.iot.traineevacancy.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ua.lviv.iot.traineevacancy.data.RoomRepository;
import ua.lviv.iot.traineevacancy.exceptions.CornersAreNotClockwiseException;
import ua.lviv.iot.traineevacancy.model.Room;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@AllArgsConstructor
public class RoomService {
    private final RoomRepository repository;

    // returns string with error or null, if all is ok
    private String validateRoom(Room room) {

        List<int[]> coordinates = room.getCoordinates();
        // validate number of corners
        if (coordinates.size() < 4) {
            return "Too little corners: " + coordinates.size();
        }

        // to ease validations of last and first corners in list
        coordinates.add(coordinates.get(0));

        // validate clockwise (and also 90 degrees corners)
        try {
            validateClockwise(room);
        } catch (CornersAreNotClockwiseException e) {
            return "Corners are not clockwise";
        }
        // algorithm: find two farthest (to one of the sides) corners of scheme and check, to which side it is turning. Like, I found
        // 2 edge corners, understood, that they go clockwise, and then I should go in both sides of list to check
        // if there all is ok. (Or shouldn't, I'm not sure yet)


        // validate walls of new room for not crossing each other
        validateNotCrossingWalls(room);
        // algorithm: check each two walls of room:  if 2 points of 1 wall are between other two, and two other are between
        // two first (in different coordinates) (and also I need to check if these walls are in different directions,
        // like, one is in width and other is in length), then sth is wrong


        // validate walls of new room to match and not cross walls of other rooms.
        validateNotCrossingWallsOfOtherRooms(room);
        // algorithm: DEATH

        return null;
    }
    /*
       explanation of how this method works:
       First, I create list of all corners with the most right position. Then,

       If in the farthest right corner next corner to it is upper than it,
       then all room is counterclockwise, so method throws an exception
         For example,
           *   ->  *
           *  *
              *    *
         in this room there is two points with the farthest x coordinates, so I check both. For first, all is ok, as
         next point's y coordinate is lesser. For second point, it's next point's y coordinate is the same, so it's
         also ok
         (I could also check any other of farthest coordinates, like the most left, top of bottom, I just randomly
         chose the right one)
    */
    private void validateClockwise(Room room) throws CornersAreNotClockwiseException {
        List<int[]> coordinates = room.getCoordinates();

        List<Integer> farthestRightIndexes = createFarthestRightIndexesList(coordinates);
        for (int i = 0; i < farthestRightIndexes.size(); i++) {
            if (coordinates.get(i)[1] < coordinates.get(i+1)[1]){
                throw new CornersAreNotClockwiseException();
            }
        }
    }

    private List<Integer> createFarthestRightIndexesList(List<int[]> coordinates) {
        int farthestRightCoordinate = Integer.MIN_VALUE;
        List<Integer> farthestRightIndexes = new ArrayList<>();
        farthestRightIndexes.add(0);
        for (int i = 0; i < coordinates.size() - 1; i++) {
            if (coordinates.get(i)[0] > farthestRightCoordinate) {
                farthestRightCoordinate = coordinates.get(i)[0];
                farthestRightIndexes.clear();
                farthestRightIndexes.add(i);
            } else if (coordinates.get(i)[0] == farthestRightCoordinate) {
                farthestRightIndexes.add(i);
            }
        }
        return farthestRightIndexes;
    }

    private void validateNotCrossingWalls(Room room){
        List<int[]> coordinates = room.getCoordinates();
        for (int i = 0; i < coordinates.size()-1; i++) {

        };
    }

    private boolean isRightLeft(int[] firstPairOfCoordinates, int[] secondPairOfCoordinates) {
        return firstPairOfCoordinates[0] != secondPairOfCoordinates[0]
                && firstPairOfCoordinates[1] == secondPairOfCoordinates[1];
    }

    private void validateNotCrossingWallsOfOtherRooms(Room room) {
        List<Room> allRooms = new ArrayList<>(repository.getAllRooms());
        return;
    }

}
