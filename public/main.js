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
    }
}
//need this to run a function
$.get("/user",getUser);

// loop through and get photos
//definition of a function
function getPhotos(photosData) {
    for (var i in photosData) {
        var photo = photosData[i];
        var elem = $("<img>");
        elem.attr("src", photo.filename)
        $("#photos").append(elem);
    }
}
// need this to run function
    // connects to HTML
$.get("/photos", getPhotos);
