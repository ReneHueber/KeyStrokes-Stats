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
import objects.Keyboard;
import objects.TotalToday;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;

public class ControllerStatDetailWindow {

    private final ObservableList<String> statisticOptions = FXCollections.observableArrayList(
            "Key Strokes",
            "Time Pressed"
    );

    private Keyboard selectedKeyboard;

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
                reloadWindow(controller, "Key Strokes");
            }
            else {
                reloadWindow(controller, "Time Pressed");
            }
        });

        // disable the Animations and Legends
        daysChar.setLegendVisible(false);
        daysChar.setAnimated(false);
        weeksChar.setLegendVisible(false);
        weeksChar.setAnimated(false);
        monthsChar.setLegendVisible(false);
        monthsChar.setAnimated(false);
    }

    /**
     * Set's the Values for the Statistics if the Window get's reloaded.
     * @param controller Controller to call the functions that are setting the Statistics
     * @param statisticName To get the right Statistic and rename the bar chart
     */
    protected void reloadWindow(ControllerStatDetailWindow controller, String statisticName){
        controller.setSelectedKeyboard(selectedKeyboard);
        controller.renameChars(statisticName);
        controller.setDailyValueSet(statisticName);
        controller.setWeeklyMonthlyValues(statisticName, false);
        controller.setWeeklyMonthlyValues(statisticName, true);
    }

    /**
     * Passes the selected Keyboard from the other Controller.
     * @param selectedKeyboard Selected Keyboard
     */
    protected void setSelectedKeyboard(Keyboard selectedKeyboard){
        this.selectedKeyboard = selectedKeyboard;
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
        String text = "";
        // rounds the number if it is a decimal number
        if (value.toString().contains(".")){
            DecimalFormat df = new DecimalFormat("#.00");
            String roundNumber = df.format(value);
            value = Float.parseFloat(roundNumber.replace(",", "."));
            text = value + "s";
        }
        else
            text = value + "";

        XYChart.Data<String, Number> data = new XYChart.Data<>(date, value);

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
     * Set's the Values of the last 7 days.
     * @param selectedValue What value is selected, to get the right one
     */
    protected void setDailyValueSet(String selectedValue){
        XYChart.Series<String, Number> daysValues = new XYChart.Series<>();

        LocalDate end = LocalDate.now();
        LocalDate start = LocalDate.now().minusDays(7);

        // reads all the values form the db in the range from start to end date
        String sqlStmt = "SELECT keyboardId, date, keyStrokes, timePressed FROM " +
                        "totalToday WHERE date >= '" + start + "' AND date <= '" + end + "' AND keyboardId = " + selectedKeyboard.getKeyboardId();
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

    /**
     * Set's the values for the last 7 weeks or 7 month.
     * @param selectedValue What value is selected, to get the right one
     * @param month True the last 7 months are selected, else the last 7 weeks
     */
    protected void setWeeklyMonthlyValues(String selectedValue, boolean month){
        // creates the basic sqlStmt
        String sqlStmt = "";
        if (selectedValue.equals("Key Strokes")){
            sqlStmt = "SELECT SUM(keyStrokes) FROM totalToday WHERE date > '";
        }
        else{
            sqlStmt = "SELECT SUM(timePressed) FROM totalToday WHERE date > '";
        }

        // list of all the sqlStmt
        ArrayList<String> sqlStatements = new ArrayList<>();
        // list of all the dates
        ArrayList<LocalDate> dates = new ArrayList<>();

        // get the dates if month is selected
        if (month){
            LocalDate date = LocalDate.now();
            dates.add(date.plusDays(date.lengthOfMonth() - date.getDayOfMonth()));

            for(int i = 1; i <= 7; i++){
                LocalDate calcDate = dates.get(0).minusMonths(i);
                if (calcDate.lengthOfMonth() > calcDate.getDayOfMonth()){
                    calcDate = calcDate.plusDays(1);
                }
                else if (calcDate.getDayOfMonth() == 1){
                    calcDate = calcDate.minusDays(1);
                }
                dates.add(calcDate);
            }
        }
        else{
            dates.add(LocalDate.now());
            // start dates of all the weeks
            for(int i = 1; i <= 7; i++){
                dates.add(dates.get(0).minusWeeks(i));
            }
        }

        // creates the sqlStmt in reverse order
        for(int i = dates.size() - 1; i > 0; i--){
            sqlStatements.add(sqlStmt + dates.get(i).toString() + "' AND date <= '" + dates.get(i - 1) +
                    "' AND keyboardId = " + selectedKeyboard.getKeyboardId());
        }

        XYChart.Series<String, Number> weekValues = new XYChart.Series<>();
        int week = 1;
        int months = dates.get(dates.size() - 1).getMonthValue() + 1;
        // get's the sum values and adds them to the char series
        for(String stmt : sqlStatements){
            // get's the description for the month and the weeks
            String description = "";
            if (month){
                description = Integer.toString(months);
                months++;
                if (months > 12)
                    months = 1;
            }
            else {
                description = Integer.toString(week);
                week++;
            }

            // reads the values from the db
            if (selectedValue.equals("Key Strokes"))
                weekValues.getData().add(createData(description, ReadDb.executeIntSumFunction(stmt)));
            else
                weekValues.getData().add(createData(description, ReadDb.executeFloatSumFunction(stmt)));
            week++;
        }

        // adds the values to the right chart
        if (month)
            monthsChar.getData().add(weekValues);
        else
            weeksChar.getData().add(weekValues);
    }

}
