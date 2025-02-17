import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class app extends Application {
    private TextField amountField;
    private TextField resultField;
    private ComboBox<String> fromCurrencyComboBox;
    private ComboBox<String> toCurrencyComboBox;
    private final Map<String, Double> exchangeRates = new HashMap<>();

    @Override
    public void start(Stage primaryStage) {
        initializeExchangeRates();
        
        primaryStage.setTitle("Currency Converter");

        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setStyle("-fx-background-color: #f0f0f0;");
        mainLayout.setAlignment(Pos.CENTER);

        // Title
        Label titleLabel = new Label("Currency Converter");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Create main content
        VBox converterBox = createConverterContent();

        mainLayout.getChildren().addAll(titleLabel, converterBox);

        Scene scene = new Scene(mainLayout, 400, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void initializeExchangeRates() {
        // Current exchange rates as of March 2024
        exchangeRates.put("USD", 1.0);     // US Dollar (base currency)
        exchangeRates.put("EUR", 0.962);   // Euro (1 EUR = 1.04 USD)
        exchangeRates.put("GBP", 0.79);    // British Pound
        exchangeRates.put("JPY", 148.42);  // Japanese Yen
        exchangeRates.put("INR", 82.84);   // Indian Rupee
        exchangeRates.put("AUD", 1.53);    // Australian Dollar
        exchangeRates.put("CAD", 1.35);    // Canadian Dollar
        exchangeRates.put("CHF", 0.89);    // Swiss Franc
        exchangeRates.put("CNY", 7.19);    // Chinese Yuan
        exchangeRates.put("NZD", 1.65);    // New Zealand Dollar
        exchangeRates.put("ETB", 127.0);   // Ethiopian Birr
    }

    private VBox createConverterContent() {
        VBox content = new VBox(15);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: white; -fx-background-radius: 10;");

        // Amount input
        Label amountLabel = createLabel("Enter Amount:");
        amountField = createStyledTextField("Enter amount");

        // From Currency
        Label fromLabel = createLabel("From Currency:");
        fromCurrencyComboBox = createCurrencyComboBox();

        // To Currency
        Label toLabel = createLabel("To Currency:");
        toCurrencyComboBox = createCurrencyComboBox();

        // Result
        Label resultLabel = createLabel("Result:");
        resultField = createStyledTextField("");
        resultField.setEditable(false);

        // Buttons
        GridPane buttonGrid = new GridPane();
        buttonGrid.setHgap(10);
        buttonGrid.setAlignment(Pos.CENTER);

        Button convertButton = createStyledButton("Convert");
        Button clearButton = createStyledButton("Clear");
        clearButton.setStyle("""
                -fx-font-size: 16px;
                -fx-padding: 10px 20px;
                -fx-background-color: #f44336;
                -fx-text-fill: white;
                -fx-background-radius: 5;
                """);

        buttonGrid.add(convertButton, 0, 0);
        buttonGrid.add(clearButton, 1, 0);

        // Auto-convert checkbox
        CheckBox autoConvertCheckBox = new CheckBox("Enable Automatic Conversion");
        autoConvertCheckBox.setStyle("-fx-font-size: 14px;");

        // Add event handlers
        convertButton.setOnAction(e -> convertCurrency());
        clearButton.setOnAction(e -> {
            amountField.clear();
            resultField.clear();
            fromCurrencyComboBox.setValue("USD - US Dollar");
            toCurrencyComboBox.setValue("EUR - Euro");
            autoConvertCheckBox.setSelected(false);
        });

        // Add listeners for automatic conversion
        amountField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (autoConvertCheckBox.isSelected() && !newValue.isEmpty()) {
                convertCurrency();
            }
        });

        fromCurrencyComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (autoConvertCheckBox.isSelected() && !amountField.getText().isEmpty()) {
                convertCurrency();
            }
        });

        toCurrencyComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (autoConvertCheckBox.isSelected() && !amountField.getText().isEmpty()) {
                convertCurrency();
            }
        });

        content.getChildren().addAll(
                amountLabel, amountField,
                fromLabel, fromCurrencyComboBox,
                toLabel, toCurrencyComboBox,
                buttonGrid,
                autoConvertCheckBox,
                resultLabel, resultField
        );

        return content;
    }

    private ComboBox<String> createCurrencyComboBox() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().addAll(
                "USD - US Dollar",
                "EUR - Euro",
                "GBP - British Pound",
                "JPY - Japanese Yen",
                "INR - Indian Rupee",
                "AUD - Australian Dollar",
                "CAD - Canadian Dollar",
                "CHF - Swiss Franc",
                "CNY - Chinese Yuan",
                "NZD - New Zealand Dollar",
                "ETB - Ethiopian Birr"
        );
        comboBox.setValue("USD - US Dollar");
        comboBox.setMaxWidth(Double.MAX_VALUE);
        comboBox.setStyle("-fx-font-size: 14px; -fx-padding: 8px;");
        return comboBox;
    }

    private void convertCurrency() {
        try {
            double amount = Double.parseDouble(amountField.getText());
            String fromCurrencyFull = fromCurrencyComboBox.getValue();
            String toCurrencyFull = toCurrencyComboBox.getValue();
            
            // Extract currency codes from the full labels
            String fromCurrency = fromCurrencyFull.substring(0, 3);
            String toCurrency = toCurrencyFull.substring(0, 3);

            // Convert to USD first (as base currency)
            double amountInUSD = amount / exchangeRates.get(fromCurrency);
            // Convert from USD to target currency
            double result = amountInUSD * exchangeRates.get(toCurrency);

            DecimalFormat df = new DecimalFormat("#,##0.00");
            resultField.setText(df.format(result) + " " + toCurrency);
        } catch (NumberFormatException e) {
            resultField.setText("Invalid amount - Please enter a number");
        } catch (Exception e) {
            resultField.setText("Conversion error");
        }
    }

    private TextField createStyledTextField(String promptText) {
        TextField field = new TextField();
        field.setPromptText(promptText);
        field.setStyle("""
                -fx-font-size: 14px;
                -fx-padding: 8px;
                -fx-background-radius: 5;
                """);
        field.setMaxWidth(Double.MAX_VALUE);
        return field;
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setStyle("""
                -fx-font-size: 14px;
                -fx-padding: 8px 16px;
                -fx-background-color: #2196F3;
                -fx-text-fill: white;
                -fx-background-radius: 5;
                """);
        button.setMaxWidth(Double.MAX_VALUE);
        return button;
    }

    private Label createLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        return label;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
