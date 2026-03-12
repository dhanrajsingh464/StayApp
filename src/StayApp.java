```java
import java.util.*;
import java.io.*;

// Custom Exception
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

// Reservation class
class Reservation implements Serializable {

    private static final long serialVersionUID = 1L;

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

    public String getReservationId() { return reservationId; }
    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }

    public void setRoomId(String roomId) { this.roomId = roomId; }
    public String getRoomId() { return roomId; }

    public boolean isCancelled() { return cancelled; }

    public void cancelReservation() { cancelled = true; }

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
class RoomInventory implements Serializable {

    private static final long serialVersionUID = 1L;

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
class BookingHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Reservation> confirmedBookings = new ArrayList<>();

    public synchronized void addReservation(Reservation reservation) {
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


// Persistence Service
class PersistenceService {

    private static final String FILE_NAME = "hotel_state.dat";

    public void saveState(RoomInventory inventory, BookingHistory history) {

        try (ObjectOutputStream out =
                     new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {

            out.writeObject(inventory);
            out.writeObject(history);

            System.out.println("\nSystem state saved successfully.");

        } catch (IOException e) {
            System.out.println("Error saving system state.");
        }
    }

    public Object[] loadState() {

        try (ObjectInputStream in =
                     new ObjectInputStream(new FileInputStream(FILE_NAME))) {

            RoomInventory inventory = (RoomInventory) in.readObject();
            BookingHistory history = (BookingHistory) in.readObject();

            System.out.println("System state restored from file.");

            return new Object[]{inventory, history};

        } catch (Exception e) {

            System.out.println("No saved state found. Starting fresh.");

            return null;
        }
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

            if (reservation == null) break;

            String roomType = reservation.getRoomType();

            try {

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

        System.out.println("=====================================");
        System.out.println("Book My Stay - Persistence & Recovery");
        System.out.println("Version 12.0");
        System.out.println("=====================================");

        PersistenceService persistenceService = new PersistenceService();

        RoomInventory inventory;
        BookingHistory history;

        Object[] state = persistenceService.loadState();

        if (state != null) {
            inventory = (RoomInventory) state[0];
            history = (BookingHistory) state[1];
        } else {
            inventory = new RoomInventory();
            history = new BookingHistory();
        }

        BookingRequestQueue requestQueue = new BookingRequestQueue();
        RoomAllocationService allocationService = new RoomAllocationService();

        requestQueue.addRequest(new Reservation("RES-201", "Alice", "Single Room"));
        requestQueue.addRequest(new Reservation("RES-202", "Bob", "Double Room"));
        requestQueue.addRequest(new Reservation("RES-203", "Charlie", "Suite Room"));

        ConcurrentBookingProcessor t1 =
                new ConcurrentBookingProcessor(requestQueue, inventory, history, allocationService);

        ConcurrentBookingProcessor t2 =
                new ConcurrentBookingProcessor(requestQueue, inventory, history, allocationService);

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        BookingReportService reportService = new BookingReportService();
        reportService.displayAllBookings(history.getBookings());

        persistenceService.saveState(inventory, history);

        System.out.println("\nSystem shutdown complete.");
    }
}

