package app.com.perfec10.fragment.login

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.CheckBox
import app.com.perfec10.R
import app.com.perfec10.fragment.profile.Profile.mainActivity
import app.com.perfec10.network.NetworkConstants.acceptTermsAndConditions
import app.com.perfec10.util.PreferenceManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import kotlinx.android.synthetic.main.activity_term_coditions.*
import org.json.JSONObject


class TermCoditionsActivity : AppCompatActivity() {
   
   
   val url="http://18.217.249.143/perfec10/termCondition/term.pdf"
   
   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContentView(R.layout.activity_term_coditions)
      
      pdfView.isZooming
      pdfView.fromAsset("term.pdf")
       .load()
   
   
      val addFriendAllowDialoge = AddFriendAllowDialoges(this@TermCoditionsActivity, "email")
      addFriendAllowDialoge.show(this@TermCoditionsActivity.supportFragmentManager, "fsdf")
   
   
      Log.d("TAHS"," check_accept.isChecked "+check_accept.isClickable)
   
      check_accept.setOnClickListener {v->
         if ((v as CheckBox).isChecked) {
         btn_next.setBackgroundColor(ContextCompat.getColor(this,R.color.colorAccent))
         btn_next.isEnabled=true
            btn_next.setOnClickListener {
               initJson();
            }
            
      }else{
         btn_next.setBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimary))
         btn_next.isEnabled=false
      }
      
   }
   }
   
   
   private fun initJson(){
    
    val dilog= ProgressDialog(this)
      dilog.setMessage("Please wait...")
      dilog.setCancelable(true)
      dilog.show()
      val auth = PreferenceManager(this).userAuthkey
      AndroidNetworking.post(acceptTermsAndConditions).addHeaders("accessToken",auth)
       .build()
       .getAsJSONObject(object: JSONObjectRequestListener {
          override fun onResponse(response: JSONObject?) {
             
             Log.d("TAGS",response!!.toString())
             
         //    val addFriendAllowDialoge = AddFriendAllowDialoge(mainActivity, "email")
            // addFriendAllowDialoge.show(mainActivity.supportFragmentManager, "fsdf")
   
   
             dilog.dismiss()
          }
   
          override fun onError(anError: ANError?) {
             dilog.dismiss()
          }
       })
   }
 
   
   
   
   
}
