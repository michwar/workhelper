package michal.warcholinski.pl.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import michal.warcholinski.pl.data.local.entity.ProjectEntity

/**
 * Created by Michał Warcholiński on 2022-01-08.
 */
@Dao
internal interface ProjectDao {

	@Insert
	suspend fun insert(vararg objects: ProjectEntity): List<Long>

	@Query("SELECT * FROM project WHERE id = :id")
	suspend fun getById(id: Long): ProjectEntity?

	@Query("SELECT * FROM project WHERE realized = :realized")
	fun getAll(realized: Boolean): Flow<List<ProjectEntity>>

	@Query("UPDATE project SET realized = :realized where id = :projectId")
	suspend fun archive(projectId: Long, realized: Boolean = true)

	@Query("DELETE FROM project WHERE id = :projectId")
	suspend fun delete(projectId: Long)
}