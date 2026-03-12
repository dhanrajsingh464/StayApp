import java.util.*;

// Reservation class
class Reservation {

    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }
}


// Booking Request Queue
class BookingRequestQueue {

    private Queue<Reservation> bookingQueue;

    public BookingRequestQueue() {
        bookingQueue = new LinkedList<>();
    }

    public void addRequest(Reservation reservation) {
        bookingQueue.offer(reservation);
        System.out.println("Booking request added for " + reservation.getGuestName());
    }

    public Queue<Reservation> getQueue() {
        return bookingQueue;
    }
}


// Inventory Service
class RoomInventory {

    private HashMap<String, Integer> inventory;

    public RoomInventory() {

        inventory = new HashMap<>();

        inventory.put("Single Room", 2);
        inventory.put("Double Room", 2);
        inventory.put("Suite Room", 1);
    }

    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    public void decreaseAvailability(String roomType) {
        inventory.put(roomType, inventory.get(roomType) - 1);
    }
}


// Room Allocation Service
class RoomAllocationService {

    private HashMap<String, Set<String>> allocatedRooms = new HashMap<>();
    private Set<String> usedRoomIds = new HashSet<>();


    private String generateRoomId(String roomType) {

        String prefix = roomType.substring(0, 3).toUpperCase();
        String id;

        do {
            id = prefix + "-" + (int)(Math.random() * 1000);
        } while (usedRoomIds.contains(id));

        usedRoomIds.add(id);
        return id;
    }


    public List<String> processBookings(BookingRequestQueue requestQueue, RoomInventory inventory) {

        Queue<Reservation> queue = requestQueue.getQueue();
        List<String> confirmedReservations = new ArrayList<>();

        System.out.println("\n===== Processing Booking Requests =====");

        while (!queue.isEmpty()) {

            Reservation reservation = queue.poll();
            String roomType = reservation.getRoomType();

            if (inventory.getAvailability(roomType) > 0) {

                String roomId = generateRoomId(roomType);

                allocatedRooms
                        .computeIfAbsent(roomType, k -> new HashSet<>())
                        .add(roomId);

                inventory.decreaseAvailability(roomType);

                confirmedReservations.add(roomId);

                System.out.println("Reservation Confirmed!");
                System.out.println("Guest: " + reservation.getGuestName());
                System.out.println("Room Type: " + roomType);
                System.out.println("Assigned Room ID: " + roomId);
                System.out.println("----------------------------------");

            } else {

                System.out.println("Reservation Failed for "
                        + reservation.getGuestName()
                        + " (No rooms available for " + roomType + ")");
            }
        }

        return confirmedReservations;
    }
}


// Add-On Service class
class AddOnService {

    private String serviceName;
    private double price;

    public AddOnService(String serviceName, double price) {
        this.serviceName = serviceName;
        this.price = price;
    }

    public String getServiceName() {
        return serviceName;
    }

    public double getPrice() {
        return price;
    }
}


// Add-On Service Manager
class AddOnServiceManager {

    private Map<String, List<AddOnService>> reservationServices = new HashMap<>();


    public void addService(String reservationId, AddOnService service) {

        reservationServices
                .computeIfAbsent(reservationId, k -> new ArrayList<>())
                .add(service);

        System.out.println("Service added to Reservation " + reservationId
                + " : " + service.getServiceName());
    }


    public double calculateServiceCost(String reservationId) {

        List<AddOnService> services = reservationServices.get(reservationId);
        double total = 0;

        if (services != null) {
            for (AddOnService service : services) {
                total += service.getPrice();
            }
        }

        return total;
    }


    public void displayServices(String reservationId) {

        System.out.println("\nServices for Reservation " + reservationId);

        List<AddOnService> services = reservationServices.get(reservationId);

        if (services == null) {
            System.out.println("No services selected.");
            return;
        }

        for (AddOnService s : services) {
            System.out.println(s.getServiceName() + " - $" + s.getPrice());
        }
    }
}


// Main Application
public class UseCase7AddOnServiceSelection {

    public static void main(String[] args) {

        String appName = "Book My Stay - Hotel Booking System";
        String version = "Version 7.1";

        System.out.println("=====================================");
        System.out.println("Welcome to " + appName);
        System.out.println(version);
        System.out.println("=====================================");

        BookingRequestQueue requestQueue = new BookingRequestQueue();
        RoomInventory inventory = new RoomInventory();
        RoomAllocationService bookingService = new RoomAllocationService();

        // Booking requests
        requestQueue.addRequest(new Reservation("Alice", "Single Room"));
        requestQueue.addRequest(new Reservation("Bob", "Double Room"));

        // Process bookings
        List<String> reservationIds = bookingService.processBookings(requestQueue, inventory);

        // Add-on services
        AddOnServiceManager serviceManager = new AddOnServiceManager();

        AddOnService breakfast = new AddOnService("Breakfast", 20);
        AddOnService spa = new AddOnService("Spa Access", 50);
        AddOnService pickup = new AddOnService("Airport Pickup", 40);

        // Attach services to first reservation
        if (!reservationIds.isEmpty()) {

            String reservationId = reservationIds.get(0);

            serviceManager.addService(reservationId, breakfast);
            serviceManager.addService(reservationId, spa);
            serviceManager.addService(reservationId, pickup);

            serviceManager.displayServices(reservationId);

            double total = serviceManager.calculateServiceCost(reservationId);

            System.out.println("\nTotal Add-On Cost: $" + total);
        }

        System.out.println("\nCore booking and inventory remain unchanged.");
    }
}