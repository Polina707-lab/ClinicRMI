package rmiClinic;

import java.rmi.Naming;
import java.util.*;
import rmiClinic.Clinic.RemoteClinic;

public class ClinicClient {

    public static void main(String[] args) {

        try {
            RemoteClinic clinic =
                    (RemoteClinic) Naming.lookup("rmi://DESKTOP-PUTBKDN/Clinic");

            Scanner in = new Scanner(System.in);

            System.out.println("Connected to Clinic RMI server.");
            System.out.println("=================================");

            while (true) {
                printMenu();
                String cmd = in.nextLine().trim();

                switch (cmd) {

                  
                    case "1":
                        System.out.println("\nRequesting available slots...");
                        try {
                            String[] slots = clinic.getFreeSlots();

                            if (slots.length == 0)
                                System.out.println("No free slots available.");
                            else {
                                System.out.println("Available slots:");
                                for (String s : slots)
                                    System.out.println("  - " + s);
                            }

                        } catch (Exception e) {
                            System.out.println("\nServer offline. Client will exit.");
                            return;
                        }
                        break;

                   
                    case "2":
                        System.out.println("\nRequesting available slots...");
                        String[] slots;

                        try {
                            slots = clinic.getFreeSlots();
                        } catch (Exception e) {
                            System.out.println("\nServer offline. Client will exit.");
                            return;
                        }

                        if (slots.length == 0) {
                            System.out.println("No free slots available.");
                            break;
                        }

                        System.out.println("Available slots:");
                        for (String s : slots)
                            System.out.println("  - " + s);

                        System.out.print("Enter one of them (e.g., Mon 09:00): ");
                        String time = in.nextLine().trim();

                        System.out.println("-> You selected: [" + time + "]");

                        if (!Arrays.asList(slots).contains(time)) {
                            System.out.println("--- Invalid slot. Try again. ---");
                            break;
                        }

                        System.out.print("Enter full name: ");
                        String name = in.nextLine().trim();

                        System.out.print("Enter phone number: ");
                        String phone = in.nextLine().trim();

                        System.out.print("Enter complaint: ");
                        String complaint = in.nextLine().trim();

                        System.out.println("-> Sending appointment request...");

                        try {
                            clinic.bookAppointment(time, name, phone, complaint);
                            System.out.println("--- Appointment successfully created! ---");

                        } catch (Exception e) {
                            System.out.println("\nServer offline. Client will exit.");
                            return;
                        }

                        break;

                    case "3":
                        System.out.println("Disconnecting...");
                        return;

                    default:
                        System.out.println("Invalid input.");
                }
            }

        } catch (Exception e) {
            System.out.println("\nUnable to connect to server.");
            System.out.println("Reason: " + e.getMessage());
        }
    }

    static void printMenu() {
        System.out.println("\nSelect an action:");
        System.out.println("1. Show available slots");
        System.out.println("2. Make an appointment");
        System.out.println("3. Exit");
        System.out.print("Your choice: ");
    }
}
