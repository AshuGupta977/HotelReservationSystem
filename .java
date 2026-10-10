
import java.io.*;
import java.util.*;

class Room {
    int roomNumber;
    String category;
    double price;
    boolean available;

    Room(int roomNumber, String category, double price) {
        this.roomNumber = roomNumber;
        this.category = category;
        this.price = price;
        this.available = true;
    }
}

class Booking {
    String bookingId;
    String customerName;
    int roomNumber;
    String category;
    int nights;
    double total;
    String paymentStatus;

    Booking(String bookingId, String customerName, Room room,
            int nights, String paymentStatus) {
        this.bookingId = bookingId;
        this.customerName = customerName;
        this.roomNumber = room.roomNumber;
        this.category = room.category;
        this.nights = nights;
        this.total = room.price * nights;
        this.paymentStatus = paymentStatus;
    }

    String toFileString() {
        return bookingId + "|" + customerName.replace("|", "/")
                + "|" + roomNumber + "|" + category + "|"
                + nights + "|" + total + "|" + paymentStatus;
    }

    void display() {
        System.out.println("\n--- Booking Details ---");
        System.out.println("Booking ID: " + bookingId);
        System.out.println("Customer: " + customerName);
        System.out.println("Room Number: " + roomNumber);
        System.out.println("Category: " + category);
        System.out.println("Nights: " + nights);
        System.out.printf("Total Amount: Rs. %.2f%n", total);
        System.out.println("Payment: " + paymentStatus);
    }
}

public class HotelReservationSystem {
    static Scanner sc = new Scanner(System.in);
    static ArrayList<Room> rooms = new ArrayList<>();
    static ArrayList<Booking> bookings = new ArrayList<>();

    static final String FILE_NAME = "bookings.txt";
    static int nextId = 1001;

