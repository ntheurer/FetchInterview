package com.fetchinterview.app.data

import retrofit2.Response
import retrofit2.http.GET

interface HiringService {
    @GET("/hiring.json")
    suspend fun fetchHiringCandidatesFromServer(): Response<List<Candidate>>
}
