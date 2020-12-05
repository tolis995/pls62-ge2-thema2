package ge2thema4;

import java.util.ArrayList;
import java.util.Scanner;

public class LeachApp {

	public static void main(String[] args) {
		int r;     // Μεταβλητή  r = αριθμός γύρων
		double N;  // Μεταβλητή  Ν = αριθμός κόμβων,
		double p;  // Μεταβλητή  p = επιθυμητό ποσοστό επικεφαλής κόμβων από το σύνολο των κόμβων

		Scanner userInput = new Scanner(System.in); // είσοδος δεδομένων από το χρήστη μέσω πληκτρολογίου
		System.out.println("Enter number of WSNodes: ");
		N=userInput.nextDouble();

		System.out.println("Enter desired percentage of Cluster Heads (ie 5 for 5%): ");
		double k=userInput.nextDouble();
		System.out.println("Enter number of rounds: ");
		r=userInput.nextInt();

		p=k/100; // διαιρώ την τιμή που έδωσε ο χρήστης με το 100
		// Initialize WSNetwork - δημιουργία Wireless Sensor Network και των κόμβων που το απαρτίζουν
		WSNetwork network = new WSNetwork(N,p,r);  // δημιουργία αντικειμένου network. Ο κατασκευαστής της WSNetwork δημιουργεί Ν κόμβους με τις αρχικές τους τιμές.

		// r rounds of Setup Phases
		for (int i=0;i<r;i++) {
			network.setupPhase(i); // για τον καθορισμένο αριθμό γύρων (r) εκτελείται η διαδικασία SetupPhase η οποία στο τέλος εκτυπώνει και αποθηκεύει σε αρχείο (csv) τα αποτελέσματα κάθε γύρου. 
		}
		 network.writeToFile();   // Εγγραφή των μηνυμάτων σε αρχείο κειμένου (csv)
	}

}
