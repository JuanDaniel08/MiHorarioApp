package co.edu.uco.mihorario.infrastructure.adapters.persistence.entity;

import java.util.UUID;

import co.edu.uco.mihorario.domain.helpers.UuidHelper;

public class EmployeeEntity {
    private UUID id;
    private String name;
    private String lastName;
    private String document;
    private String phone;
    private String email;
    private Boolean state;

    public EmployeeEntity(UUID id, String name, String lastName, String document, String phone, String email,
            Boolean state) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.document = document;
        this.phone = phone;
        this.email = email;
        this.state = state;
    }

    public UUID getId() {
        return id;
    }

    public void setId() {
        this.id = UuidHelper.generateUuid();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }
}
