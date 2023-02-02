import {divIcon, Icon} from "leaflet";
import {TreeStatus} from "../../../generated/openapi";

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

const treeIconColor = (status: TreeStatus) => {
    if (status === TreeStatus.New) {
        return 'rgba(134,198,255,0.6)'
    } else if (status === TreeStatus.ToApprove) {
        return 'rgba(255,240,150,0.6)'
    } else if (status === TreeStatus.Approved) {
        return 'rgba(110,204,57,0.6)'
    }
}
export const treeIcon = (status: TreeStatus) => divIcon({
    html: `<div style="
                width: 15px;
                height: 15px;
                position: relative;
                top: -3px;
                left: 3px;
                border: 1px solid darkgreen;
                border-radius: 20px;
                background-color: ${treeIconColor(status)};
            "></div>`,
    className: "circle-div-icon",
    iconSize: [15, 15],
    popupAnchor: [4, 0]
})

export const newTreeIcon = new Icon({
    iconSize: [25, 41],
    iconAnchor: [10, 41],
    popupAnchor: [2, -40],
    iconUrl: "https://unpkg.com/leaflet@1.6/dist/images/marker-icon.png",
    shadowUrl: "https://unpkg.com/leaflet@1.6/dist/images/marker-shadow.png"
})