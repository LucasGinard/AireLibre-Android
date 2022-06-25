package com.lucasginard.airelibre.modules.about.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lucasginard.airelibre.modules.about.domain.AboutRepository
import com.lucasginard.airelibre.modules.about.model.Contributor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AboutViewModel @Inject constructor(private val repositoryGitHub: AboutRepository) : ViewModel() {

    val listContributors = MutableLiveData<ArrayList<Contributor>>()

    fun getAllContributors(): MutableLiveData<ArrayList<Contributor>> {

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val repoAire = async {repositoryGitHub.getContributors("melizeche","AireLibre")}.await()
                val reopoLinka = async {repositoryGitHub.getContributors("tchx84","linka")}.await()
                val repoFirmware = async {repositoryGitHub.getContributors("garyservin","linka-firmware")}.await()
                val repoLinkaBot = async {repositoryGitHub.getContributors("melizeche","linkaBot")}.await()
                val repoAqMap = async {repositoryGitHub.getContributors("matiasinsaurralde","aqmap")}.await()
                val repoAndroid = async {repositoryGitHub.getContributors("LucasGinard","AireLibre-Android")}.await()
                if(repoAire.isSuccessful && reopoLinka.isSuccessful
                    && repoFirmware.isSuccessful && repoAqMap.isSuccessful
                    && repoLinkaBot.isSuccessful && repoAndroid.isSuccessful ) {
                    repoAire.body()?.let {repo1->
                        reopoLinka.body()?.let {repo2 ->
                            repoFirmware.body()?.let{repo3 ->
                                repoAqMap.body()?.let{repo4 ->
                                    repoLinkaBot.body()?.let{repo5 ->
                                        repoAndroid.body()?.let{repo6 ->
                                            val listAux = repo1
                                            listAux.addAll(repo2)
                                            listAux.addAll(repo3)
                                            listAux.addAll(repo4)
                                            listAux.addAll(repo5)
                                            listAux.addAll(repo6)
                                            listContributors.value = listAux.distinctBy { it.nameContributor } as ArrayList<Contributor>
                                            Log.d("testReturnList","funciona")
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else{
                    Log.d("testReturnList","emprtyu ;ost -> ")
                }
            } catch (exception: Exception) {
                Log.d("testReturnList","error")
            }
        }
        Log.d("testReturnList","list -> ${listContributors.value?.size ?: "vacio"}")
        return listContributors
    }

}