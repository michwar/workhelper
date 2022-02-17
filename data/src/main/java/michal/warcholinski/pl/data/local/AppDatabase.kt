package michal.warcholinski.pl.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import michal.warcholinski.pl.data.local.dao.ProjectDao
import michal.warcholinski.pl.data.local.dao.RequestDao
import michal.warcholinski.pl.data.local.entity.ProjectEntity
import michal.warcholinski.pl.data.local.entity.RequestEntity

/**
 * Created by Michał Warcholiński on 2021-12-30.
 */
@Database(entities = [ProjectEntity::class, RequestEntity::class],
	version = 8,
	exportSchema = false)
internal abstract class AppDatabase : RoomDatabase() {

	abstract fun requestDao(): RequestDao
	abstract fun projectDao(): ProjectDao
}