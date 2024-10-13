package ac.quant.quickfixspec.documentation
import ac.quant.quickfixspec.common.FixDataDictionaryService
import com.intellij.model.Pointer
import com.intellij.platform.backend.documentation.DocumentationResult
import com.intellij.platform.backend.documentation.DocumentationTarget
import com.intellij.platform.backend.presentation.TargetPresentation

@Suppress("UnstableApiUsage")
class FixMessageDocumentationTarget(
    private val fixMessage: String,
    private val fixDelimiter: String,
    private val fixDataDictionaryService: FixDataDictionaryService,
) : DocumentationTarget {


    override fun createPointer(): Pointer<out DocumentationTarget> {
        return Pointer.hardPointer(this)
    }

    override fun computePresentation(): TargetPresentation {
        return TargetPresentation.builder(fixMessage).presentation()
    }

    override fun computeDocumentationHint(): String {
        val docHint = computeDocumentation().toString()
        return docHint
    }

    override fun computeDocumentation(): DocumentationResult {
        val componentDetails = getMessageDetails()
        return DocumentationResult.documentation(componentDetails)
    }

    private fun getMessageDetails(): String {
        // split the message by the delimiter
        val messageParts = fixMessage.split(fixDelimiter)
        // build new string with the tag equals name on each line without the last index

        val formattedMessage = messageParts.subList(0, messageParts.size - 1).joinToString("\n") {
            val tagNumber = it.substringBefore("=")
            val tagValue = it.substringAfter("=")
            val tagName = fixDataDictionaryService.getTagName(tagNumber)
            val tagValueDefinition = fixDataDictionaryService.getTagValueDefinition(tagNumber, tagValue)
            "<tr><td>$tagNumber</td><td>$tagName</td><td>$tagValue</td><td>$tagValueDefinition</td></tr>"
        }

        val displayText = "<html><head><style> table { width: 100%; } td, th { border-bottom: 1px solid; text-align: left; padding: 1px; } </style></head><body><table>$formattedMessage</table></html>"

        return displayText
    }

}