package com.example.ticket_system.model;

public class Route {
    private Long id;
    private String departure;
    private String destination;
    private Carrier carrier;
    private Integer durationMinutes;
    private Long carrierId;

    public Route() {}

    public Route(Long id, String departure, String destination, Long carrierId, Integer durationMinutes) {
        this.id = id;
        this.departure = departure;
        this.destination = destination;
        this.carrierId = carrierId;
        this.durationMinutes = durationMinutes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Long getCarrierId() {
        return carrierId;
    }
    public void setCarrierId(Long carrierId) {
        this.carrierId = carrierId;
    }
}
