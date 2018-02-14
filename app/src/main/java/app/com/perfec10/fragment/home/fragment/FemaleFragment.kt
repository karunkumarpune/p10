package app.com.perfec10.fragment.home.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import app.com.perfec10.R

/**
 * Created by fluper-pc on 14/2/18.
 */
class FemaleFragment :Fragment(){
   
   lateinit var ll_bodyview_stats:LinearLayout
   
   companion object {
      
      fun FemaleInstance(): FemaleFragment {
         return FemaleFragment()
      }
      
   }
   
   override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
     
      val v=inflater.inflate(R.layout.tab_fragment1,container,false)
   
      ll_bodyview_stats=v.findViewById(R.id.ll_bodyview_stats)
   
      ll_bodyview_stats.setBackgroundResource(R.mipmap.girl_front);
      
      return  v
   }
   
}
