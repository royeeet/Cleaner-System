import java.util.List;
import java.util.Scanner;

/*
 * MainMenu.java
 * Entry point and controller: handles user interaction and delegates to managers.
 */

public class MainMenu {
    private static Scanner scanner = new Scanner(System.in);
    private static MultipleCleaners cleanersManager = new MultipleCleaners();
    private static JobCollection jobsManager = new JobCollection();

    public static void main(String[] args) {
        // load existing records
        cleanersManager.loadFromFile();
        jobsManager.loadFromFile();

        int choice;
        do {
            // Display main menu
            System.out.println("\n************");
            System.out.println("Cleaning System");
            System.out.println("************");
            System.out.println("1. View all Cleaners");
            System.out.println("2. View all Jobs");
            System.out.println("3. Add Cleaner");
            System.out.println("4. Remove Cleaner");
            System.out.println("5. Add Job");
            System.out.println("6. Remove Job");
            System.out.println("7. Assign a Job");
            System.out.println("8. View all jobs by cleaner");
            System.out.println("0. Exit");

            // Read and validate menu choice
            while (true) {
                System.out.print("Enter choice: ");
                String line = scanner.nextLine().trim();
                try {
                    choice = Integer.parseInt(line);
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid choice. Please enter a number between 0 and 8.");
                }
            }

            // Dispatch based on user choice
            switch (choice) {
                case 1:
                    cleanersManager.viewAllCleaners();
                    break;
                case 2:
                    jobsManager.viewAllJobs();
                    break;
                case 3:
                    cleanersManager.addCleaner();
                    break;
                case 4:
                    cleanersManager.removeCleaner();
                    break;
                case 5:
                    jobsManager.addJob();
                    break;
                case 6:
                    jobsManager.removeJob();
                    break;
                case 7:
                    assignJob();
                    break;
                case 8:
                    viewAllJobsByCleaner();
                    break;
                case 0:
                    System.out.println("Exiting. Goodbye!");
                    // save before quit
                    cleanersManager.saveToFile();
                    jobsManager.saveToFile();
                    break;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }
        } while (choice != 0);
    }

    /**
     * Helper for Option 7: assigns a job to a cleaner by IDs.
     */
    private static void assignJob() {
        try {
            System.out.println("List of jobs:");
            jobsManager.getJobList().forEach(j -> System.out.println("  " + j));
            System.out.println("\nList of cleaners:");
            cleanersManager.getCleanerList().forEach(c -> System.out.println("  " + c));
            System.out.print("Enter Job ID to assign: ");
            String jobId = scanner.nextLine().trim();
            SingleJob job = jobsManager.findJobById(jobId);
            if (job == null) {
                System.out.println("Job not found.");
                return;
            }
            if (job.isAssigned()) {
                System.out.println("That job is already assigned to "
                        + job.getAssignedCleaner().getName() + ".");
                return;
            }

            System.out.print("Enter Cleaner ID to assign to: ");
            String cleanerId = scanner.nextLine().trim();
            IndividualCleaners cleaner = cleanersManager.findCleanerById(cleanerId);
            if (cleaner == null) {
                System.out.println("Cleaner not found.");
                return;
            }

            job.assignCleaner(cleaner);
            System.out.println("Assigned job "
                    + job.getId() + " to "
                    + cleaner.getName() + ".");
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    /**
     * Helper for Option 8: displays all jobs grouped by cleaner.
     */
    private static void viewAllJobsByCleaner() {
        try {
            List<IndividualCleaners> allCleaners = cleanersManager.getCleanerList();
            List<SingleJob> allJobs = jobsManager.getJobList();

            if (allCleaners.isEmpty()) {
                System.out.println("No cleaners in system.");
                return;
            }

            // Iterate each cleaner and list assigned jobs
            for (IndividualCleaners c : allCleaners) {
                System.out.println(c);
                boolean any = false;
                for (SingleJob j : allJobs) {
                    if (j.isAssigned() && j.getAssignedCleaner() != null
                            && j.getAssignedCleaner().getId().equalsIgnoreCase(c.getId())) {
                        System.out.println("  " + j);
                        any = true;
                    }
                }
                if (!any) {
                    System.out.println("  (no jobs assigned)");
                }
                System.out.println(); // blank line
            }
        } catch (Exception e) {
            System.out.println("Could not display jobs by cleaner: " + e.getMessage());
        }
    }
}
