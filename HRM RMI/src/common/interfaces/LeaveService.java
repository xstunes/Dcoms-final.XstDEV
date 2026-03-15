package common.interfaces;

import common.models.LeaveApplication;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.List;

public interface LeaveService extends Remote {

    LeaveApplication applyForLeave(String employeeId, LocalDate fromDate, LocalDate toDate)
            throws RemoteException;

    boolean submitLeaveApplication(LeaveApplication leaveApplication)
            throws RemoteException;

    int viewLeaveBalance(String employeeId)
            throws RemoteException;

    List<LeaveApplication> viewLeaveApplicationStatus(String employeeId)
            throws RemoteException;

    boolean approveLeaveApplication(String applicationId)
            throws RemoteException;

    boolean rejectLeaveApplication(String applicationId)
            throws RemoteException;

    List<LeaveApplication> getAllPendingApplications()
            throws RemoteException;

    List<LeaveApplication> getAllLeaveApplications()
            throws RemoteException;

    List<LeaveApplication> getLeaveApplicationsByEmployee(String employeeId)
            throws RemoteException;
}