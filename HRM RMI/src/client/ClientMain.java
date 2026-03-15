package client;

import client.controllers.AuthController;
import client.controllers.LeaveController;
import client.menus.HRMenu;
import client.menus.LeaveMenu;
import client.menus.LoginMenu;
import client.menus.ProfileMenu;
import common.interfaces.EmployeeService;
import common.interfaces.LeaveService;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import server.repository.EmployeeRepository;

public class ClientMain
{
    private static final String HOST= "localhost"; // RMI server host
    private static final int PORT = 2099; // RMI registry port

    public static void main(String[] args)
    {
        try
        {
            Registry registry = LocateRegistry.getRegistry(HOST, PORT);
            EmployeeService employeeService = (EmployeeService) registry.lookup("EmployeeService");
            LeaveService leaveService = (LeaveService) registry.lookup("LeaveService");

            Scanner scanner = new Scanner(System.in);

            //AUTH CONTROLLER
            AuthController authController = new AuthController();
            authController.connect();
            authController.setEmployeeService(employeeService);

            //LOGIN -- loop until exit hehe
            LoginMenu loginMenu = new LoginMenu(authController, scanner);
            while(true)
            {
                boolean loggedIn = loginMenu.show();
                if(!loggedIn)
                {
                    System.out.println("Exiting application. Goodbye!");
                    break;
                }

                //route user to respective roles
                if(authController.isHR())
                {
                    HRMenu hrMenu = new HRMenu(employeeService, authController, leaveService, scanner);
                    hrMenu.show();
                }
                else
                {
                    showEmployeeMenu(authController, leaveService, scanner);
                }
            }
        }
        catch (Exception e)
        {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

    //EMPLOYEE MENU aka the leave menu but with profile access
    private static void showEmployeeMenu(AuthController authController, LeaveService leaveService, Scanner scanner)
    {
        EmployeeRepository employeeRepo = new EmployeeRepository();
        LeaveController leaveController = new LeaveController(leaveService);
        LeaveMenu leaveMenu = new LeaveMenu(leaveController, scanner, employeeRepo);
        ProfileMenu profileMenu = new ProfileMenu(authController, scanner);


        while(true)
        {
            try
            {
                System.out.println("\n=== Employee Menu ===");
                System.out.println("1. Leave Management");
                System.out.println("2. My Profile");
                System.out.println("3. Logout");
                System.out.print("\nSelect an option: ");
                String choice = scanner.nextLine().trim();

                switch (choice)
                {
                    case "1" -> leaveMenu.show(authController.getCurrentUser());
                    case "2" ->
                    {
                        boolean loggedOut = profileMenu.show();
                        if(loggedOut)return;
                    }
                    case "0" -> {
                        authController.logout();
                        return; //back to login menu
                    }
                    default -> System.out.println("Invalid option. Please try again.");
                }
            }
            catch (Exception e)
            {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }
    
    
}