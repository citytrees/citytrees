import {TreeBarkCondition, TreeBranchCondition, TreeCondition, TreeGetAttachedFileResponse, TreeGetResponse, TreePlantingType, TreeState, TreeStatus} from "../../../generated/openapi";
import {CtFile} from "./CtFile";

export interface CtTree {
    id: string,
    latitude: number
    longitude: number
    woodTypeId?: string
    status: TreeStatus
    state?: TreeState
    age?: number,
    condition?: TreeCondition
    barkCondition?: TreeBarkCondition[]
    branchesCondition?: TreeBranchCondition[]
    plantingType?: TreePlantingType
    comment?: string
    userId?: string
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
        age: tree.age,
        condition: tree.condition,
        barkCondition: tree.barkCondition,
        branchesCondition: tree.branchesCondition,
        plantingType: tree.plantingType,
        comment: tree.comment,
        userId: tree.userId,
        files: files.map(file => ({id: file.id, name: file.name, url: file.url}))
    }
}
