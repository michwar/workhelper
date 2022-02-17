package michal.warcholinski.pl.workerhelper.app

import androidx.fragment.app.FragmentActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

/**
 * Created by Michał Warcholiński on 2021-12-23.
 */

@Module
@InstallIn(ActivityComponent::class)
object ActivityModule {

    @Provides
    @ActivityScoped
    fun provideFragmentManager(activity: FragmentActivity) = activity.supportFragmentManager
}