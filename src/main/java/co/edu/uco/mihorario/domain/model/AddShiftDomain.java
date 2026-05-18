package co.edu.uco.mihorario.domain.model;

import java.sql.Date;
import java.sql.Time;
import java.util.UUID;

import co.edu.uco.mihorario.crosscutting.helpers.UuidHelper;

public class AddShiftDomain {
    private UUID id;
    private UUID idEmployee;
    private UUID idLabor;
    private Date date;
    private Time startTime;
    private Time endTime;
    private Boolean state;
    private String observation;

    public AddShiftDomain(UUID id, UUID idEmployee, UUID idLabor, Date date, Time startTime, Time endTime,
            Boolean state,
            String observation) {
        super();
        UuidHelper.generateUuid();
        setIdEmployee(idEmployee);
        setIdLabor(idLabor);
        setDate(date);
        setStartTime(startTime);
        setEndTime(endTime);
        setState(state);
        setObservation(observation);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getIdEmployee() {
        return idEmployee;
    }

    public void setIdEmployee(UUID idEmployee) {
        this.idEmployee = idEmployee;
    }

    public UUID getIdLabor() {
        return idLabor;
    }

    public void setIdLabor(UUID idLabor) {
        this.idLabor = idLabor;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }
}
