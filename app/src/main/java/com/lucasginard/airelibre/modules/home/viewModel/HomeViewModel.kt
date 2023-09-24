package com.lucasginard.airelibre.modules.home.viewModel

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.lucasginard.airelibre.R
import com.lucasginard.airelibre.modules.home.domain.HomeRepository
import com.lucasginard.airelibre.modules.home.model.CardsAQI
import com.lucasginard.airelibre.modules.home.model.SensorResponse
import com.lucasginard.airelibre.modules.home.model.StatusResponse
import com.lucasginard.airelibre.utils.ToastCustom
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: HomeRepository) : ViewModel() {

    val getListSensors = MutableLiveData<ArrayList<SensorResponse>>()
    val errorMessage = MutableLiveData<String>()
    val getStatus = MutableLiveData<StatusResponse>()

    lateinit var sensorNotify:SensorResponse

    fun getAllSensors() {
        val response = repository.getAllSensors()
        response.enqueue(object : Callback<ArrayList<SensorResponse>> {
            override fun onResponse(
                call: Call<ArrayList<SensorResponse>>,
                response: Response<ArrayList<SensorResponse>>
            ) {
                val listSensors = response.body()
                listSensors?.let {list ->
                    val listValidateSensorNotifyList = getActiveScheduleAlarmSensor(list)
                    getListSensors.postValue(listValidateSensorNotifyList)
                }
            }

            override fun onFailure(call: Call<ArrayList<SensorResponse>>, t: Throwable) {
                errorMessage.postValue(t.message)
            }
        })
    }

    fun getStatusService(){
        val response = repository.getStatus()
        response.enqueue(object : Callback<StatusResponse> {
            override fun onResponse(
                call: Call<StatusResponse>,
                response: Response<StatusResponse>
            ) {
                getStatus.postValue(response.body())
            }

            override fun onFailure(call: Call<StatusResponse>, t: Throwable) {
                errorMessage.postValue(t.message)
            }
        })
    }

    fun isNotDefaultTheme():Boolean{
        return repository.getIsThemeSave()
    }

    fun getTheme():Boolean{
        return repository.getThemeCustom()
    }

    fun getCards(activity: Activity):ArrayList<CardsAQI>{
        val listCards = ArrayList<CardsAQI>()
        listCards.add(CardsAQI(activity.getString(R.string.textTitleGreen),activity.getString(R.string.tvDescriptionGreen),R.color.cardGreen))
        listCards.add(CardsAQI(activity.getString(R.string.textTitleYellow),activity.getString(R.string.tvDescriptionYellow),R.color.cardYellow))
        listCards.add(CardsAQI(activity.getString(R.string.textTitleOrange),activity.getString(R.string.tvDescriptionOrange),R.color.cardOrange))
        listCards.add(CardsAQI(activity.getString(R.string.textTitleRed),activity.getString(R.string.tvDescriptionRed),R.color.cardRed))
        listCards.add(CardsAQI(activity.getString(R.string.textTitlePurple),activity.getString(R.string.tvDescriptionPurple),R.color.cardPurple))
        listCards.add(CardsAQI(activity.getString(R.string.textTitleDanger),activity.getString(R.string.tvDescriptionDanger),R.color.cardDanger))
        return listCards
    }

    fun getCustomMap():String{
        return repository.getStyleMap()
    }

    fun showReviewForPlayStore(fragment:Fragment){
        val manager = ReviewManagerFactory.create(fragment.requireContext())
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                manager.launchReviewFlow(fragment.requireActivity(),task.result)
            } else {
                fragment.ToastCustom(fragment.requireContext().getString(R.string.toastErrorRetry) ?: "Hubo un problema intente de nuevo")
            }
        }
    }

    private fun getActiveScheduleAlarmSensor(list:ArrayList<SensorResponse>):ArrayList<SensorResponse> {
        val listSchedule = repository.getListScheduledNotifications()
        list.forEach { filterSensor ->
            filterSensor.isEnableNotification = listSchedule.any { it.startsWith(filterSensor.description) }
        }
        return list
    }

    fun authForRealtime(){
        val auth: FirebaseAuth = Firebase.auth
        auth.signInAnonymously()
    }

    fun isActiveScheduleAlarmSensor():Boolean{
        return repository.getListScheduledNotifications().isNotEmpty()
    }
}