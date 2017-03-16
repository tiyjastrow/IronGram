
function getUser(userData) {
    if (userData.name === undefined) {
        $("#login").show();
    } else {
        $("#logout").show();
        $("#username").text("Welcome, " + userData.name + ".");
        $("#upload").show();
    }
}

function getPhotos(photosData) {
    for (var i in photosData) {
        var photo = photosData[i];
        var elem = $("<img>");
        elem.attr("src", photo.filename);
        $("#photos").append(elem);
    }
}

$.get("/photo", getPhotos);
$.get("/user", getUser);

//setInterval(function(){$.get("/photo", getPhotos)}, 10000);