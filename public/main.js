function getPhotos(photosData) {
    for (var i in photosData) {
        var photo = photosData[i];

        var elem = $("<img>");
        var elem2 = $("<br>");
        elem.attr("src", photo.filename);
        elem.attr("id", 'timer');


        var seconds_left = photo.time;
        var interval = setInterval(function () {
            document.getElementById('timer_div').innerHTML = --seconds_left;
            if (seconds_left <= 0) {
                $("#photos").empty();
                clearInterval(interval);
                $.get("/delete-photos");
            }
        }, 1000);

        $("#photos").append(elem).append(elem2);
    }
}


function getUser(userData) {
    if (userData.name === undefined) {
        $("#login").show();
    }
    else {
        $("#logout").show();
        $("#username").text("Welcome, " + userData.name + "!");
        $("#upload").show();
        $("#view").show();
        $("#view").click(function () {
            $("#photos").show();
            $.get("/photos", getPhotos);
        });
    }
}
$.get("/user", getUser);

