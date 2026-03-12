import java.util.*;

// Abstract Room Class
abstract class Room {

    private String roomType;
    private int beds;
    private int size;
    private double price;

    public Room(String roomType, int beds, int size, double price) {
        this.roomType = roomType;
        this.beds = beds;
        this.size = size;
        this.price = price;
    }

    public String getRoomType() {
        return roomType;
    }

    public void displayRoomDetails() {
        System.out.println("Room Type : " + roomType);
        System.out.println("Beds      : " + beds);
        System.out.println("Size      : " + size + " sq ft");
        System.out.println("Price     : $" + price + " per night");
    }
}


// Single Room
class SingleRoom extends Room {
    public SingleRoom() {
        super("Single Room", 1, 200, 100);
    }
}


// Double Room
class DoubleRoom extends Room {
    public DoubleRoom() {
        super("Double Room", 2, 350, 180);
    }
}


// Suite Room
class SuiteRoom extends Room {
    public SuiteRoom() {
        super("Suite Room", 3, 500, 300);
    }
}


// Inventory Class
class RoomInventory {

    private HashMap<String, Integer> inventory;

    public RoomInventory() {

        inventory = new HashMap<>();

        inventory.put("Single Room", 5);
        inventory.put("Double Room", 3);
        inventory.put("Suite Room", 0); // Example unavailable room
    }

    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    public void updateAvailability(String roomType, int count) {
        inventory.put(roomType, count);
    }
}


// Search Service (Read-only access)
class RoomSearchService {

    public void searchAvailableRooms(RoomInventory inventory, List<Room> rooms) {

        System.out.println("\n===== Available Rooms =====");

        for (Room room : rooms) {

            int available = inventory.getAvailability(room.getRoomType());

            // Show only available rooms
            if (available > 0) {

                room.displayRoomDetails();
                System.out.println("Available Rooms: " + available);
                System.out.println("--------------------------------");
            }
        }

        System.out.println("Search completed successfully.");
    }
}


// Main Application
public class StayApp {

    public static void main(String[] args) {

        String appName = "Book My Stay - Hotel Booking System";
        String version = "Version 4.1";

        System.out.println("=====================================");
        System.out.println("Welcome to " + appName);
        System.out.println(version);
        System.out.println("=====================================");

        // Create Room Objects
        List<Room> rooms = new ArrayList<>();
        rooms.add(new SingleRoom());
        rooms.add(new DoubleRoom());
        rooms.add(new SuiteRoom());

        // Initialize inventory
        RoomInventory inventory = new RoomInventory();

        // Search service
        RoomSearchService searchService = new RoomSearchService();

        // Guest searches for available rooms
        searchService.searchAvailableRooms(inventory, rooms);

        System.out.println("\nApplication executed successfully.");
        System.out.println("Thank you for using Book My Stay!");
    }
}