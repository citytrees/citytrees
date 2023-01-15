import {divIcon, Icon} from "leaflet";

export const clusterIcon = (count: number) => divIcon({
    html: `<div style="
                width: 30px;
                height: 30px;
                border-radius: 20px;
                background-color:rgba(110,204,57,0.6);
                text-align: center;
                font-size: 12px;
            "><span style=" line-height: 30px;">${count}</span></div>`,
    className: "circle-div-icon",
    iconSize: [30, 30]
})

export const treeIcon = () => divIcon({
    html: `<div style="
                width: 15px;
                height: 15px;
                border: 1px solid darkgreen;
                border-radius: 20px;
                background-color:rgba(110,204,57,0.6);
            "></div>`,
    className: "circle-div-icon",
    iconSize: [15, 15],
    popupAnchor: [1, 0]
})

export const newTreeIcon = new Icon({
    iconSize: [25, 41],
    iconAnchor: [10, 41],
    popupAnchor: [2, -40],
    iconUrl: "https://unpkg.com/leaflet@1.6/dist/images/marker-icon.png",
    shadowUrl: "https://unpkg.com/leaflet@1.6/dist/images/marker-shadow.png"
})