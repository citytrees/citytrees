import React, {useEffect, useState} from "react"
import {MapContainer, Marker, Popup, TileLayer, useMap, useMapEvents} from "react-leaflet"
import 'leaflet/dist/leaflet.css'
import 'leaflet.markercluster';
import {MapContainerProps} from "react-leaflet/lib/MapContainer";
import {TileLayerProps} from "react-leaflet/lib/TileLayer";
import {useUser} from "../../../app/hooks";
import {TreesGetResponseTree, TreeStatus} from "../../../generated/openapi";
import MarkerClusterGroup from "react-leaflet-cluster";
import {Button, Card, notification, Skeleton} from "antd";
import api from "../../../api";
import {mapBoundsOf} from "../Bounds/MapBounds";
import uuid from "react-uuid";
import {clusterIcon, newTreeIcon, treeIcon} from "../Icon/MapIcons";
import {CtTree, ctTreeOf} from "../Models/CtTree";
import TreeView from "../TreeView";
import {DragEndEvent, LatLng} from "leaflet";
import Meta from "antd/es/card/Meta";
import {useSearchParams} from "react-router-dom";

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

  const [trees, setTrees] = useState<TreesGetResponseTree[]>([])
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
        map.flyTo(new LatLng(parseFloat(latStr!!), parseFloat(lngStr!!)), props.maxZoom, {duration: 1})
      } catch (e) {
        console.warn("Incorrect coordinates")
      }
    }
  }, [searchParams, map, props.maxZoom])

  useEffect(() => {
    api.trees.loadTreesByRegion(mapBoundsOf(map.getBounds()))
        .then((responseTrees) => setTrees(responseTrees))
  }, [map, zoom, bounds, newTree, trigger])

  const [treeViewOpen, setTreeViewOpen] = useState(false)
  const [treeViewValue, setTreeViewValue] = useState<CtTree | undefined>()
  const [treeViesEditable, setTreeViewEditable] = useState(false)
  const setTreeViewState = (isOpen: boolean, value: CtTree) => {
    const isTreeEditable = value.status === TreeStatus.New || value.status === TreeStatus.ToApprove;
    const isUserHasPermission = user !== null && value.userId === user.sub;
    setTreeViewEditable(isUserHasPermission && isTreeEditable)
    setTreeViewValue(value)
    setTreeViewOpen(isOpen)
  }
  const createTreeMarker = (tree: TreesGetResponseTree) =>
      <Marker
          key={uuid()}
          position={[tree.latitude, tree.longitude]}
          icon={treeIcon(tree.status)}
      >
        <Popup>
          <Card
              style={{width: 300}}
              cover={
                <div style={{display: "flex", width: "100%"}}>
                  {
                    tree.fileUrl
                        ? <img style={{
                          height: 300,
                          margin: "auto",
                          objectFit: "cover",
                          objectPosition: "100% 0"
                        }}
                               alt={tree.id.toString()}
                               src={tree.fileUrl}
                        />
                        : <Skeleton.Image style={{height: 250, width: 300}}/>
                  }
                </div>
              }
              actions={[
                <Button key="tree-details" type="text" onClick={() => {
                  api.tree.getTreeById({id: tree.id})
                      .then(treeResponse => {
                        api.tree.getAllAttachedFiles({treeId: tree.id})
                            .then((filesResponse) => {
                              setTreeViewState(true, ctTreeOf(treeResponse, filesResponse));
                            })
                      })
                      .catch()
                }}>Details</Button>,
              ]}
          >
            <Meta
                title={tree.id}
                description={
                  <div>
                    <p>Tree height: {tree.treeHeight}</p>
                    <p>Trunk girth: {tree.trunkGirth}</p>
                    <p>Wood type: {tree.woodTypeName}</p>
                  </div>
                }
            />
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
                map.flyTo(new LatLng(tree.latitude, tree.longitude), props.maxZoom, {duration: 1})
              }
            }
          }}>
        <Popup>
          {tree.latitude} {tree.longitude}
          <Button
              type="primary"
              onClick={() => {
                api.tree.createTree({treeCreateRequest: tree}).then((response) =>
                    api.tree.getTreeById({id: response.treeId}).then(tree => {
                      setNewTree(null)
                      setTreeViewState(true, ctTreeOf(tree, []))
                    }))
              }}
          >Create tree</Button>
        </Popup>
      </Marker>

  const updateTree = (tree: CtTree, status: TreeStatus, onSuccess: () => void) => {
    api.tree.updateTreeById(
        {
          id: tree.id,
          treeUpdateRequest: {
            woodTypeId: tree.woodTypeId,
            status: status,
            state: tree.state,
            age: tree.age,
            condition: tree.condition,
            barkCondition: tree.barkCondition,
            branchesCondition: tree.branchesCondition,
            plantingType: tree.plantingType,
            comment: tree.comment,
            fileIds: tree.files.map(file => file.id),
            diameterOfCrown: tree.diameterOfCrown,
            heightOfTheFirstBranch: tree.heightOfTheFirstBranch,
            numberOfTreeTrunks: tree.numberOfTreeTrunks,
            treeHeight: tree.treeHeight,
            trunkGirth: tree.trunkGirth,
          }
        }
    ).then(() => onSuccess())
  }

  const onSave = (tree: CtTree) => {
    updateTree(tree, tree.status,
        () => {
          notification.open({message: "Tree was saved", type: "info", placement: "topRight"})
          setTrigger(!trigger)
        })
  }

  const onPublish = (tree: CtTree) => {
    updateTree(tree, TreeStatus.ToApprove, () => {
      notification.open({message: "Tree was published!", type: "info", placement: "topRight"});
      setTreeViewOpen(false)
      map.closePopup()
      setTrigger(!trigger)
    })
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
        {treeViewValue && <TreeView
            bodyStyle={{overflowY: 'auto', maxHeight: 'calc(100vh - 100px)'}}
            width={600}
            centered={true}
            editable={treeViesEditable}
            open={treeViewOpen}
            initial={treeViewValue}
            onSave={onSave}
            onPublish={onPublish}
            onCancel={() => setTreeViewOpen(false)}
        />}
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