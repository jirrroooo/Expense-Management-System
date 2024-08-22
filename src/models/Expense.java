package models;

import java.util.List;

public class Expense {
    public int expense_id;
    public String expense_description;
    double amount;
    String date;
    int lender;
    boolean is_settled;
    int group_id;

    public Expense(int expense_id, String expense_description, double amount, String date, int lender, boolean is_settled, int group_id) {
        this.expense_id = expense_id;
        this.expense_description = expense_description;
        this.amount = amount;
        this.date = date;
        this.lender = lender;
        this.is_settled = is_settled;
        this.group_id = group_id;
    }

}
