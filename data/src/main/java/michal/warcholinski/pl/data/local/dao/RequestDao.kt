package michal.warcholinski.pl.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import michal.warcholinski.pl.data.local.entity.RequestEntity

/**
 * Created by Michał Warcholiński on 2022-01-02.
 */
@Dao
internal interface RequestDao {

	@Insert
	fun insert(vararg objects: RequestEntity): List<Long>

	@Query("SELECT * FROM request WHERE id = :id")
	suspend fun getById(id: Long): RequestEntity?

	@Query("SELECT * FROM request")
	suspend fun getAll(): List<RequestEntity>

	@Query("SELECT * FROM request WHERE project_id = :projectId ORDER BY id ASC")
	fun getAllForProject(projectId: Long): Flow<List<RequestEntity>>

	@Update
	fun update(requestEntity: RequestEntity): Int

	@Query("DELETE FROM request WHERE id = :id")
	suspend fun delete(id: Long)
}