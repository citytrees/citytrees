import React from "react";
import {Button, Form, Input, notification} from 'antd';
import {useTranslation} from "react-i18next";
import api from "../../api";
import {ErrorResponse} from "../../generated/openapi";
import {useNavigate} from "react-router-dom";
import CenteredContainer from "../../components/forms";
import AppRoutes from "../../constants/AppRoutes";

function RegistrationPage() {
  let navigate = useNavigate();
  const {t} = useTranslation();
  const [form] = Form.useForm();
  const email = Form.useWatch('email', form);
  const password = Form.useWatch('password', form);

  const validatePassword = (rule: any, value: string, callback: any) => {
    let passwordPattern = /^(?=.*\d)(?=.*[_?,.!@#$%^&*])(?=.*[a-z])(?=.*[A-Z]).{8,}$/;
    if (value && passwordPattern.test(value)) {
      callback();
    } else {
      callback(t('registrationPage.password.notValidError'));
    }
  };

  return (
      <CenteredContainer>
        <Form
            form={form}
            name="basic"
            layout="vertical"
            autoComplete="off"
        >
          <Form.Item
              label={t('registrationPage.email.label')}
              name="email"
              rules={[
                {
                  type: 'email',
                  required: true,
                  message: t<string>('registrationPage.email.notValidError'),
                }
              ]}
          >
            <Input/>
          </Form.Item>

          <Form.Item
              label={t('registrationPage.password.label')}
              name="password"
              rules={
                [
                  {validator: validatePassword},
                  {required: true, message: t<string>('registrationPage.password.emptyError')}
                ]
              }
              hasFeedback
          >
            <Input.Password/>
          </Form.Item>

          <Form.Item>
            <Button
                type="primary"
                htmlType="submit"
                onClick={() =>
                    api.user.registerNewUser({userRegisterRequest: {email: email, password: password}})
                        .then(() => {
                          navigate(AppRoutes.LOGIN)
                        })
                        .catch(error => {
                          error.response.json()
                              .then((body: ErrorResponse) => {
                                    notification.open({
                                      message: body.message,
                                      type: "error",
                                      placement: "top"
                                    })
                                  }
                              )
                        })
                }
            >
              {t('registrationPage.submit')}
            </Button>
            <Button
                type="link"
                onClick={() => navigate(AppRoutes.LOGIN)}
            >
              {t('registrationPage.signIn.label')}
            </Button>
          </Form.Item>
        </Form>
      </CenteredContainer>
  );
}

export default RegistrationPage;