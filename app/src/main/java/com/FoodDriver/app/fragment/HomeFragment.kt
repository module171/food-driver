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
import com.FoodDriver.app.api.*
import com.FoodDriver.app.base.BaseAdaptor
import com.FoodDriver.app.base.BaseFragmnet
import com.FoodDriver.app.model.*
import com.FoodDriver.app.utils.Common
import com.FoodDriver.app.utils.Common.alertErrorOrValidationDialog
import com.FoodDriver.app.utils.Common.dismissLoadingProgress
import com.FoodDriver.app.utils.Common.getDate
import com.FoodDriver.app.utils.Common.showLoadingProgress
import com.FoodDriver.app.utils.SharePreference
import com.FoodDriver.app.utils.SharePreference.Companion.getStringPref
import com.FoodDriver.app.utils.SharePreference.Companion.isCurrancy
import kotlinx.android.synthetic.main.fragment_home.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class HomeFragment : BaseFragmnet() {
    override fun setView(): Int {
        return R.layout.fragment_home
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
        tv_NevProfileName.text="Welcome\n${getStringPref(activity!!,SharePreference.userName)}"
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
        map.put("driver_id",getStringPref(activity!!, SharePreference.userId)!!)
        val call = ApiClient.getClient.setOnGoingOrder(map)
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
                            if(isAdded){
                                rvOutGoingOrder.visibility = View.VISIBLE
                                tvNoDataFound.visibility = View.GONE
                            }
                            val foodCategoryList = restResponce.getData()
                            setFoodCategoryAdaptor(foodCategoryList)
                        } else {
                            rvOutGoingOrder.visibility = View.GONE
                            tvNoDataFound.visibility = View.VISIBLE
                        }
//                        SharePreference.setStringPref(activity!!,isCurrancy,restResponce.getCurrency()!!)
                        tv_CountOrderComplate.text=restResponce.getCompleted_order()
                        tv_CountOrderOutgoing.text=restResponce.getOngoing_order()

                    } else if (restResponce.getStatus().equals("0")) {
                        dismissLoadingProgress()
                        rvOutGoingOrder.visibility = View.GONE
                        tvNoDataFound.visibility = View.VISIBLE
                    }
                }else{
                    val error= JSONObject(response.errorBody()!!.string())
                    if(error.getString("status").equals("2")){
                        dismissLoadingProgress()
                        Common.setLogout(activity!!)
                    }else{
                        dismissLoadingProgress()
                        alertErrorOrValidationDialog(
                            activity!!,
                            error.getString("message")
                        )
                          rvOutGoingOrder.visibility = View.GONE
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
                    tvPrice.text = getStringPref(activity!!, isCurrancy) +String.format(Locale.US,"%.2f",orderHistoryList.get(position).getTotal_price()!!.toDouble())
                    tvQtyNumber.text = orderHistoryList.get(position).getQty()
                    val tvDate: TextView = holder.itemView.findViewById(R.id.tvDate)
                    tvDate.text = getDate(orderHistoryList.get(position).getDate()!!)

                    if(orderHistoryList.get(position).getStatus().equals("3")){
                        tvOrderStatus.text="Order on the way"
                    }else if(orderHistoryList.get(position).getStatus().equals("4")){
                        tvOrderStatus.text="Order Delivered"
                    }

                    if(orderHistoryList.get(position).getPayment_type()!!.toInt()==0){
                        tvPaymentType.text = "Pay by Cash"
                    }else if(orderHistoryList.get(position).getPayment_type()!!.toInt()==1){
                        tvPaymentType.text = "VnPay"
                    }else if(orderHistoryList.get(position).getPayment_type()!!.toInt()==2){
                        tvPaymentType.text = "Stripe"
                    }


                    holder.itemView.setOnClickListener {
                        startActivity(Intent(activity!!, OrderDetailActivity::class.java).putExtra("order_id",orderHistoryList.get(position).getId()).putExtra("status",orderHistoryList.get(position).getStatus())
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
        rvOutGoingOrder.adapter = orderHistoryAdapter
        rvOutGoingOrder.layoutManager = LinearLayoutManager(activity!!)
        rvOutGoingOrder.itemAnimator = DefaultItemAnimator()
        rvOutGoingOrder.isNestedScrollingEnabled = true
    }

    override fun onResume() {
        super.onResume()
        Common.getCurrentLanguage(activity!!,false)
    }

}