package com.lucasginard.airelibre.modules.home.viewModel

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lucasginard.airelibre.R
import com.lucasginard.airelibre.modules.home.domain.HomeRepository
import com.lucasginard.airelibre.modules.home.model.CardsAQI
import com.lucasginard.airelibre.modules.home.model.CityResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel constructor(private val repository: HomeRepository) : ViewModel() {

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

    fun getFlatTheme():Boolean{
        return repository.getIsThemeSave()
    }

    fun getTheme():Boolean{
        return repository.getThemeCustom()
    }

    fun getCards(activity: Activity):ArrayList<CardsAQI>{
        val listCards = ArrayList<CardsAQI>()
        listCards.add(CardsAQI("0-50 | Libre",activity.getString(R.string.tvDescriptionGreen),R.color.cardGreen))
        listCards.add(CardsAQI("51-100 | Maso",activity.getString(R.string.tvDescriptionYellow),R.color.cardYellow))
        listCards.add(CardsAQI("101-150 | No tan bien",activity.getString(R.string.tvDescriptionOrange),R.color.cardOrange))
        listCards.add(CardsAQI("151-200 | Insalubre",activity.getString(R.string.tvDescriptionRed),R.color.cardRed))
        listCards.add(CardsAQI("201-300 | Muy insalubre",activity.getString(R.string.tvDescriptionPurple),R.color.cardPurple))
        listCards.add(CardsAQI("300+ | Peligroso",activity.getString(R.string.tvDescriptionDanger),R.color.cardDanger))
        return listCards
    }


}