import java.util.*;

// Reservation class
class Reservation {

    private String reservationId;
    private String guestName;
    private String roomType;
    private String roomId;   // NEW: store allocated room id

    public Reservation(String reservationId, String guestName, String roomType) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomId(String roomId) {   // NEW
        this.roomId = roomId;
    }

    public String getRoomId() {   // NEW
        return roomId;
    }

    public void displayReservation() {
        System.out.println("Reservation ID: " + reservationId +
                " | Guest: " + guestName +
                " | Room Type: " + roomType +
                " | Room ID: " + roomId);
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


// Booking History
class BookingHistory {

    private List<Reservation> confirmedBookings = new ArrayList<>();

    public void addReservation(Reservation reservation) {
        confirmedBookings.add(reservation);
    }

    public List<Reservation> getBookings() {
        return confirmedBookings;
    }
}


// Booking Report Service
class BookingReportService {

    public void displayAllBookings(List<Reservation> reservations) {

        System.out.println("\n===== Booking History =====");

        for (Reservation reservation : reservations) {
            reservation.displayReservation();
        }

        System.out.println("============================");
    }

    public void generateSummary(List<Reservation> reservations) {

        System.out.println("\n===== Booking Summary Report =====");

        Map<String, Integer> roomTypeCount = new HashMap<>();

        for (Reservation reservation : reservations) {

            String roomType = reservation.getRoomType();

            roomTypeCount.put(roomType,
                    roomTypeCount.getOrDefault(roomType, 0) + 1);
        }

        for (Map.Entry<String, Integer> entry : roomTypeCount.entrySet()) {
            System.out.println(entry.getKey() + " bookings: " + entry.getValue());
        }

        System.out.println("Total Reservations: " + reservations.size());
        System.out.println("===================================");
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


    public void processBookings(BookingRequestQueue requestQueue,
                                RoomInventory inventory,
                                BookingHistory history) {

        Queue<Reservation> queue = requestQueue.getQueue();

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

                reservation.setRoomId(roomId);   // NEW: save roomId in reservation
                history.addReservation(reservation);

                System.out.println("Reservation Confirmed!");
                reservation.displayReservation();
                System.out.println("----------------------------------");

            } else {

                System.out.println("Reservation Failed for "
                        + reservation.getGuestName()
                        + " (No rooms available for " + roomType + ")");
            }
        }
    }
}


// Main Application
public class UseCase8BookingHistoryReport {

    public static void main(String[] args) {

        String appName = "Book My Stay - Hotel Booking System";
        String version = "Version 8.1";

        System.out.println("=====================================");
        System.out.println("Welcome to " + appName);
        System.out.println(version);
        System.out.println("=====================================");

        BookingRequestQueue requestQueue = new BookingRequestQueue();
        RoomInventory inventory = new RoomInventory();
        BookingHistory history = new BookingHistory();
        RoomAllocationService bookingService = new RoomAllocationService();

        // Booking requests
        requestQueue.addRequest(new Reservation("RES-101", "Alice", "Single Room"));
        requestQueue.addRequest(new Reservation("RES-102", "Bob", "Double Room"));
        requestQueue.addRequest(new Reservation("RES-103", "Charlie", "Suite Room"));
        requestQueue.addRequest(new Reservation("RES-104", "David", "Single Room"));

        // Process bookings
        bookingService.processBookings(requestQueue, inventory, history);

        // Reporting
        BookingReportService reportService = new BookingReportService();

        reportService.displayAllBookings(history.getBookings());
        reportService.generateSummary(history.getBookings());

        System.out.println("\nBooking history stored successfully.");
    }
}