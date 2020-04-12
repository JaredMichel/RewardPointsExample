import axios from 'axios';
import moment from 'moment';
import { Transaction } from '../model/Transaction';

const dateFormat = 'YYYY-MM-DD';

export const postNewTransactionByUserId = (userId: string, rawTransaction: { date: moment.Moment; amount: number }) => {
    return axios
        .post<UnsanitizedTransaction[]>(`/transaction/${userId}`, {
            date: rawTransaction.date.format(dateFormat),
            amount: rawTransaction.amount
        })
        .then((response) => {
            return sanitizeTransactions(response.data);
        });
};

export const retrieveTransactionsByUserId = (userId: string): Promise<Transaction[]> => {
    return axios.get<UnsanitizedTransaction[]>(`/transactions/${userId}`).then((response) => {
        return sanitizeTransactions(response.data);
    });
};

interface UnsanitizedTransaction {
    id: string;
    date: string;
    amount: number;
}

const sanitizeTransactions = (trans: UnsanitizedTransaction[]) =>
    trans.map((datum) => ({
        id: datum.id,
        date: moment(datum.date, dateFormat),
        amount: datum.amount
    }));
