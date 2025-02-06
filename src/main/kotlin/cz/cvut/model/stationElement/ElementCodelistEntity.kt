package cz.cvut.model.measurement

import cz.cvut.database.table.ElementCodelistTable
import cz.cvut.model.stationElement.ElementCodelist
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class ElementCodelistEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ElementCodelistEntity>(ElementCodelistTable)

    var abbreviation by ElementCodelistTable.abbreviation
    var name by ElementCodelistTable.name
    var unit by ElementCodelistTable.unit
}

fun ElementCodelistEntity.toElementCodelist(): ElementCodelist {
    return ElementCodelist(
        abbreviation = this.abbreviation,
        name = this.name,
        unit = this.unit
    )
}

fun ElementCodelist.toElementCodelistEntity(): ElementCodelistEntity {
    return ElementCodelistEntity.new {
        abbreviation = this@toElementCodelistEntity.abbreviation
        name = this@toElementCodelistEntity.name
        unit = this@toElementCodelistEntity.unit
    }
}
