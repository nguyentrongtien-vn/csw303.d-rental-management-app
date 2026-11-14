package controller;

import java.time.LocalDate;

import model.Property;
import model.PropertyOwner;
import model.RentalContract;
import model.Tenant;

public class RentalController {

    public Property createProperty(PropertyOwner owner, int propertyID, String description, double rentalRate) {
        if (owner == null) {
            throw new IllegalArgumentException("PropertyOwner must not be null.");
        }
        return owner.createProperty(propertyID, description, rentalRate);
    }

    public RentalContract rentProperty(Tenant tenant, int contractId, Property property,
            LocalDate startDate, LocalDate endDate) {
        if (tenant == null) {
            throw new IllegalArgumentException("Tenant must not be null.");
        }
        return tenant.rentProperty(contractId, property, startDate, endDate);
    }

    public void terminateContract(RentalContract contract) {
        if (contract == null) {
            throw new IllegalArgumentException("RentalContract must not be null.");
        }
        contract.terminate();
    }
}
