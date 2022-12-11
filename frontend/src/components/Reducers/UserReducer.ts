import {createSlice, PayloadAction} from "@reduxjs/toolkit";
import {useSelector} from "react-redux";

export type User = {
    sub: string
    email: string
    roles: string[]
    firstName: string
    lastName: string
} | null

export type UserState = {
    user: User
}

const initialState: UserState = {user: null};

const UserReducer = (state = initialState, action: PayloadAction<User>) => {
    let payload = action.payload;
    if (payload == null) return
    state.user = payload
}

export const userSlice = createSlice({
    name: 'user',
    initialState,
    reducers: {
        setUser: UserReducer
    }
});

export const {setUser} = userSlice.actions;
export const selectUser = (state: UserState) => state.user;
export const useUser: () => User = () => useSelector(selectUser);

export default UserReducer;