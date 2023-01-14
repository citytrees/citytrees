import React from "react";
import 'leaflet/dist/leaflet.css';
import MapComponent from "../../components/Map/Component";

const MainPage = () => {
  return (
      <div>
        <MapComponent
            url="http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
            attributionControl={false}
            style={{height: "85vh"}}
            center={[56.8, 60.6]}
            zoom={10}
            minZoom={10}
            maxZoom={19}
            scrollWheelZoom={true}
            doubleClickZoom={false}
        />
      </div>
  )
}

export default MainPage;
