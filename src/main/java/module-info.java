module com.example {
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires transitive javafx.base;
    requires org.apache.commons.io;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.annotation;
    

    opens com.example.Utils to javafx.base, com.fasterxml.jackson.databind;
    opens com.example.Controllers to javafx.fxml;
    exports com.example;
}
