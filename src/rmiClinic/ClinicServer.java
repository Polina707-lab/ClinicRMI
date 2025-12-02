package rmiClinic;

import java.io.*;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.server.RemoteServer;
import java.util.*;
import rmiClinic.Clinic.RemoteClinic;
import rmiClinic.Clinic.ClinicException;

public class ClinicServer 
        extends UnicastRemoteObject 
        implements Clinic.RemoteClinic {

    private Map<String, Appointment> schedule = new HashMap<>();
    private List<String> weeklySlots = new ArrayList<>();

    private static final String FILE_APPOINTMENTS = "data/appointments.dat";
    private static final String FILE_SLOTS = "data/slots.dat";

    private static final String DOCTOR_PASSWORD = "doc111";

    public ClinicServer() throws RemoteException {
        super();
        loadSchedule();
        loadOrGenerateSlots();
        System.out.println("ClinicServer initialized.");
    }

    private void logClient() {
        try {
            String host = RemoteServer.getClientHost();
            System.out.println("Client connected from: " + host);
        } catch (Exception ignored) { }
    }

    @Override
    public synchronized String[] getFreeSlots() {
        logClient();
        return weeklySlots.stream()
                .filter(slot -> !schedule.containsKey(slot))
                .toArray(String[]::new);
    }

    @Override
    public synchronized void bookAppointment(
            String datetime, String fullName,
            String phone, String complaint) throws ClinicException {

        logClient();

        if (schedule.containsKey(datetime))
            throw new ClinicException("Time slot already taken: " + datetime);

        Appointment a = new Appointment(datetime, fullName, phone, complaint);
        schedule.put(datetime, a);
        saveSchedule();

        System.out.println("Created: " + a);
    }

    @Override
    public synchronized void cancelAppointment(String datetime)
            throws ClinicException {

        logClient();

        if (!schedule.containsKey(datetime))
            throw new ClinicException("No appointment at: " + datetime);

        schedule.remove(datetime);
        saveSchedule();

        System.out.println("Canceled: " + datetime);
    }

    @Override
    public synchronized String[] getAllAppointments() {
        logClient();
        return schedule.values().stream()
                .map(Appointment::toString)
                .toArray(String[]::new);
    }


    @SuppressWarnings("unchecked")
    private void loadSchedule() {
        File f = new File(FILE_APPOINTMENTS);
        if (!f.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            schedule = (Map<String, Appointment>) ois.readObject();
            System.err.println("Appointments loaded: " + schedule.size());
        } catch (Exception e) {
            System.err.println("Error loading appointments: " + e.getMessage());
        }
    }

    private void saveSchedule() {
        try {
            File f = new File(FILE_APPOINTMENTS);
            f.getParentFile().mkdirs();

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f))) {
                oos.writeObject(schedule);
            }

        } catch (IOException e) {
            System.err.println("Error saving appointments: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadOrGenerateSlots() {
        File f = new File(FILE_SLOTS);
        if (f.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
                weeklySlots = (List<String>) ois.readObject();
                return;
            } catch (Exception ignored) { }
        }
        weeklySlots = generateSlots();
        saveSlots();
    }

    private List<String> generateSlots() {
        List<String> allSlots = new ArrayList<>();
        String[] days = { "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };

        Random rnd = new Random();
        for (String d : days) {
            for (int i = 9; i < 17; i++) {
                allSlots.add(String.format("%s %02d:00", d, i));
                if (rnd.nextBoolean())
                    allSlots.add(String.format("%s %02d:30", d, i));
            }
        }
        return allSlots;
    }

    private void saveSlots() {
        try {
            File f = new File(FILE_SLOTS);
            f.getParentFile().mkdirs();

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f))) {
                oos.writeObject(weeklySlots);
            }

        } catch (IOException e) {
            System.err.println("Error saving slots: " + e.getMessage());
        }
    }


    @Override
    public boolean doctorLogin(String password) {
        logClient();
        boolean ok = DOCTOR_PASSWORD.equals(password);
        System.out.println(ok ? "Doctor logged in." : "Doctor login failed.");
        return ok;
    }


    public static void main(String[] args) {
        try {
            ClinicServer server = new ClinicServer();
            Naming.rebind("Clinic", server);
            System.out.println("Clinic RMI server ready.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
