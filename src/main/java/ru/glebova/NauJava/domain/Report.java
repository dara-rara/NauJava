package ru.glebova.NauJava.domain;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "report")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status;
    @Column(nullable = true, columnDefinition = "TEXT")
    private String description;
    @Column(nullable = true)
    private Long processingTimeMillisOne;
    @Column(nullable = true)
    private Long processingTimeMillisTwo;
    @Column(nullable = true)
    private Long processingTimeMillisGeneral;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private LocalDateTime dateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getProcessingTimeMillisOne() {
        return processingTimeMillisOne;
    }

    public void setProcessingTimeMillisOne(Long processingTimeMillisOne) {
        this.processingTimeMillisOne = processingTimeMillisOne;
    }

    public Long getProcessingTimeMillisTwo() {
        return processingTimeMillisTwo;
    }

    public void setProcessingTimeMillisTwo(Long processingTimeMillisTwo) {
        this.processingTimeMillisTwo = processingTimeMillisTwo;
    }

    public Long getProcessingTimeMillisGeneral() {
        return processingTimeMillisGeneral;
    }

    public void setProcessingTimeMillisGeneral(Long processingTimeMillisGeneral) {
        this.processingTimeMillisGeneral = processingTimeMillisGeneral;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}