    public static void main(String[] args) {
        initializeRooms();
        loadBookings();

        int choice;

        do {
            System.out.println("\n===== HOTEL RESERVATION SYSTEM =====");
            System.out.println("1. Search Available Rooms");
            System.out.println("2. Book a Room");
            System.out.println("3. View All Bookings");
            System.out.println("4. Search Booking by ID");
            System.out.println("5. Cancel Booking");
            System.out.println("6. Exit");
            System.out.print("Enter choice: ");

            choice = readInt();

            switch (choice) {
                case 1:
                    searchRooms();
                    break;
                case 2:
                    bookRoom();
                    break;
                case 3:
                    viewBookings();
                    break;
                case 4:
                    searchBooking();
                    break;
                case 5:
                    cancelBooking();
                    break;
                case 6:
                    System.out.println("Thank you for using our system!");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } while (choice != 6);

        sc.close();
    }

    static void initializeRooms() {
        for (int i = 101; i <= 105; i++)
            rooms.add(new Room(i, "Standard", 1500));

        for (int i = 201; i <= 204; i++)
            rooms.add(new Room(i, "Deluxe", 2500));

        for (int i = 301; i <= 303; i++)
            rooms.add(new Room(i, "Suite", 4500));
    }

    static int readInt() {
        while (!sc.hasNextInt()) {
            System.out.print("Enter a valid whole number: ");
            sc.next();
        }
        return sc.nextInt();
    }

    static void searchRooms() {
        sc.nextLine();
        System.out.print("Enter category (Standard/Deluxe/Suite): ");
        String category = sc.nextLine().trim();

        boolean found = false;
        System.out.println("\nAvailable Rooms:");

        for (Room room : rooms) {
            if (room.available &&
                room.category.equalsIgnoreCase(category)) {
                System.out.printf("Room %d | %s | Rs. %.2f per night%n",
                        room.roomNumber, room.category, room.price);
                found = true;
            }
        }

        if (!found)
            System.out.println("No rooms available in this category.");
    }

    static void bookRoom() {
        sc.nextLine();
        System.out.print("Enter customer name: ");
        String name = sc.nextLine().trim();

        if (name.isEmpty()) {
            System.out.println("Customer name cannot be empty.");
            return;
        }

        System.out.print("Enter room category (Standard/Deluxe/Suite): ");
        String category = sc.nextLine().trim();

        System.out.print("Enter number of nights: ");
        int nights = readInt();

        if (nights <= 0) {
            System.out.println("Number of nights must be positive.");
            return;
        }

        Room selected = null;

        for (Room room : rooms) {
            if (room.available &&
                room.category.equalsIgnoreCase(category)) {
                selected = room;
                break;
            }
        }

        if (selected == null) {
            System.out.println("No available room in that category.");
            return;
        }

        double total = selected.price * nights;

        System.out.printf("Room: %d%n", selected.roomNumber);
        System.out.printf("Total amount: Rs. %.2f%n", total);
        System.out.print("Proceed with simulated payment? (yes/no): ");

        String payment = sc.next().trim();

        if (!payment.equalsIgnoreCase("yes")) {
            System.out.println("Booking cancelled before payment.");
            return;
        }

        // Payment simulation only; no real money is processed.
        selected.available = false;

        Booking booking = new Booking(
                "B" + nextId++, name, selected, nights, "Paid (Simulated)");

        bookings.add(booking);

        if (saveBookings()) {
            System.out.println("Booking successful!");
            booking.display();
        } else {
            bookings.remove(booking);
            selected.available = true;
            System.out.println("Could not save booking. Please retry.");
        }
    }

    static void viewBookings() {
        if (bookings.isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }

        for (Booking booking : bookings)
            booking.display();
    }

    static void searchBooking() {
        sc.nextLine();
        System.out.print("Enter booking ID: ");
        String id = sc.nextLine().trim();

        Booking booking = findBooking(id);

        if (booking != null)
            booking.display();
        else
            System.out.println("Booking not found.");
    }

    static Booking findBooking(String id) {
        for (Booking booking : bookings) {
            if (booking.bookingId.equalsIgnoreCase(id))
                return booking;
        }
        return null;
    }

    static void cancelBooking() {
        sc.nextLine();
        System.out.print("Enter booking ID to cancel: ");
        String id = sc.nextLine().trim();

        Booking booking = findBooking(id);

        if (booking == null) {
            System.out.println("Booking not found.");
            return;
        }

        bookings.remove(booking);

        for (Room room : rooms) {
            if (room.roomNumber == booking.roomNumber) {
                room.available = true;
                break;
            }
        }

        if (saveBookings()) {
            System.out.println("Booking cancelled successfully.");
        } else {
            // Restore the booking if saving failed.
            bookings.add(booking);
            for (Room room : rooms) {
                if (room.roomNumber == booking.roomNumber) {
                    room.available = false;
                    break;
                }
            }
            System.out.println("Could not save cancellation.");
        }
    }

    static boolean saveBookings() {
        try (PrintWriter writer =
                new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Booking booking : bookings)
                writer.println(booking.toFileString());
            return true;
        } catch (IOException e) {
            System.out.println("File error: " + e.getMessage());
            return false;
        }
    }

    static void loadBookings() {
        File file = new File(FILE_NAME);

        if (!file.exists())
            return;

        try (BufferedReader reader =
                new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\\|", -1);

                if (data.length != 7)
                    continue;

                try {
                    String id = data[0];
                    String name = data[1];
                    int roomNumber = Integer.parseInt(data[2]);
                    String category = data[3];
                    int nights = Integer.parseInt(data[4]);
                    String status = data[6];

                    if (nights <= 0 ||
                        !status.equals("Paid (Simulated)"))
                        continue;

                    Room room = null;

                    for (Room r : rooms) {
                        if (r.roomNumber == roomNumber &&
                            r.category.equals(category)) {
                            room = r;
                            break;
                        }
                    }

                    if (room == null || !room.available)
                        continue;

                    Booking booking = new Booking(
                            id, name, room, nights, status);

                    // Keep the stored total if it differs from
                    // the current room price.
                    booking.total = Double.parseDouble(data[5]);

                    bookings.add(booking);
                    room.available = false;

                    if (id.matches("B\\d+")) {
                        nextId = Math.max(nextId,
                                Integer.parseInt(id.substring(1)) + 1);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Skipping invalid booking record.");
                }
            }
        } catch (IOException e) {
            System.out.println("Could not load bookings: "
                    + e.getMessage());
        }
    }
}
