package ge2thema4;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

public class WSNetwork {  // Wireless Sensor Network
	private double N, p, threshold, randomNum;  // μεταβλητές Ν=αριθμός κόμβων, p=επιθυμητό ποσοστό επικεφαλής κόμβων, threshold για κάθε γύρο, 
												// randomNum = τυχαίος αριθμός που αντιστοιχίζεται σε κάθε κόμβο κατα τη φάση εγκατάστασης
	private int chc,pch;   // μετρητές για την ένδειξη του αριθμού των επικεφαλής κόμβων σε κάθε γύρο (chc = cluster head counter) και των κόμβων που θα μπορούσαν να είναι επικεφαλής 
						   // αλλά δεν επιτρέπεται διότι ήταν ήδη επικεφαλής κατα τους 1/p προηγούμενους γύρους. (pch = potential cluster head) 
	
	private ArrayList<WSNode> nodes = new ArrayList<>();  // δομή για την αποθήκευση των κόμβων του δικτύου.
	
	DecimalFormat df = new DecimalFormat("#.###");  // χρησιμοποιώ την κλάση DecimalFormat για να τυπώσω τιμές double με 3 δεκαδικά ψηφία.
	ArrayList<String> reportLines = new ArrayList<>(); // δημιουργώ λίστα με όλα τα μηνύματα για κάθε κόμβο και για όλους τους γύρους, για αποθηκευση σε αρχείο κειμένου.
	
	private String s;  // μεταβλητή string για την εμφάνιση και αποθήκευση των μηνυμάτων
	
	
	public WSNetwork(double N, double p, int r) {  // κατασκευαστής της κλάσης που δημιουργεί όλους τους κόμβους (Ν) και αρχικοποιεί τις τιμές τους, τοποθετώντας τους στη δομή ArrayList.
		this.p = p;
		this.N = N;
		for (int i=0;i<N;i++) {
			WSNode komvos = new WSNode();
			nodes.add(komvos);
		}
	}

	public double thresholdCalc(int r) {   // μέθοδος που υπολογίζει την τιμή threshold σύμφωνα με τον τύπο του αλγόριθμου
		threshold= p / (1-(p*(r%(int) (1/p))));
		return threshold;
	}
	
	public void setupPhase(int r) {  // κλάση που εξομοιώνει την φάση εγκατάστασης του δικτύου 
		// αρχή της φάσης εγκατάστασης
		chc=0;   //  μηδενίζεται ο μετρητής 
		pch=0;	//   μηδενίζεται ο μετρητής
		
		for (int i=0;i<nodes.size();i++) {  // διατρέχουμε όλους τους κόμβους του δικτύου 
			
			if (nodes.get(i).getRoundsCounter()>0)     // Αν ο μετρητής των γύρων που δεν μπορεί ένας κόμβος να γίνει επικεφαλής δεν είναι μηδενικός τότε μειώνεται κατά ένα
				nodes.get(i).setRoundsCounter(nodes.get(i).getRoundsCounter()-1);
			
			if (nodes.get(i).isCH()) {      // αν ο κόμβος ήταν (από τον προηγούμενο γύρο) επικεφαλής κόμβος
				nodes.get(i).setCH(false);   // βγαίνει από επικεφαλής κόμβος
				nodes.get(i).setOldCH(true);  // γίνεται κόμβος που ήταν επικεφαλής σε κάποιο από τους 1/p γύρους
				nodes.get(i).setRoundsCounter((int) (1/p));  // θέτουμε το μετρητή για τον αριθμό των γύρων που θα πρέπει να περάσουν μέχρι να μπορέσει ο κόμβος να γίνει και πάλι επικεφαλής. 
				nodes.get(i).setBelongsToG(false);  // αφαιρούμε τον κόμβο από το σύνολο Γ (κόμβοι που δεν υπήρξαν επικεφαλής τους προηγούμενους 1/p γύρους)
			}
				
			threshold=thresholdCalc(r);  // υπολογίζουμε το threshold του γύρου αυτού.
			randomNum=Math.random();  // υπολογίζουμε ένα τυχαίο αριθμό.
			nodes.get(i).setRandNum(randomNum);  // θέτουμε τον τυχαίο αριθμό στον κόμβο i
			
			if (nodes.get(i).getRoundsCounter()==0)	{  // αν ο μετρητής των γύρων που πρέπει να περάσουν μέχρι να μπορέσει ο κόμβος να γίνει και πάλι επικεφαλής είναι 0,
				nodes.get(i).setBelongsToG(true);  // τότε ο κόμβος μετακινείται στο σύνολο Γ
				nodes.get(i).setOldCH(false);   //  (reset)  ο κόμβος δεν ήταν επικεφαλής τους προηγούμενους 1/p γύρους 
			}
			
			if (nodes.get(i).belongsToG())   // αν ο κόμβος ανήκει στο σύνολο Γ 
				nodes.get(i).setThreshold(threshold);  // τότε του δίνουμε την τιμή του threshold
			else 
				nodes.get(i).setThreshold(0);    // διαφορετικά του δίνουμε την τιμή 0, (δηλ δεν μπορεί να γίνει επικεφαλής).
			
			if ((Double.compare(randomNum, threshold)<0) && (nodes.get(i).belongsToG())) {  // αν ο τυχαίος αριθμός είναι μικρότερος από το threshold ΚΑΙ ο κόμβος ανήκει στο σύνολο Γ τότε γίνεται επικεφαλής κόμβος.
				chc++;  // αυξάνω τον μετρητή των επικεφαλής κόμβων κατά ένα
				nodes.get(i).setCH(true);  // θέτω την μεταβλητή setCH σε true , δηλ ότι ο συγκεκριμένος κόμβος είναι επικεφαλής 
				nodes.get(i).setBelongsToG(false);  // αφαιρώ τον κόμβο από το σύνολο Γ (το σύνολο των κόμβων που δύνανται να επιλεγούν σαν επικεφαλής).
				nodes.get(i).setOldCH(false); // αφού ο κόμβος είναι σε αυτόν τον γύρο επικεφαλής , τότε δεν μπορεί να είναι σε αυτούς που ήταν επικεφαλής τους προηγούμενους 1/p γύρους.
			}
			if ((Double.compare(randomNum, threshold)<0) )  // αν ο τυχαίος αριθμός είναι μικρότερος του threshold τότε αυξάνουμε κατά ένα τον μετρητή των δυνητικών επικεφαλής κόμβων.
				pch++;
		}
	   reportRound(r);   // με το τέλος της φάσης εγκατάστασης εκτελείται η μέθοδος reportRound που τυπώνει τα αποτελέσματα του εκάστοτε γύρου.	
	  
	}

