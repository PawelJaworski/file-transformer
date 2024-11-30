package com.example.tests

import spock.lang.Specification

class <#list useCases as useCase>${useCase.className}Test</#list> extends Specification {

<#list useCases as useCase>
    def "${useCase.eventName}"() {
    given:
    // Add setup here for "${useCase.eventName}"

    when:
    // Simulate "${useCase.eventName}"

    then:
    // Add assertions for "${useCase.eventName}"
    }
</#list>
}
