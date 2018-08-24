package app;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.instaapp.di.annotation.ApplicationContext;

import org.mockito.Mockito;

import javax.inject.Singleton;

import dagger.Provides;

/**
 * Created by jiveksha on 8/24/18.
 */

public class MockApplicationModule {


    /**
     * Provides application level context
     *
     * @return {@link ApplicationContext}
     */
    @Provides
    @ApplicationContext
    @Singleton
    Context provideContext(InstaTestApplication application) {
        return application.getApplicationContext();
    }

    /**
     * provides application instance
     *
     * @return {@link com.instaapp.InstaApplication}
     */
    @Provides
    @Singleton
    Application provideApplication(InstaTestApplication application) {
        return application;
    }

    /**
     * Provide firebase auth instance for all auth related flows
     *
     * @return {@link FirebaseAuth}
     */
    @Provides
    @Singleton
    FirebaseAuth providesFirebaseAuth() {
        FirebaseAuth firebaseAuth = Mockito.mock(FirebaseAuth.class);
        return firebaseAuth.getInstance();
    }

    /**
     * Provides firebase database
     *
     * @return {@link FirebaseDatabase}
     */
    @Provides
    @Singleton
    FirebaseDatabase providesFirebaseDatabase() {
        return FirebaseDatabase.getInstance();
    }


    /**
     * Provides firebase storage
     *
     * @return {@link FirebaseStorage}
     */
    @Provides
    @Singleton
    FirebaseStorage providesFirebaseStorage() {
        return FirebaseStorage.getInstance();
    }


}
