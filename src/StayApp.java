import java.util.*;

// Custom Exception
class InvalidBookingException extends Exception {

    public InvalidBookingException(String message) {
        super(message);
    }
}


// Reservation class
class Reservation {

    private String reservationId;
    private String guestName;
    private String roomType;
    private String roomId;
    private boolean cancelled = false;

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

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomId() {
        return roomId;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void cancelReservation() {
        cancelled = true;
    }

    public void displayReservation() {
        System.out.println("Reservation ID: " + reservationId +
                " | Guest: " + guestName +
                " | Room Type: " + roomType +
                " | Room ID: " + roomId +
                " | Status: " + (cancelled ? "Cancelled" : "Confirmed"));
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

    public boolean isValidRoomType(String roomType) {
        return inventory.containsKey(roomType);
    }

    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    public void decreaseAvailability(String roomType) throws InvalidBookingException {

        int available = getAvailability(roomType);

        if (available <= 0) {
            throw new InvalidBookingException("No rooms available for " + roomType);
        }

        inventory.put(roomType, available - 1);
    }

    // Inventory rollback
    public void increaseAvailability(String roomType) {
        inventory.put(roomType, inventory.get(roomType) + 1);
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

    // Find reservation for cancellation
    public Reservation findReservation(String reservationId) {

        for (Reservation r : confirmedBookings) {
            if (r.getReservationId().equals(reservationId)) {
                return r;
            }
        }

        return null;
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

            try {

                if (!inventory.isValidRoomType(roomType)) {
                    throw new InvalidBookingException("Invalid Room Type: " + roomType);
                }

                inventory.decreaseAvailability(roomType);

                String roomId = generateRoomId(roomType);

                reservation.setRoomId(roomId);

                allocatedRooms
                        .computeIfAbsent(roomType, k -> new HashSet<>())
                        .add(roomId);

                history.addReservation(reservation);

                System.out.println("Reservation Confirmed!");
                reservation.displayReservation();
                System.out.println("----------------------------------");

            } catch (InvalidBookingException e) {

                System.out.println("Booking Failed for "
                        + reservation.getGuestName());
                System.out.println("Reason: " + e.getMessage());
                System.out.println("----------------------------------");
            }
        }
    }
}


// Cancellation Service
class CancellationService {

    private Stack<String> rollbackStack = new Stack<>();

    public void cancelBooking(String reservationId,
                              BookingHistory history,
                              RoomInventory inventory) {

        Reservation reservation = history.findReservation(reservationId);

        if (reservation == null) {
            System.out.println("Cancellation Failed: Reservation not found.");
            return;
        }

        if (reservation.isCancelled()) {
            System.out.println("Cancellation Failed: Already cancelled.");
            return;
        }

        rollbackStack.push(reservation.getRoomId());

        inventory.increaseAvailability(reservation.getRoomType());

        reservation.cancelReservation();

        System.out.println("\nCancellation Successful for Reservation: " + reservationId);
        System.out.println("Released Room ID: " + rollbackStack.peek());
    }
}


// Main Application
public class StayApp {

    public static void main(String[] args) {

        String appName = "Book My Stay - Hotel Booking System";
        String version = "Version 10.0";

        System.out.println("=====================================");
        System.out.println("Welcome to " + appName);
        System.out.println(version);
        System.out.println("=====================================");

        BookingRequestQueue requestQueue = new BookingRequestQueue();
        RoomInventory inventory = new RoomInventory();
        BookingHistory history = new BookingHistory();
        RoomAllocationService bookingService = new RoomAllocationService();
        CancellationService cancellationService = new CancellationService();

        requestQueue.addRequest(new Reservation("RES-101", "Alice", "Single Room"));
        requestQueue.addRequest(new Reservation("RES-102", "Bob", "Double Room"));
        requestQueue.addRequest(new Reservation("RES-103", "Charlie", "Suite Room"));

        bookingService.processBookings(requestQueue, inventory, history);

        BookingReportService reportService = new BookingReportService();
        reportService.displayAllBookings(history.getBookings());

        // Cancel booking
        cancellationService.cancelBooking("RES-102", history, inventory);

        reportService.displayAllBookings(history.getBookings());

        System.out.println("\nSystem state restored after cancellation.");
    }
}
