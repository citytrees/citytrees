import {createSlice, PayloadAction} from '@reduxjs/toolkit';
import {RootState} from '../../app/store';

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

export const userSlice = createSlice({
    name: 'user',
    initialState,
    reducers: {
        setUser: (state, action: PayloadAction<User>) => {
            let payload = action.payload;
            if (payload == null) return
            state.user = payload
        }
    }
});

export const {setUser} = userSlice.actions;
export const selectUser = (state: RootState) => state.user;

export default userSlice.reducer;