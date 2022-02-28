package michal.warcholinski.pl.data.request

import michal.warcholinski.pl.data.local.entity.RequestEntity
import michal.warcholinski.pl.data.local.mapper.RequestMapper
import michal.warcholinski.pl.domain.requests.model.RequestDataModel
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Created by Michał Warcholiński on 2022-02-24.
 */
class RequestMapperTest {

	private val requestMapper = RequestMapper()

	@Test
	fun `map entity to data model test`() {
		val requestEntity = RequestEntity(1L, 2L, "testName", Long.MIN_VALUE, null, "testDesc")
		val requestDataModel = requestMapper.fromEntity(requestEntity)

		assertEquals(requestEntity.id, requestDataModel.id)
		assertEquals(requestEntity.projectId, requestDataModel.projectId)
		assertEquals(requestEntity.name, requestDataModel.name)
		assertEquals(requestEntity.dateOfCreate, requestDataModel.dateOfCreate)
		assertEquals(requestEntity.photoPath, requestDataModel.photoPath)
		assertEquals(requestEntity.desc, requestDataModel.desc)
	}


	@Test
	fun `map data model to entity test`() {
		val requestDataModel =
			RequestDataModel(1L, 2L, "testName", Long.MIN_VALUE, null, "testDesc")
		val requestEntity = requestMapper.toEntity(requestDataModel)

		assertEquals(requestDataModel.id, requestEntity.id)
		assertEquals(requestDataModel.projectId, requestEntity.projectId)
		assertEquals(requestDataModel.name, requestEntity.name)
		assertEquals(requestDataModel.dateOfCreate, requestEntity.dateOfCreate)
		assertEquals(requestDataModel.photoPath, requestEntity.photoPath)
		assertEquals(requestDataModel.desc, requestEntity.desc)
	}

	@Test(expected = NullPointerException::class)
	fun `should throw exception if id is null when map entity to data model test`() {
		val requestEntity = RequestEntity(null, 1L, "testName", Long.MIN_VALUE, null, "testDesc")
		requestMapper.fromEntity(requestEntity)
	}
}