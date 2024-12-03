package com.sjyt.springboot_dynamodb.config.dynamodb

import com.sjyt.springboot_dynamodb.entity.TableEntity
import org.springframework.stereotype.Component
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import java.io.File
import java.net.URL

@Component
class DynamoDBEntityScanner {
    fun findAnnotatedClassesInPackage(packageName: String): List<Class<TableEntity>> {
        val resources = getPackageResources(packageName)
        return resources.flatMap { resource ->
            findClassFilesInDirectory(resource.file)
                .mapNotNull { classFile -> toClassName(packageName, resource.file, classFile) }
                .mapNotNull { className -> loadClass(className) }
                .filter { clazz -> hasValidAnnotationAndType(clazz) }
                .map { clazz -> clazz as Class<TableEntity> }
        }
    }

    // パッケージ内のリソースを取得
    private fun getPackageResources(packageName: String): List<URL> {
        val classLoader = Thread.currentThread().contextClassLoader
        val packagePath = packageName.replace('.', '/')
        return classLoader.getResources(packagePath).toList()
    }

    // ディレクトリ内の.classファイルを探す
    private fun findClassFilesInDirectory(directoryPath: String): Sequence<File> {
        val directory = File(directoryPath)
        if (!directory.isDirectory) return emptySequence()
        return directory.walkTopDown().filter { it.extension == "class" }
    }

    // クラスファイルのパスをクラス名に変換
    private fun toClassName(packageName: String, baseDir: String, classFile: File): String? {
        return try {
            val relativePath = classFile.relativeTo(File(baseDir)).path
            "$packageName.${relativePath.removeSuffix(".class").replace(File.separator, ".")}"
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    // クラス名をクラスにロード
    private fun loadClass(className: String): Class<*>? {
        return try {
            Class.forName(className)
        } catch (e: ClassNotFoundException) {
            null
        }
    }

    // アノテーションと型を確認
    private fun hasValidAnnotationAndType(clazz: Class<*>): Boolean {
        val classIsAnnotation = clazz.isAnnotationPresent(DynamoDbBean::class.java)
        val isAssignableFrom = TableEntity::class.java.isAssignableFrom(clazz)

        return classIsAnnotation && isAssignableFrom
    }
}