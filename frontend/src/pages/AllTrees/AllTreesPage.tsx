import React, {useEffect, useState} from "react";
import {Dropdown, MenuProps, Table, Tag} from "antd";
import {CtTree, ctTreeOf} from "../../components/Map/Models/CtTree";
import api from "../../api";
import {ColumnType} from "antd/es/table/interface";
import {PresetStatusColorType} from "antd/es/_util/colors";
import {TreeStatus} from "../../generated/openapi";
import {BarsOutlined} from "@ant-design/icons";
import TreeView from "../../components/Map/TreeView";
import AppRoutes from "../../constants/AppRoutes";
import {Modal} from "antd-mobile";
import TreeForm from "../../components/Map/TreeForm";

const AllTreesPage: React.FC = () => {
  const [pagination, setPagination] = useState({limit: 0, offset: 0})
  const [total, setTotal] = useState(1)

  const [data, setData] = useState<CtTree[]>([])
  const [treeViewOpen, setTreeViewOpen] = useState(false)
  const [selectedTree, setSelectedTree] = useState<CtTree>()

  const columns: ColumnType<CtTree>[] = [
    {
      title: 'Id',
      dataIndex: 'id',
    },
    {
      title: 'Lat',
      dataIndex: 'latitude',
    },
    {
      title: 'Long',
      dataIndex: 'longitude',
    },
    {
      title: 'Status',
      dataIndex: 'status',
      render: (_, record: CtTree) => {
        const status = record.status;
        let color: PresetStatusColorType
        if (status === TreeStatus.New) {
          color = "processing"
        } else if (status === TreeStatus.ToApprove) {
          color = "warning"
        } else if (status === TreeStatus.Approved) {
          color = "success"
        } else if (status === TreeStatus.Deleted) {
          color = "default"
        } else {
          color = "default"
        }
        return <Tag color={color}>{status}</Tag>
      }
    },
    {
      title: 'Actions',
      key: 'actions',
      render: (_, record: CtTree) => {
        const items: MenuProps["items"] = []

        const status = record.status;

        items.push({
          label: 'Details',
          key: "action-details-tree",
          onClick: () => {
            api.tree.getTreeById({id: record.id})
                .then(treeResponse => {
                  api.tree.getAllAttachedFiles({treeId: record.id})
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

        if (status !== TreeStatus.Deleted) {
          items.push({
            label: 'Open on map',
            key: 'action-open-on-map',
            onClick: () => {
              window.open(`${AppRoutes.MAIN}?lat=${record.latitude}&lng=${record.longitude}`, '_blank')
            }
          })
          items.push({
            label: 'Delete',
            key: "action-delete-tree",
            onClick: () => {
              api.tree.deleteTree({id: record.id}).then(() => {
                record.status = TreeStatus.Deleted
                updateTableRecord(record)
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

        if (status === TreeStatus.ToApprove) {
          items.push({
            label: 'Approve tree',
            key: "action-approve-tree",
            onClick: () => {
              api.tree.approveTree({treeId: record.id}).then(() => {
                record.status = TreeStatus.Approved
                updateTableRecord(record)
              })
            }
          })
        }

        return <Dropdown trigger={['click']} disabled={items.length === 0} menu={{items}}>
          <BarsOutlined/>
        </Dropdown>
      }
    },
  ]

  const updateTableRecord = (record: CtTree) => {
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

  useEffect(() => {
    api.tree.getAllTreesCount().then(response => {
      setTotal(response.count)
      setPagination({limit: 20, offset: 0})
    })
  }, [])

  useEffect(() => {
    api.tree.getAll(pagination)
        .then(request => setData(request.map(tree => ctTreeOf(tree, []))))
  }, [pagination])

  return (
      <div>
        <TreeView
            open={treeViewOpen}
            centered={true}
            initial={selectedTree!!}
            editable={false}
            onCancel={() => setTreeViewOpen(false)}
            bodyStyle={{overflowY: 'auto', maxHeight: 'calc(100vh - 100px)'}}
            width={600}
        />
        <Table
            dataSource={data}
            columns={columns}
            pagination={{
              position: ['bottomCenter'],
              showSizeChanger: false,
              total: total,
              onChange: (page, pageSize) => {
                setPagination({limit: pageSize, offset: (page - 1) * pageSize})
              },
            }}
        />
      </div>
  )
};

export default AllTreesPage;
