package com.lucasginard.airelibre.modules.home.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lucasginard.airelibre.modules.home.domain.HomeRepository
import com.lucasginard.airelibre.modules.home.model.CityListResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel constructor(private val repository: HomeRepository) : ViewModel() {

    val getListCitys = MutableLiveData<ArrayList<CityListResponse>>()
    val errorMessage = MutableLiveData<String>()

    fun getAllCity() {
        val response = repository.getAllCitys()
        response.enqueue(object : Callback<ArrayList<CityListResponse>> {
            override fun onResponse(
                call: Call<ArrayList<CityListResponse>>,
                response: Response<ArrayList<CityListResponse>>
            ) {
                getListCitys.postValue(response.body())
            }

            override fun onFailure(call: Call<ArrayList<CityListResponse>>, t: Throwable) {
                errorMessage.postValue(t.message)
            }
        })
    }
}