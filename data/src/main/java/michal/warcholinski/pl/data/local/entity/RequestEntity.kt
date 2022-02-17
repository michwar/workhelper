package michal.warcholinski.pl.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**`
 * Created by Michał Warcholiński on 2022-01-02.
 */
@Entity(tableName = "request",
	foreignKeys = [ForeignKey(entity = ProjectEntity::class, parentColumns = ["id"], childColumns = ["project_id"], onDelete = ForeignKey.CASCADE)]
)
data class RequestEntity(@PrimaryKey(autoGenerate = true) val id: Long?,
                         @ColumnInfo(name = "project_id") val projectId: Long,
                         val name: String,
                         @ColumnInfo(name = "date_of_create") val dateOfCreate: Long,
                         @ColumnInfo(name = "photo_path") val photoPath: String?,
                         val desc: String)

