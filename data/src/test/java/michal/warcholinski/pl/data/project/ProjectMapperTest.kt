package michal.warcholinski.pl.data.project

import michal.warcholinski.pl.data.local.entity.ProjectEntity
import michal.warcholinski.pl.data.local.mapper.ProjectMapper
import michal.warcholinski.pl.domain.project.model.ProjectDataModel
import org.junit.Assert
import org.junit.Test

/**
 * Created by Michał Warcholiński on 2022-02-28.
 */
class ProjectMapperTest {

	private val projectMapper = ProjectMapper()

	@Test
	fun `map entity to data model test`() {
		val projectEntity =
			ProjectEntity(1L, "testName", "testDesc", Long.MIN_VALUE, "123123123", "test@test.com", false)
		val projectDataModel = projectMapper.fromEntity(projectEntity)

		Assert.assertEquals(projectEntity.id, projectDataModel.id)
		Assert.assertEquals(projectEntity.name, projectDataModel.name)
		Assert.assertEquals(projectEntity.desc, projectDataModel.desc)
		Assert.assertEquals(projectEntity.createDate, projectDataModel.createDate)
		Assert.assertEquals(projectEntity.phoneNo, projectDataModel.phoneNo)
		Assert.assertEquals(projectEntity.email, projectDataModel.email)
		Assert.assertEquals(projectEntity.realized, projectDataModel.realized)
	}


	@Test
	fun `map data model to entity test`() {
		val projectDataModel =
			ProjectDataModel(1L, "testName", "testDesc", Long.MIN_VALUE, "123123123", "test@test.com", false)
		val projectEntity = projectMapper.toEntity(projectDataModel)

		Assert.assertEquals(projectDataModel.id, projectEntity.id)
		Assert.assertEquals(projectDataModel.name, projectEntity.name)
		Assert.assertEquals(projectDataModel.desc, projectEntity.desc)
		Assert.assertEquals(projectDataModel.createDate, projectEntity.createDate)
		Assert.assertEquals(projectDataModel.phoneNo, projectEntity.phoneNo)
		Assert.assertEquals(projectDataModel.email, projectEntity.email)
		Assert.assertEquals(projectDataModel.realized, projectEntity.realized)
	}

	@Test(expected = NullPointerException::class)
	fun `should throw exception if id is null when map entity to data model test`() {
		val projectEntity =
			ProjectEntity(1L, "testName", "testDesc", Long.MIN_VALUE, "123123123", "test@test.com", false)
		projectMapper.fromEntity(projectEntity)
	}
}