package ac.quant.quickfixspec.documentation

import ac.quant.quickfixspec.common.parsed.ParsedFixMessage
import ac.quant.quickfixspec.common.spec.FixDataDictionaryService
import com.intellij.ide.highlighter.XmlFileType
import com.intellij.openapi.components.service
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.xml.XmlFile
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.util.PsiErrorElementUtil
import com.jetbrains.rd.generator.nova.PredefinedType
import org.junit.Assert.assertNotEquals
import org.junit.jupiter.api.TestInstance

@TestDataPath("\$CONTENT_ROOT/src/test/testData/samples/")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TestQuickfixComponentDocumentationTargetProvider : BasePlatformTestCase() {

    val fileName: String = "test.java"
    val provider = QuickfixComponentDocumentationTargetProvider()

    fun testXmlFile() {
        val psiFile = myFixture.configureByText(XmlFileType.INSTANCE, "<foo>bar</foo>")
        val xmlFile = assertInstanceOf(psiFile, XmlFile::class.java)

        assertFalse(PsiErrorElementUtil.hasErrors(project, xmlFile.virtualFile))

        assertNotNull(xmlFile.rootTag)

        xmlFile.rootTag?.let {
            assertEquals("foo", it.name)
            assertEquals("bar", it.value.text)
        }
    }

    fun testFixMessageFromString() {
        val projectService: FixDataDictionaryService = project.service<FixDataDictionaryService>()

        val fixMessages = arrayOf(
            "8=FIX.4.4|9=100|35=i|34=12|49=X1|52=20240725-07:32:11.249|56=Q1|296=1|302=114858|295=3|299=0|190=2374.82|299=1|190=2374.835|299=2|190=2374.844|10=102|",
            "8=FIX.4.4|9=100|35=D|34=13|49=X2|52=20240725-07:32:12.249|56=Q2|11=12345|21=1|55=IBM|54=1|60=20240725-07:32:12.249|10=103|"
        )

        for (fixMessageTxt in fixMessages) {
            val psiFile: PsiFile = myFixture.configureByText(fileName, fixMessageTxt)
            val expectedMessage: ParsedFixMessage = ParsedFixMessage(fixMessageTxt, "|", projectService)
            assertFixMessageEqual(psiFile, 2, expectedMessage)
        }

    }

    fun testJavaCases() {
        val projectService: FixDataDictionaryService = project.service<FixDataDictionaryService>()
        val offsetsInsideFixMessage = arrayOf(451, 635)
        val element = getSampleFile("sample.java")

        val expectedMessages : Array<ParsedFixMessage> = arrayOf(
            ParsedFixMessage(
                "8=FIX.4.4\u00019=71\u000135=A\u000134=1\u000149=X1\u000152=20240725-07:32:11.011\u000156=Q1\u000198=0\u0001108=20\u0001141=Y\u000110=129\u0001",
                "\u0001",
                projectService
            ),
            ParsedFixMessage(
                "8=FIX.4.4\u00019=133\u000135=i\u000134=12\u000149=X1\u000152=20240725-07:32:11.249\u000156=Q1\u0001296=1\u0001302=114858\u0001295=3\u0001299=0\u0001190=2374.82\u0001299=1\u0001190=2374.835\u0001299=2\u0001190=2374.844\u000110=102\u0001",
                "\u0001",
                projectService
            ),
        )

        for (offset in offsetsInsideFixMessage) {
            assertFixMessageEqual(element.containingFile, offset, expectedMessages[offsetsInsideFixMessage.indexOf(offset)])
        }
    }

    fun testShouldFail() {
        val projectService: FixDataDictionaryService = project.service<FixDataDictionaryService>()
        val fixMessage="8=FIX.4.4|9=100|35=i|34=12|49=X1|52=20240725-07:32:11.249|56=Q1|296=1|302=114858|295=3|299=0|190=2374.82|299=1|190=2374.835|299=2|190=2374.844|10=102|"
        val psiFile: PsiFile = myFixture.configureByText(fileName, fixMessage)
        val expectedMessage: ParsedFixMessage = ParsedFixMessage(fixMessage, "\u0001", projectService)
        assertFixMessageNotEqual(psiFile, 2, expectedMessage)
    }

    fun testPythonCases() {
        val projectService: FixDataDictionaryService = project.service<FixDataDictionaryService>()

        val offsetsInsideFixMessage = arrayOf(56, 331, 581, 782)
        val element = getSampleFile("sample.py")

        val expectedMessageTemplate = "8=FIX.4.4|9={messageSize}|35=i|34=12|49=X1|52=20240725-07:32:11.249|56=Q1|296=1|302=114858|295=3|299=0|190=2374.82|299=1|190=2374.835|299=2|190=2374.844|10=102|"
        val expectedMessages: Array<ParsedFixMessage> = arrayOf(
            ParsedFixMessage(
                expectedMessageTemplate.replace("{messageSize}", "100"),
                "|",
                projectService
            ),
            ParsedFixMessage(
                expectedMessageTemplate.replace("{messageSize}", "200"),
                "|",
                projectService
            ),
            ParsedFixMessage(
                expectedMessageTemplate.replace("{messageSize}", "300"),
                "|",
                projectService
            ),
            ParsedFixMessage(
                expectedMessageTemplate.replace("{messageSize}", "400"),
                "|",
                projectService
            )
        )
        for (offset in offsetsInsideFixMessage) {
            assertFixMessageEqual(element.containingFile, offset, expectedMessages[offsetsInsideFixMessage.indexOf(offset)])
        }
    }

    fun assertFixMessageEqual(
        psiFile: PsiFile,
        offsetInsideFixMessage: Int,
        expectedMessage: ParsedFixMessage
    ) {
        val documentationTargets = provider.getFixMessageDetails(psiFile, offsetInsideFixMessage)
        val target = documentationTargets[0] as FixMessageDocumentationTarget
        val targetFixMessage = target.fixMessage

        assertFalse(PsiErrorElementUtil.hasErrors(project, psiFile.virtualFile))
        assertNotNull(documentationTargets)
        assertTrue(documentationTargets.isNotEmpty())
        assertEquals(targetFixMessage, expectedMessage)
    }

    fun assertFixMessageNotEqual(
        psiFile: PsiFile,
        offsetInsideFixMessage: Int,
        expectedMessage: ParsedFixMessage
    ) {
        val documentationTargets = provider.getFixMessageDetails(psiFile, offsetInsideFixMessage)
        val target = documentationTargets[0] as FixMessageDocumentationTarget
        val targetFixMessage = target.fixMessage

        assertFalse(PsiErrorElementUtil.hasErrors(project, psiFile.virtualFile))
        assertNotNull(documentationTargets)
        assertTrue(documentationTargets.isNotEmpty())
        assertNotEquals(targetFixMessage, expectedMessage)
    }


    fun getSampleFile(fileName: String): PsiElement {
        val pythonFile = myFixture.configureByFile(fileName)
        val element = pythonFile.findElementAt(0) as PsiElement
        return element
    }
    override fun getTestDataPath() = "src/test/testData/samples"

}