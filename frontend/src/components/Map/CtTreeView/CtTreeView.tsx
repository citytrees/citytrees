import React, {useEffect} from "react";
import {Button, Dropdown, Form, FormInstance, Input, Modal, Rate, Select, Space} from "antd";
import {ModalProps} from "antd/lib/modal/Modal";
import {useForm} from "antd/es/form/Form";
import {CtTree} from "../Models/CtTree";
import {FrownOutlined, MehOutlined, SmileOutlined} from '@ant-design/icons';
import {TreeCondition, TreeState} from "../../../generated/openapi";
import {Option} from "antd/es/mentions";

interface TreeEditorProps {
  initial: CtTree,

  onCancel?: () => void,
  onSave?: (tree: CtTree) => void,
  onPublish?: (tree: CtTree) => void,
}

const CtTreeView = ({...props}: ModalProps & TreeEditorProps) => {
  const [form]: [FormInstance<CtTree>] = useForm()

  useEffect(() => {
    form.resetFields()
    let initialValue = props.initial;
    if (initialValue) {
      form.setFieldsValue(initialValue)
      form.setFieldValue("condition", initialValue.condition ? treeConditionValues.indexOf(initialValue.condition) + 1 : null)
    }
  }, [form, props.initial])

  const getCtTree: () => CtTree = () => {
    let comment = form.getFieldValue("comment");
    if (comment && comment.size === 0) {
      comment = null
    }

    const conditionNumber: number = form.getFieldValue("condition")
    let condition: TreeCondition | undefined
    if (conditionNumber) {
      condition = treeConditionValues[conditionNumber - 1]
    }

    return {
      id: props.initial.id,
      latitude: props.initial.latitude,
      longitude: props.initial.longitude,
      status: props.initial.status,
      state: form.getFieldValue("state"),
      condition: condition,
      comment: comment
    }
  }

  const treeConditionValues = [
    TreeCondition.VeryBad,
    TreeCondition.Bad,
    TreeCondition.Normal,
    TreeCondition.Great,
    TreeCondition.Awesome,
  ]

  const rateIcons: Record<number, React.ReactNode> = {
    0: <FrownOutlined/>,
    1: <FrownOutlined/>,
    2: <MehOutlined/>,
    3: <SmileOutlined/>,
    4: <SmileOutlined/>,
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
          <Form.Item name="longitude" label="Longitude">
            <Input disabled={true}/>
          </Form.Item>
          <Form.Item name="state" label="Alive / dead">
            <Select
                allowClear
            >
              <Option value={TreeState.Alive}>Alive</Option>
              <Option value={TreeState.Dead}>Dead</Option>
            </Select>
          </Form.Item>
          <Form.Item name="condition" label="Visual condition">
            <Rate character={({index}: { index?: number }) => rateIcons[index!!]}/>
          </Form.Item>
          <Form.Item name="comment" label="Comment">
            <Input/>
          </Form.Item>
        </Form>
      </Modal>
  )
}

export default CtTreeView