package ${spec.packageName()}

import spock.lang.Specification
import ${spec.packageName()}.application.event.EventPublisherAbility

class ${spec.className()} extends Specification implements EventPublisherAbility {

<#list spec.givenWhenThenList() as gwt>
    def "given ${gwt.given()} when ${gwt.when()} then ${gwt.then()}"() {
    given:
    <#list gwt.givenCodeBlock() as code>
    ${code}
    </#list>
    when:
    // Simulate "${gwt.when()}"

    then:
    <#list gwt.thenCodeBlock() as code>
    ${code}
    </#list>
    }

</#list>
}