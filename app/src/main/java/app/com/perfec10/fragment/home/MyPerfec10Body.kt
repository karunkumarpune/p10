package app.com.perfec10.fragment.home

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import app.com.perfec10.R
import app.com.perfec10.fragment.home.fragment.FemaleFragment.Companion.FemaleInstance
import app.com.perfec10.fragment.home.fragment.MaleFragment.Companion.MaleInstance
import kotlinx.android.synthetic.main.activity_my_perfec10_body.*

class MyPerfec10Body : AppCompatActivity() {
   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContentView(R.layout.activity_my_perfec10_body)
   
      view_tab1.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
      view_tab2.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent))
    
   
      supportFragmentManager.beginTransaction()
       .replace(R.id.frame,FemaleInstance())
       .commit()
      
      
      btn_female.setOnClickListener {
         view_tab1.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
         view_tab2.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent))
   
         supportFragmentManager.beginTransaction()
          .replace(R.id.frame,FemaleInstance())
          .commit()
      }
      
      btn_male.setOnClickListener {
           view_tab1.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent))
         view_tab2.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
   
         supportFragmentManager.beginTransaction()
          .replace(R.id.frame,MaleInstance())
          .commit()
      }
   
      iv_close_stats.setOnClickListener {
         onBackPressed()
      }
   
   }
}
