import React, {useEffect, useState} from "react"
import {MapContainer, Marker, Popup, TileLayer, useMap, useMapEvents} from "react-leaflet"
import 'leaflet/dist/leaflet.css'
import 'leaflet.markercluster';
import {MapContainerProps} from "react-leaflet/lib/MapContainer";
import {TileLayerProps} from "react-leaflet/lib/TileLayer";
import {useUser} from "../../../app/hooks";
import {TreeShortGetResponse, TreeStatus} from "../../../generated/openapi";
import MarkerClusterGroup from "react-leaflet-cluster";
import {Button, Card, Image, Modal, Toast} from "antd-mobile";
import api from "../../../api";
import {mapBoundsOf} from "../Bounds/MapBounds";
import uuid from "react-uuid";
import {clusterIcon, newTreeIcon, treeIcon} from "../Icon/MapIcons";
import {CtTree, ctTreeOf, deleteTree, isTreeDeletable, isTreeEditable, updateTree} from "../Models/CtTree";
import {DragEndEvent, LatLng} from "leaflet";
import {useSearchParams} from "react-router-dom";
import TreeForm from "../TreeForm";

interface DraftCtTree {
  latitude: number
  longitude: number
}

const InvalidateSize = () => {
  useMap().invalidateSize()
  return null
}

interface TreeMapProps {
  maxZoom: number
}

// todo #32 refactor TreeMap
const TreeMap = ({...props}: TreeMapProps & MapContainerProps) => {
  const [searchParams] = useSearchParams();
  const user = useUser()

  const [trees, setTrees] = useState<TreeShortGetResponse[]>([])
  const [newTree, setNewTree] = useState<DraftCtTree | null>(null)
  const [trigger, setTrigger] = useState(false) // todo #32 refactor TreeMap

  const map = useMapEvents({
    click: (event) => {
      if (user) {
        let coordinates = event.latlng
        setNewTree({latitude: coordinates.lat, longitude: coordinates.lng})
      }
    },
    dragend: () => setBounds(mapBoundsOf(map.getBounds())),
    zoom: () => setZoom(map.getZoom()),
  })

  const [bounds, setBounds] = useState(mapBoundsOf(map.getBounds()))
  const [zoom, setZoom] = useState(props.zoom)

  useEffect(() => {
    let latStr = searchParams.get("lat");
    let lngStr = searchParams.get("lng");

    if (latStr !== null && lngStr !== null) {
      try {
        map.setView(new LatLng(parseFloat(latStr!!), parseFloat(lngStr!!)), props.maxZoom)
      } catch (e) {
        console.warn("Incorrect coordinates")
      }
    }
  }, [searchParams, map, props.maxZoom])

  useEffect(() => {
    api.trees.loadTreesByRegion(mapBoundsOf(map.getBounds()))
        .then((responseTrees) => setTrees(responseTrees))
  }, [map, zoom, bounds, newTree, trigger])

  const showTreeModal = (tree: CtTree) => {
    let modal = Modal.show({
      content: <TreeForm
          initial={tree}
          onSave={onSave}
          onPublish={(tree) => {
            onPublish(tree)
            modal.close()
          }}
          onCancel={() => modal.close()}
          onDelete={() => {
            onDelete(tree)
            modal.close()
          }}
          isDeletable={isTreeDeletable(tree, user)}
          editable={isTreeEditable(tree, user)}
      />,
      closeOnMaskClick: true
    })
    map.closePopup()
  }

  const createTreeMarker = (tree: TreeShortGetResponse) =>
      <Marker
          key={uuid()}
          position={[tree.latitude, tree.longitude]}
          icon={treeIcon(tree.status)}
      >
        <Popup>
          <Card>
            <Image
                alt={tree.id.toString()}
                src={tree.fileUrls.length ? tree.fileUrls[0] : undefined}
                width={280}
                height={280}
                fit='cover'
                style={{borderRadius: 4}}
            />
            <div>
              <p>Tree height: {tree.treeHeight}</p>
              <p>Trunk girth: {tree.trunkGirth}</p>
              <p>Wood type: {tree.woodTypeName}</p>
            </div>
            <Button onClick={() => {
              api.tree.getTreeById({id: tree.id})
                  .then((treeResponse) => {
                    api.tree.getAllAttachedFiles({treeId: tree.id})
                        .then((filesResponse) => showTreeModal(ctTreeOf(treeResponse, filesResponse)))
                  })
                  .catch()
            }}>Details</Button>
          </Card>
        </Popup>
      </Marker>

  const createDraftTreeMarker = (tree: DraftCtTree) =>
      <Marker
          icon={newTreeIcon}
          position={[tree.latitude, tree.longitude]}
          draggable={true}
          eventHandlers={{
            dragend: (event: DragEndEvent) => {
              let latLng = event.target.getLatLng()
              setNewTree({latitude: latLng.lat, longitude: latLng.lng})
            },
            click: () => {
              if (map.getZoom() !== props.maxZoom) {
                map.setView(new LatLng(tree.latitude, tree.longitude), props.maxZoom)
              }
            }
          }}>
        <Popup>
          {tree.latitude} {tree.longitude}
          <Button
              color="primary"
              onClick={() => {
                api.tree.createTree({treeCreateRequest: tree}).then((response) =>
                    api.tree.getTreeById({id: response.treeId}).then(tree => {
                      showTreeModal(ctTreeOf(tree, []))
                      setNewTree(null)
                    }))
              }}
          >Create tree</Button>
        </Popup>
      </Marker>

  const onSave = (tree: CtTree) => {
    updateTree(tree, tree.status,
        () => {
          Toast.show({content: "Tree was saved", position: "top"})
          setTrigger(!trigger)
        })
  }

  const onPublish = (tree: CtTree) => {
    updateTree(tree, TreeStatus.ToApprove, () => {
      Toast.show({content: "Tree was published!", position: "top"})
      setTrigger(!trigger)
    })
  }

  const onDelete = (tree: CtTree) => {
    deleteTree(tree, () => setTrigger(!trigger))
  }

  return (
      <div>
        <MarkerClusterGroup
            iconCreateFunction={(cluster) => clusterIcon(cluster.getChildCount())}
            disableClusteringAtZoom={props.maxZoom}
            spiderfyOnEveryZoom={false}
            spiderfyOnMaxZoom={false}
        >
          {trees.map((tree) => createTreeMarker(tree))}
        </MarkerClusterGroup>
        {newTree && user && createDraftTreeMarker(newTree)}
      </div>
  )
}

const MapComponent = ({...props}: MapContainerProps & TileLayerProps & TreeMapProps) => {
  return (
      <div>
        <MapContainer {...props}>
          <TileLayer
              url={props.url}
              maxNativeZoom={props.maxZoom}
              maxZoom={props.maxZoom}
          />
          <InvalidateSize/>
          <TreeMap {...props}/>
        </MapContainer>
      </div>
  )
}

export default MapComponent