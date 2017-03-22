function getUser(userData) {
    if (userData.name === undefined) {
        $("#login").show();
    }
    else {
        $("#logout").show();
        $("#username").text(userData.name);
        $("#upload").show();
    }
}

$.get("/user", getUser);

function getPhotos(photosData) {
    for (var i in photosData) {
        console.log(photosData[i]);
        var photo = photosData[i];
        var elem = $("<img width=\"320\" >");
        elem.attr("src", photo.filename);
        elem.attr("id", photo.id);
        $("#photos").append(elem);
    }
}

$.get("/photos", getPhotos);

//since receiving list from deletePhotos
function deletePhotos(photosData) {
    console.log(photosData);
    if (photosData.length > 0) {
        for (var i in photosData) {
            $("#" + photosData[i]).remove();
        }
    }
}



$.get("/public-photos", getPhotos);

setInterval(function () {
    $.get("/deletePhotos", deletePhotos);
}, 1000);