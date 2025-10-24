import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

/**
 * RestaurantUI is a Java Swing-based graphical user interface for searching restaurants.
 * It allows users to input search criteria such as name, minimum rating, maximum distance,
 * maximum price, and cuisine type, and displays matching results in a text area.
 * <p>
 * The class uses a {@link RestaurantSearch} object to perform the actual search logic
 * and presents results in a scrollable text area. The layout uses a combination of
 * GridLayout, FlowLayout, and BorderLayout for clean spacing and alignment.
 */
public class RestaurantUI extends JFrame {

    // The search engine used to query restaurant data.
    private RestaurantSearch searcher;

    // Text fields for entering search criteria.
    private final JTextField nameField = new JTextField(15);
    private final JTextField ratingField = new JTextField(2);
    private final JTextField distanceField = new JTextField(2);
    private final JTextField priceField = new JTextField(2);
    private final JTextField cuisineField = new JTextField(10);

    // Text area to display the search results.
    private final JTextArea resultArea = new JTextArea(10, 40);

    /**
     * Constructs the RestaurantUI window, initializes the searcher,
     * sets up input fields, the search button, and the results area.
     * <p>
     * The UI is laid out with padding and spacing for readability,
     * and the search button is centered below the input fields.
     */
    public RestaurantUI() {
        try {
            searcher = new RestaurantSearch(
                    "csv/restaurants.csv",
                    "csv/cuisines.csv");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
            System.exit(1);
        }

        setTitle("ByteFinder - Restaurant Search Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 0, 15));

        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Rating (1 - 5):"));
        inputPanel.add(ratingField);
        inputPanel.add(new JLabel("Distance (1 - 10):"));
        inputPanel.add(distanceField);
        inputPanel.add(new JLabel("Price (10 - 50):"));
        inputPanel.add(priceField);
        inputPanel.add(new JLabel("Cuisine:"));
        inputPanel.add(cuisineField);

        // Search Button
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> doSearch());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(searchButton);

        // Combine Input + Button
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Results Area
        resultArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(resultArea);
        scroll.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Add to Frame
        add(topPanel, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null); // Center window on screen
        setVisible(true);
    }

    /**
     * Performs the search operation using the input from the text fields.
     * <p>
     * Retrieves values from the input fields, converts numeric fields to Integer,
     * and calls {@link RestaurantSearch#search} with the criteria. Displays results
     * in the resultArea. If no matches are found, a message is displayed.
     */
    private void doSearch() {
        try {
            resultArea.setText("");

            String name = nameField.getText().trim();
            Integer rating = parseOrNull(ratingField.getText());
            Integer distance = parseOrNull(distanceField.getText());
            Integer price = parseOrNull(priceField.getText());
            String cuisine = cuisineField.getText().trim();

            List<Restaurant> results = searcher.search(name, rating, distance, price, cuisine);

            if (results.isEmpty()) {
                resultArea.append("No matches found.\n");
            } else {
                for (int i = 0; i < results.size(); i++) {
                    resultArea.append((i + 1) + ").  " + results.get(i).toString() + "\n");
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    /**
     * Parses a string into an Integer. Returns null if the string is null or empty.
     *
     * @param s The string to parse
     * @return The parsed Integer, or null if the string is empty
     * @throws NumberFormatException If the string is not a valid integer
     */
    private Integer parseOrNull(String s) throws NumberFormatException {
        if (s == null || s.isEmpty()) {
            return null;
        }

        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Rating/Distance/Price must be a whole number.");
        }
    }

    /**
     * The main method launches the RestaurantUI application.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(RestaurantUI::new);
    }
}
