
function getUser(userData) {
    if (userData.name === undefined) {
        $('#login').show();
    }
    else {
        $('#logout').show();
        $('#upload').show();
        var elem = $('<a>');
        elem.attr("href", "public/" + userData.name);
        elem.text("View public photo data.");
        $('#photos').append(elem);
        $('#photos').append($('<br>'));

    }
}

function getPhotos(photosData) {
    var delay = 0;
    photosData.forEach(function(i) {
        setTimeout(function(){nextPhoto(i)}, delay);
        delay += (i.secondsToDelete * 1000);
        setTimeout(function(){$.post('/delete', {"id":i.id})}, delay);
        setTimeout(delayedDelete, delay);
    });
}

function nextPhoto(photo) {
    var elem = $('<img>');
    elem.attr("src", photo.filename);
    elem.attr("id", "thisPhoto");
    $('#photos').append(elem);
    $('#photos').append($('<br>'));
}

function delayedDelete() {
    $('#thisPhoto').remove();
    $('<br>').remove();
}

$.get('/user', getUser);
$.get('/photos', getPhotos);
