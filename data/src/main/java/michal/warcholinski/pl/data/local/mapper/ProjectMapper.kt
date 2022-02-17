package michal.warcholinski.pl.data.local.mapper

import michal.warcholinski.pl.data.local.entity.ProjectEntity
import michal.warcholinski.pl.domain.project.model.ProjectDataModel
import javax.inject.Inject

/**
 * Created by Michał Warcholiński on 2022-01-12.
 */
internal class ProjectMapper @Inject constructor() : Mapper<ProjectEntity, ProjectDataModel> {

	override fun fromEntity(entity: ProjectEntity): ProjectDataModel {
		return ProjectDataModel(entity.id!!, entity.name, entity.desc, entity.createDate, entity.phoneNo, entity.email, entity.realized)
	}

	override fun toEntity(model: ProjectDataModel): ProjectEntity {
		return ProjectEntity(model.id, model.name, model.desc, model.createDate, model.phoneNo, model.email, model.realized)
	}
}