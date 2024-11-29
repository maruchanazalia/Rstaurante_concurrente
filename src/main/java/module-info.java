module Restaurant {
    requires javafx.fxml;
    requires javafx.controls;
    requires kotlin.stdlib;

    opens Init;
    opens Controller;
    opens principal;
}