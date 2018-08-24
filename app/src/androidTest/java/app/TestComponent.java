package app;

import android.app.Application;
import android.content.Context;

import com.instaapp.InstaApplication;
import com.instaapp.di.builder.ActivityBuilder;
import com.instaapp.di.module.ApplicationModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import dagger.android.support.DaggerApplication;

/**
 * Created by jiveksha on 8/24/18.
 */

@Singleton
@Component(modules = {AndroidSupportInjectionModule.class, ApplicationModule.class, ActivityBuilder.class})
public interface TestComponent extends AndroidInjector<DaggerApplication> {

    void inject(InstaApplication app);

    @Override
    void inject(DaggerApplication instance);

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder application(Application application);

        @BindsInstance
        Builder context(Context context);

        TestComponent build();
    }
}
