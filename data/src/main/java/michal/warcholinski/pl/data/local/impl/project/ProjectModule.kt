package michal.warcholinski.pl.data.local.impl.project

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import michal.warcholinski.pl.domain.project.domain.ProjectRepository
import javax.inject.Singleton

/**
 * Created by Michał Warcholiński on 2022-01-20.
 */
@Module
@InstallIn(SingletonComponent::class)
internal abstract class ProjectModule {

	@Binds
	@Singleton
	abstract fun bindProjectRepository(repository: ProjectRepositoryImpl): ProjectRepository

}