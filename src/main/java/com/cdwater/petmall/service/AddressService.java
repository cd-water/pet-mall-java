package com.cdwater.petmall.service;

import com.cdwater.petmall.entity.Address;

import java.util.List;

public interface AddressService {

    List<Address> list();

    void add(Address address);

    void removeOne(Integer id);

    void edit(Address address);

    Address query(Integer id);
}
