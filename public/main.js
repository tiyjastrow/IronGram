
function getUser(userData) {
    if (userData.name === undefined) {
        $("#login").show();
    }
    else {
        $("#logout").show();
        $("#upload").show();
        $("#pub").show();
    }
}
$.get("/user", getUser);
function getPhotos(photosData) {
    for (var i in photosData) {
        var photo = photosData[i];
        var elem = $("<img>");
        elem.attr("src", photo.filename);
        $("#photos").append(elem);
        var delay = (photo.secondsUntilDelete * 1000);
        setTimeout(($.post("/delete", {"id":photo.id})), delay);
    }
}

$.get("/photos", getPhotos);

