import React, { useCallback, useEffect, useMemo, useState } from 'react';
import styles from './App.module.scss';
import { Button, DatePicker, InputNumber } from 'antd';
import moment from 'moment';
import { Transaction } from '../../model/Transaction';
import { postNewTransaction, retrieveTransactions } from '../../service/TransactionService';
import { retrieveRewardPoints } from '../../service/RewardPointsService';

const App = () => {
    const [selectedDate, setSelectedDate] = useState<moment.Moment | null>(null);
    const [transactionAmount, setTransactionAmount] = useState<number | undefined>();

    const [submittingTransaction, setSubmittingTransaction] = useState(false);
    const [retrievingTransactions, setRetrievingTransactions] = useState(false);
    const [rewardPoints, setRewardPoints] = useState<number | null>(null);
    const [transactions, setTransactions] = useState<Transaction[] | null>(null);

    const addTransaction = useCallback(() => {
        if (selectedDate != null && transactionAmount != null) {
            setSubmittingTransaction(true);
            postNewTransaction({
                date: selectedDate,
                amount: transactionAmount
            }).then((updatedTransactions) => {
                setTransactions(updatedTransactions);
                setSubmittingTransaction(false);
            });
            retrieveRewardPoints().then(setRewardPoints);
            setSelectedDate(null);
            setTransactionAmount(undefined);
        }
    }, [selectedDate, transactionAmount]);
    useEffect(() => {
        setRetrievingTransactions(true);
        retrieveTransactions().then((trans) => {
            setTransactions(trans);
            setRetrievingTransactions(false);
        });
        retrieveRewardPoints().then(setRewardPoints);
    }, []);

    const sortedTransactions = useMemo(() => {
        return transactions?.sort((a, b) => b.date.diff(a.date)) ?? [];
    }, [transactions]);

    return (
        <div className={styles.app}>
            <div className={styles.left}>
                <h2>Add a New Transaction</h2>
                <h5>Select a Date</h5>
                <DatePicker onChange={setSelectedDate} />
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
            <div className={styles.right}>
                <h2>Current Reward Points</h2>
                <span>{rewardPoints ?? '-'}</span>
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

export default App;
