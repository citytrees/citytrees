import React, {useEffect, useState} from "react";
import {Button, Col, Form, FormProps, Input, Modal, Row, Space, Table} from "antd";
import api from "../../api";
import {ModalProps} from "antd/lib/modal/Modal";
import {useForm} from "antd/es/form/Form";

interface WoodTypeViewProps {
  onCreate: (type: WoodType) => void
  onCancel: () => void
}

const WoodTypeView = ({...props}: ModalProps & FormProps & WoodTypeViewProps) => {
  const [form] = useForm()

  useEffect(() => form.resetFields(), [form, props.open])

  return (
      <Modal {...props} footer={null}>
        <Form form={form} {...props}>
          <Form.Item
              name="name"
              label="Name"
              rules={[{required: true, message: 'Please input wood type name'}]}
          >
            <Input/>
          </Form.Item>
          <Form.Item>
            <Space>
              <Button
                  type="primary"
                  htmlType="submit"
                  onClick={() => props.onCreate({name: form.getFieldValue('name')})}
              >
                Submit
              </Button>
              <Button onClick={() => props.onCancel()}>
                Cancel
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Modal>
  )
}

interface WoodType {
  name: string
}

const WoodTypePage: React.FC = () => {
  const [data, setData] = useState<WoodType[]>([])
  const [isNewTypeModalOpen, setIsNewTypeModalOpen] = useState(false)

  const columns = [
    {
      title: 'Name',
      dataIndex: 'name',
    },
  ]

  useEffect(() => {
    api.woodType.getAllWoodTypes()
        .then(response => setData(response.map(type => ({name: type.name}))))
  }, [])

  const handleOnCreate = (woodType: WoodType) => {
    api.woodType.createWoodType({woodTypeCreateRequest: {name: woodType.name}})
        .then(() => {
          api.woodType.getAllWoodTypes().then(response => setData(response.map(type => ({name: type.name}))))
          setIsNewTypeModalOpen(false)
        })
  }

  return (
      <Row style={{minHeight: "100%"}} justify="center" align="middle">
        <Col lg={{span: 10}}>
          <Button
              onClick={() => setIsNewTypeModalOpen(true)}
          >Add wood type</Button>
          <Table
              dataSource={data}
              columns={columns}
          />
        </Col>
        <WoodTypeView
            preserve={false}
            open={isNewTypeModalOpen}
            layout="vertical"
            onCreate={handleOnCreate}
            onCancel={() => setIsNewTypeModalOpen(false)}
            centered={true}
        />
      </Row>
  )
};

export default WoodTypePage;
