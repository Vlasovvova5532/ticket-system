package com.example.ticket_system.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;


public class Ticket {
    private Long id;
    private Long routeId;
    private Long userId; // null, если не куплен
    private LocalDateTime dateTime;
    private Integer seatNumber;
    private BigDecimal price;
    private Boolean isSold;

    public Ticket() {}

    public Ticket(Long id, Long routeId, Long userId, LocalDateTime dateTime,
                  Integer seatNumber, BigDecimal price, Boolean isSold) {
        this.id = id;
        this.routeId = routeId;
        this.userId = userId;
        this.dateTime = dateTime;
        this.seatNumber = seatNumber;
        this.price = price;
        this.isSold = isSold;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getRouteId() {
        return routeId;
    }
    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }
    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Integer getSeatNumber() {
        return seatNumber;
    }
    public void setSeatNumber(Integer seatNumber) {
        this.seatNumber = seatNumber;
    }

    public BigDecimal getPrice() {
        return price;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Boolean getIsSold() {
        return isSold;
    }
    public void setIsSold(Boolean isSold) {
        this.isSold = isSold;
    }
}
