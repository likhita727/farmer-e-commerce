package org.likesyou.bensalcie.pushharder.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.likesyou.bensalcie.pushharder.data.LoginDataSource
import org.likesyou.bensalcie.pushharder.data.LoginRepository

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
class LoginViewModelFactory : ViewModelProvider.Factory {

    //    override fun <T : ViewModel?> create(modelClass: Class<T>): T & Any {
//        return if (modelClass.isAssignableFrom(LoginViewModel::class.java)) ({
//            LoginViewModel(LoginRepository.Companion.getInstance(LoginDataSource())) as T
//        })!!
//        else {
//            throw IllegalArgumentException("Unknown ViewModel class")
//        }
//    }
    override fun <T : ViewModel?> create(modelClass: Class<T>): T & Any {
        TODO("Not yet implemented")
    }
}