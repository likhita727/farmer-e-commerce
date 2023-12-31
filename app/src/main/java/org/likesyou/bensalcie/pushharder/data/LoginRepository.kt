package org.likesyou.bensalcie.pushharder.data

import org.likesyou.bensalcie.pushharder.data.model.LoggedInUser

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
class LoginRepository // private constructor : singleton access
private constructor(private val dataSource: LoginDataSource) {
    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private var user: LoggedInUser? = null
    val isLoggedIn: Boolean
        get() = user != null

    fun logout() {
        user = null
        dataSource.logout()
    }

    private fun setLoggedInUser(user: LoggedInUser?) {
        this.user = user
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }

    fun login(username: String?, password: String?): Result<LoggedInUser?>? {
        // handle login
        val result = dataSource.login(username, password)
        if (result is Result.Success<*>) {
            setLoggedInUser((result as Result.Success<LoggedInUser?>).data)
        }
        return result
    }

    companion object {
        @Volatile
        private var instance: LoginRepository? = null
        fun getInstance(dataSource: LoginDataSource): LoginRepository? {
            if (instance == null) {
                instance = LoginRepository(dataSource)
            }
            return instance
        }
    }
}