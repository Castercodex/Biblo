package db;


import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import com.mongodb.MongoException;
import com.mongodb.client.*;
import com.password4j.Hash;
import com.password4j.Password;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.ArrayList;



public class MongoDBConnection {
    // Database Connection
    private static final String uri = "mongodb+srv://admin:admin%40ESTA@cluster0.9eieh.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0";
    private static final MongoClient mongoClient = MongoClients.create(uri);
    private static final MongoDatabase database = mongoClient.getDatabase("biblo");
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
            MongoCollection<Document> membersCollection = database.getCollection("books");

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

    public static List<Document> getMembers() {
        // Get the collection from MongoDB
        MongoCollection<Document> membersCollection = database.getCollection("members");

        // Retrieve all members from the collection
        List<Document> members = new ArrayList<>();
        membersCollection.find().into(members);

        return members;
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