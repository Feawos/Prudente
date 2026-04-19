package com.pfm.i18n;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Currency;
import java.util.Locale;

@Service
public class LocalizationService {

    private final MessageSource messageSource;

    public LocalizationService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String message(String key, Locale locale) {
        return messageSource.getMessage(key, null, locale);
    }

    public String formatMoney(BigDecimal amount, String currencyCode, Locale locale) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
        formatter.setCurrency(Currency.getInstance(currencyCode));
        return formatter.format(amount);
    }

    public String formatDate(LocalDate date, Locale locale) {
        return DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                .localizedBy(locale)
                .format(date);
    }
}
