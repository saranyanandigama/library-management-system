import java.util.*;
import java.time.*;
import java.time.temporal.ChronoUnit;

class Book {
    int ISBN;
    String title;
    String author;
    int totalCopies;
    int availableCopies;
    ArrayList<BorrowRecord> borrowers;

    Book(int ISBN, String title, String author, int copies) {
        this.ISBN = ISBN;
        this.title = title;
        this.author = author;
        this.totalCopies = copies;
        this.availableCopies = copies;
        this.borrowers = new ArrayList<>();
    }
}

class BorrowRecord {
    String borrowerName;
    LocalDate borrowDate;
    LocalDate dueDate;
    boolean fineCleared;
    double fineAmount;

    BorrowRecord(String name) {
        this.borrowerName = name;
        this.borrowDate = LocalDate.now();
        this.dueDate = borrowDate.plusDays(30);  // Due date = 30 days from borrow date
        this.fineCleared = true;
        this.fineAmount = 0;
    }
}

public class LibraryManagementSystem {
    static Scanner sc = new Scanner(System.in);
    static ArrayList<Book> library = new ArrayList<>();
    static HashMap<String, Double> userFines = new HashMap<>();

    // Add a new book
    public static void addBook() {
        System.out.print("Enter ISBN: ");
        int ISBN = sc.nextInt();
        sc.nextLine();
        System.out.print("Enter Title: ");
        String title = sc.nextLine();
        System.out.print("Enter Author: ");
        String author = sc.nextLine();
        System.out.print("Enter Number of Copies: ");
        int copies = sc.nextInt();

        for (Book b : library) {
            if (b.ISBN == ISBN) {
                System.out.println("Book with this ISBN already exists!");
                return;
            }
        }

        library.add(new Book(ISBN, title, author, copies));
        System.out.println("✅ Book added successfully!");
    }

    // Search a book
    public static Book searchBook(int ISBN) {
        for (Book b : library) {
            if (b.ISBN == ISBN) {
                return b;
            }
        }
        return null;
    }

    // Borrow a book
    public static void borrowBook() {
        System.out.print("Enter your name: ");
        String name = sc.nextLine();

        if (userFines.getOrDefault(name, 0.0) > 0) {
            System.out.println("⚠️ You have an unpaid fine of ₹" + userFines.get(name) + ". Please clear it before borrowing another book.");
            return;
        }

        System.out.print("Enter ISBN to borrow: ");
        int ISBN = sc.nextInt();
        sc.nextLine();
        Book book = searchBook(ISBN);

        if (book == null) {
            System.out.println("❌ Book not found!");
            return;
        }

        if (book.availableCopies > 0) {
            BorrowRecord record = new BorrowRecord(name);
            book.availableCopies--;
            book.borrowers.add(record);
            System.out.println("✅ " + name + " borrowed \"" + book.title + "\" on " + record.borrowDate);
            System.out.println("📅 Due Date: " + record.dueDate);
        } else {
            System.out.println("❌ No copies available for \"" + book.title + "\".");
        }
    }

    // Return a book
    public static void returnBook() {
        System.out.print("Enter your name: ");
        String name = sc.nextLine();
        System.out.print("Enter ISBN to return: ");
        int ISBN = sc.nextInt();
        sc.nextLine();
        Book book = searchBook(ISBN);

        if (book == null) {
            System.out.println("❌ Book not found!");
            return;
        }

        BorrowRecord recordToRemove = null;
        for (BorrowRecord record : book.borrowers) {
            if (record.borrowerName.equalsIgnoreCase(name)) {
                recordToRemove = record;
                break;
            }
        }

        if (recordToRemove == null) {
            System.out.println("⚠️ You did not borrow this book.");
            return;
        }

        System.out.println("Borrow Date: " + recordToRemove.borrowDate);
        System.out.println("Due Date: " + recordToRemove.dueDate);
        System.out.print("Enter Return Date (yyyy-mm-dd): ");
        String returnDateStr = sc.nextLine();

        LocalDate returnDate;
        try {
            returnDate = LocalDate.parse(returnDateStr);
        } catch (Exception e) {
            System.out.println("❌ Invalid date format! Please use yyyy-mm-dd.");
            return;
        }

        long daysBetween = ChronoUnit.DAYS.between(recordToRemove.borrowDate, returnDate);
        double fine = 0;

        if (daysBetween > 30) {
            fine = daysBetween - 30;
            System.out.println("⚠️ You are late by " + (daysBetween - 30) + " days. Fine = ₹" + fine);
            userFines.put(name, userFines.getOrDefault(name, 0.0) + fine);
        } else if (daysBetween < 0) {
            System.out.println("❌ Return date cannot be before borrow date!");
            return;
        } else {
            System.out.println("✅ Book returned on time. No fine.");
        }

        book.borrowers.remove(recordToRemove);
        book.availableCopies++;
        System.out.println("📘 " + name + " returned \"" + book.title + "\" successfully on " + returnDate);
    }

    // Pay Fine
    public static void payFine() {
        System.out.print("Enter your name: ");
        String name = sc.nextLine();
        double fine = userFines.getOrDefault(name, 0.0);

        if (fine <= 0) {
            System.out.println("✅ No pending fines!");
            return;
        }

        System.out.println("💰 Your total fine: ₹" + fine);
        System.out.print("Do you want to pay now? (yes/no): ");
        String choice = sc.nextLine();

        if (choice.equalsIgnoreCase("yes")) {
            userFines.put(name, 0.0);
            System.out.println("✅ Fine cleared successfully!");
        } else {
            System.out.println("⚠️ Fine not cleared. You cannot borrow new books until you pay.");
        }
    }

    // Display all books
    public static void displayBooks() {
        if (library.isEmpty()) {
            System.out.println("📚 No books in the library.");
            return;
        }

        for (Book b : library) {
            System.out.println("\n---------------------------------");
            System.out.println("ISBN: " + b.ISBN);
            System.out.println("Title: " + b.title);
            System.out.println("Author: " + b.author);
            System.out.println("Total Copies: " + b.totalCopies);
            System.out.println("Available Copies: " + b.availableCopies);

            if (b.borrowers.isEmpty()) {
                System.out.println("Borrowers: None");
            } else {
                System.out.println("Borrowers:");
                for (BorrowRecord br : b.borrowers) {
                    System.out.println(" - " + br.borrowerName + " (Borrowed on: " + br.borrowDate + ", Due: " + br.dueDate + ")");
                }
            }
        }
    }

    // Main Menu
    public static void main(String[] args) {
        while (true) {
            System.out.println("\n===== Library Management System =====");
            System.out.println("1. Add Book");
            System.out.println("2. Search Book");
            System.out.println("3. Borrow Book");
            System.out.println("4. Return Book");
            System.out.println("5. Display All Books");
            System.out.println("6. Pay Fine");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> addBook();
                case 2 -> {
                    System.out.print("Enter ISBN to search: ");
                    int ISBN = sc.nextInt();
                    Book found = searchBook(ISBN);
                    if (found != null) {
                        System.out.println("📘 Book Found: " + found.title + " by " + found.author);
                        System.out.println("Available Copies: " + found.availableCopies);
                    } else {
                        System.out.println("❌ Book not found!");
                    }
                }
                case 3 -> borrowBook();
                case 4 -> returnBook();
                case 5 -> displayBooks();
                case 6 -> payFine();
                case 7 -> {
                    System.out.println("👋 Exiting... Thank you!");
                    sc.close();
                    System.exit(0);
                }
                default -> System.out.println("❌ Invalid choice! Try again.");
            }
        }
    }
}