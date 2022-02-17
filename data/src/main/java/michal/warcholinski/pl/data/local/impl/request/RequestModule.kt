package michal.warcholinski.pl.data.local.impl.request

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import michal.warcholinski.pl.data.local.impl.localfiles.LocalAppFilesRepositoryImpl
import michal.warcholinski.pl.domain.localfiles.domain.LocalAppFilesRepository
import michal.warcholinski.pl.domain.requests.domain.RequestRepository
import javax.inject.Singleton

/**
 * Created by Michał Warcholiński on 2022-01-20.
 */
@Module
@InstallIn(SingletonComponent::class)
internal abstract class RequestModule {

	@Binds
	@Singleton
	abstract fun bindRequestRepository(repository: RequestRepositoryImpl): RequestRepository

	@Binds
	@Singleton
	abstract fun bindLocalAppFilesRepo(repository: LocalAppFilesRepositoryImpl): LocalAppFilesRepository

}