package co.edu.uco.mihorario.domain.model.entity;

import java.util.UUID;

import co.edu.uco.mihorario.crosscutting.helpers.StringHelper;
import co.edu.uco.mihorario.crosscutting.helpers.UuidHelper;

public class JobEntity {
    private UUID id;
    private UUID idZone;
    private String name;
    private String description;

    public JobEntity(UUID id, String name, String description) {
        super();
        setId(id);
        setName(name);
        setDescription(description);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = UuidHelper.nullSafeId(id);
    }

    public UUID getIdZone() {
        return idZone;
    }

    public void setIdZone(UUID idZone) {
        this.idZone = idZone;
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

}
