package co.edu.uco.mihorario.domain.model.entity;

import java.util.UUID;

import co.edu.uco.mihorario.crosscutting.helpers.StringHelper;
import co.edu.uco.mihorario.crosscutting.helpers.UuidHelper;

public class EmployeeEntity {
    private UUID id;
    private String name;
    private String lastName;
    private String identification;
    private String phone;
    private String email;
    private boolean state;

    public EmployeeEntity(UUID id, String name, String lastName, String document, String phone, String email,
            boolean state) {
        setId(id);
        setName(name);
        setLastName(lastName);
        setIdentification(document);
        setPhone(phone);
        setEmail(email);
        setState(state);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = UuidHelper.nullSafeId(id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = StringHelper.stringNotNullOrEmpty(name);
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = StringHelper.stringNotNullOrEmpty(lastName);
    }

    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = StringHelper.stringNotNullOrEmpty(identification);
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = StringHelper.stringNotNullOrEmpty(phone);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = StringHelper.stringNotNullOrEmpty(email);
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}
