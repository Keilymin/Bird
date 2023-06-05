package com.keilymin.bird.viewmodels

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.keilymin.bird.utility.InternetUtility
import kotlinx.coroutines.launch

class SplashViewModel(application: Application) : AndroidViewModel(application) {

    private val firebaseRemoteConfig : FirebaseRemoteConfig = Firebase.remoteConfig
    private val ref = Firebase.database.getReference("url")

    val remoteConfig: LiveData<Boolean>
        get() = _remoteConfig

    private var _remoteConfig = MutableLiveData<Boolean>()


    var isShowDialog = MutableLiveData(false)

    var url : String? = null

    fun closeDialog() {
        isShowDialog.postValue(false)
    }

    private fun getUrl(){
        if (InternetUtility.isInternetConnected(getApplication<Application>().applicationContext)){
            ref.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val value = snapshot.getValue(String::class.java)
                    url = value
                    _remoteConfig.postValue(true)
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        } else{
            isShowDialog.postValue(true)
        }
    }
   fun getConfig() = viewModelScope.launch{
       val configSettings = remoteConfigSettings {
           minimumFetchIntervalInSeconds = 1
       }
       firebaseRemoteConfig.setConfigSettingsAsync(configSettings)
       firebaseRemoteConfig.fetchAndActivate().addOnCompleteListener { task ->
           val configValue = firebaseRemoteConfig.getBoolean("RemoteConfig")
           if (configValue){
               getUrl()
           }else{
               _remoteConfig.postValue(false)
           }
       }
   }

}