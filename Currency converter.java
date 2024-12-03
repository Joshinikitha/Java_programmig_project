import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class CurrencyConverter extends Frame implements ActionListener {
    // Declare components
    Label lblHeader, lblFooter, lblFrom, lblTo, lblAmount, lblResult, lblInfo;
    TextField txtAmount, txtResult;
    Choice fromCurrency, toCurrency;
    Button convertButton, showGraphButton;

    public CurrencyConverter() {
        // Set up the frame
        setTitle("Real-Time Currency Converter");
        setSize(400, 500); // Adjusted size
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Add padding
        setBackground(new Color(240, 248, 255));

        // Header
        lblHeader = new Label("REAL-TIME CURRENCY CONVERTER", Label.CENTER);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(lblHeader, gbc);

        // Label for "Currencyrate from exchangerateapi.com" (just below header with no space)
        Label lblSnippet = new Label("CurrencyRate from exchangerateapi.com", Label.CENTER);
        lblSnippet.setFont(new Font("Arial", Font.PLAIN, 10));  // Set small font
        lblSnippet.setForeground(Color.GRAY);  // Set grey color

        // Adjust GridBagConstraints for lblSnippet
        gbc.gridy = 1;  // Position it immediately below the header, no space
        gbc.insets = new Insets(0, 0, 20, 0);  // No padding
        gbc.weighty = 0.1;  // Allow a little vertical space, but not stretching
        gbc.anchor = GridBagConstraints.CENTER;  // Center the label
        add(lblSnippet, gbc);

        // Label for "Customizing conversion between currencies" (with a very small vertical space)
        lblInfo = new Label("Customizing conversion between currencies", Label.CENTER);
        lblInfo.setFont(new Font("Arial", Font.PLAIN, 14));  // Same font size as before
        lblInfo.setForeground(Color.BLUE);  // Set blue color

        // Adjust GridBagConstraints for lblInfo
        gbc.gridy = 2;  // Place it below the previous label
        gbc.insets = new Insets(1, 0, 30, 0);  // Very small vertical space (top and bottom)
        add(lblInfo, gbc);

        // From Currency
        lblFrom = new Label("From Currency    :");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        add(lblFrom, gbc);

        fromCurrency = new Choice();
        String[] currencies = { "USD", "EUR", "INR", "GBP", "JPY", "AUD", "CAD" };
        for (String currency : currencies) {
            fromCurrency.add(currency);
        }
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(fromCurrency, gbc);

        // To Currency
        lblTo = new Label("To Currency         :");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        add(lblTo, gbc);

        toCurrency = new Choice();
        for (String currency : currencies) {
            toCurrency.add(currency);
        }
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(toCurrency, gbc);

        // Amount
        lblAmount = new Label("Amount to convert:");
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        add(lblAmount, gbc);

        txtAmount = new TextField(10);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(txtAmount, gbc);

        // Buttons - Convert and Show Graph placed side by side
        Panel buttonPanel = new Panel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        convertButton = new Button("Convert");
        convertButton.setBackground(Color.GREEN);
        convertButton.setForeground(Color.WHITE);
        convertButton.setFont(new Font("Arial", Font.BOLD, 14));
        buttonPanel.add(convertButton);

        showGraphButton = new Button("Show Graph");
        showGraphButton.setBackground(Color.CYAN);
        showGraphButton.setForeground(Color.BLACK);
        showGraphButton.setFont(new Font("Arial", Font.BOLD, 14));
        buttonPanel.add(showGraphButton);

        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);

        // Result
        lblResult = new Label("Converted Amount:");
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(lblResult, gbc);

        txtResult = new TextField(10);
        txtResult.setEditable(false);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(txtResult, gbc);

        // Footer
        lblFooter = new Label("Made by Joshi", Label.CENTER);
        lblFooter.setFont(new Font("Arial", Font.ITALIC, 12));
        gbc.gridx = 1;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        add(lblFooter, gbc);

        // Add action listeners for the buttons
        convertButton.addActionListener(this);
        showGraphButton.addActionListener(this);

        // Add window listener for closing
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                dispose();
            }
        });

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == convertButton) {
            try {
                String from = fromCurrency.getSelectedItem();
                String to = toCurrency.getSelectedItem();
                double amount = Double.parseDouble(txtAmount.getText());

                double convertedAmount = getConvertedAmount(from, to, amount);
                txtResult.setText(String.format("%.2f", convertedAmount));
            } catch (Exception ex) {
                txtResult.setText("Error!");
            }
        } else if (e.getSource() == showGraphButton) {
            String from = fromCurrency.getSelectedItem();
            String to = toCurrency.getSelectedItem();
            showGraph(from, to);
        }
    }

    private double getConvertedAmount(String from, String to, double amount) {
        String apiKey = "5c9bb566ed62b8722511f24b"; // Replace with your actual API key
        String apiUrl = "https://v6.exchangerate-api.com/v6/" + apiKey + "/latest/" + from;

        try {
            // Create URL and open connection
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Read response
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Parse JSON response
            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONObject conversionRates = jsonResponse.getJSONObject("conversion_rates");

            // Get conversion rate and calculate result
            double rate = conversionRates.getDouble(to);
            return amount * rate;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0.0;
    }

    private void showGraph(String from, String to) {
        String apiKey = "5c9bb566ed62b8722511f24b";
        String apiUrl = "https://v6.exchangerate-api.com/v6/" + apiKey + "/latest/" + from;

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONObject conversionRates = jsonResponse.getJSONObject("conversion_rates");

            // Mock data: Populate graph with random values for demo purposes
            double[] rates = new double[7];
            for (int i = 0; i < 7; i++) {
                rates[i] = conversionRates.getDouble(to) + (Math.random() - 0.5);
            }

            // Display graph
            Frame graphFrame = new Frame("Exchange Rate Graph");
            graphFrame.setSize(800, 600);
            graphFrame.add(new GraphPanel(rates, from, to));
            graphFrame.setVisible(true);

        } catch (Exception ex) {
            ex.printStackTrace();
            showErrorDialog("Failed to fetch exchange rate data.");
        }
    }

    private void showErrorDialog(String message) {
        Dialog errorDialog = new Dialog(this, "Error", true);
        errorDialog.setSize(300, 150);
        errorDialog.setLocation(250, 100);
        errorDialog.add(new Label(message));
        Button okButton = new Button("OK");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                errorDialog.setVisible(false);
            }
        });
        errorDialog.add(okButton);
        errorDialog.setVisible(true);
    }

    // Inner class for GraphPanel
    class GraphPanel extends Panel {
        private final double[] rates;
        private final String fromCurrency;
        private final String toCurrency;

        public GraphPanel(double[] rates, String fromCurrency, String toCurrency) {
            this.rates = rates;
            this.fromCurrency = fromCurrency;
            this.toCurrency = toCurrency;
        }

        @Override
        public void paint(Graphics g) {
            int width = getWidth();
            int height = getHeight();
            int margin = 50;

            // Draw axes
            g.setColor(Color.BLACK);
            g.drawLine(margin, height - margin, width - margin, height - margin); // X-axis
            g.drawLine(margin, margin, margin, height - margin); // Y-axis

            // Draw title
            g.setFont(new Font("TimesNewRoman", Font.BOLD, 16));
            g.drawString("Exchange Rate Graph: " + fromCurrency + " to " + toCurrency, width / 4, margin / 2);

            // Draw X and Y labels
            g.setFont(new Font("Arial", Font.BOLD, 12));
            g.drawString("Time (Days)", width / 2, height - margin / 5); // X label

            // Rotate the Y-axis label
            Graphics2D g2d = (Graphics2D) g;
            g2d.rotate(-Math.PI / 2);
            g.drawString("Exchange Rate", -height / 2, margin / 3);
            g2d.rotate(Math.PI / 2); // Reset rotation

            // Scale rates
            double maxRate = Double.MIN_VALUE;
            double minRate = Double.MAX_VALUE;
            for (double rate : rates) {
                maxRate = Math.max(maxRate, rate);
                minRate = Math.min(minRate, rate);
            }

            // Plot data points and draw X, Y points
            int numPoints = rates.length;
            int graphWidth = width - 2 * margin;
            int graphHeight = height - 2 * margin;

            for (int i = 0; i < numPoints - 1; i++) {
                int x1 = margin + (i * graphWidth / (numPoints - 1));
                int y1 = height - margin - (int) ((rates[i] - minRate) / (maxRate - minRate) * graphHeight);
                int x2 = margin + ((i + 1) * graphWidth / (numPoints - 1));
                int y2 = height - margin - (int) ((rates[i + 1] - minRate) / (maxRate - minRate) * graphHeight);

                g.setColor(Color.BLUE);
                g.drawLine(x1, y1, x2, y2); // Connect points

                // Draw coordinates as points on the graph
                g.setColor(Color.RED);
                g.fillOval(x1 - 3, y1 - 3, 6, 6); // Draw point at (x1, y1)
                g.fillOval(x2 - 3, y2 - 3, 6, 6); // Draw point at (x2, y2)

                // Annotate points with their coordinates
                g.setColor(Color.BLACK);
                g.drawString("(" + i + ", " + String.format("%.2f", rates[i]) + ")", x1 + 5, y1 - 5);
                g.drawString("(" + (i + 1) + ", " + String.format("%.2f", rates[i + 1]) + ")", x2 + 5, y2 - 5);
            }
        }
    }

    public static void main(String[] args) {
        new CurrencyConverter();
    }
}
