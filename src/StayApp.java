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


public class StayApp {

    public static void main(String[] args) {

        String appName = "Book My Stay - Hotel Booking System";
        String version = "Version 2.1";

        System.out.println("=====================================");
        System.out.println("Welcome to " + appName);
        System.out.println(version);
        System.out.println("=====================================");

        // Create Room Objects
        Room single = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suite = new SuiteRoom();

        // Static availability variables
        int singleRoomAvailability = 5;
        int doubleRoomAvailability = 3;
        int suiteRoomAvailability = 2;

        System.out.println("\nRoom Details & Availability\n");

        single.displayRoomDetails();
        System.out.println("Available Rooms: " + singleRoomAvailability);
        System.out.println("--------------------------------");

        doubleRoom.displayRoomDetails();
        System.out.println("Available Rooms: " + doubleRoomAvailability);
        System.out.println("--------------------------------");

        suite.displayRoomDetails();
        System.out.println("Available Rooms: " + suiteRoomAvailability);
        System.out.println("--------------------------------");

        System.out.println("\nApplication executed successfully.");
        System.out.println("Thank you for using Book My Stay!");
    }
}