package ac.quant.quickfixspec.documentation
import ac.quant.quickfixspec.common.parsed.ParsedFixMessage
import ac.quant.quickfixspec.common.spec.FieldElement
import ac.quant.quickfixspec.common.spec.IFixDataDictionaryService
import com.intellij.model.Pointer
import com.intellij.platform.backend.documentation.DocumentationResult
import com.intellij.platform.backend.documentation.DocumentationTarget
import com.intellij.platform.backend.presentation.TargetPresentation

@Suppress("UnstableApiUsage")
class FixMessageDocumentationTarget(
    private val fixMessage: ParsedFixMessage,
) : DocumentationTarget {

    override fun createPointer(): Pointer<out DocumentationTarget> {
        return Pointer.hardPointer(this)
    }

    override fun computePresentation(): TargetPresentation {
        return TargetPresentation.builder(fixMessage.msgName).presentation()
    }

    override fun computeDocumentationHint(): String {
        val docHint = computeDocumentation().toString()
        return docHint
    }

    override fun computeDocumentation(): DocumentationResult {
        val componentDetails = fixMessage.getMessageDetails()
        return DocumentationResult.documentation(componentDetails)
    }

}
