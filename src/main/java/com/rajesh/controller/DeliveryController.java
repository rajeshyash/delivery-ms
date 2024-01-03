package com.rajesh.controller;

import com.rajesh.dto.CustomerOrder;
import com.rajesh.dto.DeliveryEvent;
import com.rajesh.entity.Delivery;
import com.rajesh.repository.DeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeliveryController {
    @Autowired
    private DeliveryRepository repository;

    @Autowired
    private KafkaTemplate<String, DeliveryEvent> kafkaTemplate;

    @KafkaListener(topics = "new-stock", groupId = "stock-group")
    public void deliverOrder(DeliveryEvent inventoryEvent) {
        System.out.println("Inside delivery-ms for order : "+inventoryEvent);

        Delivery shipment = new Delivery();
        CustomerOrder order = inventoryEvent.getOrder();
        try {
            if (order.getAddress() == null) {
                throw new Exception("Address not present");
            }

            shipment.setAddress(order.getAddress());
            shipment.setOrderId(order.getOrderId());

            shipment.setStatus("success");

            repository.save(shipment);
        } catch (Exception e) {
            shipment.setOrderId(order.getOrderId());
            shipment.setStatus("failed");
            repository.save(shipment);

            System.out.println("Delivery failed : -> " + order);

            DeliveryEvent reverseEvent = new DeliveryEvent();
            reverseEvent.setType("STOCK_REVERSED");
            reverseEvent.setOrder(order);
            kafkaTemplate.send("reversed-stock", reverseEvent);
            System.out.println("STOCK_REVERSED");
        }
    }
}
