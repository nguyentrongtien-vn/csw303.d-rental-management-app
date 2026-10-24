
public class Property {
    private int propertyID;
    private String description;
    private double rentalRate;
    private boolean isAvailable;

    public Property(int propertyID, String description, double rentalRate) {
        if (rentalRate <= 0) {
            throw new IllegalArgumentException("Rental rate must be greater than zero.");
        }

        this.propertyID = propertyID;
        this.description = description;
        this.rentalRate = rentalRate;
        this.isAvailable = true;
    }

    public void UpdateDescription(String description) {
        this.description = description;
    }

    public void UpdateRentalRate(double rentalRate) {
        if (rentalRate <= 0) {
            throw new IllegalArgumentException("Rental rate must be greater than zero.");
        }

        this.rentalRate = rentalRate;
    }

    public int getPropertyID() {
        return propertyID;
    }

    public String getDescription() {
        return description;
    }

    public double getRentalRate() {
        return rentalRate;
    }

    public boolean isAvailable() {
        return isAvailable;
    }
}
