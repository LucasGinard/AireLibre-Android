package com.lucasginard.airelibre.modules.home.viewModel

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lucasginard.airelibre.R
import com.lucasginard.airelibre.modules.home.domain.HomeRepository
import com.lucasginard.airelibre.modules.home.model.CardsAQI
import com.lucasginard.airelibre.modules.home.model.CityResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: HomeRepository) : ViewModel() {

    val getListCitys = MutableLiveData<ArrayList<CityResponse>>()
    val errorMessage = MutableLiveData<String>()

    fun getAllCity() {
        val response = repository.getAllCitys()
        response.enqueue(object : Callback<ArrayList<CityResponse>> {
            override fun onResponse(
                call: Call<ArrayList<CityResponse>>,
                response: Response<ArrayList<CityResponse>>
            ) {
                getListCitys.postValue(response.body())
            }

            override fun onFailure(call: Call<ArrayList<CityResponse>>, t: Throwable) {
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

}