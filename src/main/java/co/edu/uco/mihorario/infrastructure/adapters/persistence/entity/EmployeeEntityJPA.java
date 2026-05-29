package co.edu.uco.mihorario.infrastructure.adapters.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import java.util.UUID;

@Entity
@Table(name = "empleado")
public class EmployeeEntityJPA {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(nullable = true)
    private String identification;

    @Column(nullable = true)
    private String phone;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private Boolean active;

    public EmployeeEntityJPA() {}

    public EmployeeEntityJPA(UUID id, String name, String lastName, String identification, String phone, String email, Boolean active) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.identification = identification;
        this.phone = phone;
        this.email = email;
        this.active = active;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getIdentification() { return identification; }
    public void setIdentification(String identification) { this.identification = identification; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
