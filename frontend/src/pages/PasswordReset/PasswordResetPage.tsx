import {useNavigate, useSearchParams} from "react-router-dom";
import {useTranslation} from "react-i18next";
import {Button, Form, Input} from "antd";
import CenteredContainer from "../../components/forms";
import React, {useState} from "react";
import AppRoutes from "../../constants/AppRoutes";
import api from "../../api";

function RequestPasswordResetForm() {
  const {t} = useTranslation();
  const [isResetRequested, setIsResetRequested] = useState(false)
  const navigate = useNavigate();
  const [form] = Form.useForm();
  const email = Form.useWatch('email', form);

  return (
      <div>
        {isResetRequested ? <p>Check your email to reset password</p> :
            <Form
                form={form}
                name="basic"
                layout="vertical"
                autoComplete="off"
            >
              <Form.Item
                  label={t('passwordResetPage.email.label')}
                  name="email"
                  rules={[
                    {
                      required: true,
                    },
                  ]}
              >
                <Input/>
              </Form.Item>

              <Form.Item>
                <Button
                    type="primary"
                    htmlType="submit"
                    onClick={() => {
                      api.user.requestPasswordReset({userRequestPasswordResetRequest: {email: email}})
                          .then(() => {
                            setIsResetRequested(true)
                          })
                          .catch(() => {
                            // TODO #12
                            setIsResetRequested(true)
                          })
                    }}
                >
                  {t('passwordResetPage.submit.label')}
                </Button>
                <Button
                    type="link"
                    onClick={() => navigate(AppRoutes.LOGIN)}
                >
                  {t('passwordResetPage.signIn.label')}
                </Button>
              </Form.Item>
            </Form>
        }
      </div>
  )
}

enum ResetState {
  WAITING,
  SUCCESS,
  ERROR
}

function NewPasswordForm({userEmail, resetId}: { userEmail: string, resetId: string }) {
  const [form] = Form.useForm();
  const password = Form.useWatch('password', form);
  const [isReset, setIsReset] = useState(ResetState.WAITING)

  const content = () => {
    if (isReset === ResetState.WAITING) {
      return <Form
          form={form}
          name="basic"
          layout="vertical"
          autoComplete="off"
      >
        <p>Change password for {userEmail}</p>
        <Form.Item
            label="Password"
            name="password"
            rules={[{required: true, message: "Please set new password"}]}
        >
          <Input.Password/>
        </Form.Item>
        <Form.Item>
          <Button
              type="primary"
              htmlType="submit"
              onClick={() =>
                  api.user.resetPassword({userPasswordResetRequest: {email: userEmail, token: resetId, newPassword: password}})
                      .then(() => {
                        setIsReset(ResetState.SUCCESS)
                      })
                      .catch(() => {
                        setIsReset(ResetState.ERROR)
                      })
              }
          >
            "Submit"
          </Button>
        </Form.Item>
      </Form>
    } else if (isReset === ResetState.SUCCESS) {
      return <p>New password was set</p>
    } else {
      return <p>Error</p>
    }
  }

  return (
      <div>
        {content()}
      </div>
  )
}

function PasswordResetPage() {
  const [searchParams] = useSearchParams();

  const content = () => {
    const userEmail = searchParams.get("userEmail")
    const resetId = searchParams.get("resetId")

    if (userEmail !== null && resetId !== null) {
      return <NewPasswordForm userEmail={userEmail} resetId={resetId}/>
    } else {
      return <RequestPasswordResetForm/>
    }
  }

  return (
      <CenteredContainer>
        {content()}
      </CenteredContainer>
  )
}

export default PasswordResetPage;