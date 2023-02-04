import React, {useState} from "react";
import {ctTreeOf} from "../../components/Map/Models/CtTree";
import api from "../../api";
import {TreeStatus} from "../../generated/openapi";
import AppRoutes from "../../constants/AppRoutes";
import {Avatar, Button, DotLoading, Dropdown, InfiniteScroll, List, Modal, Space, Tag} from "antd-mobile";
import TreeForm from "../../components/Map/TreeForm";
import {ctShortTreeOf, CtTreeShort} from "../../components/Map/Models/CtTreeShort";

const AllTreesPage: React.FC = () => {
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
    {
      const items = []
      const status = tree.status;
      items.push({
        label: 'Details',
        key: "action-details-tree",
        onClick: () => {
          api.tree.getTreeById({id: tree.id})
              .then(treeResponse => {
                api.tree.getAllAttachedFiles({treeId: tree.id})
                    .then((filesResponse) => {
                      const tree = ctTreeOf(treeResponse, filesResponse)
                      Modal.show({
                        content: <TreeForm initial={tree} editable={false}></TreeForm>,
                        closeOnMaskClick: true
                      })
                    })
              })
              .catch()
        }
      })

      if (status === TreeStatus.ToApprove) {
        items.push({
          label: 'Approve tree',
          key: "action-approve-tree",
          onClick: () => {
            api.tree.approveTree({treeId: tree.id}).then(() => {
              tree.status = TreeStatus.Approved
              updateTableRecord(tree)
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
            api.tree.deleteTree({id: tree.id}).then(() => {
              tree.status = TreeStatus.Deleted
              updateTableRecord(tree)
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

      return items
    }
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
          {data.map((item, index) => (
              <List.Item
                  key={item.id}
                  prefix={item.fileUrls && <Avatar src={item.fileUrls[0]}/>}
                  description={`${item.latitude} ${item.longitude}`}
                  extra={
                    <Dropdown>
                      <Dropdown.Item key={`details-dropdown-${index}`} title="Details">
                        <div style={{display: "flex", justifyContent: "flex-end", padding: 12}}>
                          {listActions(item).reverse().map(action =>
                              <Button key={action.key} fill="none" onClick={action.onClick}>{action.label}</Button>
                          )}
                        </div>
                      </Dropdown.Item>
                    </Dropdown>
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
