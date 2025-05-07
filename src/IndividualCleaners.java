
/**
 * Represents an individual cleaner (model class).
 */
public class IndividualCleaners {
    private String id; // unique cleaner ID (e.g., C001)
    private String name; // cleaner's name
    private String phone; // cleaner's phone number

    /**
     * Constructor to initialise a cleaner with an ID, name, and phone.
     */
    public IndividualCleaners(String id, String name, String phone) {
        this.id = id;
        this.name = name;
        this.phone = phone;
    }

    /* Getters */
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    /**
     * toString: formatted representation for console display.
     */
    @Override
    public String toString() {
        return String.format("Cleaner ID: %s | Name: %s | Phone: %s", id, name, phone);
    }
}
