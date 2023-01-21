import React, {useEffect, useState} from "react";
import {Dropdown, MenuProps, Table, Tag} from "antd";
import {CtTree, ctTreeOf} from "../../components/Map/Models/CtTree";
import api from "../../api";
import {ColumnType} from "antd/es/table/interface";
import {PresetStatusColorType} from "antd/es/_util/colors";
import {TreeStatus} from "../../generated/openapi";
import {BarsOutlined} from "@ant-design/icons";

const AllTreesPage: React.FC = () => {
  const [pagination, setPagination] = useState({limit: 0, offset: 0})
  const [total, setTotal] = useState(1)

  const [data, setData] = useState<CtTree[]>([])

  const columns: ColumnType<CtTree>[] = [
    {
      title: 'Id',
      dataIndex: 'id',
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
          color = "error"
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

        if (record.status === TreeStatus.ToApprove) {
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

        items.push({
          label: 'Delete',
          key: "action-delete-tree",
          onClick: () => {
            api.tree.deleteTree({id: record.id}).then(() => {
              record.status = TreeStatus.Deleted
              deleteTableRecord(record)
            })
          }
        })

        return <Dropdown trigger={['click']} disabled={items.length === 0} menu={{items}}>
          <BarsOutlined/>
        </Dropdown>
      }
    },
  ]

  const deleteTableRecord = (record: CtTree) => {
    setData(data.filter(tree => record.id !== tree.id))
  }

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
  )
};

export default AllTreesPage;
