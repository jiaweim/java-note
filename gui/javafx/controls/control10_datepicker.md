# DatePicker

2023-07-24, 15:15
****
## 1. 简介

DatePicker 为 combo-box 类型控件，支持从文本输入 date，或从日历选择 date。

日历以 pop-up 控件显示。DatePicker 继承自 `ComboBoxBase<LocalDate>`，所以继承了 ComboBoxBase 的所有属性。

![|250](Pasted%20image%2020230724142905.png)

pop-up 窗口：

- pop-up 的第一行显示 month 和 year。通过箭头可以调整 month 和 year
- 第二行显示 week 的缩写
- 第一列显示一年中的周数，默认不显示
    - 可以通过 pop-up 的 contextMenu 显示周数
    - 也可以使用 `showWeekNumbers` 属性设置

calendar 总是显示 42 天，非当月的日期不能选择。pop-up 中每天的 cell 为 DateCell 类。可以使用 cellFactory 自定义 cell。

右键单击第一行、week names、week number column 将显示 context menu。context menu 包含一个 Show Today 选项，将日历切换到当前日期。

## 2. DatePicker 的使用

DatePicker 默认构造函数的初始日期为 null，可以传入 LocalDate 指定初始日期：

```java
// Create a DatePicker with null as its initial value
DatePicker birthDate1 = new DatePicker();

// Use September 19, 1969 as its initial value
DatePicker birthDate2 = new DatePicker(LocalDate.of(1969, 9, 19));
```

`value` 属性指定控件的当前日期，可以通过该属性设置日期。当 value 值为 null，pop-up 显示当前月的日历。否则 pop-up 显示 value 对应的月：

```java
// Get the current value
LocalDate dt = birthDate.getValue();

// Set the current value
birthDate.setValue(LocalDate.of(1969, 9, 19));
```

DatePicker 提供了一个 TextField 用于输入文本格式的日期。其 `editor` 属性保存该 TextField 的引用。editor 为 read-only。如果不希望用户输入日期，将 editable 属性设置为 false 即可：

```java
DatePicker birthDate = new DatePicker();

// Users cannot enter a date. They must select one from the popup.
birthDate.setEditable(false);
```

`DatePicker` 包含一个 StringConverter 类型的 converter 属性，用于 LocalDate 和 String 来回转换。

`value` 属性将 date 保存为 `LocalDate` 类型，其 `editor` 以字符串显示 date：

- 当输入字符串，convert 将其转换为 LocalDate 并保存到 value 属性
- 从 pop-up 选择 date，converter 创建 LocalDate 并保存到 value 属性，并将其转换为 string 在 editor 显示

默认 converter 使用默认 Locale 来格式化日期。当输入文本作为 date，默认 converter 也是采用默认 Locale 进行解析。

**示例：** 定义 MM/dd/yyyy 格式的 StringConverter

```java
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateStringConverter extends StringConverter<LocalDate> {

    private String pattern = "MM/dd/yyyy";
    private DateTimeFormatter dtFormatter;

    public LocalDateStringConverter() {
        dtFormatter = DateTimeFormatter.ofPattern(pattern);
    }

    public LocalDateStringConverter(String pattern) {
        this.pattern = pattern;
        dtFormatter = DateTimeFormatter.ofPattern(pattern);
    }

    @Override
    public LocalDate fromString(String text) {
        LocalDate date = null;
        if (text != null && !text.trim().isEmpty()) {
            date = LocalDate.parse(text, dtFormatter);
        }
        return date;
    }

    @Override
    public String toString(LocalDate date) {
        String text = null;
        if (date != null) {
            text = dtFormatter.format(date);
        }
        return text;
    }
}
```

设置为 "MMMM dd, yyyy" 格式的 converter：

```java
DatePicker birthDate = new DatePicker();
birthDate.setConverter(new LocalDateStringConverter("MMMM dd, yyyy"));
```

设置 DatePicker 使用特定年表，而非默认。例如，将年表设置为泰国佛教年表：

```java
birthDate.setChronology(ThaiBuddhistChronology.INSTANCE);
```

修改当前 JVM 的默认 Locale，DatePicker 会使用对应日期格式和年表：

