package com.FoodDriver.app.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.FoodDriver.app.R
import com.FoodDriver.app.activity.DashboardActivity
import com.FoodDriver.app.activity.OrderDetailActivity
import com.FoodDriver.app.api.ApiClient
import com.FoodDriver.app.api.ListResponceDriverOrder
import com.FoodDriver.app.base.BaseAdaptor
import com.FoodDriver.app.base.BaseFragmnet
import com.FoodDriver.app.model.OrderHistoryModel
import com.FoodDriver.app.utils.Common
import com.FoodDriver.app.utils.Common.alertErrorOrValidationDialog
import com.FoodDriver.app.utils.Common.dismissLoadingProgress
import com.FoodDriver.app.utils.Common.showLoadingProgress
import com.FoodDriver.app.utils.SharePreference
import com.FoodDriver.app.utils.SharePreference.Companion.getStringPref
import kotlinx.android.synthetic.main.fragment_orderhistory.*
import kotlinx.android.synthetic.main.fragment_orderhistory.ivMenu
import kotlinx.android.synthetic.main.fragment_orderhistory.swiperefresh
import kotlinx.android.synthetic.main.fragment_orderhistory.tvNoDataFound
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class OrderHistoryFragment : BaseFragmnet() {
    override fun setView(): Int {
        return R.layout.fragment_orderhistory
    }

    override fun Init(view: View) {
        Common.getCurrentLanguage(activity!!,false)
        if (Common.isCheckNetwork(activity!!)) {
            callApiOrderHistory()
        } else {
            alertErrorOrValidationDialog(activity!!, resources.getString(R.string.no_internet))
        }

        ivMenu.setOnClickListener {
            (activity as DashboardActivity?)!!.onDrawerToggle()
        }
        swiperefresh.setOnRefreshListener { // Your code to refresh the list here.
            if (Common.isCheckNetwork(activity!!)) {
                swiperefresh.isRefreshing=false
                callApiOrderHistory()
            } else {
                alertErrorOrValidationDialog(activity!!,resources.getString(R.string.no_internet))
            }
        }
    }

    private fun callApiOrderHistory() {
        showLoadingProgress(activity!!)
        val map = HashMap<String, String>()
        map.put("driver_id", getStringPref(activity!!, SharePreference.userId)!!)
        val call = ApiClient.getClient.getOrderHistory(map)
        call.enqueue(object : Callback<ListResponceDriverOrder> {
            override fun onResponse(
                call: Call<ListResponceDriverOrder>,
                response: Response<ListResponceDriverOrder>
            ) {
                if (response.code() == 200) {
                    dismissLoadingProgress()
                    val restResponce: ListResponceDriverOrder = response.body()!!
                    if (restResponce.getStatus().equals("1")) {
                        if (restResponce.getData().size > 0) {
                            if (isAdded) {
                                rvOrderHistory.visibility = View.VISIBLE
                                tvNoDataFound.visibility = View.GONE
                            }
                            val foodCategoryList = restResponce.getData()
                            setFoodCategoryAdaptor(foodCategoryList)
                        } else {
                            rvOrderHistory.visibility = View.GONE
                            tvNoDataFound.visibility = View.VISIBLE
                        }

                    } else if (restResponce.getStatus().equals("0")) {
                        dismissLoadingProgress()
                        rvOrderHistory.visibility = View.GONE
                        tvNoDataFound.visibility = View.VISIBLE
                    }
                }
            }

            override fun onFailure(call: Call<ListResponceDriverOrder>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    activity!!,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }


    fun setFoodCategoryAdaptor(
        orderHistoryList: ArrayList<OrderHistoryModel>
    ) {
        val orderHistoryAdapter =
            object : BaseAdaptor<OrderHistoryModel>(activity!!, orderHistoryList) {
                @SuppressLint("SetTextI18n")
                override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: OrderHistoryModel,
                    position: Int
                ) {
                    val tvOrderNumber: TextView = holder!!.itemView.findViewById(R.id.tvOrderNumber)
                    val tvPrice: TextView = holder.itemView.findViewById(R.id.tvPrice)
                    val tvQtyNumber: TextView = holder.itemView.findViewById(R.id.tvQtyNumber)
                    val tvOrderStatus: TextView = holder.itemView.findViewById(R.id.tvOrderStatus)
                    val tvPaymentType: TextView = holder.itemView.findViewById(R.id.tvPaymentType)

                    tvOrderNumber.text = orderHistoryList.get(position).getOrder_number()
                    tvPrice.text = getStringPref(activity!!,SharePreference.isCurrancy)+String.format(Locale.US,"%.2f",orderHistoryList.get(position).getTotal_price()!!.toDouble())
                    tvQtyNumber.text = orderHistoryList.get(position).getQty()

                    val tvDate: TextView = holder.itemView.findViewById(R.id.tvDate)
                    tvDate.text = Common.getDate(orderHistoryList.get(position).getDate()!!)

                    if (orderHistoryList.get(position).getStatus().equals("3")) {
                        tvOrderStatus.text = "Order on the way"
                    } else if (orderHistoryList.get(position).getStatus().equals("4")) {
                        tvOrderStatus.text = "Order Delivered"
                    }

                    if (orderHistoryList.get(position).getPayment_type()!!.toInt() == 0) {
                        tvPaymentType.text = "Pay by Cash"
                    } else if (orderHistoryList.get(position).getPayment_type()!!.toInt() == 1){
                        tvPaymentType.text = "Razorpay"
                    }else if (orderHistoryList.get(position).getPayment_type()!!.toInt() == 2){
                        tvPaymentType.text = "Stripe"
                    }


                    holder.itemView.setOnClickListener {
                        startActivity(
                            Intent(
                                activity!!,
                                OrderDetailActivity::class.java
                            ).putExtra("order_id",orderHistoryList.get(position).getId()).putExtra("status",orderHistoryList.get(position).getStatus())
                        )
                    }
                }

                override fun setItemLayout(): Int {
                    return R.layout.row_orderdelivery
                }

                override fun setNoDataView(): TextView? {
                    return null
                }
            }
        rvOrderHistory.adapter = orderHistoryAdapter
        rvOrderHistory.layoutManager = LinearLayoutManager(activity!!)
        rvOrderHistory.itemAnimator = DefaultItemAnimator()
        rvOrderHistory.isNestedScrollingEnabled = true
    }

    override fun onResume() {
        super.onResume()
        Common.getCurrentLanguage(activity!!,false)
    }
}