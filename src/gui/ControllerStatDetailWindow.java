package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;

public class ControllerStatDetailWindow {

    private final ObservableList<String> statisticOptions = FXCollections.observableArrayList(
            "Key Strokes",
            "Time Pressed"
    );

    @FXML
    ComboBox<String> statistic;

    @FXML
    BarChart <String, Number> daysChar;
    @FXML
    CategoryAxis daysX;
    @FXML
    NumberAxis daysY;

    @FXML
    BarChart<String, Number> monthsChar;
    @FXML
    CategoryAxis monthsX;
    @FXML
    NumberAxis monthsY;


    public void initialize(){

        statistic.setItems(statisticOptions);
        statistic.setValue(statisticOptions.get(0));

        XYChart.Series<String, Number> daysValues = new XYChart.Series<String, Number>();

        daysValues.getData().add(new XYChart.Data<>("21", 5000));
        daysValues.getData().add(new XYChart.Data<>("25", 10000));
        daysValues.getData().add(new XYChart.Data<>("22", 2000));
        daysValues.getData().add(new XYChart.Data<>("23", 500));
        daysValues.getData().add(new XYChart.Data<>("24", 500));
        daysValues.getData().add(new XYChart.Data<>("26", 500));
        daysValues.getData().add(new XYChart.Data<>("2?", 500));

        daysChar.getData().add(daysValues);
        daysChar.setLegendVisible(false);
    }
}
