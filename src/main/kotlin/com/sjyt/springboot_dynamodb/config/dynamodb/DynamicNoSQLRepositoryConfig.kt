package com.sjyt.springboot_dynamodb.config.dynamodb

import com.sjyt.springboot_dynamodb.entity.TableEntity
import com.sjyt.springboot_dynamodb.repository.DynamoDBRepository
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.support.DefaultListableBeanFactory
import org.springframework.beans.factory.support.GenericBeanDefinition
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.TableSchema

@Configuration
class DynamicNoSQLRepositoryConfig(
    private val dynamoDbEnhancedClient: DynamoDbEnhancedClient,
    private val applicationContext: ConfigurableApplicationContext,
    private val dynamoDBEntityScanner: DynamoDBEntityScanner,
    @Value("\${dynamodb.table-name-suffix}")
    private val tableNameSuffix: String,
) {
    @PostConstruct
    fun registerNoSQLRepositories() {
        val packageToScan = "com.sjyt.springboot_dynamodb.entity"
        val tableEntities = dynamoDBEntityScanner.findAnnotatedClassesInPackage(packageToScan)

        tableEntities.forEach { entityClass ->
            val tableEntity = entityClass.getDeclaredConstructor().newInstance()
            val tableName = "${tableEntity.tableName}_$tableNameSuffix"

            val table = dynamoDbEnhancedClient.table(
                tableName,
                TableSchema.fromClass(entityClass)
            )
            val repositoryBean = DynamoDBRepository(table)

            val beanName = "${entityClass.simpleName.replace("Entity", "").replaceFirstChar { it.lowercase() }}Repository"
            println("beanName: $beanName")

            registerGenericBean(entityClass, repositoryBean, beanName)
        }
    }

    private fun <T : TableEntity> registerGenericBean(
        entityClass: Class<T>,
        repositoryBean: DynamoDBRepository<T>,
        beanName: String
    ) {
        val beanFactory = applicationContext.beanFactory as DefaultListableBeanFactory

        val beanDefinition = GenericBeanDefinition().apply {
            setBeanClass(DynamoDBRepository::class.java)
            setInstanceSupplier { repositoryBean }
        }

        beanFactory.registerBeanDefinition(beanName, beanDefinition)

        beanFactory.registerSingleton(beanName, repositoryBean)

        println("Registered DynamoDBRepository: $beanName for entity ${entityClass.simpleName}")
    }
}
