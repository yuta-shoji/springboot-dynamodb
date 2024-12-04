package com.sjyt.springboot_dynamodb

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component

@Component
class RootPackageDetector(
    private val applicationContext: ApplicationContext
) {
    fun getRootPackage(): String {
        val mainClassPackage = getMainClassPackage()
        if (mainClassPackage.isNullOrEmpty()) {
            throw IllegalStateException("Failed to detect the root package.")
        }
        return mainClassPackage
    }

    private fun getMainClassPackage(): String? {
        val mainClass = applicationContext
            .getBeansWithAnnotation(SpringBootApplication::class.java)
            .values
            .firstOrNull()
            ?.javaClass

        return mainClass?.packageName
    }
}