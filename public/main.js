function getUser(userData) {
    if (userData.name === undefined) {
        console.log("NOT LOGGED IN");
        $("#login").show();
    }
    else {
        console.log("LOGGED IN");
        $("#logout").show();
        $("#username").text(userData.name);
        $("#upload").show();
        $("#photos").show();

        $.get("/photos", getPhotos);

        $.get("/public-photos", getPhotos);

        setInterval(function () {
            $.get("/deletePhotos", deletePhotos);
        }, 1000);
    }
}

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

function deletePhotos(photosData) {
    console.log(photosData);
    if (photosData.length > 0) {
        for (var i in photosData) {
            $("#" + photosData[i]).remove();
        }
    }
}


console.log("BEFORE AJAX");
$.get("/user", getUser);
console.log("AFTER AJAX");