package db;


import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import com.mongodb.MongoException;
import com.mongodb.client.*;
import com.password4j.Hash;
import com.password4j.Password;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;


public class MongoDBConnection {
    // Database Connection
    private static final String uri = "";
    private static final MongoClient mongoClient = MongoClients.create(uri);
    public static final MongoDatabase database = mongoClient.getDatabase("biblo");
    public static String signupError;
    public static String loginError;

    // Login user authentication
    public static boolean authLogin(String email, String password)  {

        try{
            MongoCollection<Document> userCollection = database.getCollection("users");
            Document user = userCollection.find(eq("email", email)).first();

            if (user == null) {
                System.out.println("User not found");
                loginError = "User not found";
                return false;
            }else{
            // Retrieve Hashed Password from DB
            String hashFromDB = user.get("password").toString();
            // Verify Hashed Password with User Password
            boolean verified = Password.check(password, hashFromDB).addPepper("hotHot").withBcrypt();
            if (!verified) {
                System.out.println("Password verification failed");
                loginError = "Password verification failed";
                return false;
            }else {
                System.out.println("Password verified");
                loginError = "";
                return true;
            }
            }

        }
        catch(MongoException e){
            String error = "Error authenticating user: " + e.getMessage();
            System.out.println(error);
        }
        return true;
    }
    // Register Auth Method
    public static boolean authRegister(String fullName, String email, String password) {
        try {
            // Normalize the email to lowercase and trim whitespace
            email = email.trim().toLowerCase();

            // Get the users collection
            MongoCollection<Document> userCollection = database.getCollection("users");

            // Hash the password with a pepper and Bcrypt
            String pepper = "hotHot";
            Hash hash = Password.hash(password).addPepper(pepper).withBcrypt();
            password = hash.getResult();

            // Debug: Check if method is called more than once
            System.out.println("authRegister method called with email: " + email);

            // Check if the user already exists
            Document user = userCollection.find(eq("email", email)).first();
            System.out.println("Query result for email: " + email + " -> " + user);

            if (user != null) {
                System.out.println("User already exists 1");
                signupError = "User already exists";
                return false;
            }


            // Create a new user document and insert it into the database
            Document newUser = new Document("email", email)
                    .append("name", fullName)
                    .append("password", password)
                    .append("fullName", fullName);
            userCollection.insertOne(newUser);

            System.out.println("User created successfully");
            signupError = "";
            return true;

        } catch (MongoException e) {
            System.err.println("Error registering user: " + e.getMessage());
            return false; // Signal failure to the caller
        }
    }

    public static Boolean addBook(String bookTitle, String bookAuthor, String bookCategory, String bookImage, String datePublished) {
        try {
            // Get the "books" collection
            MongoCollection<Document> booksCollection = database.getCollection("books");

            // Check if the book already exists
            Document existingBook = booksCollection.find(and(eq("title", bookTitle), eq("author", bookAuthor))).first();
            if (existingBook != null) {
                System.out.println("Book already exists");
                return false; // Book already in the database
            }

            // Create and insert a new book document
            Document newBook = new Document("title", bookTitle)
                    .append("author", bookAuthor)
                    .append("category", bookCategory)
                    .append("image", bookImage)
                    .append("published", datePublished);
            booksCollection.insertOne(newBook);

            System.out.println("Book created successfully");
            return true; // Book successfully added

        } catch (MongoException e) {
            // Handle MongoDB exceptions
            System.err.println("Error adding a book: " + e.getMessage());
            return false; // Return false to indicate failure
        } catch (Exception e) {
            // Catch any unexpected exceptions
            System.err.println("Unexpected error: " + e.getMessage());
            return false; // Return false to indicate failure
        }
    }
    public static List<Document> getBooks() {
        // Get the collection from MongoDB
        MongoCollection<Document> booksCollection = database.getCollection("books");

        // Retrieve all books from the collection
        List<Document> books = new ArrayList<>();
        booksCollection.find().into(books);

        return books;
    }
    public Document getBookByTitle(String title) {
        MongoCollection<Document> booksCollection = database.getCollection("books"); // Replace "books" with your collection name
        return booksCollection.find(new Document("title", title)).first();
    }

    public static List<Document> getBooksByAuthor(String author) {
        // Get the collection from MongoDB
        MongoCollection<Document> booksCollection = database.getCollection("books");

        // Query to get books by a specific author
        List<Document> books = new ArrayList<>();
        booksCollection.find(eq("author", author)).into(books);

        return books;
    }

    public static List<Document> getBooksByTitle(String title) {
        // Get the collection from MongoDB
        MongoCollection<Document> booksCollection = database.getCollection("books");

        // Query to get books by a specific title
        List<Document> books = new ArrayList<>();
        booksCollection.find(eq("title", title)).into(books);

        return books;
    }

