module comparer {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.prefs;


    opens comparer to javafx.fxml;
    opens comparer.controller to javafx.fxml;

    exports comparer;
    exports comparer.controller;
}