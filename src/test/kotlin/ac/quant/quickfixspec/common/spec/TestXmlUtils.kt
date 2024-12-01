package ac.quant.quickfixspec.common.spec

import ac.quant.quickfixspec.common.utils.Utils.getXmlFile
import com.intellij.psi.xml.XmlAttributeValue
import com.intellij.psi.xml.XmlTag
import com.intellij.testFramework.fixtures.BasePlatformTestCase


class TestXmlUtils : BasePlatformTestCase() {

    fun testGetRootTag() {
        val psiFile = getXmlFile(myFixture, "FIX44.xml")
        val expectedRootTagName = "fix"

        val indexes = arrayOf(3005, 205, 1476)
        for (index in indexes) {
            val psiElement = psiFile.findElementAt(index)!!
            val xmlTag = psiElement.parent!!
            val rootTag = XmlUtils.getRootTag(xmlTag)
            assertEquals("Root tag for '${xmlTag.text}' should be '${expectedRootTagName}' but was '${rootTag!!.name}'", expectedRootTagName, rootTag.name)
        }
    }

    fun testFindDefinition() {
        val psiFile = getXmlFile(myFixture, "FIX44.xml")
        val rootTag = psiFile.findElementAt(0)!!.parent as XmlTag


        val fieldNames = arrayOf("Text", "CxlQty", "AllocTransType")
        val expectedFieldDefinitions = arrayOf(
            "<field number='58' name='Text' type='STRING' />",
            "<field number='84' name='CxlQty' type='QTY' />",
            "<field number='71' name='AllocTransType' type='CHAR'>\n" +
                "   <value enum='0' description='NEW' />\n" +
                "   <value enum='1' description='REPLACE' />\n" +
                "   <value enum='2' description='CANCEL' />\n" +
                "  </field>"
        )

        assertDefinitionsEqual("field", fieldNames, expectedFieldDefinitions, rootTag)

        val groupNames = arrayOf("NoOrders", "NoAllocs")
        val expectedGroupDefinitions = arrayOf(
            "<field number='73' name='NoOrders' type='NUMINGROUP' />",
            "<field number='78' name='NoAllocs' type='NUMINGROUP' />"
        )

        assertDefinitionsEqual("group", groupNames, expectedGroupDefinitions, rootTag)

        val componentNames = arrayOf("Instrument")
        val expectedComponentDefinitions = arrayOf(
            "<component name='Instrument'>\n" +
                    "   <field name='Symbol' required='N' />\n" +
                    "   <field name='SymbolSfx' required='N' />\n" +
                    "   <field name='SecurityID' required='N' />\n" +
                    "   <field name='SecurityIDSource' required='N' />\n" +
                    "   <component name='SecAltIDGrp' required='N' />\n" +
                    "   <field name='Product' required='N' />\n" +
                    "   <field name='CFICode' required='N' />\n" +
                    "   <field name='SecurityType' required='N' />\n" +
                    "   <field name='SecuritySubType' required='N' />\n" +
                    "   <field name='MaturityMonthYear' required='N' />\n" +
                    "   <field name='MaturityDate' required='N' />\n" +
                    "   <field name='PutOrCall' required='N' />\n" +
                    "   <field name='CouponPaymentDate' required='N' />\n" +
                    "   <field name='IssueDate' required='N' />\n" +
                    "   <field name='RepoCollateralSecurityType' required='N' />\n" +
                    "   <field name='RepurchaseTerm' required='N' />\n" +
                    "   <field name='RepurchaseRate' required='N' />\n" +
                    "   <field name='Factor' required='N' />\n" +
                    "   <field name='CreditRating' required='N' />\n" +
                    "   <field name='InstrRegistry' required='N' />\n" +
                    "   <field name='CountryOfIssue' required='N' />\n" +
                    "   <field name='StateOrProvinceOfIssue' required='N' />\n" +
                    "   <field name='LocaleOfIssue' required='N' />\n" +
                    "   <field name='RedemptionDate' required='N' />\n" +
                    "   <field name='StrikePrice' required='N' />\n" +
                    "   <field name='StrikeCurrency' required='N' />\n" +
                    "   <field name='OptAttribute' required='N' />\n" +
                    "   <field name='ContractMultiplier' required='N' />\n" +
                    "   <field name='CouponRate' required='N' />\n" +
                    "   <field name='SecurityExchange' required='N' />\n" +
                    "   <field name='Issuer' required='N' />\n" +
                    "   <field name='EncodedIssuerLen' required='N' />\n" +
                    "   <field name='EncodedIssuer' required='N' />\n" +
                    "   <field name='SecurityDesc' required='N' />\n" +
                    "   <field name='EncodedSecurityDescLen' required='N' />\n" +
                    "   <field name='EncodedSecurityDesc' required='N' />\n" +
                    "   <field name='Pool' required='N' />\n" +
                    "   <field name='ContractSettlMonth' required='N' />\n" +
                    "   <field name='CPProgram' required='N' />\n" +
                    "   <field name='CPRegType' required='N' />\n" +
                    "   <component name='EvntGrp' required='N' />\n" +
                    "   <field name='DatedDate' required='N' />\n" +
                    "   <field name='InterestAccrualDate' required='N' />\n" +
                    "  </component>"
        )

        assertDefinitionsEqual("component", componentNames, expectedComponentDefinitions, rootTag)
    }

    fun assertDefinitionsEqual(parentTag: String, names: Array<String>, expectedDefinitions: Array<String>, rootTag: XmlTag) {
        for (i in names.indices) {
            val name = names[i]
            val expectedDefinition = expectedDefinitions[i]

            val definition = XmlUtils.findDefinition(name, parentTag, rootTag)!!.text
            assertNotNull("Expected definition for tag '$name' with parent '$parentTag' but was null", definition)
            assertEquals("Definition for '$name' with parent '$parentTag' should be '$expectedDefinition' but was '${definition}'", expectedDefinition, definition)
        }
    }

    fun testIsTagDeclaration() {
        val psiFile = getXmlFile(myFixture, "FIX44.xml")

        val indexes = arrayOf(2650, 2785, 108734, 167517)
        val expectedDeclarations = arrayOf(false, false, true, true)
        for (i in indexes.indices) {
            val index = indexes[i]
            val expectedDeclaration = expectedDeclarations[i]

            val psiElement = psiFile.findElementAt(index)!!.parent

            assertTrue("Element at index $index is not an XmlAttributeValue but a ${psiElement.javaClass.simpleName}", psiElement is XmlAttributeValue)

            val xmlAttributeValue = psiElement as XmlAttributeValue
            val isDeclaration = XmlUtils.isTagDeclaration(xmlAttributeValue)
            assertEquals("Attribute value '${xmlAttributeValue.value}' at index $index should be a declaration: $expectedDeclaration", expectedDeclaration, isDeclaration)
        }

    }

    fun testGetCurrentTag() {
        val psiFile = getXmlFile(myFixture, "FIX44.xml")

        val expectedTagsMap = mapOf(
            0 to "fix",
            2650 to "field",
            2785 to "component",
            4879 to "message",
            12818 to "group",
            108734 to "component",
            167517 to "field"
        )

        for (positionInFile in expectedTagsMap.keys) {
            val expectedTag = expectedTagsMap[positionInFile]!!
            val psiElement = psiFile.findElementAt(positionInFile)!!

            val currentTag = XmlUtils.getCurrentTag(psiElement)
            assertEquals("Current tag for element at positionInFile $positionInFile should be '$expectedTag' but was '${currentTag.name}'", expectedTag, currentTag.name)
        }
    }

    override fun getTestDataPath() = "src/main/resources/spec"

}
