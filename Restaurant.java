/**
 * Represents a single restaurant entry with its basic attributes.
 */
public class Restaurant {

    String name;
    int rating;
    int distance;
    int price;
    String cuisine;

    /**
     * Constructs a Restaurant object.
     *
     * @param name     Restaurant name
     * @param rating   Customer rating (1 – 5)
     * @param distance Distance from the company (1 – 10 miles)
     * @param price    Average price per person ($10 – $50)
     * @param cuisine  Cuisine type (mapped from cuisines.csv)
     */
    public Restaurant(String name, int rating, int distance, int price, String cuisine) {
        this.name = name;
        this.rating = rating;
        this.distance = distance;
        this.price = price;
        this.cuisine = cuisine;
    }

    @Override
    public String toString() {
        return String.format(
                "%s | Rating: %d | Distance: %d | Price: %d | Cuisine: %s ",
                name, rating, distance, price, cuisine);
    }
}
