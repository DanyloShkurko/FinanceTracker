package org.example.expensetracker.service;

import org.example.expensetracker.entity.User;

public interface UserService {
    User findUserById(long id);
}
