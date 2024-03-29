package com.betkey.network.models

import com.google.gson.annotations.SerializedName

class TokenObject (
    @SerializedName("token")
    var token: String = "",

    @SerializedName("token_expires")
    var tokenExpires: Long = 0
) : MStatus()