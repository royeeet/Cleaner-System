import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * Manages all jobs: add, view, remove, lookup operations.
 */
public class JobCollection {
    private List<SingleJob> jobList = new ArrayList<>(); // in-memory list
    private int jobIdCounter = 1; // for generating unique IDs
    private Scanner scanner = new Scanner(System.in);

    /**
     * Creates & stores a job, validating location, date, and time.
     * 
     * @throws IllegalArgumentException on any invalid input
     */
    public SingleJob createJob(String location, String dateStr, String timeStr) {
        // 1) Validate location
        if (location == null || location.trim().isEmpty()) {
            throw new IllegalArgumentException("Location cannot be empty");
        }

        // 2) Validate date
        LocalDate date;
        try {
            date = LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format (YYYY-MM-DD)");
        }

        // 3) Validate time
        LocalTime time;
        try {
            time = LocalTime.parse(timeStr);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid time format (HH:MM)");
        }

        // 4) Create & store
        String id = String.format("J%03d", jobIdCounter++);
        SingleJob job = new SingleJob(id, location.trim(), date.toString(), time.toString());
        jobList.add(job);
        return job;
    }

    /**
     * Assigns a job to a cleaner by IDs (for GUI use).
     * 
     * @throws IllegalArgumentException / IllegalStateException on errors
     */
    public void assignJob(String jobId, String cleanerId, MultipleCleaners cm) {
        SingleJob job = findJobById(jobId);
        if (job == null)
            throw new IllegalArgumentException("Job not found");
        if (job.isAssigned())
            throw new IllegalStateException("Job already assigned");
        IndividualCleaners cleaner = cm.findCleanerById(cleanerId);
        if (cleaner == null)
            throw new IllegalArgumentException("Cleaner not found");
        job.assignCleaner(cleaner);
    }

    /**
     * Load jobs from 'jobs.txt' (4 lines per record).
     */
    public void loadFromFile() {
        try (
                FileReader fr = new FileReader("jobs.txt");
                BufferedReader br = new BufferedReader(fr);) {
            String id;
            while ((id = br.readLine()) != null) {
                String location = br.readLine();
                String date = br.readLine();
                String time = br.readLine();

                SingleJob job = new SingleJob(id, location, date, time);
                jobList.add(job);

                int numeric = Integer.parseInt(id.substring(1));
                jobIdCounter = Math.max(jobIdCounter, numeric + 1);
            }
        } catch (FileNotFoundException e) {
            // first run, no file yet
        } catch (IOException e) {
            System.out.println("Error reading jobs.txt: " + e.getMessage());
        }
    }

    /**
     * Save all jobs to 'jobs.txt'.
     */
    public void saveToFile() {
        try (
                FileWriter fw = new FileWriter("jobs.txt");
                PrintWriter pw = new PrintWriter(fw);) {
            for (SingleJob j : jobList) {
                pw.println(j.getId());
                pw.println(j.getLocation());
                pw.println(j.getDate());
                pw.println(j.getTime());
            }
        } catch (IOException e) {
            System.out.println("Error writing jobs.txt: " + e.getMessage());
        }
    }

    /**
     * Add a new job with validation for date and time formats.
     */
    public void addJob() {
        // Prompt for location
        System.out.print("Enter job location: ");
        String location = scanner.nextLine().trim();

        // Validate date input
        LocalDate date;
        while (true) {
            System.out.print("Enter job date (YYYY-MM-DD): ");
            String dateInput = scanner.nextLine().trim();
            try {
                date = LocalDate.parse(dateInput);
                break;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use YYYY-MM-DD.");
            }
        }

        // Validate time input
        LocalTime time;
        while (true) {
            System.out.print("Enter job time (HH:MM): ");
            String timeInput = scanner.nextLine().trim();
            try {
                time = LocalTime.parse(timeInput);
                break;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid time format. Please use HH:MM.");
            }
        }

        // Generate ID and add to list
        String id = String.format("J%03d", jobIdCounter++);
        SingleJob job = new SingleJob(id, location, date.toString(), time.toString());
        jobList.add(job);
        System.out.println("Job added: " + job);
    }

    /**
     * View all jobs, or inform if none exist.
     */
    public void viewAllJobs() {
        if (jobList.isEmpty()) {
            System.out.println("No jobs in the system.");
            return;
        }
        System.out.println("List of jobs:");
        jobList.forEach(j -> System.out.println("  " + j));
    }

    /**
     * Remove a job by ID.
     */
    public void removeJob() {
        if (jobList.isEmpty()) {
            System.out.println("No jobs to remove.");
            return;
        }
        System.out.println("List of jobs:");
        jobList.forEach(j -> System.out.println("  " + j));
        System.out.print("Enter Job ID to remove: ");
        String id = scanner.nextLine().trim();
        SingleJob toRemove = findJobById(id);
        if (toRemove != null) {
            jobList.remove(toRemove);
            System.out.println("Removed: " + toRemove);
        } else {
            System.out.println("Job ID not found.");
        }
    }

    /**
     * Lookup helper: find a job by ID (case-insensitive).
     */
    public SingleJob findJobById(String id) {
        for (SingleJob j : jobList) {
            if (j.getId().equalsIgnoreCase(id)) {
                return j;
            }
        }
        return null; // not found
    }

    /**
     * Expose read-only list for reports or assignments.
     */
    public List<SingleJob> getJobList() {
        return Collections.unmodifiableList(jobList);
    }
}
