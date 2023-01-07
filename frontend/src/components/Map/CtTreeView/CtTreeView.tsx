import React, {useEffect} from "react";
import {Button, Dropdown, Form, FormInstance, Input, Modal, Space} from "antd";
import {ModalProps} from "antd/lib/modal/Modal";
import {useForm} from "antd/es/form/Form";
import {CtTree} from "../Models/CtTree";

interface TreeEditorProps {
  initial: CtTree,

  onCancel?: () => void,
  onSave?: (tree: CtTree) => void,
  onPublish?: (tree: CtTree) => void,
}

const CtTreeView = ({...props}: ModalProps & TreeEditorProps) => {
  const [form]: [FormInstance<CtTree>] = useForm()
  const isEditable = true

  useEffect(() => {
    form.resetFields()
    let initialValue = props.initial;
    if (initialValue)
      form.setFieldsValue(initialValue)

  }, [form, props.open, props.initial])

  const getCtTree: () => CtTree = () => {
    return {
      id: props.initial.id,
      latitude: props.initial.latitude,
      longitude: props.initial.longitude,
      status: props.initial.status
    }
  }

  return (
      <Modal
          {...props}
          footer={[
            <Space>
              <Dropdown.Button
                  type="primary"
                  style={{display: "inline"}}
                  menu={{
                    items: [
                      {
                        label: 'Save and publish',
                        key: 'ct-tree-view-save-and-publish',
                        onClick: () => props.onPublish?.(getCtTree())
                      },
                    ]
                  }}
                  onClick={() => props.onSave?.(getCtTree())}
              >
                Save
              </Dropdown.Button>
              <Button
                  onClick={() => props.onCancel?.()}
              >
                Close
              </Button>
            </Space>
          ]}
      >
        <Form form={form} layout="vertical">
          <Form.Item name="latitude" label="Latitude">
            <Input disabled={true}/>
          </Form.Item>
          <Form.Item name="longitude" label="longitude">
            <Input disabled={true}/>
          </Form.Item>
        </Form>
      </Modal>
  )
}

export default CtTreeView