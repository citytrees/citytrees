import React, {useEffect, useState} from "react";
import {Button, Form, Input, List, Modal, SearchBar, Space, Tag, Toast} from "antd-mobile";
import api from "../../api";
import {useForm} from "antd/es/form/Form";
import {AddOutline, SearchOutline} from 'antd-mobile-icons'
import {WoodTypeStatus} from "../../generated/openapi";


interface WoodTypeViewProps {
  onCreate: (type: WoodType) => void
  onCancel: () => void
}

const WoodTypeForm = ({...props}: WoodTypeViewProps) => {
  const [form] = useForm()

  useEffect(() => form.resetFields(), [form])

  return (
      <Form
          form={form}
          footer={
            <Space>
              <Button
                  type="submit"
                  color="primary"
                  fill="outline"
                  onClick={() => {
                    let name = form.getFieldValue('name');
                    if (name) {
                      props.onCreate({name: name})
                    }
                  }}
              >
                Submit
              </Button>
              <Button onClick={() => props.onCancel()}>
                Cancel
              </Button>
            </Space>
          }
      >
        <Form.Item
            name="name"
            label="Name"
            rules={[{required: true, message: 'Please input wood type name'}]}
        >
          <Input/>
        </Form.Item>
      </Form>
  )
}

interface WoodType {
  id?: string
  name: string,
  status?: WoodTypeStatus
}

const WoodTypePage: React.FC = () => {
  const [data, setData] = useState<WoodType[]>([])
  const [searchString, setSearchString] = useState('')

  useEffect(() => {
    api.woodType.getAllWoodTypes()
        .then(response => setData(response))
  }, [])

  const doUpdate = (search: string | null) => {
    if (search !== null && search.length > 0) {
      api.woodType.getAllWoodTypesByName({name: searchString})
          .then(response => setData(response))
    } else {
      api.woodType.getAllWoodTypes()
          .then(response => setData(response))
    }
  }

  const handleNewWoodTypeClick = () => {
    const modal = Modal.show({
      title: "Add new wood type",
      content: <WoodTypeForm
          onCreate={(type) => {
            handleNewWoodTypeCreate(type)
            modal.close()
          }}
          onCancel={() => modal.close()}
      />,
      closeOnMaskClick: true,
    })
  }

  const handleNewWoodTypeCreate = (newType: WoodType) => {
    api.woodType.createWoodType({woodTypeCreateRequest: {name: newType.name}})
        .then(() => doUpdate(null))
  }

  const renderWoodTypeTag = (woodType: WoodType) => {
    const status = woodType.status
    let color
    if (status === WoodTypeStatus.Active) {
      color = "success"
    } else if (status === WoodTypeStatus.Deleted) {
      color = "default"
    } else {
      color = "default"
    }
    return <Tag color={color} fill="outline">{status}</Tag>
  }

  const handleItemClick = (woodType: WoodType) => {
    let status = woodType.status;
    if (status === WoodTypeStatus.Active) {
      Modal.confirm({
        content: "Are you sure you want to delete type of wood?",
        onConfirm: () => {
          api.woodType.deleteWoodType({id: woodType.id!!})
              .then(() => doUpdate(null))
        },
        confirmText: "Delete",
        cancelText: "Cancel",
      })
    } else if (status === WoodTypeStatus.Deleted) {
      Toast.show({icon: "fail", content: "Restoration coming soon"})
    }
  }

  return (
      <div>
        <div
            style={{display: "flex", alignItems: "center"}}
        >
          <SearchBar
              style={{flex: 1}}
              onChange={value => setSearchString(value)}
              onSearch={value => doUpdate(value)}
              onClear={() => doUpdate('')}
          />
          <Button
              color="primary"
              fill="none"
              size="small"
              onClick={() => doUpdate(searchString)}
          >
            <SearchOutline/>
          </Button>
          <Button
              color="primary"
              fill="none"
              size="small"
              onClick={handleNewWoodTypeClick}
          >
            <AddOutline/>
          </Button>
        </div>
        <List>
          {data.map(type =>
              <List.Item
                  key={type.id}
                  onClick={() => handleItemClick(type)}
              >
                <Space justify="center">
                  <span style={{verticalAlign: "middle"}}>{type.name}</span>
                  {renderWoodTypeTag(type)}
                </Space>
              </List.Item>
          )}
        </List>
      </div>
  )
}

export default WoodTypePage;
