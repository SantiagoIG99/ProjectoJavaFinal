package com.alura.challenge.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import com.alura.challenge.rates.RatesRequest;
import com.google.gson.Gson;

public class ConversionService {

    Conversion conversion;
    List<String> codes;
    Double quantity = 0.0;
    String from = "USD";
    String to = "";
    private Scanner scanner = new Scanner(System.in);

    private static final String apiUrl = "https://v6.exchangerate-api.com/v6/";
    private static final String apiKey = "3e587c547af9662cd661da89";

    public ConversionService() {
        setConverter();
    }

    public void init() {
        showBanner();
        String input = "";
        do {
            System.out.print("\n\n");
            showStats();
            showMenu();
            input = getUserInput("Ingrese una opción: ");
            switch (input) {
                case "1":
                    quantity = getQuantity();
                    cls();
                    System.out.println("Nueva cantidad ingresada: " + quantity);
                    break;
                case "2":
                    this.to = selectCurrency();
                    Double result = convertTo(quantity, from, to);
                    cls();
                    System.out.println("Conversión de divisas: ");
                    System.out.println( quantity + from + " -> " + String.format("%.2f", result));
                    break;
                case "3":
                    this.from = selectCurrency();
                    cls();
                    System.out.println("Nueva divisa seleccionada: " + from);
                    break;
                case "4":
                    exit();
                default:
                    cls();
                    System.out.println("ERROR: Opción inválida.");
            }
        } while (!input.equals("4"));
    }

    public void exit() {
        scanner.close();
        System.out.println("Hasta la próxima!");
        System.exit(0);
    }

    private void setConverter() {
        RatesRequest ratesRequest = getApiRequest();
        if (ratesRequest == null) {
            System.out.println("ERROR: No fue posible establecer la conección con la API.");
        } else {
            System.out.println("STATUS: " + ratesRequest.result());
            if (ratesRequest.result() != "succes") {
                System.out.println("ERROR: No fue posible conseguir la información de la API.");
            }
        }
        HashMap<String, Double> rates = ratesRequest.conversion_rates();
        this.conversion = new Conversion(rates);
        this.codes = new ArrayList<>(rates.keySet());
        Collections.sort(this.codes);
    }

    private RatesRequest getApiRequest() {
        Gson gson = new Gson();
        String currencyCode = "/latest/" + "USD";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + apiKey + currencyCode))
                .build();
        try {
            HttpResponse<String> response = client
                    .send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();
            RatesRequest ratesRequest = gson.fromJson(body, RatesRequest.class);
            System.out.println("STATUS: Conección con la API establecida.");
            return ratesRequest;
        } catch (IOException | InterruptedException | IllegalArgumentException | SecurityException ex) {
            System.out.println("EXCEPCION: " + ex);
            return null;
        }
    }

    public String getUserInput(String msg) {
        System.out.print(msg);
        return scanner.nextLine();
    }

    public Double parseDouble(String input, double min) {
        try {
            double parsedValue = Double.parseDouble(input);
            if (parsedValue < min) {
                System.out.println("ERROR: Ingrese un valor mayor a: " + min);
                return null;
            }
            return parsedValue;
        } catch (NumberFormatException ex) {
            System.out.println("ERROR: Valor ingresado inválido.");
            return null;
        }
    }

    public Double getQuantity() {
        Double doubleInput = null;
        do {
            String input = getUserInput("Ingrese una cantidad de dinero: ");
            doubleInput = parseDouble(input, 0);
        } while (doubleInput == null);
        return doubleInput;
    }

    public String selectCurrency() {
        String input = "";
        do {
            showCodes(5);
            input = getUserInput("Ingrese un código válido de la lista: ");
            input = input.toUpperCase();
            if (!codes.contains(input)) {
                System.out.println("ERROR: Código inválido.");
            }
        } while(!codes.contains(input));
        return input;
    }

    public Double convertTo(Double quantity, String from, String to) {
        return conversion.convert(quantity, from, to);
    }

    private void cls() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public void showBanner() {
        cls();
        System.out.println("****************************");
        System.out.println("Conversor De Monedas");
        System.out.println("****************************");
        System.out.println("");
    }

    public void showMenu() {
        System.out.println("********* Menu *********");
        System.out.println("1) Ingresar cantidad");
        System.out.println("2) Convertir a...");
        System.out.println("3) Cambiar moneda");
        System.out.println("4) Salir");

    }

    public void showCodes(int cols) {
        for (int i=0; i < codes.size(); i++) {
            System.out.printf("%-20s", codes.get(i));
            if ((i + 1) % cols == 0) {
                System.out.println();
            }
        }
        System.out.println();
    }

    public void showStats() {
        System.out.println("Moneda: " + from + "\t" + "Cantidad: " + quantity);
    }

}