	private void reportRound(int r) {
		s="Round : "+r+"   "+"Threshold="+df.format(threshold);
		System.out.println(s);  // εκτύπωση του αποτελέσματος στην κονσόλα του συστήματος 
		reportLines.add(s);    // προσθήκη σε λίστα για να εγγραφούν ολα τα μηνύματα σαν report σε αρχείο κειμένου
		
		for (int i=0;i<nodes.size();i++) {
			if (nodes.get(i).isCH()) {  // αν ο κόμβος είναι επικεφαλής τότε τυπώνεται το αντίστοιχο μήνυμα.
				s="Node : N"+i+" with Random Number= "+df.format(nodes.get(i).getRandNum())+" and threshold : "+df.format(nodes.get(i).getThreshold())+"- is CLUSTER HEAD!!!";
				System.out.println(s);  // εκτύπωση μηνύματος στην κονσόλα του συστήματος
				reportLines.add(s);   // προσθήκη του μηνύματος σε λίστα για την εγγραφή σε αρχείο κειμένου.
			}
			if (nodes.get(i).isOldCH()) { // αν ο κόμβος υπήρξε επικεφαλής για 1/p γύρους τότε δεν ανήκει στο σύνολο Γ και εκτυπώνεται το αντίστοιχο μήνυμα.
				s="Node : N"+i+" doesn't belong to set G";
				System.out.println(s);   // εκτύπωση μηνύματος στην κονσόλα του συστήματος
				reportLines.add(s);     // προσθήκη του μηνύματος σε λίστα για την εγγραφή σε αρχείο κειμένου.
			}
			
			if ( (Double.compare(nodes.get(i).getThreshold(),nodes.get(i).getRandNum())<0) && (nodes.get(i).getThreshold()!=0) ) { // η μέθοδος Double.compare συγκρίνει δύο αριθμούς ακρίβειας double 
																						   // και επιστρέφει αρνητικό αριθμό όταν ο πρώτος όρος είναι μικρότερος από τον δεύτερο.
																						  // Αν το threshold είναι μικρότερο από τον τυχαίο αριθμό του κόμβου τότε ο κόμβος είναι απλός κόμβος 
			s="Node : N"+i+" with Random Number= "+df.format(nodes.get(i).getRandNum())+" and threshold : "+df.format(nodes.get(i).getThreshold())+"- is a Simple NODE!";
				System.out.println(s);   // εκτύπωση μηνύματος στην κονσόλα του συστήματος
				reportLines.add(s);	// προσθήκη του μηνύματος σε λίστα για την εγγραφή σε αρχείο κειμένου.
			}
			
			if (nodes.get(i).getThreshold()==0) {// αν το threshold είναι μηδέν τότε ο κόμβος δεν μπορεί να είναι επικεφαλής αν δεν περάσουν 1/p γύροι.
				s="Node : N"+i+" with Random Number= "+df.format(nodes.get(i).getRandNum())+" and threshold 0, must wait "+df.format(nodes.get(i).getRoundsCounter())+" rounds, and is a Simple NODE!";
				System.out.println(s);   // εκτύπωση μηνύματος στην κονσόλα του συστήματος
				reportLines.add(s); 	// προσθήκη του μηνύματος σε λίστα για την εγγραφή σε αρχείο κειμένου.
			}
			
			
		}
		s=chc+" Cluster Heads in this round, out of "+pch+" potential Cluster Heads.";
		System.out.println(s);  // τυπώνεται μήνυμα με τον αριθμό των κόμβων που έγιναν επικεφαλής στον γύρο αυτό. 
														//  Επίσης τυπώνεται μήνυμα με τον αριθμό των κόμβων που θα μποούσαν να είναι επικεφαλής αλλά αποκλείστηκαν διότι ήταν ήδη πριν από 1/p γύρους.
		reportLines.add(s);
		s="*End of round*";
	    System.out.println(s);  // Τέλος γύρου.
	    
	    reportLines.add(s);  	// προσθήκη του μηνύματος σε λίστα για την εγγραφή σε αρχείο κειμένου.

	}
	
	public void writeToFile() {
		try {
			FileOutputStream fileStream= new FileOutputStream(new File("report-LEACH.csv"));
			OutputStreamWriter writer=new OutputStreamWriter(fileStream, "UTF-8");; 
			BufferedWriter bwr = new BufferedWriter(writer);
			
			for (int i=0;i<reportLines.size();i++) {
				bwr.write(reportLines.get(i));
				bwr.newLine();
			}
			bwr.close();
		} 
		
		catch (IOException e) {
			e.printStackTrace();
		}; 
			
	}
}
