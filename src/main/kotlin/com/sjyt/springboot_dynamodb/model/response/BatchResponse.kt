package com.sjyt.springboot_dynamodb.model.response

import com.sjyt.springboot_dynamodb.entity.TableEntity

data class BatchResponse(
    val items: List<TableEntity>,
)