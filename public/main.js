/**
 * Created by robculclasure on 3/15/17.
 */

function getUser(userData) {
    if (userData.name === undefined) {
        $("#login").show();
    } else {
        $("#logout").show();
        $("#upload").show();
    }
}

function getPhotos(photosData){
    for (var i in photosData){
        var photo = photosData[i];
        var elem = $("<img>");
        elem.attr("src", photo.filename);
        $("#photos").append(elem);
    }
}

$.get("/photo", getPhotos);
$.get("/user", getUser);