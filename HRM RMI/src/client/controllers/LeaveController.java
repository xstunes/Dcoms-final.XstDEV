package client.controllers;

import common.interfaces.LeaveService;
import common.models.LeaveApplication;

import java.rmi.RemoteException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class LeaveController {

    private final LeaveService leaveService;

    public LeaveController(LeaveService leaveService) {
        this.leaveService = leaveService;
    }

    // CHANGED: removed name/role params — only needs employeeId now
    public LeaveApplication applyAndSubmit(String employeeId,
                                           String fromDateStr,
                                           String toDateStr) throws Exception {
        LocalDate fromDate;
        LocalDate toDate;
        try {
            fromDate = LocalDate.parse(fromDateStr);
            toDate   = LocalDate.parse(toDateStr);
        } catch (DateTimeParseException e) {
            throw new Exception("Invalid date format. Please use YYYY-MM-DD.");
        }

        LeaveApplication la = leaveService.applyForLeave(employeeId, fromDate, toDate);
        boolean ok = leaveService.submitLeaveApplication(la);
        if (!ok) throw new Exception("Failed to submit leave application.");
        return la;
    }

    // CHANGED: parameter is employeeId, not email
    public int getLeaveBalance(String employeeId) throws RemoteException {
        return leaveService.viewLeaveBalance(employeeId);
    }

    // CHANGED: parameter is employeeId, not email
    public List<LeaveApplication> getMyApplications(String employeeId) throws RemoteException {
        return leaveService.viewLeaveApplicationStatus(employeeId);
    }

    public List<LeaveApplication> getPendingApplications() throws RemoteException {
        return leaveService.getAllPendingApplications();
    }

    public boolean approve(String applicationId) throws RemoteException {
        return leaveService.approveLeaveApplication(applicationId);
    }

    public boolean reject(String applicationId) throws RemoteException {
        return leaveService.rejectLeaveApplication(applicationId);
    }

    public List<LeaveApplication> getAllApplications() throws RemoteException {
        return leaveService.getAllLeaveApplications();
    }
}