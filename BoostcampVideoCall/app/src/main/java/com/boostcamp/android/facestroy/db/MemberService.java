package com.boostcamp.android.facestroy.db;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by Jusung on 2017. 2. 17..
 */

public interface MemberService {
    @GET("/boostcampSelectAll")
    Call<List<Member>> getMembers();
}
