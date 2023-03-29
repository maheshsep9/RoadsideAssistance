package org.geico.rsa.entity;

import lombok.Data;

@Data
public class Customer {
    String id;
    public Customer(String id) {
        this.id = id;
    }
}
