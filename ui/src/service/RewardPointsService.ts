import axios from 'axios';

export const retrieveRewardPoints = (): Promise<number> => {
    return axios.get<number>('/reward-points').then((response) => response.data);
};
