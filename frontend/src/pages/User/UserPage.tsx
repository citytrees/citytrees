import React, {useEffect} from "react";
import {Button, Form, Input, Space, Toast} from "antd-mobile";
import {useForm} from "antd/lib/form/Form";
import api from "../../api";
import {useUser} from "../../app/hooks";

const UserPage: React.FC = () => {
  const user = useUser()
  const [form] = useForm()

  useEffect(() => {
    if (user) {
      api.user.getUserById({id: user.sub})
          .then(response => {
            form.setFieldsValue(response)
          })
    }
  }, [form, user])

  const handleSave = () => {
    const formUser = form.getFieldsValue()
    if (user) {
      api.user.updateUserById({
        id: user.sub,
        userUpdateRequest: {
          lastName: formUser.lastName,
          firstName: formUser.firstName
        }
      }).then(() => Toast.show({content: "Updated!"}))
    }
  }

  return (
      <Form
          form={form}
          footer={<Space>
            <Button color="primary" fill="outline" onClick={handleSave}>Submit</Button>
          </Space>}
      >
        <Form.Item name="lastName" label="Last name">
          <Input/>
        </Form.Item>
        <Form.Item name="firstName" label="First name">
          <Input/>
        </Form.Item>
      </Form>
  )
};

export default UserPage;
