import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.util.Pair;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CleaningSystemAppGUI.java
 * 
 * JavaFX-based graphical user interface for the Cleaning Company System.
 * Provides buttons for all core operations (view, add, assign, save) and
 * displays output in a text area. Loads data on startup and saves on exit.
 */
public class CleaningSystemAppGUI extends Application {

    /**
     * Simple generic triple container for three values (used in Add Job dialog).
     */
    private static class Triple<A, B, C> {
        public final A first;
        public final B second;
        public final C third;

        public Triple(A first, B second, C third) {
            this.first = first;
            this.second = second;
            this.third = third;
        }
    }

    // Managers for cleaners and jobs (business logic + file I/O)
    private MultipleCleaners cm = new MultipleCleaners();
    private JobCollection jm = new JobCollection();

    @Override
    public void start(Stage stage) {
        // 1) Load persisted data from files into memory
        cm.loadFromFile();
        jm.loadFromFile();

        // 2) Create UI components
        // Title label styled and centered
        Label titleLabel = new Label("Cleaning Company System");
        titleLabel.setStyle("-fx-font-size: 24px;" + "-fx-font-weight: bold;" + "-fx-text-fill: #005f9e;");
        titleLabel.setId("titleLabel");
        HBox titleBox = new HBox(titleLabel);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(10));

        // Buttons for each feature
        Button bViewCleaners = new Button("View All Cleaners");
        Button bAddCleaner = new Button("Add Cleaner");
        Button bViewJobs = new Button("View All Jobs");
        Button bAddJob = new Button("Add Job");
        Button bAssignJob = new Button("Assign Job");
        Button bViewByCleaner = new Button("View Jobs by Cleaner");
        Button bSaveQuit = new Button("Save & Quit");

        // Arrange buttons vertically with spacing and padding
        VBox buttons = new VBox(8);
        buttons.setPadding(new Insets(10));
        buttons.setAlignment(Pos.TOP_CENTER); // centre buttons in column
        buttons.setPrefWidth(200); // fixed column width
        buttons.setStyle(
                "-fx-background-color: #ffffff;" + "-fx-border-color: #c9c9c9;" + "-fx-border-width: 0 1 0 0;");
        buttons.setId("buttonColumn");
        buttons.getChildren().addAll(
                bViewCleaners,
                bAddCleaner,
                bViewJobs,
                bAddJob,
                bAssignJob,
                bViewByCleaner,
                bSaveQuit);
        // Give all buttons the same blue theme
        for (Button b : Arrays.asList(
                bViewCleaners, bAddCleaner,
                bViewJobs, bAddJob,
                bAssignJob, bViewByCleaner, bSaveQuit)) {

            b.setPrefWidth(190);
            b.setStyle("-fx-background-color: #005f9e;" + "-fx-text-fill: white;" + "-fx-background-radius: 4;");
            // simple hover effect
            b.setOnMouseEntered(ev -> b.setStyle(
                    "-fx-background-color: #007ad9;" + "-fx-text-fill: white;" + "-fx-background-radius: 4;"));
            b.setOnMouseExited(ev -> b.setStyle(
                    "-fx-background-color: #005f9e;" + "-fx-text-fill: white;" + "-fx-background-radius: 4;"));
        }
        // Output area for displaying lists and messages
        TextArea output = new TextArea();
        output.setEditable(false);
        output.setWrapText(true); // nicer wrapping
        output.setPadding(new Insets(10)); // internal padding

        // 3) Wire button actions

        // View all cleaners
        bViewCleaners.setOnAction(e -> {
            String text = cm.getCleanerList().stream()
                    .map(Object::toString)
                    .collect(Collectors.joining("\n"));
            output.setText(text.isEmpty() ? "No cleaners." : text);
        });

        // Add a new cleaner via dialog
        bAddCleaner.setOnAction(e -> {
            Dialog<Pair<String, String>> dlg = new Dialog<>();
            dlg.setTitle("Add Cleaner");

            // Dialog layout
            GridPane gp = new GridPane();
            gp.setHgap(10);
            gp.setVgap(10);
            TextField tfName = new TextField();
            TextField tfPhone = new TextField();
            gp.addRow(0, new Label("Name:"), tfName);
            gp.addRow(1, new Label("Phone:"), tfPhone);
            dlg.getDialogPane().setContent(gp);
            dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            // Convert result to Pair<name,phone>
            dlg.setResultConverter(bt -> bt == ButtonType.OK
                    ? new Pair<>(tfName.getText(), tfPhone.getText())
                    : null);

            // Handle user input
            dlg.showAndWait().ifPresent(pair -> {
                try {
                    IndividualCleaners c = cm.createCleaner(pair.getKey(), pair.getValue());
                    output.setText("Added: " + c);
                } catch (Exception ex) {
                    showAlert("Error", ex.getMessage());
                }
            });
        });

