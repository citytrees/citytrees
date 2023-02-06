import {createSlice, PayloadAction} from '@reduxjs/toolkit';
import {RootState} from '../../app/store';
import {UserRole} from "../../generated/openapi";

export type User = {
    sub: string
    email: string
    roles: string[]
    firstName: string
    lastName: string
}

export const isUserAdmin = (user: NullableUser) => user?.roles?.indexOf(UserRole.Admin) !== -1

export type NullableUser = User | null

export type UserState = {
    user: NullableUser
}

const initialState: UserState = {user: null};

export const userSlice = createSlice({
    name: 'user',
    initialState,
    reducers: {
        setUser: (state, action: PayloadAction<NullableUser>) => {
            state.user = action.payload
        }
    }
});

export const {setUser} = userSlice.actions;
export const selectUser = (state: RootState) => state.user;

export default userSlice.reducer;
