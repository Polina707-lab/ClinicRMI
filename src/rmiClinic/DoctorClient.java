package rmiClinic;

import java.rmi.Naming;
import java.util.Scanner;

public class DoctorClient {

    public static void main(String[] args) {
        try {
        	Clinic.RemoteClinic clinic =
        		    (Clinic.RemoteClinic) Naming.lookup("rmi://DESKTOP-PUTBKDN/Clinic");

            Scanner in = new Scanner(System.in);

            System.out.print("Enter doctor password: ");
            String pass = in.nextLine().trim();

            if (!clinic.doctorLogin(pass)) {
                System.out.println("Access denied. Wrong password.");
                return;
            }

            System.out.println("Welcome, Doctor!");
            System.out.println("======================");

            while (true) {
                System.out.println("\nDoctor menu:");
                System.out.println("1. View all appointments");
                System.out.println("2. Cancel appointment");
                System.out.println("3. Exit");
                System.out.print("Your choice: ");

                String cmd = in.nextLine().trim();

                switch (cmd) {
                    case "1":
                        String[] all = clinic.getAllAppointments();
                        if (all.length == 0)
                            System.out.println("No appointments.");
                        else {
                            System.out.println("Appointments:");
                            for (String s : all)
                                System.out.println(" - " + s);
                        }
                        break;

                    case "2":
                        System.out.print("Enter appointment time to cancel: ");
                        String time = in.nextLine().trim();

                        try {
                            clinic.cancelAppointment(time);
                            System.out.println("Appointment cancelled.");
                        } catch (Clinic.ClinicException e) {
                            System.err.println("Error: " + e.getMessage());
                        }
                        break;

                    case "3":
                        System.out.println("Goodbye!");
                        return;

                    default:
                        System.out.println("Invalid option.");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
