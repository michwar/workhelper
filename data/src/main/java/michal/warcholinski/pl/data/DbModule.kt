package michal.warcholinski.pl.data

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import michal.warcholinski.pl.data.local.AppDatabase
import javax.inject.Singleton

/**
 * Created by Michał Warcholiński on 2022-01-02.
 */
@Module
@InstallIn(SingletonComponent::class)
internal object DbModule {

	@Singleton
	@Provides
	fun provideDatabase(@ApplicationContext context: Context) =
		Room.databaseBuilder(context, AppDatabase::class.java, "worker_helper_bd")
			.fallbackToDestructiveMigration()
			.build()


	@Singleton
	@Provides
	fun provideProjectDao(db: AppDatabase) = db.projectDao()

	@Singleton
	@Provides
	fun provideRequestDao(db: AppDatabase) = db.requestDao()

	@Provides
	@Singleton
	fun provideDefaultSharedPref(@ApplicationContext context: Context): SharedPreferences =
		PreferenceManager.getDefaultSharedPreferences(context)
}