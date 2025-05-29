package com.lswmobile.app.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Withdrawal model
 */
@Serializable
data class Withdrawal(
    @SerialName("_id")
    val _id: String,
    val userId: String,
    val amount: Double,
    val status: String,
    val createdAt: String,
    val updatedAt: String
)

/**
 * Withdrawal request body
 */
@Serializable
data class WithdrawalBody(
    val amount: Double,
    val bankAccountId: String
)

/**
 * Withdrawal response
 */
@Serializable
data class GetWithdrawalResponse(
    val success: Boolean,
    val data: Withdrawal
)

/**
 * My withdrawals response
 */
@Serializable
data class MyWithdrawalsResponse(
    val success: Boolean,
    val data: List<Withdrawal>
)

/**
 * Withdrawal fees
 */
@Serializable
data class WithdrawalFees(
    val LOCAL: Double,
    val ITL: Double
)
