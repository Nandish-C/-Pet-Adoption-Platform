package com.petadoption.backend.services;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class CurrencyService {

    // Static exchange rates (in a real application, these would come from an external API)
    private static final Map<String, Double> EXCHANGE_RATES = new HashMap<>();

    static {
        // Exchange rates relative to USD (1 USD = X units of target currency)
        EXCHANGE_RATES.put("USD", 1.0);
        EXCHANGE_RATES.put("EUR", 0.85);
        EXCHANGE_RATES.put("GBP", 0.73);
        EXCHANGE_RATES.put("CAD", 1.25);
        EXCHANGE_RATES.put("AUD", 1.35);
        EXCHANGE_RATES.put("JPY", 110.0);
        EXCHANGE_RATES.put("INR", 74.5);
        EXCHANGE_RATES.put("CNY", 6.45);
        EXCHANGE_RATES.put("BRL", 5.2);
        EXCHANGE_RATES.put("MXN", 20.0);
    }

    /**
     * Convert amount from USD to target currency
     * @param amount Amount in USD
     * @param targetCurrency Target currency code
     * @return Converted amount
     */
    public Double convertFromUSD(Double amount, String targetCurrency) {
        if (amount == null || targetCurrency == null) {
            return amount;
        }

        Double rate = EXCHANGE_RATES.get(targetCurrency.toUpperCase());
        if (rate == null) {
            // Default to USD if currency not found
            return amount;
        }

        return amount * rate;
    }

    /**
     * Convert amount from target currency to USD
     * @param amount Amount in target currency
     * @param fromCurrency Source currency code
     * @return Amount in USD
     */
    public Double convertToUSD(Double amount, String fromCurrency) {
        if (amount == null || fromCurrency == null) {
            return amount;
        }

        Double rate = EXCHANGE_RATES.get(fromCurrency.toUpperCase());
        if (rate == null) {
            // Default to amount as-is if currency not found
            return amount;
        }

        return amount / rate;
    }

    /**
     * Get the default currency for a country
     * @param country Country name
     * @return Currency code
     */
    public String getCurrencyForCountry(String country) {
        if (country == null) {
            return "USD";
        }

        switch (country.toUpperCase()) {
            case "UNITED STATES":
            case "USA":
            case "US":
                return "USD";
            case "UNITED KINGDOM":
            case "UK":
            case "GREAT BRITAIN":
                return "GBP";
            case "EUROPEAN UNION":
            case "EU":
            case "GERMANY":
            case "FRANCE":
            case "ITALY":
            case "SPAIN":
            case "NETHERLANDS":
                return "EUR";
            case "CANADA":
                return "CAD";
            case "AUSTRALIA":
                return "AUD";
            case "JAPAN":
                return "JPY";
            case "INDIA":
                return "INR";
            case "CHINA":
                return "CNY";
            case "BRAZIL":
                return "BRL";
            case "MEXICO":
                return "MXN";
            default:
                return "USD"; // Default to USD
        }
    }

    /**
     * Check if a currency code is supported
     * @param currency Currency code
     * @return true if supported
     */
    public boolean isCurrencySupported(String currency) {
        return currency != null && EXCHANGE_RATES.containsKey(currency.toUpperCase());
    }

    /**
     * Get all supported currencies
     * @return Map of currency codes to names
     */
    public Map<String, String> getSupportedCurrencies() {
        Map<String, String> currencies = new HashMap<>();
        currencies.put("USD", "US Dollar");
        currencies.put("EUR", "Euro");
        currencies.put("GBP", "British Pound");
        currencies.put("CAD", "Canadian Dollar");
        currencies.put("AUD", "Australian Dollar");
        currencies.put("JPY", "Japanese Yen");
        currencies.put("INR", "Indian Rupee");
        currencies.put("CNY", "Chinese Yuan");
        currencies.put("BRL", "Brazilian Real");
        currencies.put("MXN", "Mexican Peso");
        return currencies;
    }
}
