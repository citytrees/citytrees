import {TreeBarkCondition, TreeBranchCondition, TreeCondition, TreeGetAttachedFileResponse, TreeGetResponse, TreeState, TreeStatus} from "../../../generated/openapi";
import {CtFile} from "./CtFile";

export interface CtTree {
    id: string,
    latitude: number
    longitude: number
    woodTypeId?: string
    status: TreeStatus
    state?: TreeState
    condition?: TreeCondition
    barkCondition?: TreeBarkCondition[]
    branchesCondition?: TreeBranchCondition[]
    comment?: string
    files: CtFile[]
}

export const ctTreeOf: (tree: TreeGetResponse, files: TreeGetAttachedFileResponse[]) => CtTree = (tree, files) => {
    return {
        id: tree.id,
        latitude: tree.latitude,
        longitude: tree.longitude,
        woodTypeId: tree.woodTypeId,
        status: tree.status,
        state: tree.state,
        condition: tree.condition,
        barkCondition: tree.barkCondition,
        branchesCondition: tree.branchesCondition,
        comment: tree.comment,
        files: files.map(file => ({id: file.id, name: file.name, url: file.url}))
    }
}
