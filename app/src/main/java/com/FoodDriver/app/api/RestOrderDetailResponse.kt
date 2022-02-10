package com.FoodDriver.app.api

import com.FoodDriver.app.model.AddonsModel
import com.FoodDriver.app.model.OrderDetailModel
import com.FoodDriver.app.model.SummaryModel

class RestOrderDetailResponse {

    private var delivery_address: String? = null

    private var profile_image: String? = null

    private var name: String? = null

    private var mobile: String? = null

    private var lang: String? = null

    private var lat: String? = null

    private var data:ArrayList<OrderDetailModel>?=null

    private var message: String? = null

    private var status: String? = null

    private var summery: SummaryModel? = null

    private var order_number: String? = null

    private var address: String? = null

    private var landmark: String? = null

    private var building: String? = null

    private var pincode: String? = null




    fun getData():ArrayList<OrderDetailModel> {
        return data!!
    }

    fun setData(data: ArrayList<OrderDetailModel>) {
        this.data = data
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

    fun getSummery(): SummaryModel? {
        return summery
    }

    fun setSummery(summery: SummaryModel?) {
        this.summery = summery
    }

    fun getAddress(): String? {
        return address
    }

    fun setAddress(address: String) {
        this.address = address
    }

    fun getOrder_number(): String? {
        return order_number
    }

    fun setOrder_number(order_number: String) {
        this.order_number = order_number
    }

    fun getLat(): String? {
        return lat
    }

    fun setLat(lat: String?) {
        this.lat = lat
    }


    fun getLang(): String? {
        return lang
    }

    fun setLang(lang: String?) {
        this.lang = lang
    }

    fun getMobile(): String? {
        return mobile
    }

    fun setMobile(mobile: String?) {
        this.mobile = mobile
    }

    fun getName(): String? {
        return name
    }

    fun setName(name: String?) {
        this.name = name
    }

    fun getProfile_image(): String? {
        return profile_image
    }

    fun setProfile_image(profile_image: String?) {
        this.profile_image = profile_image
    }

    fun getDelivery_address(): String? {
        return delivery_address
    }

    fun setDelivery_address(delivery_address: String?) {
        this.delivery_address = delivery_address
    }

    fun getBuilding(): String? {
        return building
    }

    fun setBuilding(building: String?) {
        this.building = building
    }

    fun getLandmark(): String? {
        return landmark
    }

    fun setLandmark(landmark: String?) {
        this.landmark = landmark
    }

    fun getPincode(): String? {
        return pincode
    }

    fun setPincode(pincode: String?) {
        this.pincode = pincode
    }


}