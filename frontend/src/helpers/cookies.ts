import Cookies from "js-cookie";


export const getAccessToken = () => Cookies.get("ct_access_token") as string | null
export const removeAccessToken = () => Cookies.remove("ct_access_token")
