package org.rapla.client.swing.dagger;

import dagger.Module;
import dagger.Provides;
import org.rapla.client.UserClientService;
import org.rapla.components.iolayer.DefaultIO;
import org.rapla.components.iolayer.IOInterface;
import org.rapla.components.iolayer.WebstartIO;
import org.rapla.entities.User;
import org.rapla.framework.RaplaException;
import org.rapla.framework.StartupEnvironment;
import org.rapla.framework.logger.Logger;

import javax.inject.Provider;
import javax.inject.Singleton;

@Module public class DaggerRaplaJavaClientStartupModule
{
    private final StartupEnvironment context;
    private final Logger logger;
    private final Provider<UserClientService> userClientServiceProvider;

    public DaggerRaplaJavaClientStartupModule(StartupEnvironment context,Provider<UserClientService> userClientServiceProvider)
    {
        this.context = context;
        this.logger = context.getBootstrapLogger();
        this.userClientServiceProvider = userClientServiceProvider;
    }

    @Provides UserClientService provideService()
    {
        return userClientServiceProvider.get();
    }

    @Provides public Logger provideLogger()
    {
        return logger;
    }

    @Provides StartupEnvironment provideContext()
    {
        return context;
    }

    @Provides @Singleton IOInterface provideIOContext()
    {
        boolean webstartEnabled = context.getStartupMode() == StartupEnvironment.WEBSTART;
        if (webstartEnabled)
        {
            return new WebstartIO( logger);
        }
        else
        {
            return new DefaultIO( logger);
        }
    }

}
