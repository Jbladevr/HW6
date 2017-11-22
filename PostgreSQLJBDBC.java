/************************************************************
 *HW6 for Databases class.
 * By Egor Muscat.
 ************************************************************/
package p;
import java.sql.*;
import java.io.*;
import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
public class PostgreSQLJBDBC {
	static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
	static Connection conn = null;
    static ResultSet rs = null;
    static Statement ps = null;
    static String firstName = "",
    		lastName= "",
    		street = "",
    		city = "",
    		state = "",
    		zip = "",
    		country = "",
    		phone = "",
    		email = "",
    		username = "",
    		password = "",
    		origin = "",
    		destination = "",
    		carrier_name = "",
    		dtd = "",
    		dta = "",
    		time_of_flight = "";
    static int countryCode;
	static int areaCode;
	static String localNumber = "";
    static int u_flight_number = 0;
    static Scanner in = new Scanner(System.in);
    public static void main(String args[])throws IOException{
    	loginToDB();
    	userData();
        userDataInsert();
        selectFlight();
        confirmation();
    }
    public static void loginToDB() {
    	System.out.print("Please enter the username to connect to Database: "+username);
    	username = in.nextLine();
    	System.out.print("Please enter the password to connect to Database: "+password);
    	password = in.nextLine();
    	System.out.println();
    }
    public static void openConnection() {
        try {
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/HW5", username, password);
			
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
    }
    
    public static void userDataInsert(){
    	try {
    		openConnection();
    		conn.setAutoCommit(false);
    		ps = conn.createStatement();
    		countryCode = Integer.valueOf(phone.substring(0,1));
    		areaCode = Integer.valueOf(phone.substring(2,5));
    		localNumber = phone.substring(6,14);
    		String sqlphone = "INSERT INTO phone (primary_phone_id,country_code,area_code,local_number)"
    				+ "Values ((select count(*) from phone)+1,'"+countryCode+ "','"+areaCode+"','" +localNumber+"')";
    		ps.executeUpdate(sqlphone);
    		String sqlma = "INSERT INTO mailing_address (address_id,street,city,province_state,postal_code,country)"
    				+ "Values ((select count(*) from mailing_address)+1,'"+street+ "','"+city+"','" +state+"','"+zip+"','" +country+"')";
    		ps.executeUpdate(sqlma);
    		String sql = "INSERT INTO customer (first_name,last_name,mailing_address_id,primary_phone_id,email)"
    				+ "Values ('"+firstName+"','"+lastName+"',(select count(*) from mailing_address),"
    						+ "(select count(*) from phone),'"+email+"')";
    		ps.executeUpdate(sql);
    		ps.close();
    		conn.commit();
    		conn.close();
    		System.out.println("Data was inserted into the database successfully");
    	} catch (SQLException e) {
    		System.err.print( e.getClass().getName()+": " + e.getMessage() );
    		System.exit(0);
    	}
    }
    public static void userData()throws IOException{
    	FileWriter fw = new FileWriter("HW6.error");
    	PrintWriter pw = new PrintWriter(fw);
    	System.out.println();
    	System.out.println("***********************************************");
    	System.out.println("*************** USER DATA INPUT ***************");
    	System.out.println("***********************************************");
    	
    	System.out.print("Please enter the first name: "+firstName);
    	firstName = in.nextLine();
    	System.out.print("Please enter the last name: "+lastName);
    	lastName = in.nextLine();
    	System.out.print("Please enter the address (ex:123 Duke st.): "+street);
    	street = in.nextLine();
    	System.out.print("Please enter the city: "+city);
    	city = in.nextLine();
    	System.out.print("Please enter the state obriviation: "+state);
    	state = in.nextLine();
    	System.out.print("Please enter the zip code (5 digits): "+zip);
    	zip = in.nextLine();
    	if (zip.length() != 5 || !(zip.matches("^[0-9]*$")))
    	{
    		System.out.println("Error, zip format is invalid.");
    		pw.print(firstName +" "+ lastName + " has entered an invalid zip code.");
    		pw.close();
    		System.exit(0);
    	}
    	System.out.print("Please enter the name of the country: "+country);
    	country= in.nextLine();
    	System.out.print("Please enter the phone number (ex: 1(xxx)123-4567): "+phone);
    	phone = in.nextLine();
    	if(!(phone.length() == 14 || phone.charAt(1) == '(' || phone.charAt(5) == ')' || phone.charAt(9) =='-'))
    	{
    		System.out.println("Error, phone format is invalid.");
    		pw.print(firstName +" "+ lastName + " has entered an invalid phone number.");
    		pw.close();
    		System.exit(0);
    	}
    	System.out.print("Please enter the email: "+email);
    	email = in.nextLine();
    	if (!(email.contains("@")))
    	{
    		System.out.println("Error, email format is invalid.");
    		pw.print(firstName +" "+ lastName + " has entered an invalid email.");
    		pw.close();
    		System.exit(0);
    	}
    	System.out.println();
    }
    
    public static void selectFlight(){
    	LocalDate localDate = LocalDate.now();
    	System.out.println();
    	System.out.println("***********************************************");
    	System.out.println("*************** FLIGHT SELECTION **************");
    	System.out.println("***********************************************");
    	System.out.print("Which city would you like to fly from (Origin)?: ");
    	origin = in.nextLine();
    	try{
    		openConnection();
    		conn.setAutoCommit(false);
    		ps = conn.createStatement();
    		rs = ps.executeQuery("SELECT D.city "
    				+ "FROM flight "
    				+ "inner join airport O on O.airport_id = flight.origin_id "
    				+ "inner join airport D on D.airport_id = flight.destination_id "
    				+ "Where O.city = '"+origin+"';");
    			System.out.println("The list of cities you can go to from "+origin+" is:");
    		while (rs.next()){
    			destination = rs.getString("city");
    			System.out.println(origin+" flies to "+destination);
    		}
    		rs.close();
    		ps.close();
    		conn.close();
    	}catch (SQLException e) {
    		System.err.print( e.getClass().getName()+": " + e.getMessage() );
        	System.exit(0);
    	}
    	System.out.println();
    	int counter = 0;
    	while (counter < 3) {
    		System.out.print("Which city would you like to fly to (Destination)?: ");
    		destination = in.nextLine();
    		try{
    			openConnection();
    			conn.setAutoCommit(false);
    			ps = conn.createStatement();
    			rs = ps.executeQuery("SELECT * "
    				+ "FROM flight "
    				+ "inner join airport O on O.airport_id = flight.origin_id "
    				+ "inner join airport D on D.airport_id = flight.destination_id "
    				+ "Where O.city = '"+origin+"' AND D.city = '"+destination+"';");
    			if (!rs.isBeforeFirst()) {
    				System.out.println("City doesn't exist, try again please.");
    				counter++;
    				if (counter == 3) {
    					System.out.println("Start over please.");
    					rs.close();
    					ps.close();
    					conn.close();
    					System.exit(0);
    				}
    			}
    			else {
    				rs.next();
    				u_flight_number = rs.getInt("Unique_flight_number");
    				break;
    			}
    			rs.close();
        		ps.close();
        		conn.close();
    		}catch (SQLException e) {
    			System.err.print( e.getClass().getName()+": " + e.getMessage() );
    			System.exit(0);
    		}
    	}
    	try {
    		openConnection();
    		conn.setAutoCommit(false);
    		ps = conn.createStatement();
    		String sqlbooking = "INSERT INTO booking (booking_number,city_booked,date_booked,first_name_payment,last_name_payment,"
    				+ "first_name_ticket,last_name_ticket,unique_flight_number)"
    				+ "Values ((select count(*) from booking)+1,'"+city+"','"+(dtf.format(localDate))+"','" +firstName+"','"+lastName+"','"
    				+firstName+"','"+lastName+"','" +u_flight_number+"')";
    		ps.executeUpdate(sqlbooking);
    		ps.close();
    		conn.commit();
    		conn.close();
    		System.out.println("Data was inserted into the database successfully");
    	} catch (SQLException e) {
    		System.err.print( e.getClass().getName()+": " + e.getMessage() );
    		System.exit(0);
    	}
    		
    }
    
    public static void confirmation() throws IOException{
    	FileWriter fw = new FileWriter("HW6.flight");
    	PrintWriter pw = new PrintWriter(fw);
    	try {
    		openConnection();
    		conn.setAutoCommit(false);
    		ps = conn.createStatement();
    		rs = ps.executeQuery("SELECT * "
    				+ "FROM flight "
    				+ "inner join carrier on carrier.airline_id = flight.airline_id ");
    		rs.next();
    		carrier_name = rs.getString("carrier_name");
    		dtd = rs.getString("date_time_departure");
    		dta = rs.getString("date_time_arrival");
    		time_of_flight = rs.getString("time_of_flight");
    		pw.println();
        	pw.println("***********************************************");
        	pw.println("*************** CONFIRMATION ******************");
        	pw.println("***********************************************");
        	pw.println();
    		pw.println("Customer's information:");
	    	pw.println("************************");
	    	pw.println("Customer's Name: "+firstName+" "+lastName);
	    	pw.println("Address: "+street);
	    	pw.println("Address: "+city+", "+state+" "+zip);
	    	pw.println("Country: "+country);
	    	pw.println("Customer's phone number: "+countryCode+"("+areaCode+")"+localNumber);
	    	pw.println("Customer's email: "+email);
	    	pw.println();
	    	pw.println("Flight's information:");
	    	pw.println("*********************");
	    	pw.println("Flight: "+ u_flight_number);
	    	pw.println("From: "+origin);
	    	pw.println("To: "+destination);
	    	pw.println("Carrier: "+carrier_name);
	    	pw.println("Departure date and time: "+dtd);
	    	pw.println("Arrival date and time: "+dta);
	    	pw.println("Flight time: "+time_of_flight+" hours");
    		rs.close();
    		ps.close();
    		pw.close();
    		conn.close();
    		System.out.println();
        	System.out.println("***********************************************");
        	System.out.println("*************** CONFIRMATION  *****************");
        	System.out.println("***********************************************");
        	System.out.println();
    		System.out.println("Your reservation was successful.");
    		System.out.println();
	    	System.out.println("Customer's information:");
	    	System.out.println("************************");
	    	System.out.println("Customer's Name: "+firstName+" "+lastName);
	    	System.out.println("Address: "+street);
	    	System.out.println("Address: "+city+", "+state+" "+zip);
	    	System.out.println("Country: "+country);
	    	System.out.println("Customer's phone number: "+countryCode+"("+areaCode+")"+localNumber);
	    	System.out.println("Customer's email: "+email);
	    	System.out.println();
	    	System.out.println("Flight's information:");
	    	System.out.println("*********************");
	    	System.out.println("Flight: "+ u_flight_number);
	    	System.out.println("From: "+origin);
	    	System.out.println("To: "+destination);
	    	System.out.println("Carrier: "+carrier_name);
	    	System.out.println("Departure date and time: "+dtd);
	    	System.out.println("Arrival date and time: "+dta);
	    	System.out.println("Flight time: "+time_of_flight+" hours");	
    	}catch (SQLException e) {
    		System.err.print( e.getClass().getName()+": " + e.getMessage() );
        	System.exit(0);
    	}	
    }
}