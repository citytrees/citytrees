import React, {useEffect, useState} from "react"
import {MapContainer, Marker, Popup, TileLayer, useMap, useMapEvents} from "react-leaflet"
import 'leaflet/dist/leaflet.css'
import {DragEndEvent, Icon, LatLng} from "leaflet"
import {TreesGetResponseTree} from "../../generated/openapi"
import api from "../../api"
import {CtTree, ctTreeOf} from "./Models/CtTree"
import {Button, notification} from "antd";
import CtTreeView from "./CtTreeView";
import {MapContainerProps} from "react-leaflet/lib/MapContainer";
import {TileLayerProps} from "react-leaflet/lib/TileLayer";
import {useUser} from "../../app/hooks";

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

interface DraftCtTree {
  latitude: number
  longitude: number
}

const TreeMap = () => {
  const [updateCount, setUpdateCount] = useState(0)
  const [newTree, setNewTree] = useState<DraftCtTree | null>(null)
  const [trees, setTrees] = useState<TreesGetResponseTree[]>([])

  const [isTreeViewOpen, setIsTreeViewOpen] = useState(false)
  const [treeViewValue, setTreeViewValue] = useState<CtTree | undefined>()

  const user = useUser()

  const map = useMapEvents({
    click: (event) => {
      if (user) {
        let latlng = event.latlng
        setNewTree({latitude: latlng.lat, longitude: latlng.lng})
      }
    },
    dragend: () => triggerUpdate(),
    zoomend: () => triggerUpdate()
  })

  // TODO #18 replace using map events
  const triggerUpdate = () => {
    setUpdateCount(updateCount + 1)
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
  }, [map, updateCount, newTree])

  const setTreeViewState = (isOpen: boolean, isEditable: boolean, value: CtTree) => {
    setIsTreeViewOpen(isOpen)
    setTreeViewValue(value)
  }

  const renderMarker = (id: string, tree: TreesGetResponseTree) =>
      <Marker key={id} icon={icon} position={[tree.latitude, tree.longitude]}>
        <Popup>
          <Button
              type="primary"
              onClick={() => {
                // todo #18 implement catch()
                api.tree.getTreeById({id: tree.id})
                    .then(value => setTreeViewState(true, false, ctTreeOf(value)))
                    .catch()
              }}
          >
            Details
          </Button>
        </Popup>
      </Marker>

  const renderDraftMarker = (tree: DraftCtTree) =>
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
            },
            popupclose: () => {
              setNewTree(null)
            }
          }}>
        <Popup>
          <Button
              type="primary"
              onClick={() => {
                api.tree.createTree({
                      treeCreateRequest:
                          {
                            latitude: tree.latitude,
                            longitude: tree.longitude
                          }
                    }
                ).then((response) => {
                  setNewTree(null)
                  setTreeViewState(true, true, {
                    id: response.treeId,
                    latitude: tree.latitude,
                    longitude: tree.longitude,
                    status: "NEW"
                  })
                })
              }}
          >Create tree</Button>
        </Popup>
      </Marker>

  return (
      <div>
        {trees.map((tree, index) => renderMarker(`tree:${index}`, tree))}
        {newTree && user ? renderDraftMarker(newTree) : null}
        <CtTreeView
            open={isTreeViewOpen}
            initial={treeViewValue!!}
            onSave={(tree: CtTree) => {
              api.tree.updateTreeById({id: tree.id!!, treeUpdateRequest: {status: "NEW"}})
                  .then(() => notification.open({
                    message: "Tree was saved",
                    type: "info",
                    placement: "topRight"
                  }))
            }}
            onPublish={(tree: CtTree) => {
              api.tree.updateTreeById({id: tree.id!!, treeUpdateRequest: {status: "TO_APPROVE"}})
                  .then(() => {
                    notification.open({
                      message: "Tree was published!",
                      type: "info",
                      placement: "topRight"
                    });
                    setIsTreeViewOpen(false)
                    map.closePopup()
                  })
            }}
            onCancel={() => setIsTreeViewOpen(false)}
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