package michal.warcholinski.pl.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Michał Warcholiński on 2022-01-08.
 */
@Entity(tableName = "project")
data class ProjectEntity(@PrimaryKey(autoGenerate = true) val id: Long?,
                         val name: String,
                         val desc: String,
                         @ColumnInfo(name = "create_date") val createDate: Long,
                         @ColumnInfo(name = "phone_no") val phoneNo: String?,
                         @ColumnInfo(name = "email") val email: String?,
                         val realized: Boolean)