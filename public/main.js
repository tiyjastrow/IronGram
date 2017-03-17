/**
 * Created by BHarris on 3/15/17.
 */
function getUser(userData){
    if(userData.name === undefined){
        $("#login").show();
    } else {
        $("#logout").show();
        $("#name").text("Howdy, " + userData.name);
        $("#upload").show();
        $.get("/photos", getPhotos);
    }
}

$.get("/user", getUser);

function getPhotos(photosData){
    for(var i in photosData) {
        var photo = photosData[i];
        var elem = $("<img>");
        elem.attr("src", photo.filename);
        elem.attr("id", photosData[i].id);
        elem.attr("value",photosData.time);
        $("#photos").append(elem);
        var id = $("<img>");
    }
}


/*unload*/
var decrementCounter = setInterval(function() {
    $( "div" ).children('img').each(function( index ) {
        $.post("/decrement-photo", {id: this.id}, function(data){
            if(data <= 0) {
                window.location.href = "/";
            };
        });
    });
}, 1000);

