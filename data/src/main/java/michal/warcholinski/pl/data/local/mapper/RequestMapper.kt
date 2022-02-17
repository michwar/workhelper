package michal.warcholinski.pl.data.local.mapper

import michal.warcholinski.pl.data.local.entity.RequestEntity
import michal.warcholinski.pl.domain.requests.model.RequestDataModel
import javax.inject.Inject

/**
 * Created by Michał Warcholiński on 2022-01-02.
 */
class RequestMapper @Inject constructor() : Mapper<RequestEntity, RequestDataModel> {

	override fun fromEntity(entity: RequestEntity): RequestDataModel {
		return RequestDataModel(entity.id!!, entity.projectId, entity.name, entity.dateOfCreate, entity.photoPath, entity.desc)
	}

	override fun toEntity(model: RequestDataModel): RequestEntity {
		return RequestEntity(model.id, model.projectId, model.name, model.dateOfCreate, model.photoPath, model.desc)
	}
}