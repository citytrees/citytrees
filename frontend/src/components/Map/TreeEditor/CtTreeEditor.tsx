import React, {useEffect} from "react";
import {Form, FormInstance, Input, Modal} from "antd";
import {ModalProps} from "antd/lib/modal/Modal";
import {useForm} from "antd/es/form/Form";
import {CtTree} from "../Models/CtTree";

interface TreeEditorProps {
  isEditable: boolean,
  initial?: CtTree,
  onCreate?: (tree: CtTree) => void,
}

const CtTreeEditor = ({...props}: ModalProps & TreeEditorProps) => {
  const [form]: [FormInstance<CtTree>] = useForm()


  useEffect(() => {
    form.resetFields()
    let initialValue = props.initial;
    if (initialValue)
      form.setFieldsValue(initialValue)

  }, [form, props.open, props.initial])

  const getCtTree: () => CtTree = () => {
    return {
      latitude: form.getFieldValue("latitude"),
      longitude: form.getFieldValue("longitude"),
    }
  }

  return (
      <Modal
          {...props}
          onOk={(e) => {
            if (props.isEditable) props.onCreate ? props.onCreate(getCtTree()) : props.onOk?.(e)
          }}
          okButtonProps={{style: {display: props.isEditable ? 'initial' : 'none'}}}
          cancelButtonProps={{style: {display: props.isEditable ? 'initial' : 'none'}}}
      >
        <Form form={form} layout="vertical">
          <Form.Item name="latitude" label="Latitude">
            <Input disabled={!props.isEditable}/>
          </Form.Item>
          <Form.Item name="longitude" label="longitude">
            <Input disabled={!props.isEditable}/>
          </Form.Item>
        </Form>
      </Modal>
  )
}

export default CtTreeEditor