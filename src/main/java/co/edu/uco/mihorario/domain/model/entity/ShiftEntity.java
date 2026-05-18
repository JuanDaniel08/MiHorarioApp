package co.edu.uco.mihorario.domain.model.entity;

import java.sql.Time;
import java.time.LocalTime;
import java.util.UUID;

import co.edu.uco.mihorario.crosscutting.helpers.StringHelper;
import co.edu.uco.mihorario.crosscutting.helpers.TimeHelper;
import co.edu.uco.mihorario.crosscutting.helpers.UuidHelper;

public class ShiftEntity {
    private UUID id;
    private EmployeeEntity employee;
    private JobEntity job;
    private Time date;
    private LocalTime entryTime;
    private LocalTime exitTime;
    private String observation;

    public ShiftEntity(UUID id, EmployeeEntity employee, JobEntity job, Time date, LocalTime entryTime,
            LocalTime exitTime, String observation) {
        setId(id);
        setEmployee(employee);
        setJob(job);
        setDate(date);
        setEntryTime(entryTime);
        setExitTime(exitTime);
        setObservation(observation);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = UuidHelper.nullSafeId(id);
    }

    public EmployeeEntity getEmployee() {
        return employee;
    }

    public void setEmployee(EmployeeEntity employee) {
        this.employee = employee;
    }

    public JobEntity getJob() {
        return job;
    }

    public void setJob(JobEntity job) {
        this.job = job;
    }

    public Time getDate() {
        return date;
    }

    public void setDate(Time date) {
        this.date = date;
    }

    public LocalTime getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(LocalTime entryTime) {
        this.entryTime = TimeHelper.validateEntryTime(entryTime);
    }

    public LocalTime getExitTime() {
        return exitTime;
    }

    public void setExitTime(LocalTime exitTime) {
        this.exitTime = TimeHelper.validateExitTime(exitTime);
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = StringHelper.stringNotNullOrEmpty(observation);
    }
}
