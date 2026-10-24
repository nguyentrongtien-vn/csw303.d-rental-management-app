import java.util.ArrayList;
import java.util.List;

public class PropertyOwner extends User {
    
    private List<Property> ownedProperties;

    public PropertyOwner(int userID, String firstName, String lastName, String email) {
        super(userID, firstName, lastName, email);
        
        this.ownedProperties = new ArrayList<>();
    }

    public Property CreateProperty(int propertyID, String description, double rentalRate) {
        try {
            Property newProperty = new Property(propertyID, description, rentalRate);
            
            this.ownedProperties.add(newProperty);
            
            return newProperty;
            
        } catch (IllegalArgumentException e) {
            System.out.println("Lỗi tạo tài sản: " + e.getMessage());
            return null; 
        }
    }

    public List<Property> GetProperties() {
        return this.ownedProperties;
    }
}
