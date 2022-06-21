package com.FoodDriver.app.activity
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.FoodDriver.app.R
import com.FoodDriver.app.api.ApiClient
import com.FoodDriver.app.api.RestOrderDetailResponse
import com.FoodDriver.app.api.SingleResponse
import com.FoodDriver.app.base.BaseActivity
import com.FoodDriver.app.base.BaseAdaptor
import com.FoodDriver.app.model.OrderDetailModel
import com.FoodDriver.app.utils.Common
import com.FoodDriver.app.utils.Common.showLoadingProgress
import com.FoodDriver.app.utils.SharePreference.Companion.getStringPref
import com.FoodDriver.app.utils.SharePreference.Companion.isCurrancy
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_orderdetail.*
import kotlinx.android.synthetic.main.row_orderitemsummary.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.HashMap
class OrderDetailActivity:BaseActivity() {
    override fun setLayout(): Int {
        return R.layout.activity_orderdetail
    }

    override fun InitView() {
        Common.getCurrentLanguage(this@OrderDetailActivity, false)
        if(Common.isCheckNetwork(this@OrderDetailActivity)){
            callApiOrderDetail()
        }else{
            Common.alertErrorOrValidationDialog(
                this@OrderDetailActivity,
                resources.getString(R.string.no_internet)
            )
        }
        ivBack.setOnClickListener {
            finish()
        }
    }

    override fun onBackPressed() {
        finish()
    }

