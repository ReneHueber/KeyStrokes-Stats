module Keylogger {
    requires java.sql;
    requires java.desktop;
    requires jnativehook;
    requires javafx.fxml;
    requires javafx.controls;

    opens gui;
}