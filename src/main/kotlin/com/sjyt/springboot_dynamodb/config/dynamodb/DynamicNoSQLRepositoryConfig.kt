package com.sjyt.springboot_dynamodb.config.dynamodb

import com.sjyt.springboot_dynamodb.entity.TableEntity
import com.sjyt.springboot_dynamodb.repository.NoSQLRepository
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.support.DefaultListableBeanFactory
import org.springframework.beans.factory.support.GenericBeanDefinition
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Configuration

@Configuration
class DynamicNoSQLRepositoryConfig(
    private val dynamoDBRepositoryFactory: DynamoDBRepositoryFactory,
    private val applicationContext: ConfigurableApplicationContext,
    private val dynamoDBEntityScanner: DynamoDBEntityScanner,
) {
    @PostConstruct
    fun registerRepositories() {
        val packageToScan = "com.sjyt.springboot_dynamodb.entity"
        val tableEntities = dynamoDBEntityScanner.findAnnotatedClassesInPackage(packageToScan)

        tableEntities.forEach { entityClass ->
            val repositoryBean = dynamoDBRepositoryFactory.create(entityClass)
            registerRepositoryBean(entityClass, repositoryBean)
        }
    }

    private fun <T : TableEntity> registerRepositoryBean(
        entityClass: Class<T>,
        repositoryBean: NoSQLRepository<T>
    ) {
        val beanFactory = applicationContext.beanFactory as DefaultListableBeanFactory
        val beanName = "${entityClass.simpleName.replace("Entity", "").replaceFirstChar { it.lowercase() }}Repository"

        val beanDefinition = GenericBeanDefinition().apply {
            setBeanClass(NoSQLRepository::class.java)
            setInstanceSupplier { repositoryBean }
        }

        beanFactory.registerBeanDefinition(beanName, beanDefinition)
    }
}
