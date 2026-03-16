package client.menus;
import client.controllers.AuthController;
import common.models.User;
import java.util.Scanner;

public class ProfileMenu
{
    private final AuthController authController;
    private final Scanner scanner;

    public ProfileMenu(AuthController authController, Scanner scanner)
    {
        this.authController = authController;
        this.scanner = scanner;
    }

    public void show()
    {
        while (true)
        {
            printHeader();
            String choice = scanner.nextLine().trim();
            switch (choice)
            {
                //profile
                case "1"-> doViewProfile();
                case "2"-> doUpdateProfile();
                case "3"-> {return;}
                default-> printError("Invalid choice! Please try again.");
            }
        }
    }

    //view profile
    private void doViewProfile()
    {
        System.out.println();
        System.out.println("=== Your Profile ===");

        User user = authController.getProfile();
        if (user == null) {
            printError("Could not retrieve profile. Please try again.");
            return;
        }
        String fullName = authController.getFullName(user.getEmployeeId());

        System.out.println("  Employee ID : " + user.getEmployeeId());
        System.out.println("  Full Name   : " + ((fullName == null || fullName.isEmpty()) ? "(Name not set)" : fullName));
        System.out.println("  Email  : " + user.getEmail());
        System.out.println("  Role   : " + user.getRole());
        System.out.println("  Access : " + (user.isHR() ? "HR" : "Employee"));
        System.out.println();
        pause();
    }

    //update profile
private void doUpdateProfile()
{
        System.out.println();
        System.out.println("=== Update Your Profile ===");
        System.out.println("  (Press ENTER to keep the current value)");
        System.out.println();

        System.out.print("  New Email    : ");
        String newEmail = scanner.nextLine().trim();

        System.out.print("  New Password : ");
        String newPassword = scanner.nextLine().trim();

        // Pass null for blank fields — controller/server will ignore them
        String error = authController.updateProfile(
                newEmail.isEmpty()    ? null : newEmail,
                newPassword.isEmpty() ? null : newPassword
        );

        if (error == null)
        {
            User updated = authController.getCurrentUser();
            String fullName = authController.getFullName(updated.getEmployeeId());
            System.out.println();
            System.out.println("  ✔  Profile updated successfully!");
            System.out.println("     Employee ID : " + updated.getEmployeeId());
            System.out.println("     Full Name   : " + ((fullName == null || fullName.isEmpty()) ? "(Name not set)" : fullName));
            System.out.println("     Email : " + updated.getEmail());
            System.out.println("     Role  : " + updated.getRole());

            // If email was changed the server issued a new session — notify user
            System.out.println();
        }
        else
        {
            printError(error);
        }
        pause();
    }

    //UI
    private void printHeader()
    {
        System.out.println();
        System.out.println("=== Main Menu ===");
        System.out.println("1. View Profile");
        System.out.println("2. Update Profile");
        System.out.println("3. Back");
        System.out.print("\n Enter your choice: ");
    }

    private void printError(String message)
    {
        System.out.println();
        System.out.println("  ✖  " + message);
        System.out.println();
    }
    private void pause()
    {
        System.out.println("Press ENTER to continue...");
        scanner.nextLine();
    }
}