    public static Boolean addMember(String fullName, String email, String membership, String joinDate) {
        try {
            // Get the "members" collection
            MongoCollection<Document> membersCollection = database.getCollection("members");

            // Check if the member already exists
            Document existingMember = membersCollection.find(eq("email", email)).first();
            if (existingMember != null) {
                System.out.println("Member already exists");
                return false; //Member already in the database
            }

            // Create and insert a new member document
            Document newBook = new Document("fullname", fullName)
                    .append("email", email)
                    .append("membership", membership)
                    .append("join", joinDate);
            membersCollection.insertOne(newBook);

            System.out.println("Member created successfully");
            return true; // Member successfully added

        } catch (MongoException e) {
            // Handle MongoDB exceptions
            System.err.println("Error adding a member: " + e.getMessage());
            return false; // Return false to indicate failure
        } catch (Exception e) {
            // Catch any unexpected exceptions
            System.err.println("Unexpected error: " + e.getMessage());
            return false; // Return false to indicate failure
        }
    }

    public static Boolean addLoan(String userId, String fullname, String bookId, String title, String loanStart, String loanReturn) {
        try {
            // Get the loans collection
            MongoCollection<Document> loansCollection = database.getCollection("loans");

            // Check if the member already exists
            Document existingLoanMember = loansCollection.find(and(eq("userId", userId), eq("bookId", bookId))).first();
            if (existingLoanMember != null) {
                System.out.println("Loan already exists");
                return false;
            }

            // Create and insert a new member document
            Document newBook = new Document("userId", userId)
                    .append("fullname", fullname)
                    .append("bookId", bookId)
                    .append("title", title)
                    .append("loanStartDate", loanStart)
                    .append("loanReturnDate", loanReturn);
            loansCollection.insertOne(newBook);

            System.out.println("Member created successfully");
            return true; // Member successfully added

        } catch (MongoException e) {
            // Handle MongoDB exceptions
            System.err.println("Error adding a member: " + e.getMessage());
            return false; // Return false to indicate failure
        } catch (Exception e) {
            // Catch any unexpected exceptions
            System.err.println("Unexpected error: " + e.getMessage());
            return false; // Return false to indicate failure
        }
    }

    public static Boolean  removeLoan(String id){
        try{
            MongoCollection<Document> loansCollection = database.getCollection("loans");
            ObjectId ObjectId = new ObjectId(id);

            Document existingLoanMember = loansCollection.find(eq("_id", ObjectId)).first();
            if (existingLoanMember == null) {
                System.out.println("Loan does not exist");

                return false;

            } else{
                System.out.println("Loan already exists");
                loansCollection.deleteOne(existingLoanMember);
                return true;
            }
        }
        catch(Exception e){
            System.err.println("Unexpected error: " + e.getMessage());
            return false;
        }
    }
    public static Boolean  removeMember(String id){
        try{
            MongoCollection<Document> membersCollection = database.getCollection("members");
            ObjectId ObjectId = new ObjectId(id);

            Document existingMember = membersCollection.find(eq("_id", ObjectId)).first();
            if (existingMember == null) {
                System.out.println("Member does not exist");

                return false;

            } else{
                System.out.println("Member already exists");
                membersCollection.deleteOne(existingMember);
                return true;
            }
        }
        catch(Exception e){
            System.err.println("Unexpected error: " + e.getMessage());
            return false;
        }
    }


    public static List<Document> getMembers() {
        // Get the collection from MongoDB
        MongoCollection<Document> membersCollection = database.getCollection("members");
        // Retrieve all members from the collection
        List<Document> members = new ArrayList<>();
        membersCollection.find().into(members);

        return members;
    }

    public static List<Document> getLoans() {
        MongoCollection<Document> loansCollection = database.getCollection("loans");
        List<Document> loans = new ArrayList<>();
        loansCollection.find().into(loans);

        return loans;

    }

    public static Document getUserByEmail(String email) {
        // Ensure that 'database' is initialized
        if (database == null) {
            System.out.println("Database connection is not initialized.");
            return null;
        }

        // Get the members collection from the database
        MongoCollection<Document> usersCollection = database.getCollection("users");

        // Query the collection to find a user by email (case-insensitive)
        Document query = new Document("email", new Document("$regex", "^" + email + "$").append("$options", "i"));
        FindIterable<Document> result = usersCollection.find(query); // Execute query

        // Return the first match (if any)
        return result.first(); // returns the first document or null if no match found
    }




    public static void main( String[] args ) {

        try {
            database.runCommand(new Document("ping", 1));
            // Check Connection
            System.out.println("Pinged your deployment. You successfully connected to MongoDB!");
//            addBook("Harry Potter" , "JK Rowlings", "Scifi", "https://hello.com/sample.jpg", new Date());
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}