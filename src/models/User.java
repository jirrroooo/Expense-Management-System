package models;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class User {
    public int user_id;
    public String first_name;
    public String last_name;
    String email;
    String phone_number;
    public String password;
    public String username;

    private List<Expense> expenseList;
    private List<List_Friend> friendList;
    private List<List_Group> groupList;
    private List<User_expense> userExpenseList;
    private List<User_group> userGroupList;
    private List<User> friendListUser;

    Data instance;

    public User(int user_id, String first_name, String last_name, String email, String phone_number, String pw, String uname){
        this.user_id = user_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.phone_number = phone_number;
        this.password = pw;
        this.username = uname;
    }

    public User(int user_id, String first_name, String last_name, String email, String phone_number, String pw, String uname, Data instance){
        this.user_id = user_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.phone_number = phone_number;
        this.password = pw;
        this.username = uname;
        this.instance = instance;

        boolean isDuplicate = false;
        for(User user: instance.userList){
            if(
                    user.first_name.equals(first_name) &&
                            user.last_name.equals(last_name) &&
                            user.email.equals(email) &&
                            user.phone_number.equals(phone_number) &&
                            user.password.equals(pw) &&
                            user.username.equals(uname)
            ){
                System.out.println("Error: Can't add duplicate user");
                isDuplicate = true;
                break;
            }
        }
        if(!isDuplicate) {
            this.user_id = instance.addUser(this);
            instance.userList.add(this);
        }

    }


    // Fetch Data from database
    // Input: (1) Instance of Data (Object)
    // Output: update list fields, return void
    public void fetchData(Data db){
        List<User> listUsers = db.userList;
        List<Expense> expenseList = db.getListExpense(this.user_id);
        List<List_Friend> friendList = db.getListFriends(this.user_id);
        List<List_Group> groupList = db.getListGroups(this.user_id);
        List<User_expense> userExpenseList = db.getUserExpenses(this.user_id);
        List<User_group> userGroupList = db.getListUserGroups(this.user_id);

        this.expenseList = new ArrayList<>();
        this.friendList = new ArrayList<>();
        this.groupList = new ArrayList<>();
        this.userExpenseList = new ArrayList<>();
        this.userGroupList = new ArrayList<>();
        this.friendListUser = new ArrayList<>();

        this.expenseList.addAll(expenseList);
        this.friendList.addAll(friendList);
        this.groupList.addAll(groupList);
        this.userExpenseList.addAll(userExpenseList);
        this.userGroupList.addAll(userGroupList);

        for(List_Friend friend : this.friendList){
            for(User user : listUsers){
                if((this.user_id == friend.user1 && user.user_id == friend.user2 && friend.isFriend) ||
                        (this.user_id == friend.user2 && user.user_id == friend.user1 && friend.isFriend)){
                    friendListUser.add(user);
                }
            }
        }

    }



    // Adding Expense. Automatic Adding of data in user_expense
    // Input: (description, amount, date, lender - user id, isSettled, group id, list of users that will split bill)
    // Output: Add tuple data in expense table, and add data for specific user_expense. Return void.
    public void addExpense(String desc, double amt, String date, int lender, boolean settled, int groupId,
                           List<User> usersInvolved, Data instance){
        Expense newExpense = new Expense(-1, desc, amt, date, lender, settled, groupId);
        newExpense.expense_id = instance.addExpense(newExpense);

        double memberNumber = usersInvolved.size();

        User_expense user_expense;

        if(lender == this.user_id){
            user_expense = new User_expense(-1, this.user_id, newExpense.expense_id, (newExpense.amount/memberNumber)*(-1*(memberNumber-1)));
        }else{
            user_expense = new User_expense(-1,  this.user_id, newExpense.expense_id,newExpense.amount/memberNumber);
        }
//        user_expense.id = instance.addUserExpense(user_expense);

        for(User involved: usersInvolved){
            if(lender == involved.user_id){
                user_expense = new User_expense(-1, involved.user_id, newExpense.expense_id, (newExpense.amount/memberNumber)*(-1*(memberNumber-1)));
            }else{
                user_expense = new User_expense(-1, involved.user_id, newExpense.expense_id, newExpense.amount/memberNumber);
            }

            user_expense.id = instance.addUserExpense(user_expense);
        }
    }



    // Update and Expense
    // Input: String Description, double amount, String date, int lender (User_id), boolean isSettled, int group id,
    //          int expense id of the expense you want to update
    // Output: Change all column data except the id. Return void.
    public void updateExpense(User programUser, Scanner sc, Data instance){
        int menuChoice = -1, i = 1;
        String newDescription = "";
        List<Expense> expenseList = instance.getListExpense(programUser.user_id);

        while(menuChoice < 1 || menuChoice > expenseList.size()){
            i = 1;
            System.out.println("========== Update Expense ==========");
            for(Expense expense : expenseList){
                System.out.println(i + " - " + expense.expense_description);
                i++;
            }
            menuChoice = sc.nextInt();
        }

        sc.nextLine();
        System.out.print("Enter new description: ");
        newDescription = sc.nextLine();

        boolean success = instance.updateExpense(expenseList.get(menuChoice-1), newDescription);

        if(success){
            System.out.println("Update Success!");
        }else{
            System.out.println("Update Unsuccessful!");
        }
    }


    // Delete an expense
    // Input: Expense Object
    // Output: none
    public void deleteExpense(Expense expense, Data instance){
        boolean success = instance.deleteExpense(expense.expense_id);

        instance.deleteUserExpense(expense.expense_id);

        if(success){
            System.out.println("Delete Success!");
        }else{
            System.out.println("Delete Unsuccessful!");
        }
    }



    // Search an Expense
    // Input: Expense object
    // Output: Expense object that is found
    public void searchExpense(Scanner sc, Data instance){
        String desc;
        sc.nextLine();
        List<User> userList = instance.userList;

        System.out.println("========== Search Expense ==========");
        System.out.print("Enter Expense Description: ");
        desc = sc.nextLine();

        Expense expense = instance.searchExpense(desc);

        if(expense != null){
            String lenderName = "";

            for(User user : userList){
                if(user.user_id == expense.lender){
                    lenderName = user.first_name + " " + user.last_name;
                }
            }

            System.out.println("\nFound Expense");
            System.out.println("=========================================");
            System.out.println("Expense ID: " + expense.expense_id);
            System.out.println("Expense Description: " + expense.expense_description);
            System.out.println("Expense Amount: " + expense.amount);
            System.out.println("Expense Date: " + expense.date);
            System.out.println("Expense Lender: " + lenderName);
            System.out.println("Is settled: " + expense.is_settled);
            System.out.println("Group id: " + expense.group_id);
        }else{
            System.out.println("\nNo Expense Found!");
        }

    }


    // Add a friend
    // Input: User Object
    // Output: none
    public void addFriend(User programUser, Scanner sc, Data instance){
        fetchData(instance);
        int menuChoice = -1, i = 1;
        List<User> users = instance.userList;
        User selectedUser = null;
        boolean innerUpdated = false;
        boolean success = false;

        while(menuChoice < 1 || menuChoice > users.size()){
            System.out.println("\n========== Add Friend ==========");
            i = 1;
            for(User user : users){
                System.out.println(i + " - " + user.first_name + " " + user.last_name);
                i++;
            }
            System.out.print("Enter Choice: ");
            menuChoice = sc.nextInt();

            selectedUser = users.get(menuChoice-1);

            if(selectedUser.user_id == programUser.user_id){
                System.out.println("\nCan't select self as a friend");
                menuChoice = -1;
            }else{
                for(List_Friend friend : this.friendList){
                    if((selectedUser.user_id == friend.user1 && programUser.user_id == friend.user2 && friend.isFriend)
                            || (selectedUser.user_id == friend.user2 && programUser.user_id == friend.user1 && friend.isFriend)){
                        System.out.println("\nSelected User is already your friend");
                        menuChoice = -1;
                    }else if((selectedUser.user_id == friend.user1 && programUser.user_id == friend.user2)
                            || (selectedUser.user_id == friend.user2 && programUser.user_id == friend.user1)){
                        instance.updateFriend(friend.user1, friend.user2, true);
                        innerUpdated = true;
                        success = true;
                    }
                }
            }
        }

        if(!innerUpdated){
            success = instance.addFriend(this.user_id, selectedUser.user_id);
        }

        if(success){
            System.out.println("Successfully added friend!");
        }else{
            System.out.println("Add friend unsuccessful!");
        }
    }


    // Update a Friend (only the boolean isFriend is updated here)
    // Input: (1) User Object of the current user, (2) User Object of the friend
    // Output: none
    public void updateFriend(User programUser, Scanner sc, Data instance){
        fetchData(instance);

        int menuChoice = -1, menuChoice2 = -1, i = 1;
        List<User> friends = this.friendListUser;
        User selectedUser = null;

        while (menuChoice < 1 || menuChoice > friends.size()){
            System.out.println("========== Update Friend Relationship Status ==========");
            i = 1;
            for(User friend : friends){
                System.out.println(i + " - " + friend.first_name + " " + friend.last_name);
                i++;
            }
            System.out.print("Enter Choice: ");
            menuChoice = sc.nextInt();
        }

        selectedUser = friends.get(menuChoice-1);
        System.out.println("\nDo you want to unfriend " + selectedUser.first_name + " " + selectedUser.last_name + "?");
        System.out.print("1) Yes\n2) No\n\nEnter Choice: ");
        menuChoice2 = sc.nextInt();

        boolean success = instance.updateFriend(this.user_id, selectedUser.user_id, menuChoice2 != 1);

        if(success){
            System.out.println("\nSuccessfully updated friend!");
        }else{
            System.out.println("\nUpdate friend unsuccessful!");
        }
    }


    // Delete a friend
    // Input: User object of the friend
    // Output: none
    public void deleteFriend(User programUser, Scanner sc, Data instance){
        fetchData(instance);

        int menuChoice = -1, menuChoice2 = -1, i = 1;
        List<User> friends = this.friendListUser;
        User selectedUser = null;

        while (menuChoice < 1 || menuChoice > friends.size()){
            System.out.println("========== Delete Friend ==========");
            i = 1;
            for(User friend : friends){
                System.out.println(i + " - " + friend.first_name + " " + friend.last_name);
                i++;
            }
            System.out.print("Enter Choice: ");
            menuChoice = sc.nextInt();
        }

        selectedUser = friends.get(menuChoice-1);

        boolean success = instance.deleteFriend(this.user_id, selectedUser.user_id);

        if(success){
            System.out.println("\nSuccessfully deleted a friend!");
        }else{
            System.out.println("\nUnsuccessful Deletion!");
        }
    }


    // Search a Friend
    // Input: User Object of the friend
    // Output: List of Friends (object) found
    public void searchFriend(User programUser, Scanner sc, Data instance){
        fetchData(instance);

        String name;
        List<User> friends = this.friendListUser;
        List<User> matchFriend = new ArrayList<>();

        sc.nextLine();
        System.out.println("========== Search Friend ==========");
        System.out.print("Enter Friend Name: ");
        name = sc.nextLine();

        for(User user : friends){
            if((user.first_name + " " + user.last_name).contains(name)){
                matchFriend.add(user);
            }
        }

        if(matchFriend.size() > 0){
            for(User user : matchFriend){
                System.out.println("\nFound Friend");
                System.out.println("=========================================");
                System.out.println("Friend Name: " + user.first_name + " " + user.last_name);
                System.out.println("Friend Email: " + user.email);
                System.out.println("Friend Phone number: " + user.phone_number);
                System.out.println("Friend Username: " + user.username);
            }
        }else{
            System.out.println("\nNo Match Found!");
        }
    }


    // Add group
    // Input: String group name
    // Output: List_Group object;
    public int addGroup(String groupName, Data instance){
        List_Group newGroup = new List_Group(-1, groupName);

        newGroup.group_id = instance.addGroup(newGroup);

        return newGroup.group_id;
    }


    // Update Group
    // Input: (1) List_group object of the group you want to update, (2) new name of the group
    // Output: none
    public void updateGroup(Scanner sc, Data instance){
        fetchData(instance);
        int menuChoice = -1, i;
        String newGroupName = "";
        List<List_Group> groups = this.groupList;

        while(menuChoice < 1 || menuChoice > groups.size()){
            i = 1;
            System.out.println("========== Update Group Name ==========");
            for(List_Group group : groups){
                System.out.println(i + " - " + group.group_name);
                i++;
            }
            menuChoice = sc.nextInt();
        }

        sc.nextLine();
        System.out.print("Enter new group name: ");
        newGroupName = sc.nextLine();

        boolean success = instance.updateGroup(newGroupName, groups.get(menuChoice-1).group_id);

        if(success){
            System.out.println("Update Success!");
        }else{
            System.out.println("Update Unsuccessful!");
        }
    }


    // Delete a group
    // Input: List_group object of the group you want to delete
    // Output: none
    public void deleteGroup(User programUser, Scanner sc, Data instance){
        fetchData(instance);

        int menuChoice = -1, i = 1;
        List<List_Group> groups = this.groupList;
        List_Group selectedGroup;

        while (menuChoice < 1 || menuChoice > groups.size()){
            System.out.println("========== Delete Group ==========");
            i = 1;
            for(List_Group group : groups){
                System.out.println(i + " - " + group.group_name);
                i++;
            }
            System.out.print("Enter Choice: ");
            menuChoice = sc.nextInt();
        }

        selectedGroup = groups.get(menuChoice-1);

        instance.deleteUserGroup(selectedGroup.group_id);
        boolean success = instance.deleteGroup(selectedGroup.group_id);

        if(success){
            System.out.println("Successfully deleted group!");
        }else{
            System.out.println("Delete unsuccessful!");
        }
    }



    // Search for a group
    // Input: List_Group Object of the group you want to search
    // Output: List_Group object of the found group
    public void searchGroup(User programUser, Scanner sc, Data instance){
        fetchData(instance);

        String name;
        List<List_Group> groups = this.groupList;
        List<List_Group> matchGroups = new ArrayList<>();

        sc.nextLine();
        System.out.println("========== Search Group ==========");
        System.out.print("Enter Group Name: ");
        name = sc.nextLine();

        for(List_Group group : groups){
            if(group.group_name.contains(name)){
                matchGroups.add(group);
            }
        }

        if(matchGroups.size() > 0){
            for(List_Group group : matchGroups){
                System.out.println("\nFound Group");
                System.out.println("=========================================");
                System.out.println("Group Name: " + group.group_name);
                System.out.println("Group ID: " + group.group_id);
            }
        }else{
            System.out.println("\nNo Match Found!");
        }
    }



    // View all expenses made within a month
    // Input: (1) String month in number MM format, (2) String year in YYYY format
    // Output: List of Expense object
    public void viewExpensesInAMonth(Scanner sc, Data instance){
        fetchData(instance);
        String month = "";
        int i;

        while(!month.equalsIgnoreCase("01") &&
                !month.equalsIgnoreCase("02") &&
                !month.equalsIgnoreCase("03") &&
                !month.equalsIgnoreCase("04") &&
                !month.equalsIgnoreCase("05") &&
                !month.equalsIgnoreCase("06") &&
                !month.equalsIgnoreCase("07") &&
                !month.equalsIgnoreCase("08") &&
                !month.equalsIgnoreCase("09") &&
                !month.equalsIgnoreCase("10") &&
                !month.equalsIgnoreCase("11") &&
                !month.equalsIgnoreCase("12")){
            sc.nextLine();
            System.out.println("========== View Expenses made within a Month ==========");
            System.out.print("Enter month (e.g. 08 for August): ");
            month = sc.nextLine();
        }


        List<Expense> filteredList= new ArrayList<>();
        for(Expense expense : this.expenseList){
            if(expense.date.contains("-" + month + "-")){
                filteredList.add(expense);
            }
        }

        for(Expense expense : filteredList){
            String lenderName = "";

            for(User user : instance.userList){
                if(user.user_id == expense.lender){
                    lenderName = user.first_name + " " + user.last_name;
                }
            }

            i = 1;
            System.out.println("\n========== Expense " + i + " ==========");
            System.out.println("Expense ID: " + expense.expense_id);
            System.out.println("Expense Description: " + expense.expense_description);
            System.out.println("Expense Amount: " + expense.amount);
            System.out.println("Expense Date: " + expense.date);
            System.out.println("Expense Lender: " + lenderName);
            System.out.println("Is Settled: " + (expense.is_settled ? "Yes" : "No"));
            System.out.println("Group ID: " + (expense.group_id == -1 ? "None" : expense.group_id));
            i++;
        }
    }


    // View all expenses made with a friend
    // Input: User object of friend
    // Output: List of Expense object
    public void viewExpensesMadeWithAFriend(Scanner sc, Data instance){
        fetchData(instance);

        int i;
        List<Expense> filteredList = new ArrayList<>();

        System.out.println("\n===== View All Expenses Made with A Friend =====");

        for(Expense expense : this.expenseList){
            if(expense.group_id == -1 || expense.group_id == 0){
                filteredList.add(expense);
            }
        }

        i = 1;
        for(Expense expense : filteredList){
            String lenderName = "";

            for(User user : instance.userList){
                if(user.user_id == expense.lender){
                    lenderName = user.first_name + " " + user.last_name;
                }
            }

            System.out.println("\n========== Expense " + i + " ==========");
            System.out.println("Expense ID: " + expense.expense_id);
            System.out.println("Expense Description: " + expense.expense_description);
            System.out.println("Expense Amount: " + expense.amount);
            System.out.println("Expense Date: " + expense.date);
            System.out.println("Expense Lender: " + lenderName);
            System.out.println("Is Settled: " + (expense.is_settled ? "Yes" : "No"));
            System.out.println("Group ID: " + (expense.group_id == -1 ? "None" : expense.group_id));
            i++;
        }
    }

    // View expenses made with a group
    // Input: List_Group object of the group
    // Output: List of Expense object
    public void viewExpensesMadeWithAGroup(Scanner sc, Data instance){
        fetchData(instance);

        int i;
        List<Expense> filteredList = new ArrayList<>();

        System.out.println("\n===== View All Expenses Made with A Group =====");

        for(Expense expense : this.expenseList){
            if(expense.group_id > 0 && !expense.is_settled){
                filteredList.add(expense);
            }
        }

        i = 1;
        for(Expense expense : filteredList){
            String lenderName = "";

            for(User user : instance.userList){
                if(user.user_id == expense.lender){
                    lenderName = user.first_name + " " + user.last_name;
                }
            }

            System.out.println("\n========== Expense " + i + " ==========");
            System.out.println("Expense ID: " + expense.expense_id);
            System.out.println("Expense Description: " + expense.expense_description);
            System.out.println("Expense Amount: " + expense.amount);
            System.out.println("Expense Date: " + expense.date);
            System.out.println("Expense Lender: " + lenderName);
            System.out.println("Is Settled: " + (expense.is_settled ? "Yes" : "No"));
            System.out.println("Group ID: " + (expense.group_id == -1 ? "None" : expense.group_id));
            i++;
        }
    }


    // View current balance from all expenses
    // Input: none
    // Output: sum of all balance on all expenses
    public void viewCurrentBalanceFromExpenses(Data instance){
        fetchData(instance);

        int i = 1;

        System.out.println("\n===== View Current Balance From All Expense =====");

        for(User_expense user_expense : this.userExpenseList){
            for(Expense expense : this.expenseList){
                if(user_expense.expense_id == expense.expense_id && user_expense.balance > 0){
                    System.out.println("\n========== Expense " + i + " ==========");
                    System.out.println("Expense Description: " + expense.expense_description);
                    System.out.println("Expense Amount: " + expense.amount);
                    System.out.println("Current Balance: " + user_expense.balance);
                    i++;
                }
            }
        }
    }


    // View all friends with outstanding balance
    // Input: none
    // Output: List of User objects
    public void viewAllFriendsWithOutstandingBalance(Data instance){
        fetchData(instance);

        System.out.println("===== View all Friends with Outstanding Balance =====");
        for(User friend: this.friendListUser){
            System.out.println("\n\n===== Friend: " + friend.first_name + " " + friend.last_name + " =====");
            friend.fetchData(instance);
            for(User_expense user_expense : friend.userExpenseList){
                for(Expense expense : this.expenseList){
                    if(expense.expense_id == user_expense.expense_id && user_expense.balance > 0){
                        System.out.println("Expense: " + expense.expense_description);
                        System.out.println("Outstanding Balance: " + user_expense.balance);
                        System.out.println();
                    }
                }
            }
        }

    }


    // View all groups the user belong to
    // Input: none
    // Output: List of List_Group Objects
    public void viewAllGroups(Data instance){
        fetchData(instance);

        System.out.println("========== User Groups ==========");
        int i = 1;
        for(List_Group group : this.groupList){
            System.out.println("Group " + i + ": " + group.group_name);
            i++;
        }
    }


    // View all groups with an outstanding balance
    // Input: none
    // Output: List of List_Group objects
    public void viewAllGroupsWithAnOutstandingBalance(Data instance){
        fetchData(instance);

        System.out.println("\n===== View All Groups with an Outstanding Balance =====");
        for(List_Group group : this.groupList){
            for(Expense expense : this.expenseList){
                for(User_expense user_expense: this.userExpenseList){
                    if(user_expense.expense_id == expense.expense_id &&
                            expense.group_id == group.group_id && user_expense.balance > 0){
                        System.out.println("\n===== Group: " + group.group_name + " =====");
                        System.out.println("Outstanding Balance: " + user_expense.balance);
                    }
                }
            }
        }
    }

    public void payBalance(Scanner sc, Data instance){
        fetchData(instance);
        int menuChoice = -1, i;
        double payment;
        Expense selectedExpense = null;

        List<Expense> expenseToPay = new ArrayList<>();
        List<Double> balances = new ArrayList<>();

        for(Expense expense: this.expenseList){
            for(User_expense user_expense: this.userExpenseList){
                if(expense.expense_id == user_expense.expense_id && user_expense.balance > 0){
                    expenseToPay.add(expense);
                    balances.add(user_expense.balance);
                }
            }
        }


        while(menuChoice < 1 || menuChoice > expenseToPay.size()){
            i = 1;
            System.out.println("========== Pay Balance ==========");
            for(Expense expense: expenseToPay){
                System.out.println(i + " - " + expense.expense_description + " (Balance: " + balances.get(i-1) +")");
                i++;
            }
            System.out.print("\nEnter Choice: ");
            menuChoice = sc.nextInt();
        }

        selectedExpense = expenseToPay.get(menuChoice-1);

        System.out.print("Enter Amount to Pay: ");
        payment = sc.nextDouble();

        double newBalance = balances.get(menuChoice-1) - payment;

        // Current payee
        instance.updateBalance(this.user_id, selectedExpense.expense_id, newBalance);

        User lender = this;

        for(User user : instance.userList){
            if(user.user_id == selectedExpense.lender){
                lender = user;
            }
        }

        lender.fetchData(instance);
        double lenderBalance = 0;

        for(User_expense user_expense : lender.userExpenseList){
            if(user_expense.expense_id == selectedExpense.expense_id){
                lenderBalance = user_expense.balance;
            }
        }

        // For Lender
        instance.updateBalance(selectedExpense.lender, selectedExpense.expense_id, lenderBalance + payment);

    }
}
