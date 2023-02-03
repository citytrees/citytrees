import {Button, Form, ImageUploader, ImageUploadItem, Input, Selector, Space} from "antd-mobile";
import {CtTree} from "../Models/CtTree";
import {useForm} from "antd/es/form/Form";
import {TreeBarkCondition, TreeBranchCondition, TreeCondition, TreePlantingType, TreeState} from "../../../generated/openapi";
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

const availableTreeConditionValues = [
  TreeCondition.VeryBad,
  TreeCondition.Bad,
  TreeCondition.Normal,
  TreeCondition.Great,
  TreeCondition.Awesome,
]

const TreeForm = ({...props}: TreeEditorProps) => {
  const [form] = useForm()
  const [fileList, setFileList] = useState<ImageUploadItem[]>([])
  const [woodTypes, setWoodTypes] = useState<WoodType[]>([])

  useEffect(() => {
    form.resetFields()
    let initialValue = props.initial
    if (initialValue) {
      setFileList(initialValue.files.map(file => ({url: file.url!!})) ?? [])
      form.setFieldsValue(initialValue)
      form.setFieldValue("state", [initialValue.state])
      form.setFieldValue("plantingType", [initialValue.plantingType])
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
    let state = form.getFieldValue("state");
    let plantingType = form.getFieldValue("plantingType");
    return {
      id: value.id,
      latitude: value.latitude,
      longitude: value.longitude,
      woodTypeId: form.getFieldValue("woodTypeId"),
      status: value.status,
      state: state ? state[0] : null,
      age: form.getFieldValue("age"),
      condition: condition,
      barkCondition: form.getFieldValue("barkCondition"),
      branchesCondition: form.getFieldValue("branchesCondition"),
      plantingType: plantingType ? plantingType[0] : null,
      comment: comment,
      files: fileList.map(file => ({id: file.key as string, url: file.url})),
      diameterOfCrown: form.getFieldValue("diameterOfCrown"),
      heightOfTheFirstBranch: form.getFieldValue("heightOfTheFirstBranch"),
      numberOfTreeTrunks: form.getFieldValue("numberOfTreeTrunks"),
      treeHeight: form.getFieldValue("treeHeight"),
      trunkGirth: form.getFieldValue("trunkGirth"),
    }
  }

  return (
      <div>
        <Form
            form={form}
            layout="vertical"
            footer={props.editable ? [
              <Space direction="vertical">
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
            <Input disabled={true}/>
          </Form.Item>

          <Form.Item name="longitude" label="Longitude">
            <Input disabled={true}/>
          </Form.Item>


          <Form.Item name="state" label="Alive / dead">
            <Selector
                options={Object.values(TreeState).map(item => ({label: item, value: item}))}
            />
          </Form.Item>

          <Form.Item name="plantingType" label="Planting type">
            <Selector
                options={Object.values(TreePlantingType).map(item => ({label: item.toUpperCase(), value: item}))}
            />
          </Form.Item>

          <Form.Item name="woodTypeId" label="Type of wood">
            <Selector
                multiple
                options={woodTypes.map(item => ({label: item.name, value: item.id}))}
            />
          </Form.Item>

          <Form.Item name="barkCondition" label="Bark condition">
            <Selector
                multiple
                options={Object.values(TreeBarkCondition).map(item => ({label: item, value: item}))}
            />
          </Form.Item>

          <Form.Item name="branchesCondition" label="Branches condition">
            <Selector
                multiple
                options={Object.values(TreeBranchCondition).map(item => ({label: item, value: item}))}
            />
          </Form.Item>

          <Form.Item name="age" label="Age">
            <Input type="numeric"/>
          </Form.Item>

          <Form.Item name="diameterOfCrown" label="Diameter of crown">
            <Input type="numeric"/>
          </Form.Item>

          <Form.Item name="heightOfTheFirstBranch" label="Height of the first branch">
            <Input type="numeric"/>
          </Form.Item>

          <Form.Item name="numberOfTreeTrunks" label="Number of tree trunks">
            <Input type="numeric"/>
          </Form.Item>

          <Form.Item name="treeHeight" label="Tree height">
            <Input type="numeric"/>
          </Form.Item>

          <Form.Item name="trunkGirth" label="Trunk girth">
            <Input type="numeric"/>
          </Form.Item>

          <Form.Item name="comment" label="Comment">
            <TextArea rows={2} maxLength={150} showCount/>
          </Form.Item>

          <Form.Item label="Files">
            <ImageUploader
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