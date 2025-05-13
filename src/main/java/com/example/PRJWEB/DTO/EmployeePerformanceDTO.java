package com.example.PRJWEB.DTO;

import lombok.Data;

@Data
public class EmployeePerformanceDTO {
    private int tours;
    private long revenue;
    private double commission;

    public EmployeePerformanceDTO(int tours, long revenue, double commission) {
        this.tours = tours;
        this.revenue = revenue;
        this.commission = commission;
    }
}