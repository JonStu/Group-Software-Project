module uk.ac.liverpool.groupsoftwareproject {
    requires javafx.controls;
    requires javafx.fxml;


    opens uk.ac.liverpool.groupsoftwareproject to javafx.fxml;
    exports uk.ac.liverpool.groupsoftwareproject;
}