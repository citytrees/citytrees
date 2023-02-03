import React, {useEffect, useState} from "react";
import {Button, Checkbox, Col, Dropdown, Form, FormInstance, Input, InputNumber, Modal, Rate, Row, Select, Space, Upload, UploadFile} from "antd";
import {ModalProps} from "antd/lib/modal/Modal";
import {useForm} from "antd/es/form/Form";
import {CtTree} from "../Models/CtTree";
import {FrownOutlined, MehOutlined, PlusOutlined, SmileOutlined} from '@ant-design/icons';
import {TreeBarkCondition, TreeBranchCondition, TreeCondition, TreePlantingType, TreeState} from "../../../generated/openapi";
import api from "../../../api";
import TextArea from "antd/es/input/TextArea";

interface TreeEditorProps {
  initial: CtTree,
  editable: boolean,
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

interface WoodType {
  id: string,
  name: string,
}

// todo #18 optimize
const TreeView = ({...props}: ModalProps & TreeEditorProps) => {
  const [form]: [FormInstance<CtTree>] = useForm()

  const [fileList, setFileList] = useState<UploadFile[]>([])
  const [woodTypes, setWoodTypes] = useState<WoodType[]>([])

  useEffect(() => {
    form.resetFields()
    let initialValue = props.initial
    if (initialValue) {
      setFileList(initialValue.files.map(file => ({uid: file.id, name: file.id, status: 'done', url: file.url,})) ?? [])
      form.setFieldsValue(initialValue)
      form.setFieldValue("condition", initialValue.condition ? availableTreeConditionValues.indexOf(initialValue.condition) + 1 : null)
      api.woodType.getAllWoodTypes()
          .then((responce) => {
            setWoodTypes(responce.map(type => ({id: type.id, name: type.name})))
          })
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
      woodTypeId: form.getFieldValue("woodTypeId"),
      status: value.status,
      state: form.getFieldValue("state"),
      age: form.getFieldValue("age"),
      condition: condition,
      barkCondition: form.getFieldValue("barkCondition"),
      branchesCondition: form.getFieldValue("branchesCondition"),
      plantingType: form.getFieldValue("plantingType"),
      comment: comment,
      files: fileList.map(file => ({id: file.uid, name: file.name, url: file.url})),
      diameterOfCrown: form.getFieldValue("diameterOfCrown"),
      heightOfTheFirstBranch: form.getFieldValue("heightOfTheFirstBranch"),
      numberOfTreeTrunks: form.getFieldValue("numberOfTreeTrunks"),
      treeHeight: form.getFieldValue("treeHeight"),
      trunkGirth: form.getFieldValue("trunkGirth"),
    }
  }

  const rateIcons: Record<number, React.ReactNode> = {
    0: <FrownOutlined/>,
    1: <FrownOutlined/>,
    2: <MehOutlined/>,
    3: <SmileOutlined/>,
    4: <SmileOutlined/>,
  }

  const [previewOpen, setPreviewOpen] = useState(false);
  const [previewImage, setPreviewImage] = useState('');

  const handlePreview = async (file: UploadFile) => {
    if (file.url) {
      setPreviewImage(file.url);
      setPreviewOpen(true);
    }
  };

  return (
      <div>
        <Modal
            {...props}
            footer={props.editable ? [
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
            ] : null}
        >
          <Form
              form={form}
              disabled={!props.editable}
              layout="vertical"
          >
            <Row gutter={12}>
              <Col span={12}>
                <Form.Item name="latitude" label="Latitude">
                  <Input disabled={true}/>
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item name="longitude" label="Longitude">
                  <Input disabled={true}/>
                </Form.Item>
              </Col>
            </Row>

            <Row gutter={12}>
              <Col span={8}>
                <Form.Item name="state" label="Alive / dead">
                  <Select allowClear>
                    {Object.keys(TreeState).map(item =>
                        <Select.Option value={item.toUpperCase()}>{item}</Select.Option>
                    )}
                  </Select>
                </Form.Item>
              </Col>

              <Col span={8}>
                <Form.Item name="plantingType" label="Planting type">
                  <Select allowClear>
                    {Object.values(TreePlantingType).map(item =>
                        <Select.Option value={item}>{item}</Select.Option>
                    )}
                  </Select>
                </Form.Item>
              </Col>

              <Col span={8}>
                <Form.Item name="woodTypeId" label="Type of wood">
                  <Select allowClear>
                    {woodTypes.map(type => <Select.Option value={type.id}>{type.name}</Select.Option>)}
                  </Select>
                </Form.Item>
              </Col>
            </Row>

            <Form.Item name="condition" label="Visual condition">
              <Rate disabled={!props.editable} character={({index}: { index?: number }) => rateIcons[index!!]}/>
            </Form.Item>

            <Form.Item name="barkCondition" label="Bark condition">
              <Checkbox.Group>
                {Object.values(TreeBarkCondition).map(item =>
                    <Checkbox value={item}>{item}</Checkbox>
                )}
              </Checkbox.Group>
            </Form.Item>

            <Form.Item name="branchesCondition" label="Branches condition">
              <Checkbox.Group>
                {Object.values(TreeBranchCondition).map(item =>
                    <Checkbox value={item}>{item}</Checkbox>
                )}
              </Checkbox.Group>
            </Form.Item>

            <Row gutter={12}>
              <Col span={8}>
                <Form.Item name="age" label="Age">
                  <InputNumber/>
                </Form.Item>
              </Col>

              <Col span={8}>
                <Form.Item name="diameterOfCrown" label="Diameter of crown">
                  <InputNumber step={0.01}/>
                </Form.Item>
              </Col>

              <Col span={8}>
                <Form.Item name="heightOfTheFirstBranch" label="Height of the first branch">
                  <InputNumber step={0.01}/>
                </Form.Item>
              </Col>
            </Row>

            <Row gutter={12}>
              <Col span={8}>
                <Form.Item name="numberOfTreeTrunks" label="Number of tree trunks">
                  <InputNumber/>
                </Form.Item>
              </Col>

              <Col span={8}>
                <Form.Item name="treeHeight" label="Tree height">
                  <InputNumber step={0.01}/>
                </Form.Item>
              </Col>

              <Col span={8}>
                <Form.Item name="trunkGirth" label="Trunk girth">
                  <InputNumber step={0.01}/>
                </Form.Item>
              </Col>
            </Row>

            <Form.Item name="comment" label="Comment">
              <TextArea rows={2} maxLength={150} showCount/>
            </Form.Item>

            <Form.Item label="Files">
              <Upload
                  listType="picture-card"
                  maxCount={8}
                  fileList={fileList}
                  onPreview={handlePreview}
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
                {props.editable && <PlusOutlined/>}
              </Upload>
            </Form.Item>
          </Form>
        </Modal>
        <Modal open={previewOpen} footer={null} onCancel={() => setPreviewOpen(false)}>
          <img alt="preview" style={{width: '100%'}} src={previewImage}/>
        </Modal>
      </div>
  )
}

export default TreeView