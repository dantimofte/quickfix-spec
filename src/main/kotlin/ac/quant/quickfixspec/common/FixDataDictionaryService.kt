package ac.quant.quickfixspec.common

import com.intellij.lang.xml.XMLLanguage
import com.intellij.openapi.project.Project
import com.intellij.openapi.components.Service
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.xml.XmlFile
import java.net.URL

@Service(Service. Level. PROJECT)
class FixDataDictionaryService(private val project: Project) {

    private var fields: FixFields? = null

    init {
        loadFixSpecs()
    }

    private fun loadFixSpecs() {
        val filePath: URL? = this::class.java.getResource("/spec/FIX44.xml")

        val fixSpecs : PsiFile? = PsiFileFactory.getInstance(project).createFileFromText(
            "FIX40.xml",
            XMLLanguage.INSTANCE,
            filePath!!.readText()
        )

        fields = getFields(fixSpecs as XmlFile)
    }

    private fun getFields(file: XmlFile): FixFields {
        val fieldsTag = file.rootTag?.findFirstSubTag("fields")
        return FixFields(fieldsTag!!)
    }

    fun getTagName(tag: String): String {
        return fields?.getTagName(tag) ?: ""
    }

    fun getTagValueDefinition(tag: String, value: String): String {
        return fields?.getTagValueDefinition(tag, value) ?: ""
    }

}
