package ac.quant.quickfixspec.common.spec

class Elements(private val elementsType: ElementType,private val fixDataDictionary: IFixDataDictionaryService) {
    val valuesByName:  Map<String, IElement>
    val valueByNumber: Map<String, IElement>

    init {
        when (elementsType) {
            ElementType.FIELD -> {
                valuesByName = parseElements()
                valueByNumber = valuesByName.values.associateBy { it.number }
            }
            ElementType.COMPONENT -> {
                valuesByName = parseElements()
                valueByNumber = mutableMapOf()
            }
            ElementType.MESSAGE -> {
                valuesByName = parseElements()
                valueByNumber = mutableMapOf()
            }
            else -> {
                valuesByName = mutableMapOf()
                valueByNumber = mutableMapOf()
            }
        }
    }

    /*
     This will set the proper references to subComponents
     Also parses the groups now so that the groups can set the proper references to subComponents from the first time
     */
    fun initSubTags() {
        initSubComponents()
        parseGroups()
    }

    private fun initSubComponents() {
        for (component in valuesByName.values) {
            if (component !is ComponentElement) {
                continue
            }
            component.setSubComponents()
        }
    }

    private fun parseGroups() {
        for (element in valuesByName.values) {
            element.parseGroups()
        }
    }

    // simplify call to parametrize the type of the element
    private fun parseElements(): Map<String, IElement> {
        val mutableName = mutableMapOf<String, IElement>()
        try {
            val xmlTag = fixDataDictionary.rootTag.findSubTags(elementsType.xmlContainerName)
            for (elementTag in xmlTag.first().subTags) {
                val elementName = elementTag.getAttribute("name")?.value ?: ""
                when (elementTag.name) {
                    "field" -> {
                        mutableName[elementName] = FieldElement(elementName, elementsType, elementTag, fixDataDictionary)
                    }
                    "component" -> {
                        mutableName[elementName] = ComponentElement(elementName, elementsType, elementTag, fixDataDictionary)
                    }
                    "message" -> {
                        mutableName[elementName] = MessageElement(elementName, elementsType, elementTag, fixDataDictionary)
                    }
                    else -> {
                        mutableName[elementName] = DefaultElement(elementName, "", elementsType,elementTag,  fixDataDictionary)
                    }
                }
            }
        } catch (e: Exception) {
            println("Error processing fields. Error: ${e.message}")
        }
        return mutableName
    }
}
