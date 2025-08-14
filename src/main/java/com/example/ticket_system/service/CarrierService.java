package com.example.ticket_system.service;

import com.example.ticket_system.dao.CarrierDAO;
import com.example.ticket_system.model.Carrier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarrierService {
    private final CarrierDAO dao;

    public CarrierService(CarrierDAO dao) {
        this.dao = dao;
    }

    public long addCarrier(Carrier carrier) {
        return dao.create(carrier);
    }

    public List<Carrier> getAllCarrier() {
        return dao.findAll();
    }

    public Carrier getById(long id) {
        return dao.findById(id);
    }

    public int getRoutesCount(long carrierId) {
        return dao.countRoutesByCarrier(carrierId);
    }
}
