import models.Data;
import models.Expense;
import models.List_Group;
import models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Data instance = new Data();
        Scanner sc = new Scanner(System.in);
        User programUser = null;

        int mainMenu = -1;
        int tempChoice = -1;
        int tempChoice2 = -1;

        // Welcome section ==============================================
        tempChoice = welcomePage(sc);

        if (tempChoice == 1) {
            programUser = loginPage(sc, instance);
        } else if (tempChoice == 2) {
            programUser = signupPage(sc, instance);
        } else {
            System.out.println("\nInvalid input!");
            System.exit(0);
        }

        if (programUser == null) {
            System.exit(0);
        }

        // Main Menu Section ==============================================
        System.out.println("\nHello, " + programUser.first_name + " " + programUser.last_name + "!");

        while (mainMenu != 0) {
            tempChoice = mainMenu(sc, programUser);

            if (tempChoice >= 1 && tempChoice <= 3) {
                tempChoice2 = commonMenus(sc, tempChoice);
            } else if (tempChoice == 4) {
                tempChoice2 = reportsMenu(sc);
            } else if (tempChoice == 0) {
                mainMenu = 0;
            } else if(tempChoice == 5){
              programUser.payBalance(sc, instance);
            } else {
                System.out.println("\nInvalid input!");
                continue;
            }

            switch (tempChoice2) {
                case 1:
                    if (tempChoice == 1) {
                        addExpense(programUser, sc, instance);
                    } else if (tempChoice == 2) {
                        programUser.addFriend(programUser, sc, instance);
                    } else if (tempChoice == 3) {
                        addGroup(programUser, sc, instance);
                    } else if (tempChoice == 4) {
                        programUser.viewExpensesInAMonth(sc, instance);
                    }
                    break;
                case 2:
                    if (tempChoice == 1) {
                        deleteExpense(programUser, sc, instance);
                    } else if (tempChoice == 2) {
                        programUser.deleteFriend(programUser, sc, instance);
                    } else if (tempChoice == 3) {
                        programUser.deleteGroup(programUser, sc, instance);
                    } else if (tempChoice == 4) {
                        programUser.viewExpensesMadeWithAFriend(sc, instance);
                    }
                    break;
                case 3:
                    if (tempChoice == 1) {
                        programUser.searchExpense(sc, instance);
                    } else if (tempChoice == 2) {
                        programUser.searchFriend(programUser, sc, instance);
                    } else if (tempChoice == 3) {
                        programUser.searchGroup(programUser, sc, instance);
                    } else if (tempChoice == 4) {
                        programUser.viewExpensesMadeWithAGroup(sc, instance);
                    }
                    break;
                case 4:
                    if (tempChoice == 1) {
                        programUser.updateExpense(programUser, sc, instance);
                    } else if (tempChoice == 2) {
                        programUser.updateFriend(programUser, sc, instance);
                    } else if (tempChoice == 3) {
                        programUser.updateGroup(sc, instance);
                    } else if (tempChoice == 4) {
                        programUser.viewCurrentBalanceFromExpenses(instance);
                    }
                    break;
                case 5:
                    if(tempChoice == 4){
                        programUser.viewAllFriendsWithOutstandingBalance(instance);
                    }
                    break;
                case 6:
                    if(tempChoice == 4){
                        programUser.viewAllGroups(instance);
                    }
                    break;
                case 7:
                    if(tempChoice == 4){
                        programUser.viewAllGroupsWithAnOutstandingBalance(instance);
                    }
                    break;
            }
        }

