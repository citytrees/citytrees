import {TypedUseSelectorHook, useDispatch, useSelector} from 'react-redux';
import type {AppDispatch, RootState} from './store';
import {selectUser, User} from "../features/user/userSlice";

// Use throughout your app instead of plain `useDispatch` and `useSelector`
export const useAppDispatch = () => useDispatch<AppDispatch>();
export const useAppSelector: TypedUseSelectorHook<RootState> = useSelector;
export const useUser: () => User = () => useSelector(selectUser).user;
