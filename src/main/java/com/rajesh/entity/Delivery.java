package com.rajesh.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "DELIVERY_TBL")
public class Delivery {
    @Id
    @GeneratedValue
    private Long id;
    @Column
    private String address;
    @Column
    private String status;
    @Column
    private long orderId;
}
