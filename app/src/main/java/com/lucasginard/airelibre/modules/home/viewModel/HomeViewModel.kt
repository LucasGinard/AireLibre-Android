package com.lucasginard.airelibre.modules.home.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lucasginard.airelibre.modules.home.domain.HomeRepository
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


}