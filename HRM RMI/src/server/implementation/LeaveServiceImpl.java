package server.implementation;

import common.interfaces.LeaveService;
import common.models.Employee;
import common.models.LeaveApplication;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDate;
import java.util.List;

import server.repository.EmployeeRepository;
import server.repository.LeaveRepository;

public class LeaveServiceImpl extends UnicastRemoteObject implements LeaveService {

    private static final long serialVersionUID = 1L;

    private final LeaveRepository    leaveRepo;
    private final EmployeeRepository employeeRepo;

    public LeaveServiceImpl() throws RemoteException {
        super();
        this.leaveRepo    = new LeaveRepository();
        this.employeeRepo = new EmployeeRepository();
    }

    @Override
    public LeaveApplication applyForLeave(String employeeId, LocalDate fromDate, LocalDate toDate)
            throws RemoteException {

        if (employeeRepo.findById(employeeId) == null) {
            throw new RemoteException("Employee not found: " + employeeId);
        }
        if (fromDate.isAfter(toDate)) {
            throw new RemoteException("Invalid dates: 'from' date must be before or equal to 'to' date.");
        }

        String id = generateApplicationId();
        return new LeaveApplication(id, employeeId, fromDate, toDate);
    }

    @Override
    public boolean submitLeaveApplication(LeaveApplication leaveApplication) throws RemoteException {
        int balance = viewLeaveBalance(leaveApplication.getEmployeeId());
        if (leaveApplication.getAmountOfDays() > balance) {
            throw new RemoteException("Insufficient leave balance. Requested: "
                    + leaveApplication.getAmountOfDays() + " days, Available: " + balance + " days.");
        }
        leaveRepo.add(leaveApplication);
        return true;
    }

    @Override
    public int viewLeaveBalance(String employeeId) throws RemoteException {
        Employee employee = employeeRepo.findById(employeeId);
        if (employee == null) {
            throw new RemoteException("Employee not found: " + employeeId);
        }
        int totalBalance = employee.getLeaveDays();

        int usedDays = leaveRepo.findByEmployeeId(employeeId)
                .stream()
                .filter(la -> la.getStatus().equalsIgnoreCase("Approved"))
                .mapToInt(LeaveApplication::getAmountOfDays)
                .sum();

        return totalBalance - usedDays;
    }

    @Override
    public List<LeaveApplication> viewLeaveApplicationStatus(String employeeId)
            throws RemoteException {
        // Returns ALL applications for this employee from leave_requests.json
        return leaveRepo.findByEmployeeId(employeeId);
    }

    @Override
    public boolean approveLeaveApplication(String applicationId) throws RemoteException {
        LeaveApplication la = leaveRepo.findById(applicationId);
        if (la == null) throw new RemoteException("Application not found: " + applicationId);
        if (!la.getStatus().equalsIgnoreCase("Pending"))
            throw new RemoteException("Application is not in Pending status.");
        la.setStatus("Approved");
        return leaveRepo.update(la);
    }

    @Override
    public boolean rejectLeaveApplication(String applicationId) throws RemoteException {
        LeaveApplication la = leaveRepo.findById(applicationId);
        if (la == null) throw new RemoteException("Application not found: " + applicationId);
        if (!la.getStatus().equalsIgnoreCase("Pending"))
            throw new RemoteException("Application is not in Pending status.");
        la.setStatus("Declined");
        return leaveRepo.update(la);
    }

    @Override
    public List<LeaveApplication> getAllPendingApplications() throws RemoteException {
        return leaveRepo.findByStatus("Pending");
    }

    @Override
    public List<LeaveApplication> getAllLeaveApplications() throws RemoteException {
        return leaveRepo.loadAll();
    }

    @Override
    public List<LeaveApplication> getLeaveApplicationsByEmployee(String employeeId)
            throws RemoteException {
        return leaveRepo.findByEmployeeId(employeeId);
    }

    private String generateApplicationId() {
        List<LeaveApplication> all = leaveRepo.loadAll();
        int max = 0;
        for (LeaveApplication la : all) {
            String appId = la.getApplicationId();
            if (appId != null && appId.matches("LA\\d+")) {
                int num = Integer.parseInt(appId.substring(2));
                if (num > max) max = num;
            }
        }
        return String.format("LA%03d", max + 1);
    }
}