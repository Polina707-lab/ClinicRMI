package rmiClinic;

import java.rmi.Remote;
import java.rmi.RemoteException;

public class Clinic {

    public interface RemoteClinic extends Remote {

        String[] getFreeSlots()
                throws RemoteException, ClinicException;

        void bookAppointment(String datetime, String fullName,
                             String phone, String complaint)
                throws RemoteException, ClinicException;

        void cancelAppointment(String datetime)
                throws RemoteException, ClinicException;

        String[] getAllAppointments()
                throws RemoteException, ClinicException;
        
        boolean doctorLogin(String password) 
    			throws RemoteException;
    }
    
    
    
    
    public static class ClinicException extends Exception {
        public ClinicException(String msg) {
            super(msg);
        }
    }
}
