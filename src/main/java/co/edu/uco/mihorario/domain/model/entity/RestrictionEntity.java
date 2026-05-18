package co.edu.uco.mihorario.domain.model.entity;

import java.util.UUID;

import co.edu.uco.mihorario.crosscutting.helpers.StringHelper;
import co.edu.uco.mihorario.crosscutting.helpers.UuidHelper;

public class RestrictionEntity {
    private UUID id;
    private String name;
    private String description;

    public RestrictionEntity(UUID id, String name, String description) {
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
