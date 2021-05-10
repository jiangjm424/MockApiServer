package com.grand.mockapiserver.core

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import okhttp3.mockwebserver.MockResponse
import java.io.File

class DefaultMockServer(context: Context, gson: Gson = Gson()) : AbsMockServer(context, gson) {
    override fun configResponse(fileName: String?, count: Int): MockResponse? {
        val file = File(context.getExternalFilesDir(""),
                "mock" + File.separator + fileName
        )
        return respFromFile(file)
    }

    private fun respFromFile(file: File): MockResponse? {
        file.takeIf {
            it.exists()
        }?.also { f ->
            val resp = f.bufferedReader().use { reader -> reader.readText() }

            return MockResponse().setResponseCode(200)
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .addHeader("Cache-Control", "no-cache")
                    .setBody(resp)
        }
        Log.i(TAG, "${file.name} not exit, return null file")
        return null
    }
}