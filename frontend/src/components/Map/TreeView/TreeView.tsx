import React, {useEffect, useState} from "react";
import {Button, Checkbox, Dropdown, Form, FormInstance, Input, Modal, Rate, Select, Space, Upload, UploadFile} from "antd";
import {ModalProps} from "antd/lib/modal/Modal";
import {useForm} from "antd/es/form/Form";
import {CtTree} from "../Models/CtTree";
import {FrownOutlined, MehOutlined, PlusOutlined, SmileOutlined} from '@ant-design/icons';
import {TreeBarkCondition, TreeBranchCondition, TreeCondition, TreeState} from "../../../generated/openapi";
import api from "../../../api";
import TextArea from "antd/es/input/TextArea";

interface TreeEditorProps {
  initial: CtTree,
  onCancel?: () => void,
  onSave?: (tree: CtTree) => void,
  onPublish?: (tree: CtTree) => void,
}

const availableTreeConditionValues = [
  TreeCondition.VeryBad,
  TreeCondition.Bad,
  TreeCondition.Normal,
  TreeCondition.Great,
  TreeCondition.Awesome,
]

// todo #18 optimize
const TreeView = ({...props}: ModalProps & TreeEditorProps) => {
  const [form]: [FormInstance<CtTree>] = useForm()

  const [fileList, setFileList] = useState<UploadFile[]>([])

  useEffect(() => {
    form.resetFields()
    let initialValue = props.initial
    if (initialValue) {
      setFileList(initialValue.files.map(file => ({uid: file.id, name: file.name, status: 'done', url: file.url,})) ?? [])
      form.setFieldsValue(initialValue)
      form.setFieldValue("condition", initialValue.condition ? availableTreeConditionValues.indexOf(initialValue.condition) + 1 : null)
    }
  }, [form, props.initial])

  const getCtTree: () => CtTree = () => {
    let comment = form.getFieldValue("comment")
    if (comment && comment.size === 0) {
      comment = null
    }

    const conditionNumber: number = form.getFieldValue("condition")
    let condition: TreeCondition | undefined
    if (conditionNumber) {
      condition = availableTreeConditionValues[conditionNumber - 1]
    }

    let value = props.initial;
    return {
      id: value.id,
      latitude: value.latitude,
      longitude: value.longitude,
      status: value.status,
      state: form.getFieldValue("state"),
      condition: condition,
      barkCondition: form.getFieldValue("barkCondition"),
      branchesCondition: form.getFieldValue("branchesCondition"),
      comment: comment,
      files: fileList.map(file => ({id: file.uid, name: file.name, url: file.url}))
    }
  }

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
            <Select allowClear>
              <Select.Option value={TreeState.Alive}>Alive</Select.Option>
              <Select.Option value={TreeState.Dead}>Dead</Select.Option>
            </Select>
          </Form.Item>

          <Form.Item name="condition" label="Visual condition">
            <Rate character={({index}: { index?: number }) => rateIcons[index!!]}/>
          </Form.Item>

          <Form.Item name="barkCondition" label="Bark condition">
            <Checkbox.Group>
              {Object.keys(TreeBarkCondition).map(item =>
                  <Checkbox value={item.toUpperCase()}>{item}</Checkbox>
              )}
            </Checkbox.Group>
          </Form.Item>

          <Form.Item name="branchesCondition" label="Branches condition">
            <Checkbox.Group>
              {Object.keys(TreeBranchCondition).map(item =>
                  <Checkbox value={item.toUpperCase()}>{item}</Checkbox>
              )}
            </Checkbox.Group>
          </Form.Item>

          <Form.Item name="comment" label="Comment">
            <TextArea rows={2} maxLength={150} showCount/>
          </Form.Item>

          <Form.Item label="Files">
            <Upload
                listType="picture-card"
                fileList={fileList}
                customRequest={(options) => {
                  const {onSuccess, onError} = options;
                  return api.file.uploadFile({file: options.file as File})
                      .then((response) => onSuccess?.(response))
                      .catch((reason) => onError?.(new Error(reason.message ?? 'File upload error')))
                }}
                onChange={(info) => {
                  let newFileList = [...info.fileList];

                  newFileList = newFileList.map((file) => {
                    let response = file.response;
                    if (response !== undefined) {
                      file.uid = response.fileId
                      file.url = response.url
                    }
                    return file;
                  });

                  setFileList(newFileList);
                }}
            >
              <PlusOutlined/>
            </Upload>
          </Form.Item>
        </Form>
      </Modal>
  )
}

export default TreeView