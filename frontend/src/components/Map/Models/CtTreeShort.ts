import {TreeShortGetResponse, TreeStatus} from "../../../generated/openapi";

export interface CtTreeShort {
    id: number,
    latitude: number
    longitude: number
    woodTypeId?: string
    woodTypeName?: string
    status: TreeStatus
    treeHeight?: number,
    trunkGirth?: number,
    fileUrls: string[],
    userId?: string,
}

export const ctShortTreeOf: (tree: TreeShortGetResponse) => CtTreeShort = (tree) => {
    return {
        id: tree.id,
        latitude: tree.latitude,
        longitude: tree.longitude,
        woodTypeId: tree.woodTypeId,
        woodTypeName: tree.woodTypeName,
        status: tree.status,
        treeHeight: tree.treeHeight,
        trunkGirth: tree.trunkGirth,
        fileUrls: tree.fileUrls,
        userId: tree.userId,
    }
}
