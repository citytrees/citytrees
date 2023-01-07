import React from "react";
import 'leaflet/dist/leaflet.css';
import MapComponent from "../../components/Map";

function MainPage() {
  return (
      <div>
        <MapComponent
            url="http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
            style={{height: "90vh"}}
            center={[56.8, 60.6]}
            zoom={10}
            minZoom={10}
            maxZoom={19}
            scrollWheelZoom={true}
            doubleClickZoom={false}
        ></MapComponent>
      </div>
  )
}

export default MainPage;