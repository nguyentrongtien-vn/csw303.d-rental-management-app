import java.time.LocalDate;

public class RentalContract {
    private int contractID;
    private Property property;
    private Tenant tenant;
    private LocalDate startDate;
    private LocalDate endDate;
    private double rentAmount;
    private boolean isActive;

    public RentalContract(int contractID, Property property, Tenant tenant, LocalDate startDate, LocalDate endDate) {
        // Validate everything first, so no state is changed if anything fails
        if (property == null) {
            throw new IllegalArgumentException("Property must not be null.");
        }
        if (tenant == null) {
            throw new IllegalArgumentException("Tenant must not be null.");
        }
        if (!property.isAvailable()) {
            throw new IllegalStateException("Property is not available for rent.");
        }
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date must not be null.");
        }
        if (!endDate.isAfter(startDate)) {
            throw new IllegalArgumentException("End date must be later than start date.");
        }

        this.contractID = contractID;
        this.property = property;
        this.tenant = tenant;
        this.startDate = startDate;
        this.endDate = endDate;
        this.rentAmount = property.getRentalRate();
        this.isActive = true;

        property.setAvailable(false);
    }

    public int getContractID() {
        return contractID;
    }

    public Property getProperty() {
        return property;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public double getRentAmount() {
        return rentAmount;
    }

    public boolean isActive() {
        return isActive;
    }

    public void terminate() {
        if (!isActive) {
            return; 
        }
        this.isActive = false;
        this.property.setAvailable(true);
    }
}