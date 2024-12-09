package com.spotify.oauth2.tests;

import com.spotify.oauth2.api.StatusCode;
import com.spotify.oauth2.api.applicationApi.PlaylistApi;
import com.spotify.oauth2.pojo.Error;
import com.spotify.oauth2.pojo.Playlist;
import com.spotify.oauth2.utils.DataLoader;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static com.spotify.oauth2.utils.FakerUtils.generateDescription;
import static com.spotify.oauth2.utils.FakerUtils.generateName;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;


@Epic("Spotify Oauth 2.0")
@Feature("Playlist API")
public class PlaylistTests extends BaseTest{

    @Story("Create a playlist story")
    @Link("https://example.org")
    @Link(name="allure", type="mylink")
    @TmsLink("12345")
    @Issue("890456")
    @Description("This test case is creating a playlist")
    @Test(description = "This test is used to create a playlist")
    public void ShouldBeAbleToCreateAPlaylist(){

        Playlist requestPlaylist = playlistBuilder(generateName(), generateDescription(), false);
        Response response = PlaylistApi.post(requestPlaylist);
        assertStatusCode(response.statusCode(), StatusCode.CODE_201.code);
        assertPlaylistEqual(response.as(Playlist.class), requestPlaylist );

    }

 //   @Test(description = "This test is used to get a playlist once it has been created")
//    public void ShouldBeAbleToGetAPlaylist(){
//
//        Playlist requestPlaylist = playlistBuilder("Hubba Bubba is updated", "Awesome Chewing Gum WIth" +
//                " a Nice Song is also updated !", true);
//
//        Response response = PlaylistApi.get(DataLoader.getInstance().getPlayListId());
//        assertStatusCode(response.statusCode(), StatusCode.CODE_200.code);
//        assertPlaylistEqual(response.as(Playlist.class), requestPlaylist);
//    }

    @Test(description = "This test is used to update a playlist once it has been created")
    public void ShouldBeAbleToUpdateAPlaylist(){

        Playlist requestPlaylist = playlistBuilder(generateName(), generateDescription(), false);
        Response response = PlaylistApi.update(requestPlaylist, DataLoader.getInstance().getPlayListId());
        assertStatusCode(response.statusCode(), StatusCode.CODE_200.code);

    }



    @Story("Create a playlist story")
    @Test(description = "This test is used to create a playlist without name and error exception should be generated")
    public void ShouldNotBeAbleToCreateAPlaylistWithoutName(){

        Playlist requestPlaylist = playlistBuilder("", generateDescription(), false);

        Response response = PlaylistApi.post(requestPlaylist);
        assertThat(response.statusCode(), equalTo(400));
        assertError(response.as(Error.class), StatusCode.CODE_400.code, StatusCode.CODE_400.msg);
    }


    @Story("Create a playlist story")
    @Test(description = "This test is used to create a playlist and error should be generated due to incorrect" +
            "access token")
    public void ShouldNotBeAbleToCreateAPlaylistDueToWrongAccessToken(){

        String invalid_token = "12345";
        Playlist requestPlaylist = playlistBuilder(generateName(), generateDescription(), false);

        Response response = PlaylistApi.post(invalid_token,requestPlaylist);
        assertThat(response.statusCode(), equalTo(401));
       // assertError(response.as(Error.class), 400, "Invalid access token");
    }

    @Step
    public Playlist playlistBuilder(String name, String description, boolean _public) {

        return Playlist.builder()
                .name(name).
                description(description).
                _public(_public).
                build();
    }

    @Step
    public void assertPlaylistEqual(Playlist responsePlaylist, Playlist requestPlaylist){

        assertThat(responsePlaylist.getName(), equalTo(requestPlaylist.getName()));
        assertThat(responsePlaylist.getDescription(), equalTo(requestPlaylist.getDescription()));
        //assertThat(responsePlaylist.get_public(), equalTo(requestPlaylist.get_public()));
    }

    @Step
    public void assertStatusCode(int actualStatusCode, int expectedStatusCode){
        assertThat(actualStatusCode, equalTo(expectedStatusCode));
    }

    @Step
    public void assertError(Error responseErr, int expectedStatusCode, String expectedMsg)
    {
        assertThat(responseErr.getError().getStatus(), equalTo(400));
        assertThat(responseErr.getError().getMessage(), equalTo("Missing required field: name"));
    }
}
