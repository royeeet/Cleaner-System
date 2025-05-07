import java.util.Collections;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Manages all cleaning staff: add, view, remove, lookup operations.
 */
public class MultipleCleaners {
    private List<IndividualCleaners> cleanerList = new ArrayList<>();
    private int cleanerIdCounter = 1;
    private Scanner scanner = new Scanner(System.in);

    /**
     * Creates and stores a cleaner from name+phone (for GUI use).
     * 
     * @throws IllegalArgumentException if name empty or phone non-digits
     */
    public IndividualCleaners createCleaner(String name, String phone) {
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("Name cannot be empty");
        if (!phone.matches("\\d+"))
            throw new IllegalArgumentException("Phone must be digits only");

        String id = String.format("C%03d", cleanerIdCounter++);
        IndividualCleaners c = new IndividualCleaners(id, name.trim(), phone.trim());
        cleanerList.add(c);
        return c;
    }

    public void loadFromFile() {
        try (
                FileReader fr = new FileReader("cleaners.txt");
                BufferedReader br = new BufferedReader(fr);) {
            String id;
            while ((id = br.readLine()) != null) {
                String name = br.readLine();
                String phone = br.readLine();
                cleanerList.add(new IndividualCleaners(id, name, phone));

                // track max ID so counter continues correctly
                int numeric = Integer.parseInt(id.substring(1));
                cleanerIdCounter = Math.max(cleanerIdCounter, numeric + 1);
            }
        } catch (FileNotFoundException e) {
            // no existing file → first run
        } catch (IOException e) {
            System.out.println("Error reading cleaners.txt: " + e.getMessage());
        }
    }

    /**
     * Save all cleaners to 'cleaners.txt'.
     */
    public void saveToFile() {
        try (
                FileWriter fw = new FileWriter("cleaners.txt");
                PrintWriter pw = new PrintWriter(fw);) {
            for (IndividualCleaners c : cleanerList) {
                pw.println(c.getId());
                pw.println(c.getName());
                pw.println(c.getPhone());
            }
        } catch (IOException e) {
            System.out.println("Error writing cleaners.txt: " + e.getMessage());
        }
    }

    /**
     * 3. Add a new cleaner (with validation for empty name & numeric phone)
     */
    public void addCleaner() {
        // 1) Get non-empty name
        String name;
        do {
            System.out.print("Enter cleaner's name: ");
            name = scanner.nextLine().trim();
            if (name.isEmpty()) {
                System.out.println("Name cannot be empty. Please try again.");
            }
        } while (name.isEmpty());

        // 2) Get phone consisting of digits only
        String phone;
        do {
            System.out.print("Enter cleaner's phone number: ");
            phone = scanner.nextLine().trim();
            if (!phone.matches("\\d+")) {
                System.out.println("Phone must be numbers only. Please try again.");
            }
        } while (!phone.matches("\\d+"));

        // 3) Create and store the cleaner
        String id = String.format("C%03d", cleanerIdCounter++);
        IndividualCleaners cleaner = new IndividualCleaners(id, name, phone);
        cleanerList.add(cleaner);
        System.out.println("Cleaner added: " + cleaner);
    }

    /** 1. View all cleaners */
    public void viewAllCleaners() {
        if (cleanerList.isEmpty()) {
            System.out.println("No cleaners in the system.");
            return;
        }
        System.out.println("List of cleaners:");
        cleanerList.forEach(c -> System.out.println("  " + c));
    }

    /** 4. Remove a cleaner by ID */
    public void removeCleaner() {
        if (cleanerList.isEmpty()) {
            System.out.println("No cleaners to remove.");
            return;
        }
        System.out.println("List of cleaners:");
        cleanerList.forEach(c -> System.out.println("  " + c));
        System.out.print("Enter Cleaner ID to remove: ");
        String id = scanner.nextLine().trim();
        IndividualCleaners toRemove = findCleanerById(id);
        if (toRemove != null) {
            cleanerList.remove(toRemove);
            System.out.println("Removed: " + toRemove);
        } else {
            System.out.println("Cleaner ID not found.");
        }
    }

    /** Lookup for assignment logic */
    public IndividualCleaners findCleanerById(String id) {
        for (IndividualCleaners c : cleanerList) {
            if (c.getId().equalsIgnoreCase(id)) {
                return c;
            }
        }
        return null;
    }

    /** If you need the raw list elsewhere */
    public List<IndividualCleaners> getCleanerList() {
        return Collections.unmodifiableList(cleanerList);
    }
}
