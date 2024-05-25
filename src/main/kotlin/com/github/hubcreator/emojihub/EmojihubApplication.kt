package com.github.hubcreator.emojihub

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class EmojihubApplication

fun main(args: Array<String>) {

    runApplication<EmojihubApplication>(*args)
}
