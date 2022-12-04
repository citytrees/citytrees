import {Action, configureStore, ThunkAction} from '@reduxjs/toolkit';
import userReducer from '../features/user/userSlice';
import storage from 'redux-persist/lib/storage'
import {persistReducer, persistStore} from "redux-persist";


const persisConfig = {
    key: 'main-root',
    storage
}

const persistedUserReducer = persistReducer(persisConfig, userReducer)

export const store = configureStore({
    reducer: {
        user: persistedUserReducer,
    },
});

export const Persistor = persistStore(store)

export type AppDispatch = typeof store.dispatch;
export type RootState = ReturnType<typeof store.getState>;
export type AppThunk<ReturnType = void> = ThunkAction<ReturnType,
    RootState,
    unknown,
    Action<string>>;