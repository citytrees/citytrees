import {AuthControllerApi, UserControllerApi} from "../generated/openapi";

const api = {
    auth: new AuthControllerApi(),
    user: new UserControllerApi()
}
export default api
