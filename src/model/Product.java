package model;

/**
 * Created by Nimish on 15/02/15.
 */
public class Product {
    private String name;
    private String availability;
    private String brand;
    private String color;

    public Product(String name, String availability, String brand, String color) {
        this.name = name;
        this.availability = availability;
        this.brand = brand;
        this.color = color;
    }

    @Override
    public String toString() {
        return String.format("%-20s%-20s%-20s%-20s", name, availability, brand, color);
    }
}
