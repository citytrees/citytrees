import {AuthControllerApi, Configuration, FileControllerApi, TreeControllerApi, TreeFilesControllerApi, TreesControllerApi, UserControllerApi} from "../generated/openapi";
import authMiddleware from "./authMiddleware";
import {getAccessToken} from "../helpers/cookies";

const api = {
    auth: new AuthControllerApi(),
    file: new FileControllerApi(new Configuration({accessToken: () => getAccessToken() || ""}))
        .withMiddleware(authMiddleware),
    tree: new TreeControllerApi(new Configuration({accessToken: () => getAccessToken() || ""}))
        .withMiddleware(authMiddleware),
    treeFiles: new TreeFilesControllerApi(new Configuration({accessToken: () => getAccessToken() || ""}))
        .withMiddleware(authMiddleware),
    trees: new TreesControllerApi(),
    user: new UserControllerApi(new Configuration({accessToken: () => getAccessToken() || ""}))
        .withMiddleware(authMiddleware),
}
export default api
