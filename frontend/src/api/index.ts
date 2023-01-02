import {AuthControllerApi, Configuration, TreeControllerApi, TreesControllerApi, UserControllerApi} from "../generated/openapi";
import authMiddleware from "./authMiddleware";
import {getAccessToken} from "../helpers/cookies";

const api = {
    auth: new AuthControllerApi(),
    tree: new TreeControllerApi(new Configuration({accessToken: () => getAccessToken() || ""}))
        .withMiddleware(authMiddleware),
    trees: new TreesControllerApi(),
    user: new UserControllerApi(new Configuration({accessToken: () => getAccessToken() || ""}))
        .withMiddleware(authMiddleware),
}
export default api
