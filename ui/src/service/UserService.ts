import axios from 'axios';
import { User } from '../model/User';

export const retrieveUsers = (): Promise<User[]> => {
    return axios.get<User[]>('/users').then((response) => response.data);
};
