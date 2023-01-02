import React, {useEffect, useState} from "react";
import 'leaflet/dist/leaflet.css';
import {Button, Form, FormInstance, Input, notification, Upload, UploadFile, UploadProps} from "antd";
import {PlusOutlined} from "@ant-design/icons";
import {useForm} from "antd/es/form/Form";
import {CtTree} from "../Models/CtTree";

const TreeForm = (
    {
      initValue,
      isEditable,
      onSubmit,
      onCancel
    }: {
      initValue?: CtTree,
      isEditable: boolean,
      onSubmit: (form: FormInstance, ctTree: CtTree) => void,
      onCancel: (form: FormInstance, ctTree: CtTree) => void,
    }
) => {
  const [form] = useForm()
  const [fileList, setFileList] = useState<UploadFile[]>([])

  useEffect(() => {
    notification.open({
      message: "effect form",
      type: "info",
      placement: "topRight"
    })
    form.resetFields()
    setFileList([])
    if (initValue) {
      form.setFieldsValue(
          {
            latitude: initValue.latitude,
            longitude: initValue.longitude,
            fileList: initValue.files,
          }
      )
    }
  }, [form, initValue])

  const handleUploadChange: UploadProps['onChange'] = ({fileList: newFileList}) =>
      setFileList(newFileList)

  const getFormValue = () => (
      {
        latitude: form.getFieldValue("latitude"),
        longitude: form.getFieldValue("longitude"),
        files: fileList.map(file => file.name) as string[]
      }
  )

  return (
      <Form
          form={form}
          layout="vertical"
      >
        <Form.Item name="latitude" label="Latitude">
          <Input disabled={isEditable}></Input>
        </Form.Item>
        <Form.Item name="longitude" label="Longitude">
          <Input disabled={isEditable}></Input>
        </Form.Item>
        <Form.Item name="fileList">
          <Upload
              multiple={true}
              fileList={fileList}
              listType="picture-card"
              onChange={handleUploadChange}
          >
            <PlusOutlined/>
          </Upload>
        </Form.Item>
        {isEditable ? <Form.Item>
          <Button
              type="primary"
              onClick={() => onSubmit(form, getFormValue())}
          >
            Submit
          </Button>
          <Button
              onClick={() => onCancel(form, getFormValue())}
          >
            Cancel
          </Button>
        </Form.Item> : null}
      </Form>
  )
};

export default TreeForm;