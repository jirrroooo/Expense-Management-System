package models;

public class User_expense {
    int id;
    int user_id;
    int expense_id;
    double balance;

    public User_expense(int id, int user_id, int expense_id, double balance) {
        this.id = id;
        this.user_id = user_id;
        this.expense_id = expense_id;
        this.balance = balance;
    }
}
