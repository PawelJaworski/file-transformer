package ${spec.packageName()}

import spock.lang.Specification
import ${spec.packageName()}.application.event.EventPublisherAbility


class ${spec.className()} extends Specification implements EventPublisherAbility {

<#list spec.givenWhenThenList() as gwt>
    def "${gwt.description()}"() {
    given:
    <#list gwt.givenCodeBlock() as code>
    ${code}
    </#list>
    when:
    <#list gwt.whenCodeBlock() as code>
    ${code}
    </#list>

    then:
    <#list gwt.thenCodeBlock() as code>
    ${code}
    </#list>
    }

</#list>
}