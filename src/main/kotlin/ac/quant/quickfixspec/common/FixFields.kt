package ac.quant.quickfixspec.common

import com.intellij.psi.xml.XmlTag

class FixFields(fieldsTag: XmlTag) {
    private val fieldsByNum: Map<String, FixField>
    private val fieldsByName: Map<String, FixField>

    init {
        val result = getFields(fieldsTag)
        fieldsByNum = result[0]
        fieldsByName = result[1]
    }

    private fun getFields(fieldsTag: XmlTag): List<MutableMap<String, FixField>> {
        val mutableNum = mutableMapOf<String, FixField>()
        val mutableName = mutableMapOf<String, FixField>()
        try {
            for (field in fieldsTag.subTags) {
                val fixField = FixField(field)
                mutableNum[fixField.number] = fixField
                mutableName[fixField.name] = fixField
            }
        } catch (e: Exception) {
            println("Error processing fields. Error: ${e.message}")
        }
        return listOf(mutableNum, mutableName)
    }

    fun getTagName(tagNumber: String): String {
        return fieldsByNum[tagNumber]?.name ?: ""
    }

    fun getTagValueDefinition(tag: String, value: String): String {
        return fieldsByNum[tag]?.values?.get(value) ?: ""
    }

}
