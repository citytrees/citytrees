import {instanceOfTreeGetResponse, TreeGetResponse, TreesGetResponseTree, TreeStatus} from "../../../generated/openapi";

export interface CtTree {
    id: string,
    latitude: number
    longitude: number
    status: TreeStatus,
    files?: string[]
}

export const ctTreeOf: (tree: TreeGetResponse | TreesGetResponseTree) => CtTree = (tree: TreesGetResponseTree) => {
    if (instanceOfTreeGetResponse(tree)) {
        return {
            id: tree.id,
            latitude: tree.latitude,
            longitude: tree.longitude,
            status: tree.status,
            files: []
        }
    } else {
        return {
            id: tree.id,
            status: tree.status,
            latitude: tree.latitude,
            longitude: tree.longitude,
            files: [],
        }
    }
}
