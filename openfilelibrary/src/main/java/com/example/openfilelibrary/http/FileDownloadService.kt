package com.example.openfilelibrary.http

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url


/**
 * @author zyju
 * @date 2024/9/10 16:42
 */
interface FileDownloadService {
    @Streaming
    @GET
    fun downloadFile(@Url url: String?): Call<ResponseBody>?
}
