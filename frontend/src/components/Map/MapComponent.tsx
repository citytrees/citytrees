import React, {useEffect, useState} from "react";
import {MapContainer, Marker, Popup, TileLayer, useMap, useMapEvents} from "react-leaflet";
import 'leaflet/dist/leaflet.css';
import {DragEndEvent, Icon} from "leaflet";
import {TreesGetResponseTree} from "../../generated/openapi";
import api from "../../api";
import {Button, notification} from "antd";

const icon = new Icon({
  iconSize: [25, 41],
  iconAnchor: [10, 41],
  popupAnchor: [2, -40],
  iconUrl: "https://unpkg.com/leaflet@1.6/dist/images/marker-icon.png",
  shadowUrl: "https://unpkg.com/leaflet@1.6/dist/images/marker-shadow.png"
});

const InvalidateSize = () => {
  useMap().invalidateSize()
  return null
};

class Tree {
  latitude: number;
  longitude: number;

  constructor(latitude: number, longitude: number) {
    this.latitude = latitude;
    this.longitude = longitude;
  }
}

const TreeMap = () => {
  const [updateCount, setUpdateCount] = useState(0) // TODO #18
  const [newTree, setNewTree] = useState<Tree | null>(null)
  const [trees, setTrees] = useState<TreesGetResponseTree[]>([])

  const map = useMapEvents({
    click: (event) => {
      let latlng = event.latlng;
      setNewTree(new Tree(latlng.lat, latlng.lng))
    },
    dragend: () => update(),
    zoomend: () => update()
  });

  const update = () => {
    setUpdateCount(updateCount + 1)
  }

  useEffect(() => {
    notification.open({
      message: `effect`,
      type: "info",
      placement: "top"
    })
    const updateMarkers = () => {
      let bounds = map.getBounds()
      let northWest = bounds.getNorthWest()
      let southEast = bounds.getSouthEast()
      api.trees.loadTreesByRegion({
        treesByRegionRequest: {
          x1: northWest.lat,
          y1: northWest.lng,
          x2: southEast.lat,
          y2: southEast.lng
        }
      }).then((responseTrees) => {
        setTrees(responseTrees)
        map.closePopup()
      })
    }
    updateMarkers()
  }, [map, updateCount])

  return (
      <div>
        {trees.map((tree, index) => {
          return <Marker key={`tree${index}`} icon={icon} position={[tree.latitude, tree.longitude]}>
            <Popup>
              <span>[{tree.latitude}, {tree.longitude}]</span>
              <Button
                  onClick={() =>
                      api.tree.deleteTree({id: tree.id}).then(() => update())
                  }
              >
                Delete
              </Button>
            </Popup>
          </Marker>;
        })}
        {newTree ?
            <Marker
                icon={icon}
                position={[newTree.latitude, newTree.longitude]}
                draggable={true}
                eventHandlers={{
                  dragend: (event: DragEndEvent) => {
                    let latLng = event.target.getLatLng()
                    setNewTree(new Tree(latLng.lat, latLng.lng))
                  }
                }}>
              <Popup>
                <span>[{newTree.latitude}, {newTree.longitude}]</span>
                <Button
                    onClick={() =>
                        newTree
                            ? api.tree.createTree({treeCreateRequest: {latitude: newTree.latitude, longitude: newTree.longitude}})
                                .then(() => {
                                  debugger
                                  setNewTree(null)
                                  update()
                                })
                            : null}
                >
                  Add
                </Button>
              </Popup>
            </Marker>
            : null}
      </div>
  )
}

const MapComponent = (
    {
      centerLat,
      centerLng,
      initialZoom,
      minZoom,
    }: {
      centerLat: number,
      centerLng: number,
      initialZoom: number,
      minZoom: number
    }
) => {

  return (
      <div>
        <MapContainer
            style={{height: "90vh"}}
            center={[centerLat, centerLng]}
            zoom={initialZoom}
            minZoom={minZoom}
            scrollWheelZoom={true}
            doubleClickZoom={false}
        >
          <TileLayer url="http://{s}.tile.osm.org/{z}/{x}/{y}.png"/>
          <InvalidateSize/>
          <TreeMap/>
        </MapContainer>
      </div>
  )
}

export default MapComponent;