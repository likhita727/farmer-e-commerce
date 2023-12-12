package org.likesyou.bensalcie.pushharder.data

import org.likesyou.bensalcie.pushharder.data.model.LoggedInUser
import java.io.IOException
import java.util.UUID

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {
    fun login(username: String?, password: String?): Result<LoggedInUser?> {
        return try {
            // TODO: handle loggedInUser authentication
            val fakeUser = LoggedInUser(
                UUID.randomUUID().toString(),
                "Jane Doe"
            )
            Result.Success<LoggedInUser?>(fakeUser)
        } catch (e: Exception) {
            Result.Error(IOException("Error logging in", e))
        }
    }
//
    fun logout() {
        // TODO: revoke authentication
    }
}

