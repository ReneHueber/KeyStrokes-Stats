package gui;

import database.ReadDb;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import objects.Keyboard;
import objects.TotalToday;

import java.text.DecimalFormat;
import java.time.DayOfWeek;
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
    private Label maxDay;
    @FXML
    private Label maxWeek;
    @FXML
    private Label maxMonth;

    @FXML
    private BarChart <String, Number> daysChar;
    @FXML
    private NumberAxis daysY;

    @FXML
    private BarChart<String, Number> weeksChar;
    @FXML
    private NumberAxis weeksY;

    @FXML
    private BarChart<String, Number> monthsChar;
    @FXML
    private NumberAxis monthsY;


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
        controller.setMaxValues(statisticName);
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
     * Set's the max Value Labels.
     * @param statisticName Name of the statistic chosen
     */
    protected void setMaxValues(String statisticName){
        setDayMaxValue(statisticName);
        setWeekMonthMaxValues(statisticName, false);
        setWeekMonthMaxValues(statisticName, true);
    }

    /**
     * Set's the Data and adds a Label with the Value on top of the Bar Char.
     * @param date Name of the Bar
     * @param value Value of the Bar
     * @return XYChar.Data with the passed Values and a Node with the Value as Text
     */
    private XYChart.Data<String, Number> createData(String date, Number value) {
        String text;
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
        String sqlStmt;
        if (selectedValue.equals("Key Strokes")){
            sqlStmt = "SELECT SUM(keyStrokes) FROM totalToday WHERE date >= '";
        }
        else{
            sqlStmt = "SELECT SUM(timePressed) FROM totalToday WHERE date >= '";
        }

        // list of all the sqlStmt
        ArrayList<String> sqlStatements = new ArrayList<>();
        // list of all the dates
        ArrayList<LocalDate[]> dates;

        // get's all first 7 values, in use Since is decreased because it is not need, and so we always get 7 results.
        if (month)
            dates = getAllMonthValues(LocalDate.parse(selectedKeyboard.getInUseSince()).minusYears(1), true);
        else
            dates = getAllWeekDates(LocalDate.parse(selectedKeyboard.getInUseSince()).minusYears(1), true);


        // creates the sqlStmt in reverse order
        for(int i = dates.size() - 1; i >= 0; i--){
            sqlStatements.add(sqlStmt + dates.get(i)[0].toString() + "' AND date <= '" + dates.get(i)[1].toString() +
                    "' AND keyboardId = " + selectedKeyboard.getKeyboardId());
        }

        // creates the description of the X Axis
        XYChart.Series<String, Number> weekValues = new XYChart.Series<>();
        // we start with the last week
        int week = -6;
        int months = dates.get(dates.size() - 1)[0].getMonthValue();
        // get's the sum values and adds them to the char series
        for(String stmt : sqlStatements){
            // get's the description for the month and the weeks
            String description;
            if (month){
                description = Integer.toString(months);
                months++;
                if (months > 12)
                    months = 1;
            }
            else {
                description = Integer.toString(week);
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

    /**
     * Get's the max Value per Day and set's it to the maxDay Label.
     * @param statisticName Name of the statistic choosen
     */
    private void setDayMaxValue(String statisticName){
        int maxKeyStroke = 0;
        float maxTimePressed = 0.0f;

        String sqlStmt = "SELECT keyboardId, date, keyStrokes, timePressed FROM " +
                "totalToday WHERE date >= '" + selectedKeyboard.getInUseSince() + "' AND date <= '"
                + LocalDate.now().toString() + "' AND keyboardId = " + selectedKeyboard.getKeyboardId();

        ArrayList<TotalToday> values = ReadDb.selectAllValuesTotalToday(sqlStmt);

        for (TotalToday value : values){
            if (value.getKeyStrokes() > maxKeyStroke)
                maxKeyStroke = value.getKeyStrokes();

            if (value.getTimePressed() > maxTimePressed)
                maxTimePressed = value.getTimePressed();
        }

        if (statisticName.equals("Key Strokes"))
            maxDay.setText(Integer.toString(maxKeyStroke));
        else
            maxDay.setText(Float.toString(maxTimePressed));
    }

    /**
     * Get's the max Week or Month value and set's the belonging Label.
     * @param statisticName Name of the statistic chosen
     * @param month True the max month Value is searched and set
     */
    private void setWeekMonthMaxValues(String statisticName, boolean month){
        int maxKeyStrokes = 0;
        float maxTimePressed = 0.0f;

        // creates the basic sql Stmt
        String sqlStmt;
        if (statisticName.equals("Key Strokes")){
            sqlStmt = "SELECT SUM(keyStrokes) FROM totalToday WHERE date >= '";
        }
        else{
            sqlStmt = "SELECT SUM(timePressed) FROM totalToday WHERE date >= '";
        }

        ArrayList<LocalDate[]> dates;
        if (month)
            dates = getAllMonthValues(LocalDate.parse(selectedKeyboard.getInUseSince()), false);
        else
            dates = getAllWeekDates(LocalDate.parse(selectedKeyboard.getInUseSince()), false);

        // get's the max Values depending of the wished Value
        for (LocalDate[] dateValues : dates){
            String stmt = sqlStmt + dateValues[0].toString() + "' AND date <= '" + dateValues[1].toString()
                    + "' AND keyboardId = " + selectedKeyboard.getKeyboardId();
            if (statisticName.equals("Key Strokes")){
                int sumValue = ReadDb.executeIntSumFunction(stmt);
                if (sumValue > maxKeyStrokes)
                    maxKeyStrokes = sumValue;
            }
            else{
                float sumValue = ReadDb.executeFloatSumFunction(stmt);
                if (sumValue > maxTimePressed)
                    maxTimePressed = sumValue;
            }
        }

        if (statisticName.equals("Key Strokes")){
            if (month)
                maxMonth.setText(Integer.toString(maxKeyStrokes));
            else
                maxWeek.setText(Integer.toString(maxKeyStrokes));
        }
        else{
            if (month)
                maxMonth.setText(Float.toString(maxTimePressed));
            else
                maxWeek.setText(Float.toString(maxTimePressed));
        }
    }

    /**
     * Get's all start and the end Dates of the Weeks from the current Date until the passed Date.
     * @param useSince Stop Date
     * @param first7 Only the first 7 Dates
     * @return Start and End Dates of all the Weeks between the current and the stop Date, or only the first 7
     */
    private ArrayList<LocalDate[]> getAllWeekDates(LocalDate useSince, boolean first7){
        ArrayList<LocalDate[]> dates = new ArrayList<>();

        DayOfWeek day = LocalDate.now().getDayOfWeek();
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now();

        switch(day){
            case MONDAY: endDate = endDate.plusDays(6);
                         break;
            case TUESDAY: startDate = startDate.minusDays(1);
                          endDate = endDate.plusDays(5);
                          break;
            case WEDNESDAY: startDate = startDate.minusDays(2);
                            endDate = endDate.plusDays(4);
                            break;
            case THURSDAY: startDate = startDate.minusDays(3);
                           endDate = endDate.plusDays(3);
                           break;
            case FRIDAY: startDate = startDate.minusDays(4);
                         endDate = endDate.plusDays(2);
                         break;
            case SATURDAY: startDate = startDate.minusDays(5);
                           endDate = endDate.plusDays(1);
                           break;
            case SUNDAY: startDate = startDate.minusDays(6);
                         break;
        }

        dates.add(new LocalDate[]{startDate, endDate});

        // get's executed as long as startDate is bigger than useSince
        while (startDate.isAfter(useSince)){
            startDate = startDate.minusWeeks(1);
            endDate = endDate.minusWeeks(1);
            dates.add(new LocalDate[]{startDate, endDate});

            if (first7 && dates.size() == 7)
                break;
        }

        return dates;
    }

    /**
     * Get's all the start and End Dates of the Months from the current Date until the passed Date.
     * @param useSince Stop Date
     * @param first7 Only the first 7 Dates
     * @return Start and End Dates of all the Month between the current and the stop Date, or only the first 7
     */
    private ArrayList<LocalDate[]> getAllMonthValues(LocalDate useSince, boolean first7){
        ArrayList<LocalDate[]> dates = new ArrayList<>();

        int dayOfMonth = LocalDate.now().getDayOfMonth();
        int daysMonth = LocalDate.now().lengthOfMonth();
        LocalDate startDate;
        LocalDate endDate = LocalDate.now();

        endDate = endDate.plusDays(daysMonth - dayOfMonth);
        startDate = endDate.minusDays(daysMonth - 1);

        dates.add(new LocalDate[]{startDate, endDate});

        while (startDate.isAfter(useSince)){
            startDate = startDate.minusMonths(1);
            endDate = endDate.minusMonths(1);
            if (endDate.getDayOfMonth() < endDate.lengthOfMonth()){
                endDate = endDate.plusDays(endDate.lengthOfMonth() - endDate.getDayOfMonth());
            }
            dates.add(new LocalDate[]{startDate, endDate});

            if (first7 && dates.size() == 7)
                break;
        }

        return dates;
    }
}
