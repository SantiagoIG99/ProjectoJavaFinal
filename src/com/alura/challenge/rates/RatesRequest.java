package com.alura.challenge.rates;

import java.util.HashMap;

public record RatesRequest(
        String result,
        String base_code,
        HashMap<String, Double> conversion_rates
) {
}
