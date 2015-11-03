package app.test.com.testapp;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.Callback;
public interface gitapi {
    @GET("/repos/{owner}/{repo}/contributors")      //here is the other url part.best way is to start using /
    public void getFeed(@Path("owner") String owner,
                        @Path("repo") String repo, Callback<List<Contributors>> response);     //string user is for passing values from edittext for eg: user=basil2style,google
    //response is the response from the server which is now in the POJO
}