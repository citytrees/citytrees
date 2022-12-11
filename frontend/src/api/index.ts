import {AuthControllerApi, Configuration, UserControllerApi} from "../generated/openapi";
import authMiddleware from "./authMiddleware";
import {getAccessToken} from "../helpers/cookies";

const api = {
    auth: new AuthControllerApi(),
    user: new UserControllerApi(new Configuration({accessToken: () => getAccessToken() || ""}))
        .withMiddleware(authMiddleware)
}
export default api
