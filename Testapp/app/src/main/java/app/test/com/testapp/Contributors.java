package app.test.com.testapp;


import com.google.gson.annotations.SerializedName;

public class Contributors {



    @SerializedName("login")
    private String login;

    @SerializedName("avatar_url")
    private String avatar_url;


    @SerializedName("html_url")
    private String html_url;


    public final String get_html_url() {
        return this.html_url;
    }


    public final String get_login() {
        return this.login;
    }

    public final String get_avatar_url() {
        return this.avatar_url;
    }



}