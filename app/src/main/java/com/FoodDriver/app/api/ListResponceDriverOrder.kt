package com.FoodDriver.app.api

import com.FoodDriver.app.model.OrderHistoryModel

class ListResponceDriverOrder {
    private var data: ArrayList<OrderHistoryModel>?=null
    private var ongoing_order: String? = null

    private var completed_order: String? = null

    private var message: String? = null

    private var status: String? = null
    private var currency: String? = null
    fun getData(): ArrayList<OrderHistoryModel> {
        return data!!
    }

    fun setData(data: ArrayList<OrderHistoryModel>) {
        this.data = data
    }

    fun getOngoing_order(): String? {
        return ongoing_order
    }

    fun setOngoing_order(ongoing_order: String?) {
        this.ongoing_order = ongoing_order
    }

    fun getCompleted_order(): String? {
        return completed_order
    }

    fun setCompleted_order(completed_order: String?) {
        this.completed_order = completed_order
    }

    fun getMessage(): String? {
        return message
    }

    fun setMessage(message: String?) {
        this.message = message
    }

    fun getStatus(): String? {
        return status
    }

    fun setStatus(status: String?) {
        this.status = status
    }

    fun getCurrency(): String? {
        return currency
    }

    fun setCurrency(currency: String?) {
        this.currency = currency
    }

}