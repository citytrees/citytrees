import React from "react";
import {Button, Form, Input, notification} from 'antd';
import StyledCenteredForm from "../../components/forms/StyledCenteredForm";
import {useTranslation} from "react-i18next";
import api from "../../api";
import {ErrorResponse} from "../../generated/openapi";

function RegistrationPage() {
  const {t} = useTranslation();
  const [form] = Form.useForm();
  const email = Form.useWatch('email', form);
  const password = Form.useWatch('password', form);

  return (
      <StyledCenteredForm>
        <Form
            form={form}
            name="basic"
            layout="vertical"
            initialValues={{remember: true}}
            autoComplete="off"
        >
          <Form.Item
              label={t('registration-page.email.label')}
              name="email"
              rules={[{required: true, message: t<string>('registration-page.email.emptyError')}]}
          >
            <Input/>
          </Form.Item>

          <Form.Item
              label={t('registration-page.password.label')}
              name="password"
              rules={[{required: true, message: t<string>('registration-page.password.emptyError')}]}
          >
            <Input.Password/>
          </Form.Item>

          <Form.Item>
            <Button
                type="primary"
                htmlType="submit"
                onClick={() =>
                    api.user.registerNewUser({userRegisterRequest: {email: email, password: password}})
                        .then(data => {
                          console.log(data)
                        })
                        .catch(error => {
                          error.response.json()
                              .then((body: ErrorResponse) => {
                                notification.open({
                                  message: body.message
                                })
                              })
                        })
                }
            >
              {t('registration-page.submit')}
            </Button>
          </Form.Item>
        </Form>
      </StyledCenteredForm>
  );
}

export default RegistrationPage;