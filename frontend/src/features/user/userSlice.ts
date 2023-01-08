import {createSlice, PayloadAction} from '@reduxjs/toolkit';
import {RootState} from '../../app/store';

export type User = {
    sub: string
    email: string
    roles: string[]
    firstName: string
    lastName: string
}

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
