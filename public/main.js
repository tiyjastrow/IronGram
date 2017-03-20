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




// function displayTimer(photosData) {
//
//     for (var i in photosData) {
//         var photo = photosData[i];
//         var seconds_left = photo.time;
//         var interval = setInterval(function () {
//             document.getElementsByTagName('<img>').innerHTML = --seconds_left;
//             if (seconds_left <= 0) {
//                 $("#photos").empty();
//                 clearInterval(interval);
//             }
//         }, 1000);
//     }
// }


// <div id="timer_div"></div>
// //
// var seconds_left = 10;
// var interval = setInterval(function() {
//     document.getElementById('timer_div').innerHTML = --seconds_left;
//
//     if (seconds_left <= 0)
//     {
//         clearInterval(interval);
//     }
// }, 1000);






// setInterval(function () {
//     $.get("/delete-photos");
//     $("#photos").empty();
//     $.get("/photos", getPhotos);
//     $("#photos").show();
//     }, 5000);

