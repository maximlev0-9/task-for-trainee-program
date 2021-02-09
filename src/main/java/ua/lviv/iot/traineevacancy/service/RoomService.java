package ua.lviv.iot.traineevacancy.service;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ua.lviv.iot.traineevacancy.data.RoomRepository;
import ua.lviv.iot.traineevacancy.exceptions.CornersAreNotClockwiseException;
import ua.lviv.iot.traineevacancy.exceptions.CrossingWallsException;
import ua.lviv.iot.traineevacancy.exceptions.CrossingWallsOfExistingRooomsException;
import ua.lviv.iot.traineevacancy.model.Room;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RoomService {
    private final RoomRepository repository;

    public ResponseEntity<Map<String, String>> addRoom(Room room) {
        String error = validateRoom(room);
        if (error == null) {
            repository.addRoom(room);
            return ResponseEntity.status(HttpStatus.OK).build();

        } else {
            System.out.println(error);
            HashMap<String, String> errorMap = new HashMap<>();
            errorMap.put("error", error);
            return ResponseEntity.status(HttpStatus.OK).body(errorMap);
        }
    }


    public ResponseEntity<Collection<Room>> getAllRooms() {
        return ResponseEntity.ok(repository.getAllRooms());
    }

    /**
     * returns string with error or null, if all is ok
     */
    private String validateRoom(Room room) {
        List<int[]> coordinates = room.getCoordinates();
        // validate number of corners
        if (coordinates.size() < 4) {
            return "Too little corners: " + coordinates.size();
        }

        // to ease validations of last and first corners in list
        coordinates.add(coordinates.get(0));

        try {
            // validate clockwise (and also 90 degrees corners)
            validateClockwise(room);
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
        } catch (CornersAreNotClockwiseException e) {
            return "Corners are not clockwise";
        } catch (CrossingWallsException e) {
            return "Walls are crossing";
        } catch (CrossingWallsOfExistingRooomsException e) {
            return "This room cross walls of existing rooms";
        }
        room.getCoordinates().remove(room.getCoordinates().size() - 1);
        return null;
    }

    /**
     * explanation of how this method works:
     * First, I create list of all corners with the most right position. Then,
     * <p>
     * If in the farthest right corner next corner to it is upper than it,
     * then all room is counterclockwise, so method throws an exception
     * For example,
     * ->  *
     * *
     * *
     * in this room there is two points with the farthest x coordinates, so I check both. For first, all is ok, as
     * next point's y coordinate is lesser. For second point, it's next point's y coordinate is the same, so it's
     * also ok
     * (I could also check any other of farthest coordinates, like the most left, top of bottom, I just randomly
     * chose the right one)
     */
    private void validateClockwise(Room room) throws CornersAreNotClockwiseException {
        List<int[]> coordinates = room.getCoordinates();

        List<Integer> farthestRightIndexes = createFarthestRightIndexesList(coordinates);
        for (int i = 0; i < farthestRightIndexes.size(); i++) {
            try {
                if (coordinates.get(farthestRightIndexes.get(i))[1] <
                        coordinates.get(farthestRightIndexes.get(i) + 1)[1]) {
                    throw new CornersAreNotClockwiseException();
                }
            } catch (ArrayIndexOutOfBoundsException ignored) {
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

    /**
     *
     **/
    private void validateNotCrossingWalls(Room room) throws CrossingWallsException {
        List<int[]> coordinates = room.getCoordinates();
        for (int i = 0; i < coordinates.size() - 1; i++) {
            for (int j = 0; j < coordinates.size() - 1; j++) {
                if (i == j) continue;
                int[] firstWallFirstPoint = coordinates.get(i);
                int[] firstWallSecondPoint = coordinates.get(i + 1);
                int[] secondWallFirstPoint = coordinates.get(j);
                int[] secondWallSecondPoint = coordinates.get(j + 1);
                if (differentlyOriented(firstWallFirstPoint, firstWallSecondPoint,
                        secondWallFirstPoint, secondWallSecondPoint)
                        && crossOneAnother(firstWallFirstPoint, firstWallSecondPoint,
                        secondWallFirstPoint, secondWallSecondPoint)) {
                    throw new CrossingWallsException();
                }
            }
        }
    }

    /**
     * If look how points are located below, it's obvious that if one of the points is the farthest to any two
     * sides at the same time, then these walls are not intercepting (that is so because our walls
     * can be only horizontal or vertical, but not diagonal)
     * *
     * <p>
     * <p>
     * *                *
     * <p>
     * *
     * ----------------------------------------
     * <p>
     * *                      *
     * *
     * <p>
     * <p>
     * <p>
     * *
     * <p>
     * ----------------------------------------
     * So, this method just searches the farthest points to all sides and checks whether any of them are equal. If yes,
     * everything is ok!
     */
    private boolean crossOneAnother(int[]... points) {
        Arrays.sort(Arrays.copyOf(points, points.length), Arrays::compare);
        for (int i = 0; i < points.length - 1; i++) {
            if (Arrays.equals(points[i], points[i + 1])) return false;
        }
        Map<String, List<int[]>> pointsMap = initializeMapForCrossOneAnotherMethod(points[0]);
        comparePointsWithValuesInMapAndUpdateMap(pointsMap, points);

        List<List<int[]>> intArrays = new ArrayList<>(pointsMap.values());

//        for (int i = 0; i < values.length - 1; i++) {
//            if (Arrays.equals(values[i], values[i + 1])) {
//                return false;
//            }
//        }
        List<int[]> collect = intArrays.stream().flatMap(Collection::stream).sorted(Arrays::compare).collect(Collectors.toList());
        for (int i = 0; i < collect.size()-1; i++) {
            if (Arrays.equals(collect.get(i), collect.get(i+1))){
                return false;
            }
        }
        // if
        /*
         *    *           *
         *
         *           *
         *
         *
         *           *
         * */


        return true;
    }

    private void comparePointsWithValuesInMapAndUpdateMap(Map<String, List<int[]>> pointsMap, int[][] points) {
        for (int[] point : points) {
            if (point[0] > pointsMap.get("right").get(0)[0]) {
                ArrayList<int[]> right = new ArrayList<>();
                right.add(point);
                pointsMap.put("right", right);
            }
            if (point[0] < pointsMap.get("left").get(0)[0]) {
                ArrayList<int[]> left = new ArrayList<>();
                left.add(point);
                pointsMap.put("left", left);
            }
            if (point[1] > pointsMap.get("top").get(0)[1]) {
                ArrayList<int[]> top = new ArrayList<>();
                top.add(point);
                pointsMap.put("top", top);
            }
            if (point[1] < pointsMap.get("bottom").get(0)[1]) {
                ArrayList<int[]> bottom = new ArrayList<>();
                bottom.add(point);
                pointsMap.put("bottom", bottom);
            }
            if (point[0] == pointsMap.get("right").get(0)[0]) {
                pointsMap.get("right").add(point);
            }
            if (point[0] == pointsMap.get("left").get(0)[0]) {
                pointsMap.get("left").add(point);
            }
            if (point[1] == pointsMap.get("top").get(0)[1]) {
                pointsMap.get("top").add(point);
            }
            if (point[1] == pointsMap.get("bottom").get(0)[1]) {
                pointsMap.get("bottom").add(point);
            }
        }
    }

    private Map<String, List<int[]>> initializeMapForCrossOneAnotherMethod(int[] point) {
        Map<String, List<int[]>> pointsMap = new HashMap<>();
        List<int[]> pointTopList = new ArrayList<>();
        pointTopList.add(point);
        pointsMap.put("top", pointTopList);
        List<int[]> pointLeftList = new ArrayList<>();
        pointLeftList.add(point);
        pointsMap.put("left", pointLeftList);
        List<int[]> pointRightList = new ArrayList<>();
        pointRightList.add(point);
        pointsMap.put("right", pointRightList);
        List<int[]> pointBottomList = new ArrayList<>();
        pointBottomList.add(point);
        pointsMap.put("bottom", pointBottomList);

        return pointsMap;
    }

    private boolean differentlyOriented(int[] firstWallFirstPoint, int[] firstWallSecondPoint,
                                        int[] secondWallFirstPoint, int[] secondWallSecondPoint) {
        return (isRightLeft(firstWallFirstPoint, firstWallSecondPoint)
                && !isRightLeft(secondWallFirstPoint, secondWallSecondPoint))
                || (!isRightLeft(firstWallFirstPoint, firstWallSecondPoint)
                && isRightLeft(secondWallFirstPoint, secondWallSecondPoint));
    }

    private boolean isRightLeft(int[] firstPairOfCoordinates, int[] secondPairOfCoordinates) {
        return firstPairOfCoordinates[0] != secondPairOfCoordinates[0]
                && firstPairOfCoordinates[1] == secondPairOfCoordinates[1];
    }

    private void validateNotCrossingWallsOfOtherRooms(Room room) throws CrossingWallsOfExistingRooomsException {
        List<Room> allRooms = new ArrayList<>(repository.getAllRooms());

        List<int[]> coordinates = room.getCoordinates();
        for (int i = 0; i < coordinates.size() - 1; i++) {
            int[] firstWallFirstPoint = coordinates.get(i);
            int[] firstWallSecondPoint = coordinates.get(i + 1);
            for (Room allRoom : allRooms) {
                List<int[]> existingRoomCoordinates = allRoom.getCoordinates();
                for (int j = 0; j < existingRoomCoordinates.size() - 1; j++) {
                    int[] secondWallFirstPoint = existingRoomCoordinates.get(j);
                    int[] secondWallSecondPoint = existingRoomCoordinates.get(j + 1);
                    if (differentlyOriented(firstWallFirstPoint, firstWallSecondPoint,
                            secondWallFirstPoint, secondWallSecondPoint)
                            && crossOneAnother(firstWallFirstPoint, firstWallSecondPoint,
                            secondWallFirstPoint, secondWallSecondPoint)) {
                        throw new CrossingWallsOfExistingRooomsException();
                    }
                }
            }
        }

    }

    public ResponseEntity<Room> deleteRoom(int id) {
        Room deleteRoom = repository.deleteRoom(id);
        if (deleteRoom != null) {
            return ResponseEntity.ok(deleteRoom);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}

