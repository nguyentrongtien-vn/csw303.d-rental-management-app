
public class Property {
    private int propertyID;
    private String description;
    private double rentalRate;
    private boolean isAvailable;

    public Property(int propertyID, String description, double rentalRate) {
        if (propertyID <= 0) {
            throw new IllegalArgumentException(
                    "Property ID must be greater than zero.");
        }

        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Description cannot be empty.");
        }

        validateRentalRate(rentalRate);

        this.propertyID = propertyID;
        this.description = description;
        this.rentalRate = rentalRate;
        this.isAvailable = true;
    }

    public void UpdateDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Description cannot be empty.");
        }

        this.description = description;
    }

    public void UpdateRentalRate(double rentalRate) {
        validateRentalRate(rentalRate);
        this.rentalRate = rentalRate;
    }

    private void validateRentalRate(double rentalRate) {
        if (!Double.isFinite(rentalRate) || rentalRate <= 0) {
            throw new IllegalArgumentException(
                    "Rental rate must be a finite value greater than zero.");
        }
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

    public void setAvailable(boolean available) {
        this.isAvailable = available;
    }
}
