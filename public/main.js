/**
 * Created by halleyfroeb on 10/12/16.
 */


function getUser(userData){
    if (userData.name === undefined) {
        $("#login").show();
    }
    else{
        $("#logout").show();
        $("#upload").show();
        $("#public-photos").show();
    }
}
$.get("/user",getUser);

function getPhotos(photosData) {
    for (var i in photosData) {
        var photo = photosData[i];
        var elem = $("<img>");
        elem.attr("src", photo.filename);
        $("#photos").append(elem);
        var delay = (photo.seconds * 1000);
        setInterval(($.post("/delete", {"id":photo.id})), delay);
    }
}
$.get("/photos", getPhotos);

function setImageHidden(photo) {
    var img = photo;
    img.style.visibility = 'hidden';

}
function getSharedPhotos(photosData){
    for (var i in photosData){
        var photo = photosData[i];
        var elem = $("<img>");
        elem.attr("src", photo.filename);
        $("#public-photos").append(elem);
        var delay = (photo.seconds * 1000);
        setInterval(setImageHidden(photo), delay);
    }
}
$.get("/public-photos", getSharedPhotos);




