import moment from 'moment';

export interface Transaction {
    id: string;
    date: moment.Moment;
    amount: number;
}
