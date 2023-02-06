import {TreeBarkCondition, TreeBranchCondition, TreeCondition, TreeGetAttachedFileResponse, TreeGetResponse, TreePlantingType, TreeState, TreeStatus} from "../../../generated/openapi";
import {CtFile} from "./CtFile";
import {NullableUser} from "../../../features/user/userSlice";
import {CtTreeShort} from "./CtTreeShort";
import api from "../../../api";

export interface CtTree {
    id: number,
    latitude: number
    longitude: number
    woodTypeId?: string
    woodTypeName?: string
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
    diameterOfCrown?: number,
    heightOfTheFirstBranch?: number,
    numberOfTreeTrunks?: number,
    treeHeight?: number,
    trunkGirth?: number
}

export const ctTreeOf: (tree: TreeGetResponse, files: TreeGetAttachedFileResponse[]) => CtTree = (tree, files) => {
    return {
        id: tree.id,
        latitude: tree.latitude,
        longitude: tree.longitude,
        woodTypeId: tree.woodTypeId,
        woodTypeName: tree.woodTypeName,
        status: tree.status,
        state: tree.state,
        age: tree.age,
        condition: tree.condition,
        barkCondition: tree.barkCondition,
        branchesCondition: tree.branchesCondition,
        plantingType: tree.plantingType,
        comment: tree.comment,
        userId: tree.userId,
        files: files.map(file => ({id: file.id, name: file.name, url: file.url})),
        diameterOfCrown: tree.diameterOfCrown,
        heightOfTheFirstBranch: tree.heightOfTheFirstBranch,
        numberOfTreeTrunks: tree.numberOfTreeTrunks,
        treeHeight: tree.treeHeight,
        trunkGirth: tree.trunkGirth,
    }
}

export const isTreeEditable = (tree: CtTree | CtTreeShort, user: NullableUser) => {
    const isTreeEditable = tree.status === TreeStatus.New || tree.status === TreeStatus.ToApprove;
    const isUserHasPermission = user !== null && tree.userId === user.sub;
    return isUserHasPermission && isTreeEditable
}

export const isTreeDeletable = (tree: CtTree | CtTreeShort, user: NullableUser) => {
    const isTreeDeletable = tree.status === TreeStatus.New || tree.status === TreeStatus.ToApprove;
    const isUserHasPermission = user !== null && tree.userId === user.sub;
    return isUserHasPermission && isTreeDeletable
}

export const updateTree = (tree: CtTree, status: TreeStatus, onSuccess: () => void) => {
    api.tree.updateTreeById(
        {
            id: tree.id,
            treeUpdateRequest: {
                woodTypeId: tree.woodTypeId,
                status: status,
                state: tree.state,
                age: tree.age,
                condition: tree.condition,
                barkCondition: tree.barkCondition,
                branchesCondition: tree.branchesCondition,
                plantingType: tree.plantingType,
                comment: tree.comment,
                fileIds: tree.files.map(file => file.id),
                diameterOfCrown: tree.diameterOfCrown,
                heightOfTheFirstBranch: tree.heightOfTheFirstBranch,
                numberOfTreeTrunks: tree.numberOfTreeTrunks,
                treeHeight: tree.treeHeight,
                trunkGirth: tree.trunkGirth,
            }
        }
    ).then(onSuccess)
}

export const deleteTree = (tree: CtTree | CtTreeShort, onSuccess: () => void) => {
    api.tree.deleteTree({id: tree.id}).then(onSuccess)
}