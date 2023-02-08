import {Button, Form, ImageUploader, ImageUploadItem, Input, Picker, Selector, Space, Stepper} from "antd-mobile";
import {CtTree} from "../Models/CtTree";
import {useForm} from "antd/es/form/Form";
import {TreeBarkCondition, TreeBranchCondition, TreePlantingType, TreeState} from "../../../generated/openapi";
import TextArea from "antd/es/input/TextArea";
import api from "../../../api";
import React, {ReactNode, useEffect, useState} from "react";
import {isMobile} from 'react-device-detect';
import AppRoutes from "../../../constants/AppRoutes";


interface WoodType {
  id: string,
  name: string,
}

interface TreeEditorProps {
  initial: CtTree
  editable: boolean
  onCancel?: () => void
  onSave?: (tree: CtTree) => void
  onPublish?: (tree: CtTree) => void
  onDelete?: (tree: CtTree) => void
  isDeletable?: boolean
  footerElements?: ReactNode[]
  enableOpenOnMapOption?: boolean
}

const TreeForm = ({...props}: TreeEditorProps) => {
  const [form] = useForm()
  const [fileList, setFileList] = useState<ImageUploadItem[]>([])

  const [woodTypes, setWoodTypes] = useState<Map<string, WoodType>>(new Map())
  const [selectedWoodTypeName, setSelectedWoodTypeName] = useState<string | undefined>()
  const [woodTypePickerLoading, setWoodTypePickerLoading] = useState(true)

  const isFormEditable = props.editable
  const isTreeDeletable = props.isDeletable

  const handlePickerInit = () => {
    if (woodTypes.size === 0) {
      api.woodType.getAllWoodTypes()
          .then((response) => {
            setWoodTypePickerLoading(false)
            let woodTypesMap = new Map(response.map(item => [item.id, {id: item.id, name: item.name}]))
            let initialTree = props.initial;

            let initialWoodTypeId = initialTree.woodTypeId
            if (initialWoodTypeId !== undefined && !woodTypesMap.has(initialWoodTypeId)) {
              woodTypesMap.set(initialWoodTypeId, {id: initialWoodTypeId, name: initialTree.woodTypeName!!})
            }
            setWoodTypes(woodTypesMap)
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

  function renderFooter() {
    const items = []

    if (props.enableOpenOnMapOption === undefined || props.enableOpenOnMapOption) {
      items.push(
          <Button
              key="tree-open-on-map"
              color="primary"
              fill="outline"
              size='small'
              onClick={() => {
                const tree = getCtTree()
                window.open(`${AppRoutes.MAIN}?lat=${tree.latitude}&lng=${tree.longitude}`, '_blank')
              }}
          >Open on Map</Button>
      )
    }

    if (isFormEditable) {
      items.push(
          <Button
              color="primary"
              fill="outline"
              style={{display: "inline"}}
              size='small'
              onClick={() => props.onSave?.(getCtTree())}
          > Save</Button>,
          <Button color="primary" fill="outline" size='small' onClick={() => props.onPublish?.(getCtTree())}>Save and publish</Button>,
          <Button size='small' onClick={() => props.onCancel?.()}>Close</Button>
      )
    }

    if (isTreeDeletable) {
      items.push(<Button color='danger' size='small' onClick={() => props.onDelete?.(getCtTree())}>Delete</Button>)
    }

    if (props.footerElements) {
      items.push(props.footerElements)
    }

    if (items.length !== 0) {
      return <Space wrap direction={isMobile ? "vertical" : "horizontal"}>{items}</Space>
    } else {
      return null
    }
  }

  return (
      <div>
        <Form
            form={form}
            layout="vertical"
            footer={renderFooter()}
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