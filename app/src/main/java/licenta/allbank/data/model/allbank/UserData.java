package licenta.allbank.data.model.allbank;

public class UserData {
    private static String username;
    private static String firstName;
    private static String lastName;
    private static String phone;
    private static String email;

    public static String getUsername() {
        return username;
    }

    public static String getFirstName() {
        return firstName;
    }

    public static String getLastName() {
        return lastName;
    }

    public static String getPhone() {
        return phone;
    }

    public static String getEmail() {
        return email;
    }

    public static void setUsername(String username) {
        UserData.username = username;
    }

    public static void setFirstName(String firstName) {
        UserData.firstName = firstName;
    }

    public static void setLastName(String lastName) {
        UserData.lastName = lastName;
    }

    public static void setPhone(String phone) {
        UserData.phone = phone;
    }

    public static void setEmail(String email) {
        UserData.email = email;
    }
}
