import React from "react";
import {Tabs} from "antd-mobile";
import {useTranslation} from "react-i18next";
import {useUser} from "../../app/hooks";
import {useLocation, useNavigate} from "react-router-dom";
import AppRoutes from "../../constants/AppRoutes";
import {Space} from "antd";
import {isMobile} from 'react-device-detect';
import {CollectMoneyOutline, LocationFill, TagOutline, UnorderedListOutline, UserContactOutline, UserOutline} from "antd-mobile-icons"
import {isUserAdmin} from "../../features/user/userSlice";

const CtHeader: React.FC = () => {
  const {t} = useTranslation()
  const user = useUser();
  const navigate = useNavigate()
  const location = useLocation()

  const tabs: any[] = [
    {
      label: t("appHeader.menuItems.map"),
      key: AppRoutes.MAIN,
      icon: <LocationFill/>
    },
  ];

  if (user) {
    tabs.push({
      label: t("appHeader.menuItems.myTrees"),
      key: AppRoutes.MY_TREES,
      icon: <TagOutline/>
    })
    if (isUserAdmin(user)) {
      tabs.push({
            label: t("appHeader.menuItems.allTrees"),
            key: AppRoutes.ALL_TREES,
            icon: <UnorderedListOutline/>
          },
          {
            label: t("appHeader.menuItems.users"),
            key: AppRoutes.USERS,
            icon: <UserContactOutline/>
          },
          {
            label: t("appHeader.menuItems.woodTypes"),
            key: AppRoutes.WOOD_TYPE,
            icon: <CollectMoneyOutline/>
          })
    }
  }

  tabs.push({
    label: t("appHeader.menuItems.profile"),
    key: user !== null ? AppRoutes.USER : AppRoutes.LOGIN,
    icon: <UserOutline/>
  })

  return (
      <div style={{display: "flex"}}>
        <Tabs style={{flex: "1"}} activeKey={location.pathname}
              onChange={(activeKey) => {
                navigate(activeKey)
              }}>
          {tabs.map((tab) =>
              <Tabs.Tab
                  title={isMobile ? tab.icon : <Space>{tab.icon}{tab.label}</Space>}
                  key={tab.key}
              />
          )}
        </Tabs>
      </div>
  )
};

export default CtHeader;
