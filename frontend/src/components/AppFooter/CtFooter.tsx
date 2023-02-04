import {Footer} from "antd-mobile";
import React from "react";
import {LinkItem} from "antd-mobile/es/components/footer";

// TODO
const CtFooter: React.FC = () => {
  const links: LinkItem[] = [
    {
      text: 'About us',
      href: 'https://www.aliyun.com/',
    },
    {
      text: 'VK',
      href: 'https://www.antgroup.com/',
    },
    {
      text: 'email@example.com',
      href: 'mailto:email@example.com',
    },
  ]
  return (
      <Footer links={links}/>
  )
};

export default CtFooter;
