
public class Property {
    private int propertyID;
    private String description;
    private double rentalRate;
    private boolean isAvailable;

    public Property(int propertyID, String description, double rentalRate) {
        validatePropertyID(propertyID);
        validateDescription(description);
        validateRentalRate(rentalRate);

        this.propertyID = propertyID;
        this.description = description.trim();
        this.rentalRate = rentalRate;
        this.isAvailable = true;
    }

    private void validatePropertyID(int propertyID) {
        if (propertyID <= 0) {
            throw new IllegalArgumentException(
                    "Property ID must be greater than zero.");
        }
    }

    private void validateDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Description cannot be empty.");
        }
    }

    private void validateRentalRate(double rentalRate) {
        if (!Double.isFinite(rentalRate) || rentalRate <= 0) {
            throw new IllegalArgumentException(
                    "Rental rate must be a finite value greater than zero.");
        }
    }

    public void UpdateDescription(String description) {
        validateDescription(description);
        this.description = description.trim();
    }

    public void UpdateRentalRate(double rentalRate) {
        validateRentalRate(rentalRate);
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

    public void setAvailable(boolean available) {
        this.isAvailable = available;
    }
}
