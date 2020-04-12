import React, { useEffect, useState } from 'react';
import styles from './App.module.scss';
import { User } from '../../model/User';
import { retrieveUsers } from '../../service/UserService';
import { TransactionsDashboard } from '../../components/TransactionsDashboard/TransactionsDashboard';
import { Select } from 'antd';

const App = () => {
    const [selectedUser, setSelectedUser] = useState<User | null>(null);
    const [users, setUsers] = useState<User[] | null>(null);
    useEffect(() => {
        retrieveUsers().then(setUsers);
    }, []);

    return (
        <div className={styles.app}>
            <div className={styles.userSelect}>
                {users != null && (
                    <>
                        <h2>Select a User:</h2>
                        <div>
                            <Select style={{ width: 200 }} value={selectedUser?.id} onChange={(choice) => setSelectedUser(users.find((u) => u.id === choice)!)}>
                                {users.map((u) => (
                                    <Select.Option key={u.id} value={u.id}>
                                        {u.name}
                                    </Select.Option>
                                ))}
                            </Select>
                        </div>
                    </>
                )}
            </div>
            <div>{selectedUser != null && <TransactionsDashboard user={selectedUser} />}</div>
        </div>
    );
};

export default App;
