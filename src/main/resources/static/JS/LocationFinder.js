function initAutocomplete() {
    const input = document.getElementById('location');
    const autocomplete = new google.maps.places.Autocomplete(input, {
        types: ['(cities)'],
    });

    autocomplete.addListener('place_changed', function() {
        const place = autocomplete.getPlace();
        if (place.geometry) {
            const lat = place.geometry.location.lat();
            const lng = place.geometry.location.lng();
            console.log(`Latitude: ${lat}, Longitude: ${lng}`);
            // You can send these coordinates to your server or use them in your form
        }
    });
}

document.addEventListener('DOMContentLoaded', function() {
    initAutocomplete();
});