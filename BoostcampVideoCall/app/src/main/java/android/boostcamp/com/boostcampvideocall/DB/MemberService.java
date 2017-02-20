package android.boostcamp.com.boostcampvideocall.db;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Jusung on 2017. 2. 17..
 */

public interface MemberService {
    @GET("/boostcampSelectAll")
    Call<List<Member>> listMember();
}
