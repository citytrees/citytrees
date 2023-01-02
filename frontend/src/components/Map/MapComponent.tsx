import React, {useEffect, useState} from "react"
import {MapContainer, Marker, Popup, TileLayer, useMap, useMapEvents} from "react-leaflet"
import 'leaflet/dist/leaflet.css'
import {DragEndEvent, Icon, LatLng} from "leaflet"
import {TreesGetResponseTree} from "../../generated/openapi"
import api from "../../api"
import {CtTree, ctTreeOf} from "./Models/CtTree"
import {Button} from "antd";
import CtTreeEditor from "./TreeEditor";
import {MapContainerProps} from "react-leaflet/lib/MapContainer";
import {TileLayerProps} from "react-leaflet/lib/TileLayer";

const icon = new Icon({
  iconSize: [25, 41],
  iconAnchor: [10, 41],
  popupAnchor: [2, -40],
  iconUrl: "https://unpkg.com/leaflet@1.6/dist/images/marker-icon.png",
  shadowUrl: "https://unpkg.com/leaflet@1.6/dist/images/marker-shadow.png"
})

const InvalidateSize = () => {
  useMap().invalidateSize()
  return null
}

const TreeMap = () => {
  const [updateCount, setUpdateCount] = useState(0)
  const [newTree, setNewTree] = useState<CtTree | null>(null)
  const [trees, setTrees] = useState<TreesGetResponseTree[]>([])

  const [isEditorOpen, setIsEditorOpen] = useState(false)
  const [isEditable, setIsEditable] = useState(false)
  const [editorValue, setEditorValue] = useState<CtTree | undefined>()

  const map = useMapEvents({
    click: (event) => {
      let latlng = event.latlng
      setNewTree({latitude: latlng.lat, longitude: latlng.lng})
    },
    dragend: () => triggerUpdate(),
    zoomend: () => triggerUpdate()
  })

  // TODO #18 replace using map events
  const triggerUpdate = () => {
    setUpdateCount(updateCount + 1)
  }

  const onTreeCreate = (tree: CtTree) => {
    api.tree.createTree({
          treeCreateRequest:
              {
                latitude: tree.latitude,
                longitude: tree.longitude
              }
        }
    ).then(() => {
      setIsEditorOpen(false);
      setNewTree(null)
      triggerUpdate()
    })
  }

  useEffect(() => {
    let bounds = map.getBounds()
    let northWest = bounds.getNorthWest()
    let southEast = bounds.getSouthEast()
    api.trees.loadTreesByRegion({
      x1: northWest.lat,
      y1: northWest.lng,
      x2: southEast.lat,
      y2: southEast.lng
    }).then((responseTrees) => {
      setTrees(responseTrees)
    })
  }, [map, updateCount])

  const renderMarker = (id: string, tree: TreesGetResponseTree) =>
      <Marker key={id} icon={icon} position={[tree.latitude, tree.longitude]}>
        <Popup>
          <Button
              type="primary"
              onClick={() => {
                setIsEditable(false)
                setIsEditorOpen(true)
                setEditorValue(ctTreeOf(tree))
              }}
          >
            Details
          </Button>
        </Popup>
      </Marker>

  const renderDraftMarker = (tree: CtTree) =>
      <Marker
          icon={icon}
          position={[tree.latitude, tree.longitude]}
          draggable={true}
          eventHandlers={{
            dragend: (event: DragEndEvent) => {
              let latLng = event.target.getLatLng()
              setNewTree({latitude: latLng.lat, longitude: latLng.lng})
            },
            popupopen: () => {
              // TODO #18 zoom to props
              map.flyTo(new LatLng(tree.latitude, tree.longitude), 18)
            }
          }}>
        <Popup>
          <Button
              type="primary"
              onClick={() => {
                setIsEditable(true)
                setIsEditorOpen(true)
                setEditorValue(tree)
              }}
          >Create tree</Button>
        </Popup>
      </Marker>

  return (
      <div>
        {trees.map((tree, index) => renderMarker(`tree:${index}`, tree))}
        {newTree ? renderDraftMarker(newTree) : null}
        <CtTreeEditor
            open={isEditorOpen}
            isEditable={isEditable}
            initial={editorValue}
            onCreate={(tree: CtTree) => onTreeCreate(tree)}
            onCancel={() => setIsEditorOpen(false)}
        />
      </div>
  )
}

const MapComponent = ({...props}: MapContainerProps & TileLayerProps) => {

  return (
      <div>
        <MapContainer
            {...props}
        >
          <TileLayer
              url={props.url}
              maxNativeZoom={props.maxZoom}
              maxZoom={props.maxZoom}
          />
          <InvalidateSize/>
          <TreeMap/>
        </MapContainer>
      </div>
  )
}

export default MapComponent