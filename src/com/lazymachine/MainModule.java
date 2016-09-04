package com.lazymachine;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

public class MainModule extends AbstractModule {
    private final EventBus eventBus = new EventBus("Main Event Bus");

    @Override
    protected void configure() {
        bind(EventBus.class).toInstance(eventBus);

        bind(LocaleProvider.class).toInstance(new LocaleProviderImpl("en", "US", "MACINTOSH"));

        // TODO: Sit down and truly understand HOW this works!
        // What this does: Finally, we have arrived at our destination. Using Guice, we bind a TypeListener to every
        // object that is created and ensure that it is registered with our default EventBus. Objects that subscribe to
        // particular events are no longer required to explicitly subscribe with an EventBus and only need to express
        // what kind of events they are interested in.
        // From: http://spin.atomicobject.com/2012/01/13/the-guava-eventbus-on-guice/
        // Also read: http://www.lexicalscope.com/blog/2012/02/13/guava-eventbus-experiences/
        bindListener(Matchers.any(), new TypeListener() {
            public <I> void hear(TypeLiteral<I> typeLiteral, TypeEncounter<I> typeEncounter) {
                typeEncounter.register(new InjectionListener<I>() {
                    public void afterInjection(I i) {
                        eventBus.register(i);
                    }
                });
            }
        });
    }

}