//        instance.printAllUsers();

        System.out.println("\nThank you and goodbye!");

        instance.closeDatabaseConnection();

    }

    public static int welcomePage(Scanner sc) {
        int wp = -1;

        while (wp != 1 && wp != 2) {
            System.out.println("\nWelcome to Expense Management System!");
            System.out.println("=====================================");
            System.out.println("1 - Log in as User");
            System.out.println("2 - Sign up as new User");
            System.out.print("Enter choice: ");
            try {
                wp = sc.nextInt();
            } catch (Exception e) {
                wp = 0;
                break;
            }

        }

        return wp;
    }

    public static User loginPage(Scanner sc, Data instance) {
        String username;
        String password;
        boolean isValidated = false;
        User authenticatedUser = null;

        sc.nextLine();

        while (!isValidated) {
            System.out.println("\n========== Login Page ==========");
            System.out.print("Enter username: ");
            username = sc.nextLine();
            System.out.print("Enter password: ");
            password = sc.nextLine();

            for (User user : instance.userList) {
                if (user.username.equals(username) && user.password.equals(password)) {
                    authenticatedUser = user;
                    isValidated = true;
                }
            }

            if (!isValidated) {
                System.out.println("\nEntered Wrong Credentials!");
                System.out.print("Do you want to try again? (Y/N): ");
                String temp = sc.nextLine();
                if (temp.equalsIgnoreCase("y")) {
                    continue;
                } else {
                    break;
                }
            }
        }

        return authenticatedUser;
    }

    public static User signupPage(Scanner sc, Data instance) {
        String firstName;
        String lastName;
        String email;
        String phoneNumber;
        String password;
        String username;

        sc.nextLine();

        System.out.println("\n========== Signup Page ==========");
        System.out.print("\nEnter First Name: ");
        firstName = sc.nextLine();

        System.out.print("\nEnter Last Name: ");
        lastName = sc.nextLine();

        System.out.print("\nEnter Email Address: ");
        email = sc.nextLine();

        System.out.print("\nEnter Phone Number: ");
        phoneNumber = sc.nextLine();

        System.out.print("\nEnter Password: ");
        password = sc.nextLine();

        System.out.print("\nEnter Username: ");
        username = sc.nextLine();

        User newUser = new User(-1, firstName, lastName, email, phoneNumber, password, username, instance);

        System.out.println("\n\nSuccessfully signed up as a user!");

        return newUser;
    }

    public static int mainMenu(Scanner sc, User user) {
        int choice = -1;

        while (choice < 0 || choice > 5) {
            System.out.println("\n================== Main Menu ==================");
            System.out.println("1 - Add, Delete, Search, and Update an Expense");
            System.out.println("2 - Add, Delete, Search, and Update a Friend");
            System.out.println("3 - Add, Delete, Search, and Update a Group");
            System.out.println("4 - Generate Reports");
            System.out.println("5 - Pay Expense Balance");
            System.out.println("0 - Exit");
            System.out.print("\nEnter Choice: ");
            choice = sc.nextInt();
        }

        return choice;
    }

    public static int commonMenus(Scanner sc, int choice) {
        int menuChoice = -1;
        String obj = choice == 1 ? "Expense" : choice == 2 ? "Friend" : "Group";

        while (menuChoice < 1 || menuChoice > 5) {
            System.out.println("\n========== User " + obj + " Menu ==========");
            System.out.println("1 - Add " + obj);
            System.out.println("2 - Delete " + obj);
            System.out.println("3 - Search " + obj);
            System.out.println("4 - Update " + obj);
            System.out.println("5 - Go back");

            System.out.print("\nEnter Choice: ");
            menuChoice = sc.nextInt();
        }

        return menuChoice;
    }

    public static int reportsMenu(Scanner sc) {
        int menuChoice = -1;

        while (menuChoice < 0 || menuChoice > 7) {
            System.out.println("================ Generate Reports ===============");
            System.out.println("1 - View all expenses made within a month");
            System.out.println("2 - View all expenses made with a friend");
            System.out.println("3 - View all expenses made with a group");
            System.out.println("4 - View current balance from all expenses");
            System.out.println("5 - View all friends with outstanding balance");
            System.out.println("6 - View all groups");
            System.out.println("7 - View all groups with an outstanding balance");
            System.out.println("0 - Go Back");
            System.out.print("\nEnter Choice: ");
            menuChoice = sc.nextInt();
        }

        return menuChoice;
    }

    public static void addExpense(User programUser, Scanner sc, Data instance){
        int menuChoice = -1, menuChoice2 = -1, i = 1;
        boolean isGroup = false;
        List<User> selectedUsers = new ArrayList<>();
        List<List_Group> listGroups;

        String desc, date;
        double amt;
        int lender, groupId = -1;
        List_Group newGroup = null;


        while(menuChoice < 1 || menuChoice > 2){
            System.out.println("========= Add Expense Type =========");
            System.out.println("1 - Add Expense with another User");
            System.out.println("2 - Add Expense for a Group");
            System.out.print("\nEnter Choice: ");
            menuChoice = sc.nextInt();
        }

        if(menuChoice == 1){
            System.out.println("\n======== Select user for Expense ========");

            while(menuChoice2 < 1 || menuChoice2 > instance.userList.size()){
                for(User user : instance.userList){
                    System.out.println(i + " - " + user.first_name + " " + user.last_name);
                    i++;
                }
                System.out.print("Enter Choice: ");
                menuChoice2 = sc.nextInt();
            }

            selectedUsers.add(instance.userList.get(menuChoice2-1));
            selectedUsers.add(programUser);
        }else{
            listGroups = instance.getListGroups(programUser.user_id);
            isGroup = true;
            int tempChoice = -1;

            while (tempChoice < 0 || tempChoice > listGroups.size()){
                System.out.println("========== Choose User Groups ==========");
                i = 1;
                for(List_Group group : listGroups){
                    System.out.println(i + " - " + group.group_name);
                    i++;
                }
                System.out.println("0 - Add a new group");
                tempChoice = sc.nextInt();
            }

            if(tempChoice != 0){
                groupId = listGroups.get(tempChoice-1).group_id;

                selectedUsers = instance.getGroupMembers(groupId);
            }else{
                sc.nextLine();
                System.out.print("\nPlease Enter a Group name: ");
                String groupName = sc.nextLine();

                groupId = programUser.addGroup(groupName, instance);
                System.out.println("\n Successfully added group name");


                while(menuChoice2 != 0){
                    System.out.println("\n======== Select users to be added to the Group =========");
                    i = 1;
                    for(User user : instance.userList){
                        System.out.println(i + " - " + user.first_name + " " + user.last_name);
                        i++;
                    }
                    if(selectedUsers.size() > 2){
                        System.out.println("0 - Stop Selection");
                    }
                    System.out.print("Enter Choice: ");
                    menuChoice2 = sc.nextInt();

                    if(menuChoice2 < 1 || menuChoice2 > instance.userList.size()){
                        continue;
                    }else{
                        selectedUsers.add(instance.userList.get(menuChoice2-1));
                    }
                }

                for(User user : selectedUsers){
                    instance.addUserGroup(user.user_id, groupId);
                }
            }
        }

        i = 1;
        sc.nextLine();

        System.out.println("\n======== Add Expense Details ========");
        System.out.print("Enter Expense Description: ");
        desc = sc.nextLine();
        sc.nextLine();
        System.out.print("Enter Expense Amount: ");
        amt = sc.nextDouble();
        sc.nextLine();
        System.out.print("Enter Expense Date (MM-dd-yyyy): ");
        date = sc.nextLine();
        sc.nextLine();
        System.out.print("\nEnter Expense Lender: \n");
        for(User user : selectedUsers){
            System.out.println(i +  " - " + user.first_name + " " + user.last_name);
            i++;
        }
        System.out.print("Enter Choice: ");
        lender = sc.nextInt();
        lender = selectedUsers.get(lender-1).user_id;


        if(isGroup){
            programUser.addExpense(desc, amt, date, lender, false, groupId, selectedUsers, instance);
        }else{
            programUser.addExpense(desc, amt, date, lender, false, -1, selectedUsers, instance);
        }
    }

    public static void deleteExpense(User programUser, Scanner sc, Data instance){
        int menuChoice = -1, i = 1;
        List<Expense> expenseList = instance.getListExpense(programUser.user_id);

        if(expenseList.size() > 0){
            while(menuChoice < 1 || menuChoice > expenseList.size()){
                System.out.println("========== Delete Expense ==========");
                for(Expense expense : expenseList){
                    System.out.println(i + " - " + expense.expense_description);
                    i++;
                }
                System.out.print("Enter Expense to Delete: ");
                menuChoice = sc.nextInt();
            }

            programUser.deleteExpense(expenseList.get(menuChoice-1), instance);
        }else{
            System.out.println("\nNo Expenses Yet!");
        }
    }

    public static void addGroup(User programUser, Scanner sc, Data instance){
        int menuChoice = -1, i = 1;
        List<User> selectedUsers = new ArrayList<>();

        sc.nextLine();
        System.out.println("=============== Add New Group ===============");
        System.out.print("\nPlease Enter a Group name: ");
        String groupName = sc.nextLine();

        int groupId = programUser.addGroup(groupName, instance);
        System.out.println("\n Successfully added group name");


        while(menuChoice != 0){
            System.out.println("\n======== Select users to be added to the Group =========");
            i = 1;
            for(User user : instance.userList){
                System.out.println(i + " - " + user.first_name + " " + user.last_name);
                i++;
            }
            if(selectedUsers.size() > 2){
                System.out.println("0 - Stop Selection");
            }
            System.out.print("Enter Choice: ");
            menuChoice = sc.nextInt();

            if(menuChoice < 1 || menuChoice > instance.userList.size()){
                continue;
            }else{
                selectedUsers.add(instance.userList.get(menuChoice-1));
            }
        }

        for(User user : selectedUsers){
            instance.addUserGroup(user.user_id, groupId);
        }
    }
}