        // View all jobs
        bViewJobs.setOnAction(e -> {
            String text = jm.getJobList().stream()
                    .map(Object::toString)
                    .collect(Collectors.joining("\n"));
            output.setText(text.isEmpty() ? "No jobs." : text);
        });

        // Add a new job via dialog
        bAddJob.setOnAction(e -> {
            Dialog<Triple<String, String, String>> dlg = new Dialog<>();
            dlg.setTitle("Add Job");

            GridPane gp = new GridPane();
            gp.setHgap(10);
            gp.setVgap(10);
            TextField locField = new TextField();
            TextField dateField = new TextField();
            TextField timeField = new TextField();
            gp.addRow(0, new Label("Location:"), locField);
            gp.addRow(1, new Label("Date (YYYY-MM-DD):"), dateField);
            gp.addRow(2, new Label("Time (HH:MM):"), timeField);
            dlg.getDialogPane().setContent(gp);
            dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            dlg.setResultConverter(bt -> bt == ButtonType.OK
                    ? new Triple<>(locField.getText(), dateField.getText(), timeField.getText())
                    : null);

            dlg.showAndWait().ifPresent(t -> {
                try {
                    SingleJob job = jm.createJob(t.first, t.second, t.third);
                    output.setText("Added: " + job);
                } catch (Exception ex) {
                    showAlert("Error", ex.getMessage());
                }
            });
        });

        // Assign job to cleaner via two-stage dialog
        bAssignJob.setOnAction(e -> {
            ChoiceDialog<SingleJob> jobDlg = new ChoiceDialog<>(null, jm.getJobList());
            jobDlg.setTitle("Assign Job");
            jobDlg.setHeaderText("Pick a Job (ID – Loc – Date)");
            jobDlg.showAndWait().ifPresent(selectedJob -> {
                ChoiceDialog<IndividualCleaners> clDlg = new ChoiceDialog<>(null, cm.getCleanerList());
                clDlg.setTitle("Assign Job");
                clDlg.setHeaderText("Pick a Cleaner (ID – Name)");
                clDlg.showAndWait().ifPresent(selectedCleaner -> {
                    try {
                        jm.assignJob(selectedJob.getId(), selectedCleaner.getId(), cm);
                        output.setText("Assigned " + selectedJob + "\nto " + selectedCleaner);
                    } catch (Exception ex) {
                        showAlert("Error", ex.getMessage());
                    }
                });
            });
        });

        // View jobs filtered by selected cleaner
        bViewByCleaner.setOnAction(e -> {
            ChoiceDialog<IndividualCleaners> dlg = new ChoiceDialog<>(null, cm.getCleanerList());
            dlg.setTitle("View Jobs by Cleaner");
            dlg.setHeaderText("Pick a Cleaner");
            dlg.showAndWait().ifPresent(cleaner -> {
                List<SingleJob> assigned = jm.getJobList().stream()
                        .filter(j -> j.isAssigned() && j.getAssignedCleaner().getId().equalsIgnoreCase(cleaner.getId()))
                        .collect(Collectors.toList());
                output.setText(
                        assigned.isEmpty()
                                ? "No jobs assigned to " + cleaner
                                : assigned.stream().map(Object::toString).collect(Collectors.joining("\n")));
            });
        });

        // Save data and quit application
        bSaveQuit.setOnAction(ev -> {
            cm.saveToFile();
            jm.saveToFile();
            stage.close();
        });

        // 4) Final layout setup
        BorderPane root = new BorderPane();
        root.setTop(titleBox); // centered title
        root.setLeft(buttons); // vertical button menu
        root.setCenter(output); // main output area
        BorderPane.setMargin(output, new Insets(0, 10, 0, 10)); // gap right of column

        Scene scene = new Scene(root, 800, 600);
        // Global font & gradient background
        scene.getRoot().setStyle("-fx-font-family: 'Segoe UI','Arial';" + "-fx-font-size: 14px;" + "-fx-background-color: linear-gradient(to bottom,#f8f8f8 ,#e8e8e8);");
        stage.setScene(scene);
        stage.setTitle("Cleaning Company GUI");
        try {
            stage.getIcons().add(
                    new Image(getClass().getResourceAsStream("broom.png")));
        } catch (NullPointerException ex) {
            // icon not found – ignore
        }
        stage.show(); // display the window
    }

    /**
     * Utility to display error alerts in GUI context.
     */
    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.setHeaderText(title);
        alert.showAndWait();
    }

    // Standard JavaFX application entry point
    public static void main(String[] args) {
        launch(args);
    }
}
