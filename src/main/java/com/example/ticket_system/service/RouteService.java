package com.example.ticket_system.service;

import com.example.ticket_system.dao.RouteDAO;
import com.example.ticket_system.model.Route;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RouteService {
    private final RouteDAO dao;

    public RouteService(RouteDAO dao) {
        this.dao = dao;
    }

    public long addRoute(Route route) {
        return dao.createRoute(route);
    }

    public List<Route> getAll() {
        return dao.findAll();
    }

    public Route getById(long id) {
        return dao.findById(id);
    }

    public int countByCarrier(long carrierId) {
        return dao.countByCarrier(carrierId);
    }

    public boolean existsById(long id) {
        return dao.existsById(id);
    }

    public boolean updateRoute(long id, Route route) {
        return dao.updateRoute(id, route) > 0;
    }

    public boolean deleteRoute(long id) {
        return dao.deleteRoute(id) > 0;
    }
}

