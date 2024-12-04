package com.sjyt.springboot_dynamodb.config.dynamodb

import com.sjyt.springboot_dynamodb.RootPackageDetector
import com.sjyt.springboot_dynamodb.entity.TableEntity
import com.sjyt.springboot_dynamodb.repository.NoSQLRepository
import jakarta.annotation.PostConstruct
import org.reflections.Reflections
import org.springframework.beans.factory.support.DefaultListableBeanFactory
import org.springframework.beans.factory.support.GenericBeanDefinition
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean

@Configuration
class DynamicDynamoDBRepositoryBeanRegistrar(
    private val dynamoDBRepositoryFactory: DynamoDBRepositoryFactory,
    private val applicationContext: ConfigurableApplicationContext,
    private val rootPackageDetector: RootPackageDetector,
) {
    @PostConstruct
    fun registerRepositories() {
        val rootPackage = rootPackageDetector.getRootPackage()

        val reflections = Reflections(rootPackage)
        val tableEntities = reflections
            .getTypesAnnotatedWith(DynamoDbBean::class.java)
            .filterIsInstance<Class<TableEntity>>()

        tableEntities.forEach { entityClass ->
            val repositoryBean = dynamoDBRepositoryFactory.create(entityClass)
            registerRepositoryBean(entityClass, repositoryBean)
        }
    }

    private fun <T : TableEntity> registerRepositoryBean(
        entityClass: Class<T>,
        repositoryBean: NoSQLRepository<T>,
    ) {
        val beanFactory = applicationContext.beanFactory as DefaultListableBeanFactory
        val beanName = entityClass.simpleName.replaceFirstChar { it.lowercase() }

        val beanDefinition = GenericBeanDefinition().apply {
            setBeanClass(NoSQLRepository::class.java)
            setInstanceSupplier { repositoryBean }
        }

        beanFactory.registerBeanDefinition(beanName, beanDefinition)
    }
}