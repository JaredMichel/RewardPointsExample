import axios from 'axios';
import moment from 'moment';
import { Transaction } from '../model/Transaction';

const dateFormat = 'YYYY-MM-DD';

export const postNewTransaction = (rawTransaction: { date: moment.Moment; amount: number }) => {
    return axios
        .post<UnsanitizedTransaction[]>('/transaction', {
            date: rawTransaction.date.format(dateFormat),
            amount: rawTransaction.amount
        })
        .then((response) => {
            return sanitizeTransactions(response.data);
        });
};

export const retrieveTransactions = (): Promise<Transaction[]> => {
    return axios.get<UnsanitizedTransaction[]>('/transactions').then((response) => {
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
