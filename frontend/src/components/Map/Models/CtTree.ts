import {TreesGetResponseTree} from "../../../generated/openapi";

export interface CtTree {
    latitude: number
    longitude: number
    files?: string[]
}

export const ctTreeOf: (tree: TreesGetResponseTree) => CtTree = (tree: TreesGetResponseTree) => {
    return {latitude: tree.latitude, longitude: tree.longitude, files: []}
}

export {}