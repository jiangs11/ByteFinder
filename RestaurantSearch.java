import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Main class responsible for loading restaurant and cuisine data,
 * applying search filters, and returning the best-matched results
 * according to predefined sorting and filtering rules.
 *
 * <p><b>Matching logic:</b></p>
 * <ul>
 *   <li>Name and cuisine allow partial, case-insensitive matches.</li>
 *   <li>Customer rating: restaurant rating ≥ input rating.</li>
 *   <li>Distance: restaurant distance ≤ input distance.</li>
 *   <li>Price: restaurant price ≤ input price.</li>
 *   <li>All filters use an AND relationship.</li>
 * </ul>
 *
 * <p><b>Sorting priority:</b></p>
 * <ol>
 *   <li>Distance (ascending)</li>
 *   <li>Customer Rating (descending)</li>
 *   <li>Price (ascending)</li>
 *   <li>Ties left in arbitrary order</li>
 * </ol>
 */
public class RestaurantSearch {

    private final List<Restaurant> restaurants = new ArrayList<>();

    /**
     * Constructs a RestaurantSearch object by loading the restaurant
     * and cuisine data from the given CSV files.
     *
     * @param restaurantFile Path to restaurants.csv
     * @param cuisineFile    Path to cuisines.csv
     * @throws IOException If file read or parse errors occur
     */
    public RestaurantSearch(String restaurantFile, String cuisineFile) throws IOException {
        Map<String, String> cuisineMap = loadCuisines(cuisineFile);
        loadRestaurants(restaurantFile, cuisineMap);
    }

    /**
     * Loads cuisine data from a CSV file and builds a mapping from cuisine ID to name.
     *
     * @param filePath Path to the cuisines CSV file
     * @return Map of cuisineId → cuisineName
     * @throws IOException If the file cannot be read
     */
    private Map<String, String> loadCuisines(String filePath) throws IOException {
        System.out.println("Loading cuisines from: " + filePath);

        try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
            return stream.skip(1) // Skip header row
                    .map(line -> line.split(","))
                    .filter(parts -> parts.length >= 2) // Skip malformed rows
                    .collect(Collectors.toMap(
                            parts -> parts[0].trim(), // ID
                            parts -> parts[1].trim()  // Name
                    ));
        }
    }

    /**
     * Loads restaurant data from a CSV file and maps each cuisine ID to its name.
     * Invalid or malformed rows are skipped.
     *
     * @param filePath   Path to the restaurants CSV file
     * @param cuisineMap Mapping of cuisine IDs to names
     * @throws IOException If the file cannot be read
     */
    private void loadRestaurants(String filePath, Map<String, String> cuisineMap) throws IOException {
        System.out.println("Loading restaurants from: " + filePath);

        try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
            stream.skip(1) // Skip header row
                    .map(line -> line.split(","))
                    .filter(parts -> parts.length >= 5) // Skip malformed rows
                    .forEach(parts -> {
                        String name = parts[0].trim();
                        int rating = Integer.parseInt(parts[1].trim());
                        int distance = Integer.parseInt(parts[2].trim());
                        int price = Integer.parseInt(parts[3].trim());
                        String cuisineId = parts[4].trim();
                        String cuisine = cuisineMap.getOrDefault(cuisineId, "Other");

                        restaurants.add(new Restaurant(name, rating, distance, price, cuisine));
                    });
        }

        System.out.println("Loaded " + restaurants.size() + " restaurants.");
    }

    /**
     * Searches restaurants using up to five optional criteria and returns up to five
     * best matches according to the specified sorting rules.
     *
     * @param name     Restaurant name (partial or full case-insensitive match)
     * @param rating   Minimum customer rating (1–5)
     * @param distance Maximum distance in miles (1–10)
     * @param price    Maximum price per person ($10–$50)
     * @param cuisine  Cuisine name (partial or full case-insensitive match)
     * @return List of up to five best-matched restaurants
     * @throws IllegalArgumentException If any parameter is invalid
     */
    public List<Restaurant> search(String name, Integer rating, Integer distance, Integer price, String cuisine) {
        // Validate numeric input parameters
        if (rating != null && (rating < 1 || rating > 5)) {
            throw new IllegalArgumentException("Customer Rating must be between 1 and 5.");
        }
        if (distance != null && (distance < 1 || distance > 10)) {
            throw new IllegalArgumentException("Distance must be between 1 and 10 miles.");
        }
        if (price != null && (price < 10 || price > 50)) {
            throw new IllegalArgumentException("Price must be between $10 and $50.");
        }

        Stream<Restaurant> stream = restaurants.stream();

        // Apply filters
        if (name != null && !name.isEmpty()) {
            String n = name.toLowerCase();
            stream = stream.filter((Restaurant r) -> r.name.toLowerCase().contains(n));
        }
        if (rating != null) {
            stream = stream.filter((Restaurant r) -> r.rating >= rating);
        }
        if (distance != null) {
            stream = stream.filter((Restaurant r) -> r.distance <= distance);
        }
        if (price != null) {
            stream = stream.filter((Restaurant r) -> r.price <= price);
        }
        if (cuisine != null && !cuisine.isEmpty()) {
            String c = cuisine.toLowerCase();
            stream = stream.filter((Restaurant r) -> r.cuisine.toLowerCase().contains(c));
        }

        // Sort and return best matches
        return stream
                .sorted(Comparator
                        .comparingInt((Restaurant r) -> r.distance)
                        .thenComparingInt((Restaurant r) -> -r.rating)
                        .thenComparingInt((Restaurant r) -> r.price))
                .limit(5)
                .collect(Collectors.toList());
    }

    /**
     * Entry point for manual testing. Loads the restaurant and cuisine CSV files,
     * performs an example search, and prints the results.
     *
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            RestaurantSearch searcher = new RestaurantSearch(
                    "csv/restaurants.csv",
                    "csv/cuisines.csv"
            );

            // Test input parameters
            List<Restaurant> results = searcher.search(null, null, null, null, null);

            if (results.isEmpty()) {
                System.out.println("No matches found.");
            } else {
                System.out.println("Best matched restaurants:");
                results.forEach(System.out::println);
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
