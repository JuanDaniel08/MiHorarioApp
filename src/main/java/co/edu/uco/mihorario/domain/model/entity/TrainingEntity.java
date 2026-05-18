package co.edu.uco.mihorario.domain.model.entity;

import java.util.UUID;

import co.edu.uco.mihorario.crosscutting.helpers.StringHelper;
import co.edu.uco.mihorario.crosscutting.helpers.UuidHelper;

//Training es Curso en la base de datos
public class TrainingEntity {
    private UUID id;
    private String name;
    private String description;
    private int daysValidity;

    public TrainingEntity(UUID id, String name, String description, int daysValidity) {
        setId(id);
        setName(name);
        setDescription(description);
        setDaysValidity(daysValidity);
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = StringHelper.stringNotNullOrEmpty(description);
    }

    public int getDaysValidity() {
        return daysValidity;
    }

    public void setDaysValidity(int daysValidity) {
        this.daysValidity = daysValidity;
    }
}
