package models;

import java.sql.*;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Data {
    String URL = "jdbc:mariadb://localhost:3306/expense_management";
    String USER = "admin";
    String PASSWORD = "adminadmin";

    public List<User> userList;

    private Connection connection;

    // Fetch List of Tuples in each tables
    public static final String LIST_EXPENSES = "SELECT * FROM expense WHERE Expense_id in " +
            "(SELECT Expense_id FROM user_expense WHERE User_id = ?)";
    public static final String LIST_FRIENDS = "SELECT * FROM list_friend WHERE User_1 in (SELECT User_1 FROM list_friend " +
            "WHERE User_2 = ?) OR User_2 in (SELECT User_2 FROM list_friend WHERE User_1 = ?)";
    public static final String LIST_GROUPS = "SELECT * FROM list_group WHERE Group_id in " +
            "(SELECT Group_id FROM user_group WHERE User_id = ?)";
    public static final String LIST_USER_EXPENSES = "SELECT * FROM user_expense WHERE User_id = ?";
    public static final String LIST_USER_GROUPS = "SELECT * FROM user_group WHERE User_id = ?";


    // CRUD for expense table
    public static final String ADD_EXPENSE = "INSERT INTO expense (Expense_description, Amount, Date, Lender, " +
            "Is_settled, Group_id) VALUES(?, ?, ?, ?, ?, ?)";
    public static final String DELETE_EXPENSE = "DELETE FROM expense WHERE Expense_id = ?";
    public static final String UPDATE_EXPENSE = "UPDATE expense SET Expense_description = ? WHERE Expense_id = ?";
    public static final String SEARCH_EXPENSE = "SELECT * FROM expense WHERE Expense_description = ?";


    //CRUD for user friend
    public static final String ADD_FRIEND = "INSERT INTO list_friend VALUES(?, ?, ?)";
    public static final String DELETE_FRIEND = "DELETE FROM list_friend WHERE (User_1 = ? AND User_2 = ?) OR " +
            "(User_1 = ? AND User_2 = ?)";
    public static final String UPDATE_FRIEND = "UPDATE list_friend SET Is_friend = ? WHERE (User_1 = ? AND User_2 = ?)"
            + " OR (User_1 = ? AND User_2 = ?)";
    public static final String SEARCH_FRIEND = "SELECT * FROM user WHERE User_id = (SELECT User_1 FROM list_friend " +
            "WHERE User_2 = ? AND Is_friend = 1) AND User_id = (SELECT User_2 FROM list_friend WHERE User_1 = ? AND Is_friend = 1)";


    //CRUD for group
    public static final String ADD_GROUP = "INSERT INTO list_group (Group_name) VALUES(?)";
    public static final String DELETE_GROUP = "DELETE FROM list_group WHERE Group_id = ?";
    public static final String UPDATE_GROUP = "UPDATE list_group SET Group_name = ? WHERE Group_id = ?";
    public static final String SEARCH_GROUP = "SELECT * FROM list_group WHERE Group_id = ?";


    public static final String FETCH_USERS = "SELECT * FROM user";
    public static final String ADD_USER = "INSERT INTO user (First_name, Last_name, Email, Phone_number, Password, Username) " +
            "VALUES(?, ?, ?, ?, ?, ?)";
    public static final String ADD_USER_EXPENSE = "INSERT INTO user_expense (User_id, Expense_id, Balance) VALUES(?, ?, ?)";
    public static final String ADD_USER_GROUP = "INSERT INTO user_group VALUES(?, ?)";
    public static final String GROUP_MEMBERS = "SELECT * FROM user WHERE User_id IN (SELECT User_id FROM user_group WHERE Group_id = ?)";
    public static final String DELETE_USER_EXPENSE = "DELETE FROM user_expense WHERE Expense_id = ?";
    public static final String DELETE_USER_GROUP = "DELETE FROM user_group WHERE Group_id = ?";
    public static final String UPDATE_BALANCE = "UPDATE user_expense SET Balance = ? WHERE User_id = ? AND Expense_id = ?";

    // For fetching data
    private PreparedStatement listExpenses;
    private PreparedStatement listFriends;
    private PreparedStatement listGroups;
    private PreparedStatement listUserExpenses;
    private PreparedStatement listUserGroups;

    // Expense CRUD
    private PreparedStatement addExpense;
    private PreparedStatement deleteExpense;
    private PreparedStatement updateExpense;
    private PreparedStatement searchExpense;

    // Friend CRUD
    private PreparedStatement addFriend;
    private PreparedStatement deleteFriend;
    private PreparedStatement updateFriend;
    private PreparedStatement searchFriend;

    // Group CRUD
    private PreparedStatement addGroup;
    private PreparedStatement deleteGroup;
    private PreparedStatement updateGroup;
    private PreparedStatement searchGroup;

    private PreparedStatement addUser;
    private PreparedStatement addUserExpense;
    private PreparedStatement addUserGroup;
    private PreparedStatement groupMembers;
    private PreparedStatement deleteUserExpense;
    private PreparedStatement deleteUserGroup;
    private PreparedStatement updateBalance;

    public Data(){
        this.userList = new ArrayList<User>();
        openDatabaseConnection();

        try{
            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery(FETCH_USERS);


            while(results.next()){
                User user = new User(
                        results.getInt(1),
                        results.getString(2),
                        results.getString(3),
                        results.getString(4),
                        results.getString(5),
                        results.getString(6),
                        results.getString(7)
                );
                this.userList.add(user);
            }
        }catch (SQLException e){
            System.out.println("Error fetching users: " + e.getMessage());
        }
    }


    // This method connects the program to the database
    public boolean openDatabaseConnection(){
        try{
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Successfully connected to the database\n");

            // Fetch list of tuples in each tables
            listExpenses = connection.prepareStatement(LIST_EXPENSES);
            listFriends = connection.prepareStatement(LIST_FRIENDS);
            listGroups = connection.prepareStatement(LIST_GROUPS);
            listUserExpenses = connection.prepareStatement(LIST_USER_EXPENSES);
            listUserGroups = connection.prepareStatement(LIST_USER_GROUPS);

            // Crud for expense
            addExpense = connection.prepareStatement(ADD_EXPENSE, Statement.RETURN_GENERATED_KEYS);
            deleteExpense = connection.prepareStatement(DELETE_EXPENSE);
            updateExpense = connection.prepareStatement(UPDATE_EXPENSE);
            searchExpense = connection.prepareStatement(SEARCH_EXPENSE);

            // Crud for friend
            addFriend = connection.prepareStatement(ADD_FRIEND);
            deleteFriend = connection.prepareStatement(DELETE_FRIEND);
            updateFriend = connection.prepareStatement(UPDATE_FRIEND);
            searchFriend = connection.prepareStatement(SEARCH_FRIEND);

            // Crud for group
            addGroup = connection.prepareStatement(ADD_GROUP, Statement.RETURN_GENERATED_KEYS);
            deleteGroup = connection.prepareStatement(DELETE_GROUP);
            updateGroup = connection.prepareStatement(UPDATE_GROUP);
            searchGroup = connection.prepareStatement(SEARCH_GROUP);

            addUser = connection.prepareStatement(ADD_USER, Statement.RETURN_GENERATED_KEYS);
            addUserExpense = connection.prepareStatement(ADD_USER_EXPENSE, Statement.RETURN_GENERATED_KEYS);
            addUserGroup = connection.prepareStatement(ADD_USER_GROUP);
            groupMembers = connection.prepareStatement(GROUP_MEMBERS);
            deleteUserExpense = connection.prepareStatement(DELETE_USER_EXPENSE);
            deleteUserGroup = connection.prepareStatement(DELETE_USER_GROUP);
            updateBalance = connection.prepareStatement(UPDATE_BALANCE);

            return true;
        }catch(SQLException e){
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }


    // This method safely closes the database connection
    public void closeDatabaseConnection(){
        try {
            if(listExpenses != null){
                listExpenses.close();
            }

            if(listFriends != null){
                listFriends.close();
            }

            if(listGroups != null){
                listGroups.close();
            }

            if(listUserExpenses != null){
                listUserExpenses.close();
            }

            if(listUserGroups != null){
                listUserGroups.close();
            }

            if(addExpense != null){
                addExpense.close();
            }

            if(deleteExpense != null){
                deleteExpense.close();
            }

            if(updateExpense != null){
                updateExpense.close();
            }

            if(searchExpense != null){
                searchExpense.close();
            }

            if(addFriend != null){
                addFriend.close();
            }

            if(deleteFriend != null){
                deleteFriend.close();
            }

            if(updateFriend != null){
                updateFriend.close();
            }

            if(searchFriend != null){
                searchFriend.close();
            }

            if(addGroup != null){
                addGroup.close();
            }

            if(deleteGroup != null){
                deleteGroup.close();
            }

            if(updateGroup != null){
                updateGroup.close();
            }

            if(searchGroup != null){
                searchGroup.close();
            }

            if(addUser != null){
                addUser.close();
            }

            if(addUserGroup != null){
                addUserGroup.close();
            }

            if(groupMembers != null){
                groupMembers.close();
            }

            if(deleteUserGroup != null){
                deleteUserGroup.close();
            }

            if(deleteUserExpense != null){
                deleteUserExpense.close();
            }

            if(updateBalance != null){
                updateBalance.close();
            }

            if (connection != null) {
                connection.close();
            }
        }catch (SQLException e){
            System.out.println("Couldn't close connection: " + e.getMessage());
        }
    }

    // Get List of Expenses
    // Input: User_id
    // Output: List of Expenses (List<Expense>)
    public List<Expense> getListExpense(int user_id){
        try {
            listExpenses.setInt(1, user_id);
            ResultSet results = listExpenses.executeQuery();

            List<Expense> expenses = new ArrayList<>();

            while(results.next()){
                Expense expense = new Expense(
                        results.getInt(1),
                        results.getString(2),
                        results.getDouble(3),
                        results.getString(4),
                        results.getInt(5),
                        results.getBoolean(6),
                        results.getInt(7)
                );
                expenses.add(expense);
            }

            return expenses;

        }catch (SQLException e){
            System.out.println("Query failed (getListExpense): " + e.getMessage());
            return null;
        }
    }


    // Get List of Friends
    // Input: User_id
    // Output: List of Friends (List<List_Friend>) of the user
    public List<List_Friend> getListFriends(int user_id){
        try {
            listFriends.setInt(1, user_id);
            listFriends.setInt(2, user_id);

            ResultSet results = listFriends.executeQuery();

            List<List_Friend> friends = new ArrayList<>();

            while(results.next()){
                List_Friend friend = new List_Friend(
                        results.getInt(1),
                        results.getInt(2),
                        results.getBoolean(3)
                );
                friends.add(friend);
            }

            return friends;

        }catch (SQLException e){
            System.out.println("Query failed (getFriendList): " + e.getMessage());
            return null;
        }
    }

    // Get List of Group
    // Input: User_id
    // Output: List of List_Group (List<List_Group>)
    public List<List_Group> getListGroups(int user_id){
        try {
            listGroups.setInt(1, user_id);

            ResultSet results = listGroups.executeQuery();

            List<List_Group> groups = new ArrayList<>();

            while(results.next()){
                List_Group group = new List_Group(
                        results.getInt(1),
                        results.getString(2)
                );
                groups.add(group);
            }

            return groups;

        }catch (SQLException e){
            System.out.println("Query failed: (getListGroups) " + e.getMessage());
            return null;
        }
    }


    // Get List of Group
    // Input: User_id
    // Output: List of Groups the User Belong to (List<User_group>)
    public List<User_group> getListUserGroups(int user_id){
        try {
            listUserGroups.setInt(1, user_id);

            ResultSet results = listUserGroups.executeQuery();

            List<User_group> user_groups = new ArrayList<>();

            while(results.next()){
                User_group user_group = new User_group(
                        results.getInt(1),
                        results.getInt(2)
                );
                user_groups.add(user_group);
            }

            return user_groups;

        }catch (SQLException e){
            System.out.println("Query failed: (getListUserGroups) " + e.getMessage());
            return null;
        }
    }


    // Get List of User_expenses
    // Input: User_id
    // Output: List of User Expenses (List<User_expense>)
    public List<User_expense> getUserExpenses(int user_id){
        try {
            listUserExpenses.setInt(1, user_id);

            ResultSet results = listUserExpenses.executeQuery();

            List<User_expense> user_expenses = new ArrayList<>();

            while(results.next()){
                User_expense user_expense = new User_expense(
                        results.getInt(1),
                        results.getInt(2),
                        results.getInt(3),
                        results.getDouble(4)
                );
                user_expenses.add(user_expense);
            }

            return user_expenses;

        }catch (SQLException e){
            System.out.println("Query failed: (getUserExpenses)" + e.getMessage());
            return null;
        }
    }


    // Add an Expense
    // Input: Expense Object
    // Output: Generated ID from the auto increment
    public int addExpense(Expense expense){
        String dateFormatPattern = "MM-dd-yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatPattern);

        java.sql.Date date = null;

        try {
            date = new java.sql.Date(dateFormat.parse(expense.date).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            addExpense.setString(1, expense.expense_description);
            addExpense.setDouble(2, expense.amount);
            addExpense.setDate(3, date);
            addExpense.setInt(4, expense.lender);
            addExpense.setBoolean(5, expense.is_settled);

            if(expense.group_id == -1){
                addExpense.setNull(6, Types.INTEGER);
            }else{
                addExpense.setInt(6, expense.group_id);
            }

            int affectedRows = addExpense.executeUpdate();

            int id = -1;

            if(affectedRows > 0){
                try(ResultSet generatedKeys = addExpense.getGeneratedKeys()){
                    if(generatedKeys.next()){
                        id = generatedKeys.getInt(1);
                    }
                }
            }

            return id;

        }catch (SQLException e){
            System.out.println("Insertion failed: " + e.getMessage());
            return -1;
        }
    }


    // Update an Expense
    // Input: (1) Expense Object with updated value, (2) Expense_id of the current expense
    // Output: True or False (Successful or not)
    public boolean updateExpense(Expense expense, String description){
        try {
            updateExpense.setString(1, description);
            updateExpense.setInt(2, expense.expense_id);

            updateExpense.execute();

            return true;

        }catch (SQLException e){
            System.out.println("Insertion failed: " + e.getMessage());
            return false;
        }
    }


    // Delete an Expense
    // Input: (1) Expense_id of the current expense
    // Output: True or False (Successful or not)
    public boolean deleteExpense(int expense_id){
        try{
            deleteExpense.setInt(1, expense_id);
            deleteExpense.execute();

            return true;
        }catch (SQLException e){
            System.out.println("Error deleting expense: " + e.getMessage());
            return false;
        }
    }


    // Search an Expense
    // Input: (1) Expense_id of the current expense
    // Output: Found Expense Object
    public Expense searchExpense(String desc){
        try{
            searchExpense.setString(1, desc);

            ResultSet results = searchExpense.executeQuery();

            Expense expense = null;

            while(results.next()){
                expense = new Expense(
                results.getInt(1),
                results.getString(2),
                results.getDouble(3),
                results.getString(4),
                results.getInt(5),
                results.getBoolean(6),
                results.getInt(7)
                );
            }

            return expense;

        }catch (SQLException e){
            System.out.println("Error searching: " + e.getMessage());
            return null;
        }
    }


    // Add a user as friend
    // Input: (1) User_id of the current user, (2) User_id of the user you want to be friend with
    // Output: True or False (successful or not)
    public boolean addFriend(int user1, int user2){
        try {
            addFriend.setInt(1, user1);
            addFriend.setInt(2, user2);
            addFriend.setBoolean(3, true);

            addFriend.execute();

            return true;

        }catch (SQLException e){
            System.out.println("Insertion failed: " + e.getMessage());
            return false;
        }
    }


    // Update a friend
    // Input: (1) User_id of the current user, (2) User_id the friend
    // (3) true or false (set to friend or not)
    // Output: True or False (successful or not)
    public boolean updateFriend(int user1, int user2, boolean val){
        try {
            updateFriend.setBoolean(1, val);
            updateFriend.setInt(2, user1);
            updateFriend.setInt(3, user2);
            updateFriend.setInt(4, user2);
            updateFriend.setInt(5, user1);

            updateFriend.execute();

            return true;

        }catch (SQLException e){
            System.out.println("Insertion failed: " + e.getMessage());
            return false;
        }
    }


    // Delete a friend
    // Input: (1) User_id of the current user, (2) User_id of the friend
    // Output: True or False (successful or not)
    public boolean deleteFriend(int user1, int user2){
        try{
            deleteFriend.setInt(1, user1);
            deleteFriend.setInt(2, user2);
            deleteFriend.setInt(3, user2);
            deleteFriend.setInt(4, user1);

            deleteFriend.execute();

            return true;
        }catch (SQLException e){
            System.out.println("Error deleting friend: " + e.getMessage());
            return false;
        }
    }


    // Search list of friends
    // Input: (1) User_id of the friend
    // Output: True or False (successful or not)
    public List<User> searchFriend(int user_id){
        try{
            searchFriend.setInt(1, user_id);
            searchFriend.setInt(2, user_id);

            ResultSet results = searchFriend.executeQuery();

            List<User> users = null;

            while(results.next()){
                User user = new User(
                        results.getInt(1),
                        results.getString(2),
                        results.getString(3),
                        results.getString(4),
                        results.getString(5),
                        results.getString(6),
                        results.getString(7)
                );
                users.add(user);
            }

            return users;

        }catch (SQLException e){
            System.out.println("Error searching: " + e.getMessage());
            return null;
        }
    }


    // Add a Group in the list of groups
    // Input: (1) List_Group object
    // Output: Generated ID from auto increment
    public int addGroup(List_Group group){
        try {
            addGroup.setString(1, group.group_name);

            int affectedRows = addGroup.executeUpdate();

            int id = -1;

            if(affectedRows > 0){
                try(ResultSet generatedKeys = addGroup.getGeneratedKeys()){
                    if(generatedKeys.next()){
                        id = generatedKeys.getInt(1);
                    }
                }
            }

            return id;


        }catch (SQLException e){
            System.out.println("Insertion failed: " + e.getMessage());
            return -1;
        }
    }


    // Update Group Name
    // Input: (1) String new group name, (2) int Group id
    // Output: True or False (successful or not)
    public boolean updateGroup(String groupName, int group_id){
        try {
            updateGroup.setString(1, groupName);
            updateGroup.setInt(2, group_id);

            updateGroup.execute();

            return true;

        }catch (SQLException e){
            System.out.println("Insertion failed: " + e.getMessage());
            return false;
        }
    }

    // Delete a group from the list of groups
    // Input: (1) int group id
    // Output: True or False (successful or not)
    public boolean deleteGroup(int group_id){
        try{
            deleteGroup.setInt(1, group_id);
            deleteGroup.execute();

            return true;
        }catch (SQLException e){
            System.out.println("Error deleting group: " + e.getMessage());
            return false;
        }
    }


    // Search for a specific group
    // Input: (1) int group_id
    // Output: List_group object
    public List_Group searchGroup(int group_id){
        try{
            searchGroup.setInt(1, group_id);

            ResultSet results = searchGroup.executeQuery();

            List_Group group = null;

            while(results.next()){
                group = new List_Group(
                        results.getInt(1),
                        results.getString(2)
                );
            }

            return group;

        }catch (SQLException e){
            System.out.println("Error searching: " + e.getMessage());
            return null;
        }
    }


    // Add a User
    // Input: (1) User object
    // Output: Generated ID from auto increment
    public int addUser(User user){
        try{
            addUser.setString(1, user.first_name);
            addUser.setString(2, user.last_name);
            addUser.setString(3, user.email);
            addUser.setString(4, user.phone_number);
            addUser.setString(5, user.password);
            addUser.setString(6, user.username);

            int affectedRows = addUser.executeUpdate();

            int id = -1;

            if(affectedRows > 0){
                try(ResultSet generatedKeys = addUser.getGeneratedKeys()){
                    if(generatedKeys.next()){
                        id = generatedKeys.getInt(1);
                    }
                }
            }

            return id;

        }catch(SQLException e){
            System.out.println("Error adding user: " + e.getMessage());
            return -1;
        }
    }

    public int addUserExpense(User_expense user_expense){
        try{
            addUserExpense.setInt(1, user_expense.user_id);
            addUserExpense.setInt(2, user_expense.expense_id);
            addUserExpense.setDouble(3, user_expense.balance);

            int affectedRows = addUserExpense.executeUpdate();

            int id = -1;

            if(affectedRows > 0){
                try(ResultSet generatedKeys = addUserExpense.getGeneratedKeys()){
                    if(generatedKeys.next()){
                        id = generatedKeys.getInt(3);
                    }
                }
            }

            return id;

        }catch(SQLException e){
//            System.out.println("Error adding user_expense: " + e.getMessage());
            return -1;
        }
    }

    public void deleteUserExpense(int expenseId){
        try{
            deleteUserExpense.setInt(1, expenseId);
            deleteUserExpense.execute();
        }catch (SQLException e){
            System.out.println("Error deleting user expense: " + e.getMessage());
        }
    }

    public void addUserGroup(int userId, int groupId){
        try{
            addUserGroup.setInt(1, userId);
            addUserGroup.setInt(2, groupId);

            addUserGroup.execute();

        }catch (SQLException e){
            System.out.println("Error adding user_group: " + e.getMessage());
        }
    }

    public List<User> getGroupMembers(int groupId){
        try{
            groupMembers.setInt(1, groupId);

            ResultSet resultSet = groupMembers.executeQuery();

            List<User> users = new ArrayList<>();

            while(resultSet.next()){
                User user = new User(
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        resultSet.getString(5),
                        resultSet.getString(6),
                        resultSet.getString(7)
                );
                users.add(user);
            }

            return users;

        }catch (SQLException e){
            System.out.println("Error getting list of group members: " + e.getMessage());
            return null;
        }
    }

    public void deleteUserGroup(int groupId){
        try{
            deleteUserGroup.setInt(1, groupId);

            deleteUserGroup.execute();
        }catch(SQLException e){
            System.out.println("Error deleting user_group: " + e.getMessage());
        }
    }

    public void updateBalance(int userId, int expenseId, double newBalance){
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        newBalance = Double.parseDouble(decimalFormat.format(newBalance));

        try{
            updateBalance.setDouble(1, newBalance);
            updateBalance.setInt(2, userId);
            updateBalance.setInt(3, expenseId);

            updateBalance.execute();

            checkSettled(expenseId);
        }catch (SQLException e){
            System.out.println("Error updating balance: " + e.getMessage());
        }
    }

    public void checkSettled(int expenseId){
        boolean isSettled = true;
        String getBalances = "SELECT Balance FROM user_expense WHERE Expense_id = " + expenseId;
        String setSettled = "UPDATE expense SET Is_settled = 1 WHERE Expense_id = " + expenseId;

        try{
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(getBalances);

            while(resultSet.next()){
                if(resultSet.getInt(1) != 0){
                    isSettled = false;
                }
            }

            if (isSettled){
                statement.execute(setSettled);
            }
        }catch (SQLException e){
            System.out.println("Error checking settled expenses: " + e.getMessage());
        }
    }

    // Print all users
    // Input: none
    // Output: prompt printing, return void
    public void printAllUsers(){
        for(User user: this.userList){
            System.out.println("User ID: " + user.user_id);
            System.out.println("User Name: " + user.first_name + " " + user.last_name);
            System.out.println("===============\n");
        }
    }
}
