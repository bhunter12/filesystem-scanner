package com.lazymachine.common;

import com.google.inject.AbstractModule;

public class CommonModule extends AbstractModule {

    @Override protected void configure() {
        bind(ResourceBundleProvider.class).to(ResourceBundleProviderImpl.class);
    }
}
