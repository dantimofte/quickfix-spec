package ac.quant.quickfixspec.common.parsed

import ac.quant.quickfixspec.common.spec.FieldElement
import ac.quant.quickfixspec.common.spec.IFixDataDictionaryService

class ParsedFixMessage(private val message: String, private val delimiter: String, private val fixDataDictionary: IFixDataDictionaryService) {
    private lateinit var msgType: String
    lateinit var msgName: String

    private val headerFields = mutableListOf<FieldElement>()

    init {
        parseMessage()
    }

    private fun parseMessage() {
        val messageParts = message.split(delimiter)

        for (part in messageParts) {
            val tagNumber = clean(part.substringBefore("="))

            if (tagNumber.isEmpty()) {
                continue
            }

            val tagValue = part.substringAfter("=")
            val field = fixDataDictionary.getFieldByNumber(tagNumber)
            val fieldWithValue = field.withValue(tagValue)
            maybeAddHeaderField(fieldWithValue)
            maybeSetMessageTypeAndName(fieldWithValue)
        }
    }

    private fun maybeAddHeaderField(field: FieldElement) {
        if (field.isHeaderField()) {
            headerFields.add(field)
        }
    }

    private fun maybeSetMessageTypeAndName(field: FieldElement) {
        if (field.number == "35") {
            msgType = field.number
            msgName = field.name
        }
    }

    private fun clean(value: String): String {
        var cleanedValue = value.trim()
        cleanedValue = cleanedValue.replace("'", "")
        cleanedValue = cleanedValue.replace("\"", "")
        cleanedValue = cleanedValue.trim()
        return cleanedValue
    }

    fun getMessageDetails(): String {
        val messageParts = message.split(delimiter)

        val formattedMessage = messageParts.subList(0, messageParts.size - 1).joinToString("\n") {
            val fieldNumber = clean(it.substringBefore("="))
            val fieldValue = it.substringAfter("=")
            val field = fixDataDictionary.getFieldByNumber(fieldNumber)
            val fieldValueDefinition = field.values[fieldValue] ?: ""
            val msgPart = getMsgParts(field)
            "<tr><td>$msgPart<td><td>$fieldNumber</td><td>${field.name}</td><td>$fieldValue</td><td>$fieldValueDefinition</td></tr>"
        }

        val displayText = "<html><head><style> table { width: 100%; } td, th { border-bottom: 1px solid; text-align: left; padding: 1px; } </style></head><body><table>$formattedMessage</table></html>"

        return displayText
    }

    private fun getMsgParts(field: FieldElement): String {
        return when (true) {
            field.isHeaderField() -> "H"
            field.isTrailerField() -> "T"
            else -> "M"
        }
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        other as ParsedFixMessage

        if (!delimiterEquals(other)) return false
        if (msgType != other.msgType) return false
        if (msgName != other.msgName) return false
        if (!headerFieldsEquals(other)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = delimiter.hashCode()
        result = 31 * result + msgType.hashCode()
        result = 31 * result + msgName.hashCode()
        result = 31 * result + headerFields.hashCode()
        return result
    }

    private val delimiterCommonValues: Array<ByteArray> = arrayOf(
        byteArrayOf(92, 117, 48, 48, 48, 49),  // "\u0001"
        byteArrayOf(1), // byte representation of 1
    )

    private fun delimiterInCommonValues(delimiter: ByteArray): Boolean {
        for (delimiterValue in delimiterCommonValues) {
            if (delimiter.contentEquals(delimiterValue)) {
                return true
            }
        }
        return false
    }

    private fun delimiterEquals(other: ParsedFixMessage): Boolean {

        val delimiterByteArray: ByteArray = delimiter.toByteArray()
        val otherDelimiterByteArray: ByteArray = other.delimiter.toByteArray()

       val result = delimiterInCommonValues(delimiterByteArray) && delimiterInCommonValues(otherDelimiterByteArray)
        if (result) {
            return true
        }
        return delimiter == other.delimiter
    }

    private fun headerFieldsEquals(other: ParsedFixMessage): Boolean {
        if (headerFields.size != other.headerFields.size) return false

        for (i in headerFields.indices) {
           // use the equals method from FieldElement
            if (headerFields[i] != other.headerFields[i]) return false
        }

        return true
    }

    override fun toString(): String {
        return "ParsedFixMessage(delimiter='$delimiter', msgType='$msgType', msgName='$msgName', headerFields=$headerFields)"
    }
}