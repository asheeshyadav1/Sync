const mapContainer = document.getElementById('map-container');

document.addEventListener("DOMContentLoaded", function() {
    var map = L.map(mapContainer).setView([51.505, -0.09], 13); // Initial placeholder location

    L.tileLayer('https://maps.geoapify.com/v1/tile/osm-bright/{z}/{x}/{y}.png?apiKey=1a3450467c924eb6a40d85a2098898bd', {
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(map);

    // Check if the Geolocation API is available
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(function(position) {
            var lat = position.coords.latitude;
            var lon = position.coords.longitude;
            map.setView([lat, lon], 13); // Set the map view to user's location
            //console log the exact coordinates 
            console.log("Your coordinates are: " + lat + ", " + lon);

            // Add a marker at the user's location
            L.marker([lat, lon]).addTo(map)
                .bindPopup('You are here!')
                .openPopup();
        }, function(error) {
            console.log("Geolocation error: " + error.message);
        });
    } else {
        console.log("Geolocation is not supported by this browser.");
    }
});
