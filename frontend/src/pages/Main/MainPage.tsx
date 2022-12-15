import React from "react";
import 'leaflet/dist/leaflet.css';
import MapComponent from "../../components/Map";

function MainPage() {
  return (
      <MapComponent
          centerLat={56.8}
          centerLng={60.6}
          initialZoom={10}
          minZoom={10}
      ></MapComponent>
  )
}

export default MainPage;