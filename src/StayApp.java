
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


// Thread-safe Booking Request Queue
class BookingRequestQueue {

    private Queue<Reservation> bookingQueue;

    public BookingRequestQueue() {
        bookingQueue = new LinkedList<>();
    }

    public synchronized void addRequest(Reservation reservation) {
        bookingQueue.offer(reservation);
        System.out.println("Booking request added for " + reservation.getGuestName());
    }

    public synchronized Reservation getNextRequest() {
        return bookingQueue.poll();
    }

    public synchronized boolean isEmpty() {
        return bookingQueue.isEmpty();
    }
}


// Thread-safe Room Inventory
class RoomInventory {

    private HashMap<String, Integer> inventory;

    public RoomInventory() {

        inventory = new HashMap<>();

        inventory.put("Single Room", 2);
        inventory.put("Double Room", 2);
        inventory.put("Suite Room", 1);
    }

    public synchronized boolean isValidRoomType(String roomType) {
        return inventory.containsKey(roomType);
    }

    public synchronized int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    public synchronized void decreaseAvailability(String roomType) throws InvalidBookingException {

        int available = getAvailability(roomType);

        if (available <= 0) {
            throw new InvalidBookingException("No rooms available for " + roomType);
        }

        inventory.put(roomType, available - 1);
    }

    public synchronized void increaseAvailability(String roomType) {
        inventory.put(roomType, inventory.get(roomType) + 1);
    }
}


// Booking History
class BookingHistory {

    private List<Reservation> confirmedBookings = new ArrayList<>();

    public synchronized void addReservation(Reservation reservation) {
        confirmedBookings.add(reservation);
    }

    public List<Reservation> getBookings() {
        return confirmedBookings;
    }

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

    private Set<String> usedRoomIds = new HashSet<>();

    public synchronized String generateRoomId(String roomType) {

        String prefix = roomType.substring(0, 3).toUpperCase();
        String id;

        do {
            id = prefix + "-" + (int)(Math.random() * 1000);
        } while (usedRoomIds.contains(id));

        usedRoomIds.add(id);
        return id;
    }
}


// Concurrent Booking Processor (Thread)
class ConcurrentBookingProcessor extends Thread {

    private BookingRequestQueue requestQueue;
    private RoomInventory inventory;
    private BookingHistory history;
    private RoomAllocationService allocationService;

    public ConcurrentBookingProcessor(BookingRequestQueue requestQueue,
                                      RoomInventory inventory,
                                      BookingHistory history,
                                      RoomAllocationService allocationService) {

        this.requestQueue = requestQueue;
        this.inventory = inventory;
        this.history = history;
        this.allocationService = allocationService;
    }

    @Override
    public void run() {

        while (true) {

            Reservation reservation = requestQueue.getNextRequest();

            if (reservation == null) {
                break;
            }

            String roomType = reservation.getRoomType();

            try {

                if (!inventory.isValidRoomType(roomType)) {
                    throw new InvalidBookingException("Invalid Room Type: " + roomType);
                }

                inventory.decreaseAvailability(roomType);

                String roomId = allocationService.generateRoomId(roomType);

                reservation.setRoomId(roomId);

                history.addReservation(reservation);

                System.out.println(Thread.currentThread().getName()
                        + " confirmed booking for "
                        + reservation.getGuestName());

            } catch (InvalidBookingException e) {

                System.out.println("Booking failed for "
                        + reservation.getGuestName()
                        + " : " + e.getMessage());
            }
        }
    }
}


// Main Application
public class StayApp {

    public static void main(String[] args) {

        String appName = "Book My Stay - Hotel Booking System";
        String version = "Version 11.0 (Concurrent Booking Simulation)";

        System.out.println("=====================================");
        System.out.println("Welcome to " + appName);
        System.out.println(version);
        System.out.println("=====================================");

        BookingRequestQueue requestQueue = new BookingRequestQueue();
        RoomInventory inventory = new RoomInventory();
        BookingHistory history = new BookingHistory();
        RoomAllocationService allocationService = new RoomAllocationService();

        // Multiple booking requests
        requestQueue.addRequest(new Reservation("RES-101", "Alice", "Single Room"));
        requestQueue.addRequest(new Reservation("RES-102", "Bob", "Double Room"));
        requestQueue.addRequest(new Reservation("RES-103", "Charlie", "Suite Room"));
        requestQueue.addRequest(new Reservation("RES-104", "David", "Single Room"));
        requestQueue.addRequest(new Reservation("RES-105", "Emma", "Double Room"));
        requestQueue.addRequest(new Reservation("RES-106", "Frank", "Suite Room"));

        System.out.println("\n===== Concurrent Booking Processing =====");

        ConcurrentBookingProcessor t1 =
                new ConcurrentBookingProcessor(requestQueue, inventory, history, allocationService);

        ConcurrentBookingProcessor t2 =
                new ConcurrentBookingProcessor(requestQueue, inventory, history, allocationService);

        ConcurrentBookingProcessor t3 =
                new ConcurrentBookingProcessor(requestQueue, inventory, history, allocationService);

        t1.start();
        t2.start();
        t3.start();

        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        BookingReportService reportService = new BookingReportService();
        reportService.displayAllBookings(history.getBookings());

        System.out.println("\nAll concurrent bookings processed safely.");
    }
}

