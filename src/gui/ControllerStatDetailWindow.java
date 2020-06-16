package gui;

import database.ReadDb;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import objects.TotalToday;

import java.time.LocalDate;
import java.util.ArrayList;

public class ControllerStatDetailWindow {

    private final ObservableList<String> statisticOptions = FXCollections.observableArrayList(
            "Key Strokes",
            "Time Pressed"
    );

    @FXML
    protected ComboBox<String> statistic;

    @FXML
    BarChart <String, Number> daysChar;
    @FXML
    CategoryAxis daysX;
    @FXML
    NumberAxis daysY;

    @FXML
    BarChart<String, Number> weeksChar;
    @FXML
    CategoryAxis weeksX;
    @FXML
    NumberAxis weeksY;

    @FXML
    BarChart<String, Number> monthsChar;
    @FXML
    CategoryAxis monthsX;
    @FXML
    NumberAxis monthsY;


    public void initialize(){

        // set's the values for the combo box
        statistic.setItems(statisticOptions);

        // change the title and values if the statistic type is changes
        statistic.setOnAction(event -> {
            ProcessFxmlFiles detailWindow = new ProcessFxmlFiles("../fxml/statDetailWindow.fxml", "Detail Statistic");
            Stage stage = (Stage) daysChar.getScene().getWindow();
            ControllerStatDetailWindow controller = (ControllerStatDetailWindow) detailWindow.openInExistingStage(stage);
            if (statistic.getSelectionModel().getSelectedItem().equals("Key Strokes")) {
                controller.renameChars("Key Strokes");
                controller.setDailyValueSet("Key Strokes");
            }
            else {
                controller.renameChars("Time Pressed");
                controller.setDailyValueSet("Time Pressed");
            }
        });

        daysChar.setLegendVisible(false);
        daysChar.setAnimated(false);
        weeksChar.setLegendVisible(false);
        weeksChar.setAnimated(false);
        monthsChar.setLegendVisible(false);
        monthsChar.setAnimated(false);

    }

    /**
     * Renames the Char Title and the Number Axis.
     * @param statisticName Displayed Name
     */
    protected void renameChars(String statisticName){
        daysChar.setTitle(statisticName + " per Day");
        daysY.setLabel(statisticName);

        weeksChar.setTitle(statisticName + " per Week");
        weeksY.setLabel(statisticName);

        monthsChar.setTitle(statisticName + " per Month");
        monthsY.setLabel(statisticName);
    }

    /**
     * Set's the Data and adds a Label with the Value on top of the Bar Char.
     * @param date Name of the Bar
     * @param value Value of the Bar
     * @return XYChar.Data with the passed Values and a Node with the Value as Text
     */
    private XYChart.Data<String, Number> createData(String date, Number value) {
        XYChart.Data<String, Number> data = new XYChart.Data<>(date, value);

        String text = value + "";

        StackPane node = new StackPane();
        Label label = new Label(text);
        // label.setRotate(-90);
        Group group = new Group(label);
        StackPane.setAlignment(group, Pos.BOTTOM_CENTER);
        StackPane.setMargin(group, new Insets(0, 0, 5, 0));
        node.getChildren().add(group);
        data.setNode(node);

        return data;
    }

    /**
     * Get's the Values of the last 7 days, for the daily char.
     * @param selectedValue What value is selected, to get the right one
     */
    protected void setDailyValueSet(String selectedValue){
        XYChart.Series<String, Number> daysValues = new XYChart.Series<>();

        LocalDate end = LocalDate.now();
        LocalDate start = LocalDate.now().minusDays(7);

        // reads all the values form the db in the range from start to end date
        String sqlStmt = "SELECT keyboardId, date, keyStrokes, timePressed FROM totalToday WHERE date >= '" + start + "' AND date <= '" + end + "'";
        ArrayList<TotalToday> dates = ReadDb.selectAllValuesTotalToday(sqlStmt);

        int year = start.getYear();
        // goes throw the days form start to end Date
        for (int i = start.getDayOfYear(); i <= end.getDayOfYear(); i++){
            if (i > start.lengthOfYear()){
                i = 1;
                year = end.getYear();
            }
            boolean inDb = false;
            // check if the date is in the db
            // if the date is in the db, the value get's used
            for (TotalToday value : dates){
                // if the day is in the list
                if (LocalDate.ofYearDay(year, i).toString().equals(value.getDate().toString())){
                    LocalDate currentDate = LocalDate.ofYearDay(year, i);
                    String date = currentDate.getDayOfMonth() + "." + currentDate.getMonthValue();
                    if (selectedValue.equals("Key Strokes"))
                        daysValues.getData().add(createData(date, value.getKeyStrokes()));
                    else
                        daysValues.getData().add(createData(date, value.getTimePressed()));
                    inDb = true;
                    break;
                }
            }

            // if the value is not in the db, the value 0 is used
            if (!inDb){
                LocalDate currentDate = LocalDate.ofYearDay(year, i);
                String date = currentDate.getDayOfMonth() + "." + currentDate.getMonthValue();
                if (selectedValue.equals("Key Strokes"))
                    daysValues.getData().add(createData(date, 0));
                else
                    daysValues.getData().add(createData(date, 0.0));
            }
        }

        daysChar.getData().add(daysValues);
    }

}
