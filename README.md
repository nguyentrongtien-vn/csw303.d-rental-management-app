# CSW303.D - Rental Management Application (Week 2)

## 📌 Project Overview
An Object-Oriented Software Engineering project for Rental Management Application built for CSW303 course.

## 👥 Group Members & Task Allocation

| Role | Student Name | GitHub | Assigned Java Classes |
| :--- | :--- | :--- | :--- |
| **Team Lead** | Nguyễn Trọng Tiến | [@nguyentrongtien-vn](https://github.com/nguyentrongtien-vn) | Project Setup, Git Workflow, Architecture, `README.md`, `RentalContract` |
| **Member** | Bùi Nguyễn Anh | [@anhbuinguyencit23](https://github.com/anhbuinguyencit23) | `PropertyOwner` |
| **Member** | Nguyễn Đỗ Huy Hoàng | [@NguyenDoHuyHoang](https://github.com/NguyenDoHuyHoang) | `User` (Base), `Tenant` |
| **Member** | Nguyễn Trường Thịnh | [@thinhnguyentruongcit23-svg](https://github.com/thinhnguyentruongcit23-svg) | `Property`, Main App / Testing |

> **Note:** All team members actively manage tasks and progress on the team's Trello board.

## 🔗 Project Links
- **Trello Board:** [CSW303.D Rental Management Trello](https://trello.com/b/gIMEFvVa/csw303d-rental-management-app)
- **GitHub Repository:** [CSW303.D Rental Management App GitHub Repo](https://github.com/nguyentrongtien-vn/csw303.d-rental-management-app)

## 🛠 Project Structure (5 Core Classes)
- `User` (Base class)
- `PropertyOwner` (Inherits from User)
- `Property`
- `Tenant` (Inherits from User)
- `RentalContract`

## 📐 Class Diagram (Sơ đồ Lớp OOP)
```mermaid
classDiagram
    class User {
        # userID : int
        # firstName : String
        # lastName : String
        # email : String
        + User(userID, firstName, lastName, email) void
        + getFullName() String
        + updateProfile(firstName, lastName, email) void
    }
    class PropertyOwner {
        - ownedProperties : List~Property~

        + PropertyOwner(userID, firstName, lastName, email)\
        + createProperty(propertyID, description, rentalRate) Property
        + getProperties() List~Property~
    }
    class Tenant {
        - rentalContracts : List~RentalContract~

        + Tenant(userID, firstName, lastName, email)
        + rentProperty(contractID, property, startDate, endDate) RentalContract
        + getRentalContracts() List~RentalContract~
    }
    class Property {
        - propertyID : int
        - description : String
        - rentalRate : double
        - isAvailable : boolean

        + Property(propertyID, description, rentalRate) -
        + updateDescription(description) void
        + updateRentalRate(rentalRate) void
    }
    class RentalContract {
        - contractID : int
        - property : Property
        - tenant : Tenant
        - startDate : LocalDate
        - endDate : LocalDate
        - rentAmount : double
        - isActive : boolean
        
        + terminate() void
    }

    User <|-- PropertyOwner : Inherits
    User <|-- Tenant : Inherits
    PropertyOwner "1" --> "*" Property : Owns
    Tenant "1" --> "*" RentalContract : Holds
    RentalContract "*" --> "1" Property : References