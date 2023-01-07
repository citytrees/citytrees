import {TreeCondition, TreeGetResponse, TreeState, TreeStatus} from "../../../generated/openapi";

export interface CtTree {
    id: string,
    latitude: number
    longitude: number
    status: TreeStatus,
    state?: TreeState,
    condition?: TreeCondition,
    comment?: string,
    files?: string[]
}

export const ctTreeOf: (tree: TreeGetResponse) => CtTree = (tree: TreeGetResponse) => {
    return {
        id: tree.id,
        latitude: tree.latitude,
        longitude: tree.longitude,
        status: tree.status,
        state: tree.state,
        condition: tree.condition,
        comment: tree.comment,
        files: []
    }
}
