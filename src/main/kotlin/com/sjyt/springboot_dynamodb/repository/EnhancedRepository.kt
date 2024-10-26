package com.sjyt.springboot_dynamodb.repository

import com.sjyt.springboot_dynamodb.entity.TableEntity

interface EnhancedRepository {
    val dynamoDBEnhancedRepository: NoSQLEnhancedRepository
}