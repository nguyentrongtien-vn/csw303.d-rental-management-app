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
        if (property == null || !property.isAvailable()) {
            throw new IllegalStateException("Property is not available for rent.");
        }

        if (startDate == null || endDate == null || !endDate.isAfter(startDate)) {
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