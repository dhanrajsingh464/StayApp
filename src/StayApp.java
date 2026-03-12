import java.util.HashMap;
import java.util.Map;

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


// New Class for Inventory Management (Version 3.0)
class RoomInventory {

    private HashMap<String, Integer> inventory;

    public RoomInventory() {

        inventory = new HashMap<>();

        // Initialize availability
        inventory.put("Single Room", 5);
        inventory.put("Double Room", 3);
        inventory.put("Suite Room", 2);
    }

    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    public void updateAvailability(String roomType, int count) {
        inventory.put(roomType, count);
    }

    public void displayInventory() {

        System.out.println("\n===== Current Room Inventory =====");

        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue() + " rooms available");
        }

        System.out.println("==================================");
    }
}


// Main Application
public class StayApp {

    public static void main(String[] args) {

        String appName = "Book My Stay - Hotel Booking System";
        String version = "Version 3.1";

        System.out.println("=====================================");
        System.out.println("Welcome to " + appName);
        System.out.println(version);
        System.out.println("=====================================");

        // Create Room Objects
        Room single = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suite = new SuiteRoom();

        // Initialize centralized inventory
        RoomInventory inventory = new RoomInventory();

        System.out.println("\nRoom Details\n");

        single.displayRoomDetails();
        System.out.println("Available Rooms: " + inventory.getAvailability(single.getRoomType()));
        System.out.println("--------------------------------");

        doubleRoom.displayRoomDetails();
        System.out.println("Available Rooms: " + inventory.getAvailability(doubleRoom.getRoomType()));
        System.out.println("--------------------------------");

        suite.displayRoomDetails();
        System.out.println("Available Rooms: " + inventory.getAvailability(suite.getRoomType()));
        System.out.println("--------------------------------");

        // Display full inventory
        inventory.displayInventory();

        System.out.println("\nApplication executed successfully.");
        System.out.println("Thank you for using Book My Stay!");
    }
}