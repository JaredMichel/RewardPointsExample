import React, { useCallback, useEffect, useMemo, useState } from 'react';
import moment from 'moment';
import { Transaction } from '../../model/Transaction';
import styles from './TransactionsDashboard.module.scss';
import { Button, DatePicker, InputNumber } from 'antd';
import { User } from '../../model/User';
import { postNewTransactionByUserId, retrieveTransactionsByUserId } from '../../service/TransactionService';
import { retrieveRewardPointsByUserId } from '../../service/RewardPointsService';

interface TransactionsDashboardProps {
    user: User;
}

export const TransactionsDashboard = ({ user }: TransactionsDashboardProps) => {
    const [selectedDate, setSelectedDate] = useState<moment.Moment | null>(null);
    const [transactionAmount, setTransactionAmount] = useState<number | undefined>();

    const [submittingTransaction, setSubmittingTransaction] = useState(false);
    const [retrievingTransactions, setRetrievingTransactions] = useState(false);
    const [rewardPoints, setRewardPoints] = useState<number | null>(null);
    const [transactions, setTransactions] = useState<Transaction[] | null>(null);
    const setSortedTransactions = (newTransactions: Transaction[]) => setTransactions(newTransactions.sort((a, b) => b.date.diff(a.date)));

    const addTransaction = useCallback(() => {
        if (selectedDate != null && transactionAmount != null) {
            setSubmittingTransaction(true);
            postNewTransactionByUserId(user.id, {
                date: selectedDate,
                amount: transactionAmount
            })
                .then((updatedTransactions) => {
                    setSortedTransactions(updatedTransactions);
                    setSubmittingTransaction(false);
                    setSelectedDate(null);
                    retrieveRewardPointsByUserId(user.id).then(setRewardPoints);
                })
                .catch(() => setSubmittingTransaction(false));
            setTransactionAmount(undefined);
        }
    }, [user, selectedDate, transactionAmount]);
    useEffect(() => {
        setRetrievingTransactions(true);
        retrieveTransactionsByUserId(user.id).then((trans) => {
            setSortedTransactions(trans);
            setRetrievingTransactions(false);
        });
        retrieveRewardPointsByUserId(user.id).then(setRewardPoints);
    }, [user]);

    const monthsForRewardsPoints = useMemo(() => {
        const monthMoments = [moment()];
        for (let i = 1; i < 7; i++) {
            monthMoments.push(moment().subtract(i, 'months'));
        }
        return monthMoments;
    }, []);

    return (
        <div className={styles.transactionsDashboard}>
            <div className={styles.left}>
                <h2>Add a New Transaction</h2>
                <h5>Select a Date</h5>
                <DatePicker disabledDate={(date) => date.isAfter(moment())} value={selectedDate} onChange={setSelectedDate} />
                <h5>Specify Amount</h5>
                <InputNumber
                    value={transactionAmount}
                    formatter={(value) => `$ ${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')}
                    parser={(value) => value?.replace(/\$\s?|(,*)/g, '') ?? ''}
                    onChange={setTransactionAmount}
                />
                <Button
                    loading={submittingTransaction}
                    disabled={selectedDate == null || transactionAmount == null || submittingTransaction || retrievingTransactions}
                    onClick={addTransaction}
                >
                    Submit
                </Button>
            </div>
            <div className={styles.center}>
                <h2>Transactions</h2>
                <div className={styles.transactionTable}>
                    <h3>Date</h3>
                    <h3>Amount</h3>
                    <h3>Reward Points</h3>
                    {transactions != null ? (
                        transactions.map((tr) => (
                            <React.Fragment key={tr.id}>
                                <div>{tr.date.format('MM/DD/YYYY')}</div>
                                <div>${tr.amount.toFixed(2).replace(/(\d)(?=(\d{3})+(?!\d))/g, '$1,')}</div>
                                <div>{computeRewardPointsForATransaction(tr)}</div>
                            </React.Fragment>
                        ))
                    ) : (
                        <></>
                    )}
                </div>
            </div>
            <div className={styles.right}>
                <h2>Reward Points</h2>
                <div className={styles.rewardsTable}>
                    <h3>Total</h3>
                    <div>{rewardPoints?.toLocaleString() ?? '-'}</div>
                    {transactions != null &&
                        monthsForRewardsPoints.map((monthMoment) => {
                            return (
                                <React.Fragment key={monthMoment.format()}>
                                    <h3>{monthMoment.format('MMMM')}</h3>
                                    <div>
                                        {transactions
                                            .filter((tr) => tr.date.isSame(monthMoment, 'month'))
                                            .reduce((current, tr) => {
                                                return current + computeRewardPointsForATransaction(tr);
                                            }, 0)
                                            .toLocaleString()}
                                    </div>
                                </React.Fragment>
                            );
                        })}
                </div>
            </div>
        </div>
    );
};

const computeRewardPointsForATransaction = (transaction: Transaction) => {
    let points = 0;
    if (transaction.amount > 100) {
        points = 50 + (transaction.amount - 100.0) * 2;
    } else if (transaction.amount > 50) {
        points = transaction.amount - 50.0;
    }
    return Math.floor(points);
};
