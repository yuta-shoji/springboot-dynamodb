package com.sjyt.springboot_dynamodb.repository

import com.sjyt.springboot_dynamodb.entity.TableEntity

interface BaseRepository {
    val dynamoDBRepository: NoSQLRepository<out TableEntity>
}