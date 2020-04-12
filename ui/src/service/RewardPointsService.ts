import axios from 'axios';

export const retrieveRewardPointsByUserId = (userId: string): Promise<number> => {
    return axios.get<number>(`/reward-points/${userId}`).then((response) => response.data);
};
