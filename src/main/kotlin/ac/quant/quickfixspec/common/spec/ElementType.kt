package ac.quant.quickfixspec.common.spec

enum class ElementType(val xmlContainerName: String) {
    FIELD("fields"),
    GROUP("groups"),
    COMPONENT("components"),
    MESSAGE("messages")
}
