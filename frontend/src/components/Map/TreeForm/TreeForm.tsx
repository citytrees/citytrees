import {Button, Form, ImageUploader, ImageUploadItem, Input, Picker, Selector, Space, Stepper} from "antd-mobile";
import {CtTree} from "../Models/CtTree";
import {useForm} from "antd/es/form/Form";
import {TreeBarkCondition, TreeBranchCondition, TreePlantingType, TreeState} from "../../../generated/openapi";
import TextArea from "antd/es/input/TextArea";
import api from "../../../api";
import React, {useEffect, useState} from "react";

interface WoodType {
  id: string,
  name: string,
}

interface TreeEditorProps {
  initial: CtTree,
  editable: boolean,
  onCancel?: () => void,
  onSave?: (tree: CtTree) => void,
  onPublish?: (tree: CtTree) => void,
}

const TreeForm = ({...props}: TreeEditorProps) => {
  const [form] = useForm()
  const [fileList, setFileList] = useState<ImageUploadItem[]>([])

  const [woodTypes, setWoodTypes] = useState<Map<string, WoodType>>(new Map())
  const [selectedWoodTypeName, setSelectedWoodTypeName] = useState<string | undefined>()
  const [woodTypePickerLoading, setWoodTypePickerLoading] = useState(true)

  const isFormEditable = props.editable

  const handlePickerInit = () => {
    if (woodTypes.size === 0) {
      api.woodType.getAllWoodTypes()
          .then((response) => {
            setWoodTypePickerLoading(false)
            setWoodTypes(new Map(response.map(item => [item.id, {id: item.id, name: item.name}])))
          })
    }
  }

  useEffect(() => {
    form.resetFields()
    let initialValue = props.initial
    if (initialValue) {
      setSelectedWoodTypeName(initialValue.woodTypeName)
      form.setFieldsValue(initialValue)
      form.setFieldValue("state", [initialValue.state])
      form.setFieldValue("woodTypeId", [initialValue.woodTypeId])
      form.setFieldValue("plantingType", [initialValue.plantingType])
      setFileList(initialValue.files.map(file => ({key: file.id, url: file.url!!})))
    }
  }, [form, props.initial])

  const getCtTree: () => CtTree = () => {
    let state = form.getFieldValue("state");
    let plantingType = form.getFieldValue("plantingType");
    let woodTypeId = form.getFieldValue("woodTypeId");

    let result = {...props.initial, ...form.getFieldsValue()}

    result.woodTypeId = woodTypeId ? woodTypeId[0] : null
    result.state = state ? state[0] : null
    result.plantingType = plantingType ? plantingType[0] : null
    result.files = fileList.map(file => ({id: file.key as string, url: file.url}))

    return result
  }

  return (
      <div>
        <Form
            form={form}
            layout="vertical"
            footer={isFormEditable ? [
              <Space wrap>
                <Button
                    color="primary"
                    style={{display: "inline"}}
                    onClick={() => props.onSave?.(getCtTree())}
                > Save</Button>
                <Button onClick={() => props.onPublish?.(getCtTree())}>Save and publish</Button>
                <Button onClick={() => props.onCancel?.()}> Close</Button>
              </Space>
            ] : null}
        >
          <Form.Item name="latitude" label="Latitude">
            <Input readOnly={true}/>
          </Form.Item>

          <Form.Item name="longitude" label="Longitude">
            <Input readOnly={true}/>
          </Form.Item>

          <Form.Item name="state" label="Alive / dead" disabled={!isFormEditable}>
            <Selector
                options={Object.values(TreeState).map(item => ({label: item, value: item}))}
            />
          </Form.Item>

          <Form.Item name="plantingType" label="Planting type" disabled={!isFormEditable}>
            <Selector
                options={Object.values(TreePlantingType).map(item => ({label: item.toUpperCase(), value: item}))}
            />
          </Form.Item>

          <Form.Item
              name='woodTypeId'
              label={selectedWoodTypeName ?? 'Select type of wood'}
              trigger='onConfirm'
              onClick={(e, datePickerRef) => {
                datePickerRef.current?.open()
                handlePickerInit()
              }}
              disabled={!isFormEditable}
          >
            <Picker
                loading={woodTypePickerLoading}
                mouseWheel={true}
                cancelText="Cancel"
                confirmText="Select"
                onConfirm={(value) => {
                  if (value) {
                    setSelectedWoodTypeName(value[0] ? woodTypes.get(value[0])?.name : undefined)
                  }
                }}
                columns={[Array.from(woodTypes.values()).map(item => ({label: item.name, value: item.id}))]}
            />
          </Form.Item>

          <Form.Item name="barkCondition" label="Bark condition" disabled={!isFormEditable}>
            <Selector
                multiple
                options={Object.values(TreeBarkCondition).map(item => ({label: item, value: item}))}
            />
          </Form.Item>

          <Form.Item name="branchesCondition" label="Branches condition" disabled={!isFormEditable}>
            <Selector
                multiple
                options={Object.values(TreeBranchCondition).map(item => ({label: item, value: item}))}
            />
          </Form.Item>

          <Form.Item name="age" label="Age" disabled={!isFormEditable}>
            <Stepper min={0}/>
          </Form.Item>

          <Form.Item name="diameterOfCrown" label="Diameter of crown" disabled={!isFormEditable}>
            <Stepper min={0} digits={2}/>
          </Form.Item>

          <Form.Item name="heightOfTheFirstBranch" label="Height of the first branch" disabled={!isFormEditable}>
            <Stepper min={0} digits={2}/>
          </Form.Item>

          <Form.Item name="numberOfTreeTrunks" label="Number of tree trunks" disabled={!isFormEditable}>
            <Stepper min={0} digits={2}/>
          </Form.Item>

          <Form.Item name="treeHeight" label="Tree height" disabled={!isFormEditable}>
            <Stepper min={0} digits={2}/>
          </Form.Item>

          <Form.Item name="trunkGirth" label="Trunk girth" disabled={!isFormEditable}>
            <Stepper min={0} digits={2}/>
          </Form.Item>

          <Form.Item name="comment" label="Comment" disabled={!isFormEditable}>
            <TextArea rows={2} maxLength={150} showCount/>
          </Form.Item>

          <Form.Item label="Files">
            <ImageUploader
                disableUpload={!isFormEditable}
                deletable={isFormEditable}
                showUpload={isFormEditable}
                maxCount={8}
                value={fileList}
                upload={(file) =>
                    api.file.uploadFile({file: file})
                        .then((response) => ({url: response.url, key: response.fileId}))}
                onChange={(items) => setFileList([...items])}
            />
          </Form.Item>
        </Form>
      </div>
  )
}

export default TreeForm;