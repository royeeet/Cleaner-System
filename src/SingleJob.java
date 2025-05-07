/**
 * Represents a single cleaning job (model class).
 */
public class SingleJob {
    private String id; // unique job ID (e.g., J001)
    private String location; // job location
    private String date; // job date as string
    private String time; // job time as string
    private IndividualCleaners assignedCleaner; // assigned cleaner (null if unassigned)

    /**
     * Constructor to initialise a job with an ID, location, date, and time.
     */
    public SingleJob(String id, String location, String date, String time) {
        this.id = id;
        this.location = location;
        this.date = date;
        this.time = time;
    }

    /* Getters */
    public String getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public IndividualCleaners getAssignedCleaner() {
        return assignedCleaner;
    }

    /**
     * Assign this job to a cleaner.
     */
    public void assignCleaner(IndividualCleaners cleaner) {
        this.assignedCleaner = cleaner;
    }

    /**
     * Check if the job is already assigned.
     */
    public boolean isAssigned() {
        return assignedCleaner != null;
    }

    /**
     * toString: formatted representation for console display.
     */
    @Override
    public String toString() {
        String base = String.format(
                "Job ID: %s | Loc: %s | Date: %s | Time: %s",
                id, location, date, time);
        if (isAssigned()) {
            base += " | Assigned to: " + assignedCleaner.getName();
        } else {
            base += " | Unassigned";
        }
        return base;
    }
}
