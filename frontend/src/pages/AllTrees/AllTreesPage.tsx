import React, {useState} from "react";
import {ctTreeOf} from "../../components/Map/Models/CtTree";
import api from "../../api";
import {TreeStatus, UserRole} from "../../generated/openapi";
import AppRoutes from "../../constants/AppRoutes";
import {Button, DotLoading, Image, InfiniteScroll, List, Modal, Space, Tag} from "antd-mobile";
import TreeForm from "../../components/Map/TreeForm";
import {ctShortTreeOf, CtTreeShort} from "../../components/Map/Models/CtTreeShort";
import {useUser} from "../../app/hooks";

const AllTreesPage: React.FC = () => {
  const user = useUser()
  const [data, setData] = useState<CtTreeShort[]>([])

  const getTreeTag = (tree: CtTreeShort) => {
    const status = tree.status;
    let color
    if (status === TreeStatus.New) {
      color = "primary"
    } else if (status === TreeStatus.ToApprove) {
      color = "warning"
    } else if (status === TreeStatus.Approved) {
      color = "success"
    } else if (status === TreeStatus.Deleted) {
      color = "default"
    } else {
      color = "default"
    }
    return <Tag color={color} fill="outline">{status}</Tag>
  }

  const listActions = (tree: CtTreeShort) => {
    const items = []
    const status = tree.status;
    const isUserAdmin = user?.roles?.indexOf(UserRole.Admin) !== -1

    if (isUserAdmin) {
      if (status === TreeStatus.ToApprove) {
        items.push({
          label: 'Approve tree',
          key: "action-approve-tree",
          onClick: () => {
            Modal.confirm({
              content: "Are you sure you what to approve tree?",
              confirmText: "Approve",
              cancelText: "Cancel",
              onConfirm: () => api.tree.approveTree({treeId: tree.id}).then(() => {
                tree.status = TreeStatus.Approved
                updateTableRecord(tree)
              })
            })
          }
        })
      }

      if (status !== TreeStatus.Deleted) {
        items.push({
          label: 'Open on map',
          key: 'action-open-on-map',
          onClick: () => {
            window.open(`${AppRoutes.MAIN}?lat=${tree.latitude}&lng=${tree.longitude}`, '_blank')
          }
        })
        items.push({
          label: 'Delete',
          key: "action-delete-tree",
          onClick: () => {
            Modal.confirm({
              content: "Are you sure you what to delete tree?",
              confirmText: "Approve",
              cancelText: "Cancel",
              onConfirm: () => api.tree.deleteTree({id: tree.id}).then(() => {
                tree.status = TreeStatus.Deleted
                updateTableRecord(tree)
              })
            })
          }
        })
      } else if (status === TreeStatus.Deleted) {
        // TODO #32 restore
        items.push({
          label: 'Restore',
          key: "action-restore-tree",
          disabled: true,
          onClick: () => {
          }
        })
      }
    }

    return items
  }

  const updateTableRecord = (record: CtTreeShort) => {
    const index = data.findIndex((tree) => record.id === tree.id)
    if (index > -1) {
      const newData = [...data]
      const item = newData[index]
      newData.splice(index, 1, {
        ...item,
        ...record,
      })
      setData(newData)
    }
  }

  const [hasMore, setHasMore] = useState(true)

  const loadMore = async () => {
    let cursorPosition
    if (data.length > 0) {
      cursorPosition = data[data.length - 1].id
    }
    let append = await api.tree.getAll({limit: 30, cursorPosition: cursorPosition})
        .then(request => request.map(tree => ctShortTreeOf(tree)))

    setData(val => [...val, ...append])
    setHasMore(append.length > 0)
  };

  return (
      <div>
        <List>
          {data.map((item) => (
              <List.Item
                  key={item.id}
                  prefix={item.fileUrls &&
                      <Image
                          width={44}
                          height={44}
                          style={{borderRadius: 4}}
                          fit='cover'
                          src={item.fileUrls[0]}
                      />}
                  description={`${item.latitude} ${item.longitude}`}
                  onClick={() =>
                      api.tree.getTreeById({id: item.id}).then(treeResponse => {
                        api.tree.getAllAttachedFiles({treeId: item.id}).then((filesResponse) => {
                          const tree = ctTreeOf(treeResponse, filesResponse)
                          Modal.show({
                            content: <TreeForm
                                initial={tree}
                                editable={false}
                                footer={<Space>
                                  {listActions(item).map(action =>
                                      <Button
                                          size='small'
                                          color='primary'
                                          fill='outline'
                                          key={action.key}
                                          onClick={action.onClick}>{action.label}
                                      </Button>
                                  )}
                                </Space>}
                            />,
                            closeOnMaskClick: true
                          })
                        })
                      })
                  }
              >
                <Space justify="center">
                  <span style={{verticalAlign: "middle"}}>{item.id}</span>
                  {getTreeTag(item)}
                </Space>
              </List.Item>
          ))}
        </List>
        <InfiniteScroll aria-valuetext="" loadMore={loadMore} hasMore={hasMore}>
          <div>
            {hasMore && <>
              <span>Loading</span>
              <DotLoading/>
            </>}
          </div>
        </InfiniteScroll>
      </div>
  )
};

export default AllTreesPage;
