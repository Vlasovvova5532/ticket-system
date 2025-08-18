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

    public boolean existsById(long id) {
        return dao.existsById(id);
    }

    public boolean updateCarrier(long id, Carrier carrier) {
        return dao.update(id, carrier) > 0;
    }

    public boolean deleteCarrier(long id) {
        return dao.delete(id) > 0;
    }
}
