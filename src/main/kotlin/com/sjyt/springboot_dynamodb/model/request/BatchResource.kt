package com.sjyt.springboot_dynamodb.model.request

import com.sjyt.springboot_dynamodb.entity.TableEntity

data class BatchResource<PK, SK>(
    val tableEntity: Class<out TableEntity>,
    val primaryKeys: List<PrimaryKey<PK, SK>>,
)