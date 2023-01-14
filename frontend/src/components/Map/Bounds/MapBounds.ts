import {LatLngBounds} from "leaflet";

export interface MapBounds {
    x1: number
    y1: number
    x2: number
    y2: number
}

export const mapBoundsOf = (bounds: LatLngBounds): MapBounds => {
    let northWest = bounds.getNorthWest()
    let southEast = bounds.getSouthEast()

    return {
        x1: northWest.lat,
        y1: northWest.lng,
        x2: southEast.lat,
        y2: southEast.lng
    }
}