package com.alura.challenge.service;

import java.util.HashMap;

public class Conversion {
    private HashMap<String , Double> rates;
    public Conversion(HashMap<String, Double> rates){
        this.rates = rates;
    }

    public Double getRate(String code){
        return rates.get(code);
    }

    public Double convert(Double quantity, String from, String to){
        return quantity / rates.get(from) * rates.get(to);
    }
}
