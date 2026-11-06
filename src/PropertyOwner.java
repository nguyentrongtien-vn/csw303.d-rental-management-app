import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PropertyOwner extends User {
    
    private List<Property> ownedProperties;

    public PropertyOwner(int userID, String firstName, String lastName, String email) {
        super(userID, firstName, lastName, email);
        this.ownedProperties = new ArrayList<>();
    }

    public Property createProperty(int propertyID, String description, double rentalRate) {
        Property newProperty = new Property(propertyID, description, rentalRate);
        this.ownedProperties.add(newProperty);
        return newProperty;
    }

    public List<Property> getProperties() {
        return Collections.unmodifiableList(this.ownedProperties);
    }

    @Deprecated
    public Property CreateProperty(int propertyID, String description, double rentalRate) {
        return this.createProperty(propertyID, description, rentalRate);
    }

    @Deprecated
    public List<Property> GetProperties() {
        return this.getProperties();
    }
}