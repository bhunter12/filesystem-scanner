package com.lazymachine;

import com.google.inject.Singleton;

import java.util.Locale;

@Singleton
public class LocaleProviderImpl implements LocaleProvider {

    private final String language;
    private final String country;
    private final String variant;

    public LocaleProviderImpl(String language, String country, String variant) {
        this.language = language;
        this.country = country;
        this.variant = variant;
    }

    @Override public Locale currentLocale() {
        return new Locale(language, country, variant);
    }

}
