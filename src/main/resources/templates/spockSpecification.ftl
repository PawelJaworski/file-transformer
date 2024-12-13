package ${spec.packageName()}

import spock.lang.Specification
import ${spec.packageName()}.application.event.EventPublisherAbility


class ${spec.className()} extends Specification implements EventPublisherAbility {

<#list spec.givenWhenThenList() as gwt>
    def "${(gwt.given()!"given ") + gwt.given()} ${(gwt.when()?has_content)?string('when', '')} ${gwt.when()!""} then ${gwt.then()}"() {
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