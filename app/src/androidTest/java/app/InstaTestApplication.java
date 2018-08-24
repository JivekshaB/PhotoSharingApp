package app;

import com.instaapp.InstaApplication;
import com.instaapp.di.component.ApplicationComponent;
import com.instaapp.di.component.DaggerApplicationComponent;

import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;

/**
 * Created by jiveksha on 8/24/18.
 */

public class InstaTestApplication extends InstaApplication {

    protected AndroidInjector<? extends DaggerApplication> applicationTestInjector() {
        ApplicationComponent appComponent = DaggerApplicationComponent.builder().application(this).context(getApplicationContext()).build();
        appComponent.inject(this);
        return appComponent;
    }


}