```java
// Change the default Locale to Canada
Locale.setDefault(Locale.CANADA);
```

pop-up calendar 的每天用 DateCell 类表示，该类继承自 `Cell<LocalDate>`。DatePicker 的 dayCellFactory 属性用于自定义 dayCell。

自定义 dayCell 与前面自定义 ListView 中的 cell 一样。下面创建一个 dayCellFactory，将 weekend cells 的 text 设置为 blue，禁用所有的 future day cells：

```java
Callback<DatePicker, DateCell> dayCellFactory =
    new Callback<DatePicker, DateCell>() {
        public DateCell call(final DatePicker datePicker) {
            return new DateCell() {
                @Override
                public void updateItem(LocalDate item, boolean empty) {
                    // Must call super
                    super.updateItem(item, empty);
                    // Disable all future date cells
                    if (item.isAfter(LocalDate.now())) {
                        this.setDisable(true);
                    }
                    // Show Weekends in blue
                    DayOfWeek day = DayOfWeek.from(item);
                    if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
                        his.setTextFill(Color.BLUE);
                    }
                }
            };
        }
    };
```

下面为生日的 DatePicker 设置自定义 dayCellFactory。同时设置 DatePicker 为不可编辑的。使用户只能从 pop-up 选择 nonfuture 日期：

```java
DatePicker birthDate = new DatePicker();

// Set a day cell factory to disable all future day cells
// and show weekends in blue
birthDate.setDayCellFactory(dayCellFactory);

// Users must select a date from the popup calendar
birthDate.setEditable(false);
```

当值发生变化，DatePicker 触发 ActionEvent。当用户输入日期、从 pop-up 选择日期或使用代码修改日期均可能修改 value 属性：

```java
// Add an ActionEvent handler
birthDate.setOnAction(e -> System.out.println("Date changed to:" 
                                              + birthDate.getValue()));
```

**示例：** DatePicker 的使用

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class DatePickerTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        DatePicker birthDate = new DatePicker();
        birthDate.setEditable(false);

        // Print the new date on standard output
        birthDate.setOnAction(e ->
                System.out.println("New Date:" + birthDate.getValue()));

        String pattern = "MM/dd/yyyy";
        birthDate.setConverter(new LocalDateStringConverter(pattern));
        birthDate.setPromptText(pattern.toLowerCase());

        // Create a day cell factory
        Callback<DatePicker, DateCell> dayCellFactory =
                new Callback<DatePicker, DateCell>() {
                    public DateCell call(final DatePicker datePicker) {
                        return new DateCell() {
                            @Override
                            public void updateItem(LocalDate item, boolean empty) {
                                // Must call super
                                super.updateItem(item, empty);

                                // Disable all future date cells
                                if (item.isAfter(LocalDate.now())) {
                                    this.setDisable(true);
                                }

                                // Show Weekends in blue color
                                DayOfWeek day = DayOfWeek.from(item);
                                if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
                                    this.setTextFill(Color.BLUE);
                                }
                            }
                        };
                    }
                };

        // Set the day cell factory
        birthDate.setDayCellFactory(dayCellFactory);

        HBox root = new HBox(new Label("Birth Date:"), birthDate);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Using DatePicker Control");
        stage.show();
        stage.sizeToScene();
    }
}
```

![|400](Pasted%20image%2020230724151203.png)

## 3. CSS

DatePicker 的默认 CSS 样式类名为 date-picker；pop-up 的样式类名为 date-picker-popup。

DatePicker 的所有部分几乎都能通过 CSS 设置样式，例如，pop-up 顶部的 month-year 区域，day cells, week number cells, current day cell。详情可参考 modena.css 文件。

day cell 的 CSS 样式类名为 day-cell。当前日期的 day cell 样式类名为 `today`。下面将当天以粗体显示，所有 day 的文本显示为 blue：

```css
/* Display current day numbers in bolder font */
.date-picker-popup > * > .today {
    -fx-font-weight: bolder;
}

/* Display all day numbers in blue */
.date-picker-popup > * > .day-cell {
    -fx-text-fill: blue;
}
```
