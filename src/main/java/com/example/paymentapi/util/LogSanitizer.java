package com.example.paymentapi.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class LogSanitizer {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final List<String> SENSITIVE_FIELDS = Arrays.asList("password", "cvv", "creditCardNumber", "email", "cardNumber");

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^(.*)@(.*)$");  // Email regex
    private static final Pattern CARD_NUMBER_PATTERN = Pattern.compile("\\d{12,19}"); //  Matches 12-19 digits

    public static Object sanitize(Object obj) {
        try {
            if (obj == null) {
                return null;
            }

            JsonNode jsonNode = objectMapper.valueToTree(obj);

            if (jsonNode.isObject()) {
                return sanitizeObject((ObjectNode) jsonNode);
            } else {
                return jsonNode;
            }
        } catch (Exception e) {
            log.error("Error sanitizing log data: {}", e.getMessage(), e);
            return obj;
        }
    }

    private static JsonNode sanitizeObject(ObjectNode objectNode) {
        Iterator<String> fieldNames = objectNode.fieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            JsonNode fieldValue = objectNode.get(fieldName);

            if (fieldValue.isObject()) {
                objectNode.set(fieldName, sanitizeObject((ObjectNode) fieldValue));
            } else if (fieldValue.isArray()) {
                objectNode.set(fieldName, sanitizeArray((ArrayNode) fieldValue));
            } else if (SENSITIVE_FIELDS.contains(fieldName)) {
                if (fieldName.equals("email")) {
                    objectNode.put(fieldName, maskEmail(fieldValue.asText()));

                } else if (fieldName.equals("cardNumber") || fieldName.equals("creditCardNumber")) {
                    objectNode.put(fieldName, maskCardNumber(fieldValue.asText()));
                } else {
                    objectNode.put(fieldName, "*****");
                }
            }
        }
        return objectNode;
    }

    private static JsonNode sanitizeArray(ArrayNode arrayNode) {

        ArrayNode sanitizedArray = objectMapper.createArrayNode();
        for (JsonNode element : arrayNode) {
            if (element.isObject()) {
                sanitizedArray.add(sanitizeObject((ObjectNode) element));
            } else if (element.isArray()) {
                sanitizedArray.add(sanitizeArray((ArrayNode) element));
            } else {
                sanitizedArray.add(element);
            }
        }
        return sanitizedArray;
    }

    private static String maskEmail(String email) {
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        if (matcher.matches()) {
            String username = matcher.group(1);
            String domain = matcher.group(2);
            String maskedUsername = username.substring(0, Math.min(username.length(), 2)) + "***" + username.substring(Math.max(0, username.length() - 2));
            return maskedUsername + "@" + domain;
        }
        return "*****";
    }

    private static String maskCardNumber(String cardNumber) {
        Matcher matcher = CARD_NUMBER_PATTERN.matcher(cardNumber);

        if (matcher.find()) {
            String matchedNumber = matcher.group(0);
            String maskedNumber = matchedNumber.substring(0, 6) + "******" + matchedNumber.substring(matchedNumber.length() - 4);
            return cardNumber.replace(matchedNumber, maskedNumber); // Replace only the matched number part
        }
        return "*****";
    }
}