    private fun callApiOrderDetail() {
        showLoadingProgress(this@OrderDetailActivity)
        val map = HashMap<String, String>()
        map.put("order_id",intent.getStringExtra("order_id")!!)
        val call = ApiClient.getClient.setgetOrderDetail(map)
        call.enqueue(object : Callback<RestOrderDetailResponse> {
            override fun onResponse(
                call: Call<RestOrderDetailResponse>,
                response: Response<RestOrderDetailResponse>
            ) {
                if (response.code() == 200) {
                    Common.dismissLoadingProgress()
                    val restResponce: RestOrderDetailResponse = response.body()!!
                    if (restResponce.getStatus().equals("1")) {
                        if (restResponce.getData().size > 0) {
                            rvOrderItemFood.visibility = View.VISIBLE
                            setFoodDetailData(restResponce)
                        } else {
                            rvOrderItemFood.visibility = View.GONE
                        }
                    } else if (restResponce.getStatus().equals("0")) {
                        Common.dismissLoadingProgress()
                        rvOrderItemFood.visibility = View.GONE
                    }
                }
            }

            override fun onFailure(call: Call<RestOrderDetailResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@OrderDetailActivity,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun setFoodDetailData(response: RestOrderDetailResponse) {

        if(response.getData().size>0){
            setFoodCategoryAdaptor(response.getData())
        }
        if(response.getSummery()!!.getOrder_notes()==null){
            tvNotes.text=""
            cvOrderNote.visibility=View.GONE
        }else{
            cvOrderNote.visibility=View.VISIBLE
            tvNotes.text=response.getSummery()!!.getOrder_notes()
        }
        if(response.getSummery()!!.getPromocode()==null){
            rlDiscount.visibility=View.GONE
            tvOrderTotalPrice.text=getStringPref(this@OrderDetailActivity,isCurrancy)+String.format(Locale.US,"%.02f",response.getSummery()!!.getOrder_total()!!.toDouble())
            tvOrderTaxPrice.text=getStringPref(this@OrderDetailActivity,isCurrancy)+String.format(Locale.US,"%.02f",response.getSummery()!!.getTax()!!.toDouble())
            tvOrderDeliveryCharge.text=getStringPref(this@OrderDetailActivity,isCurrancy)+String.format(Locale.US,"%.02f",response.getSummery()!!.getDelivery_charge()!!.toDouble())

            val getTex:Float=(response.getSummery()!!.getOrder_total()!!.toFloat()*response.getSummery()!!.getTax()!!.toFloat())/100.toFloat()
            tvTitleTex.text="Tax (${response.getSummery()!!.getTax()}%)"
            tvOrderTaxPrice.text=getStringPref(this@OrderDetailActivity,isCurrancy)+String.format(Locale.US,"%.02f", getTex)
            val totalprice=response.getSummery()!!.getOrder_total()!!.toFloat()+getTex+response.getSummery()!!.getDelivery_charge()!!.toFloat()
            tvOrderTotalCharge.text=getStringPref(this@OrderDetailActivity,isCurrancy)+String.format(Locale.US,"%.02f", totalprice)
        }else {
            rlDiscount.visibility = View.VISIBLE
            tvOrderTotalPrice.text =
                  String.format(
                    Locale.US,
                    "%.02f",
                    response.getSummery()!!.getOrder_total()!!.toDouble()
                )+getStringPref(this@OrderDetailActivity, isCurrancy)
            tvOrderTaxPrice.text =
                String.format(
                    Locale.US,
                    "%.02f",
                    response.getSummery()!!.getTax()!!.toDouble()
                )+ getStringPref(this@OrderDetailActivity, isCurrancy)
            tvOrderDeliveryCharge.text =
                String.format(
                    Locale.US,
                    "%.02f",
                    response.getSummery()!!.getDelivery_charge()!!.toDouble()
                )+ getStringPref(this@OrderDetailActivity, isCurrancy)

            val getTex: Float =
                (response.getSummery()!!.getOrder_total()!!.toFloat() * response.getSummery()!!
                    .getTax()!!.toFloat()) / 100
            tvTitleTex.text = "Tax (${response.getSummery()!!.getTax()}%)"
            tvOrderTaxPrice.text =
                 String.format(
                    Locale.US,
                    "%.02f",
                    getTex
                ) + getStringPref(this@OrderDetailActivity, isCurrancy)

            tvDiscountOffer.text =
                " - "  +String.format(
                    Locale.US,
                    "%.02f",
                    response.getSummery()!!.getDiscount_amount()!!.toFloat()
                )+ getStringPref(this@OrderDetailActivity, isCurrancy)
            tvPromocode.text= response.getSummery()!!.getPromocode()

            val subtotal =
                response.getSummery()!!.getOrder_total()!!.toFloat() - response.getSummery()!!
                    .getDiscount_amount()!!.toFloat()
            val totalprice =
                subtotal + getTex + response.getSummery()!!.getDelivery_charge()!!.toFloat()
            tvOrderTotalCharge.text =
                 String.format(
                    Locale.US, "%.02f", totalprice
                ) + getStringPref(this@OrderDetailActivity, isCurrancy)
        }

        Glide.with(this@OrderDetailActivity).load(response.getProfile_image()).placeholder(ResourcesCompat.getDrawable(resources,R.drawable.ic_placeholder,null)).into(ivUserDetail!!)
        tvUserAddress.text=response.getDelivery_address()
        tvUserName.text=response.getName()

        llCall.setOnClickListener {
            if(response.getMobile()!=null){
                val call: Uri = Uri.parse("tel:${response.getMobile()}")
                val surf = Intent(Intent.ACTION_DIAL, call)
                startActivity(surf)
            }
        }

        llViewMap.setOnClickListener {
            if(response.getLang()!=null && response.getLat()!=null){
                val urlAddress = "http://maps.google.com/maps?q=" + response.getLat() + "," + response.getLang() + "(" + "FoodApp" + ")&iwloc=A&hl=es"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlAddress))
                startActivity(intent)
                Common.getLog("apimap","here")
            }else{
                Common.getLog("apimap","not")
            }
        }

        if(intent.getStringExtra("status").equals("3")){
            tvDeliverd.visibility=View.VISIBLE
        }else{
            tvDeliverd.visibility=View.GONE
        }

        tvDeliverd.setOnClickListener {
            if(Common.isCheckNetwork(this@OrderDetailActivity)){
                callApiOrderDeliver()
            }else{
                Common.alertErrorOrValidationDialog(
                    this@OrderDetailActivity,
                    resources.getString(R.string.no_internet)
                )
            }
        }
    }

    fun setFoodCategoryAdaptor(orderHistoryList: ArrayList<OrderDetailModel>) {
        val orderHistoryAdapter = object : BaseAdaptor<OrderDetailModel>(this@OrderDetailActivity, orderHistoryList) {
            @SuppressLint("SetTextI18n", "NewApi")
            override fun onBindData(
                holder: RecyclerView.ViewHolder?,
                `val`: OrderDetailModel,
                position: Int
            ) {
                val ivFoodItem: ImageView = holder!!.itemView.findViewById(R.id.ivFoodCart)
                val tvOrderFoodName: TextView = holder.itemView.findViewById(R.id.tvFoodName)
                val tvPrice: TextView = holder.itemView.findViewById(R.id.tvPrice)
                val tvQtyNumber: TextView = holder.itemView.findViewById(R.id.tvQtyPrice)
                val tvAddons: TextView = holder.itemView.findViewById(R.id.tvAddons)
                val tvNotes: TextView = holder.itemView.findViewById(R.id.tvNotes)

                tvOrderFoodName.text =orderHistoryList.get(position).getItem_name()
                tvPrice.text = String.format(Locale.US,"%.2f",orderHistoryList.get(position).getTotal_price()!!.toDouble())+getStringPref(this@OrderDetailActivity,isCurrancy)
                tvQtyNumber.text ="QTY : ${orderHistoryList.get(position).getQty()}"
                if(orderHistoryList.get(position).getAddons().size>0){
                    tvAddons.backgroundTintList= ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.colorPrimary,null))
                }else{
                    tvAddons.backgroundTintList=
                        ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.gray,null))
                }
                if(orderHistoryList.get(position).getItem_notes()==null){
                    tvNotes.backgroundTintList= ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.gray,null))
                }else{
                    tvNotes.backgroundTintList= ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.colorPrimary,null))
                }

                holder.itemView.tvAddons.setOnClickListener {
                    if(orderHistoryList.get(position).getAddons().size>0){
                        Common.openDialogSelectedAddons(this@OrderDetailActivity,orderHistoryList.get(position).getAddons())
                    }
                }

                holder.itemView.tvNotes.setOnClickListener {
                    if(orderHistoryList.get(position).getItem_notes()!=null){
                        Common.alertNotesDialog(this@OrderDetailActivity,orderHistoryList.get(position).getItem_notes())
                    }
                }

                Glide.with(this@OrderDetailActivity).load(orderHistoryList.get(position).getItemimage().getImage())
                    .placeholder(ResourcesCompat.getDrawable(resources,R.drawable.placeholder,null)).centerCrop()
                    .into(ivFoodItem)
            }
            override fun setItemLayout(): Int {
                return R.layout.row_orderitemsummary
            }
            override fun setNoDataView(): TextView? {
                return null
            }
        }
        rvOrderItemFood.adapter = orderHistoryAdapter
        rvOrderItemFood.layoutManager = LinearLayoutManager(this@OrderDetailActivity)
        rvOrderItemFood.itemAnimator = DefaultItemAnimator()
        rvOrderItemFood.isNestedScrollingEnabled = true
    }

    private fun callApiOrderDeliver() {
        showLoadingProgress(this@OrderDetailActivity)
        val map = HashMap<String, String>()
        map.put("order_id",intent.getStringExtra("order_id")!!)
        val call = ApiClient.getClient.setOrderDeliver(map)
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                if (response.code() == 200) {
                    Common.dismissLoadingProgress()
                    val restResponce: SingleResponse = response.body()!!
                    if (restResponce.getStatus().equals("1")) {
                        successfulDialog(this@OrderDetailActivity, restResponce.getMessage())
                    } else if (restResponce.getStatus().equals("0")) {
                        Common.dismissLoadingProgress()
                        rvOrderItemFood.visibility = View.GONE
                    }
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@OrderDetailActivity,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    fun successfulDialog(act: Activity, msg: String?) {
        var dialog: Dialog? = null
        try {
            if (dialog != null) {
                dialog.dismiss()
                dialog = null
            }
            dialog = Dialog(act, R.style.AppCompatAlertDialogStyleBig)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            );
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(false)
            val m_inflater = LayoutInflater.from(act)
            val m_view = m_inflater.inflate(R.layout.dlg_validation, null, false)
            val textDesc: TextView = m_view.findViewById(R.id.tvMessage)
            textDesc.text = msg
            val tvOk: TextView = m_view.findViewById(R.id.tvOk)
            val finalDialog: Dialog = dialog
            tvOk.setOnClickListener {
                finalDialog.dismiss()
                startActivity(Intent(this@OrderDetailActivity,DashboardActivity::class.java).putExtra("pos","2"))
                finish()
                finishAffinity()
            }
            dialog.setContentView(m_view)
            dialog.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        Common.getCurrentLanguage(this@OrderDetailActivity, false)
    }
}