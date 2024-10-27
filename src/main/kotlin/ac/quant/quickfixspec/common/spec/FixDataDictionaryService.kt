package ac.quant.quickfixspec.common.spec

import com.intellij.lang.xml.XMLLanguage
import com.intellij.openapi.project.Project
import com.intellij.openapi.components.Service
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag
import java.net.URL

@Service(Service. Level. PROJECT)
class FixDataDictionaryService(private val project: Project) : IFixDataDictionaryService {
    override val rootTag :  XmlTag
    override val fields: Elements
    override val components: Elements
    override val messages: Elements
    override val header: MessageElement
    override val trailer: MessageElement

    init {
        val fixSpecs : PsiFile = loadFixSpecs()
        rootTag = (fixSpecs as XmlFile).rootTag!!
        fields = Elements(ElementType.FIELD, this)
        components = Elements(ElementType.COMPONENT, this)
        components.initSubTags()
        messages = Elements(ElementType.MESSAGE, this)
        messages.initSubTags()
        header = MessageElement("header", ElementType.MESSAGE, rootTag.findFirstSubTag("header")!!, this)
        trailer = MessageElement("trailer", ElementType.MESSAGE, rootTag.findFirstSubTag("trailer")!!, this)
    }

    private fun loadFixSpecs(): PsiFile {
        val filePath: URL? = this::class.java.getResource("/spec/FIX44.xml")

        val fixSpecs : PsiFile? = PsiFileFactory.getInstance(project).createFileFromText(
            "FIX44.xml",
            XMLLanguage.INSTANCE,
            filePath!!.readText()
        )

        return fixSpecs!!
    }

}
