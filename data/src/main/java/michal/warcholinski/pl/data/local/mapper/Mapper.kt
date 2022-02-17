package michal.warcholinski.pl.data.local.mapper

/**
 * Created by Michał Warcholiński on 2022-01-02.
 */
interface Mapper<E, M> {

	fun fromEntity(entity: E): M

	fun toEntity(model: M): E
}