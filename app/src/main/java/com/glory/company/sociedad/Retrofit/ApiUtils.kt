package com.glory.company.sociedad.Retrofit

import com.glory.company.sociedad.constans


object ApiUtils {
    private fun ApiUtils() {}

    fun getAPIService(): ApiServices? {
        return ApiAdapter.getClient(constans.URL_API)!!.create(ApiServices::class.java)
    }

}