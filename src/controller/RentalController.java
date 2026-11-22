package controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import model.Property;
import model.PropertyOwner;
import model.RentalContract;
import model.Tenant;

public class RentalController {

    // In-memory storage keyed by ID. LinkedHashMap keeps insertion order,
    // so the getAll...() lists are returned in the order objects were added.
    private final Map<Integer, PropertyOwner> owners = new LinkedHashMap<>();
    private final Map<Integer, Property> properties = new LinkedHashMap<>();
    private final Map<Integer, Tenant> tenants = new LinkedHashMap<>();
    private final Map<Integer, RentalContract> contracts = new LinkedHashMap<>();

    // ===== PropertyOwner =====

    // Registers an owner; the ID is taken from the owner object itself.
    public synchronized void addOwner(PropertyOwner owner) {
        Objects.requireNonNull(owner, "PropertyOwner must not be null.");
        store(owners, owner.getUserId(), owner, "PropertyOwner");
    }

    public synchronized PropertyOwner getOwner(int ownerId) {
        return owners.get(ownerId);
    }

    public synchronized List<PropertyOwner> getAllOwners() {
        return snapshot(owners);
    }

    // ===== Tenant =====

    // Registers a tenant; the ID is taken from the tenant object itself.
    public synchronized void addTenant(Tenant tenant) {
        Objects.requireNonNull(tenant, "Tenant must not be null.");
        store(tenants, tenant.getUserId(), tenant, "Tenant");
    }

    public synchronized Tenant getTenant(int tenantId) {
        return tenants.get(tenantId);
    }

    public synchronized List<Tenant> getAllTenants() {
        return snapshot(tenants);
    }

    // ===== Property =====

    // Delegates creation to the owner (Model) and records the result.
    public synchronized Property createProperty(PropertyOwner owner, int propertyId,
            String description, double rentalRate) {
        Objects.requireNonNull(owner, "PropertyOwner must not be null.");
        // Check the ID BEFORE calling the Model so no orphan object is created.
        requireUnusedId(properties, propertyId, "Property");
        Property property = owner.createProperty(propertyId, description, rentalRate);
        properties.put(propertyId, property);
        return property;
    }

    public synchronized Property getProperty(int propertyId) {
        return properties.get(propertyId);
    }

    public synchronized List<Property> getAllProperties() {
        return snapshot(properties);
    }

    // ===== RentalContract =====

    // Delegates contract creation to the tenant (Model) and records the result.
    public synchronized RentalContract rentProperty(Tenant tenant, int contractId,
            Property property, LocalDate startDate, LocalDate endDate) {
        Objects.requireNonNull(tenant, "Tenant must not be null.");
        requireUnusedId(contracts, contractId, "RentalContract");
        RentalContract contract = tenant.rentProperty(contractId, property, startDate, endDate);
        contracts.put(contractId, contract);
        return contract;
    }

    // Delegates termination to the contract (Model); the contract stays in storage as history.
    public synchronized void terminateContract(RentalContract contract) {
        Objects.requireNonNull(contract, "RentalContract must not be null.").terminate();
    }

    public synchronized RentalContract getContract(int contractId) {
        return contracts.get(contractId);
    }

    public synchronized List<RentalContract> getAllContracts() {
        return snapshot(contracts);
    }

    // ===== Deprecated methods (kept for backward compatibility) =====

    @Deprecated
    public Property CreateProperty(PropertyOwner owner, int propertyID, String description, double rentalRate) {
        return createProperty(owner, propertyID, description, rentalRate);
    }

    @Deprecated
    public RentalContract RentProperty(Tenant tenant, int contractId, Property property,
            LocalDate startDate, LocalDate endDate) {
        return rentProperty(tenant, contractId, property, startDate, endDate);
    }

    @Deprecated
    public void TerminateContract(RentalContract contract) {
        terminateContract(contract);
    }

    // ===== Private helpers =====

    // Stores a value under its ID, never overwriting an existing entry.
    private <T> void store(Map<Integer, T> map, int id, T value, String typeName) {
        requireUnusedId(map, id, typeName);
        map.put(id, value);
    }

    // Fails fast if the ID is already used, so existing data is never overwritten silently.
    private <T> void requireUnusedId(Map<Integer, T> map, int id, String typeName) {
        if (map.containsKey(id)) {
            throw new IllegalArgumentException(typeName + " with ID " + id + " already exists.");
        }
    }

    // Returns an unmodifiable copy so callers cannot alter internal storage.
    private <T> List<T> snapshot(Map<Integer, T> map) {
        return Collections.unmodifiableList(new ArrayList<>(map.values()));
    }
}