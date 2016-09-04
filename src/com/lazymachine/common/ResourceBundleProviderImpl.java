package com.lazymachine.common;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.lazymachine.LocaleProvider;

import java.util.Locale;
import java.util.ResourceBundle;

@Singleton
public class ResourceBundleProviderImpl implements ResourceBundleProvider {

    private final Locale locale;

    @Inject
    public ResourceBundleProviderImpl(LocaleProvider localProvider) {
        this.locale = localProvider.currentLocale();
    }

    @Override public ResourceBundle getBundle(String baseName) {
        System.out.println("baseName: " + baseName);
        System.out.println("locale: " + locale);
        return ResourceBundle.getBundle(baseName, locale);
    }

}
