package db;


import static com.mongodb.client.model.Filters.eq;
import com.mongodb.MongoException;
import com.mongodb.client.*;
import com.password4j.Hash;
import com.password4j.Password;
import org.bson.Document;



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

//    public static boolean authRegister(String fullName, String email, String password){
//        try{
//            // Get collection
//            MongoCollection<Document> userCollection = database.getCollection("users");
//            //Hash user Password
//            Hash hash = Password.hash(password).addPepper("hotHot").withBcrypt();
//            password = hash.getResult();
//
//            Document user = userCollection.find(eq("email", email)).first();
//            //Check If User Exists in DB
//            if(user != null){
//                System.out.println("User already exists");
//                signupError = "User already exists";
//                System.out.println(user.toJson());
//                return false;
//
//            }else{
//                userCollection.insertOne(new Document("email", email).append("password", password).append("fullName", fullName));
//                System.out.println("User created");
//            }
//        }
//        catch(MongoException e){
//            String error = "Error registering user: " + e.getMessage();
//            System.out.println(error);
//        }
//        return true;
//
//    }


    public static void main( String[] args ) {

        try {
            database.runCommand(new Document("ping", 1));
            // Check Connection
            System.out.println("Pinged your deployment. You successfully connected to MongoDB!");
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}