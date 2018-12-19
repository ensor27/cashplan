package PDF;

import java.io.BufferedReader;
import java.io.File ;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.itextpdf.io.util.DecimalFormatUtil;
import com.itextpdf.layout.border.Border;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import PDF.Parameters;

import com.itextpdf.text.Section;
import com.itextpdf.text.Font.FontFamily;

public class cashPlan {
static Connection connection= WB.Connection2DB.dbConnector();
	
	private static Font catFont ;
	private static Font smallFont;
	private static Font smallFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 9);
	private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
	

	public static void createRaport() throws SQLException, FileNotFoundException, DocumentException{

		FontFactory.register(cashPlan.class.getClassLoader().getResource("times.ttf").toString(), "times");
		catFont = FontFactory.getFont("times", BaseFont.CP1250, BaseFont.EMBEDDED, 18);
		smallFont = FontFactory.getFont("times", BaseFont.CP1250, BaseFont.EMBEDDED, 12);
//		Document doc = new Document();
		
		// configure and get actual date
//		SimpleDateFormat doNazwy = new SimpleDateFormat("yyyy.MM.dd");
//		SimpleDateFormat godz = new SimpleDateFormat("HH;mm");
//		SimpleDateFormat nameofmonth = new SimpleDateFormat("MMMM");
//		SimpleDateFormat nameofyear = new SimpleDateFormat("yyyy");
//		Calendar calendar = Calendar.getInstance();
		
//		String path = Parameters.getPathToSaveHours()+"/"+doNazwy.format(calendar.getTime())+"/";
//		int year       = calendar.get(Calendar.YEAR);
//		int month      = calendar.get(Calendar.MONTH);
//		int day			= calendar.get(Calendar.DAY_OF_MONTH);


//		SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");

//		Parameters.createDirectory();
//    	File f = Parameters.createFile("jpk_MAG.xml");	
		
//		//create the directory for dump file
//			File theDir = new File(path);
//			if (!theDir.exists()) {
//			    try{
//			        theDir.mkdir();
//			    } 
//			    catch(SecurityException se){
//			        //handle it
//			    }
//			}
//		//end creation directory
		
		int numberOfLines = 0;	
		String sql10 = "SELECT count(firma) FROM betaalconditie WHERE CFBOEKINGSNUMMER IS null";
		Statement st10 = connection.createStatement();
		ResultSet rs10 = st10.executeQuery(sql10);
		while(rs10.next()){
			numberOfLines = rs10.getInt(1);
		}
		
		String [] header = new String [12];
		String [][] data = new String [12][numberOfLines];
		String [][] naglowek = new String[12][1];
		
		header[0] = "clientNr/Order";
		header[1] = "client";
		header[2] = "serial No";
		header[3] = "orderdate";
		header[4] = "contract delivery date";
		header[5] = "data konca montazu";
		header[6] = "salesprice";
		header[7] = "paymentdate";
		header[8] = "%";
		header[9] = "amount";
		header[10] = "valuta";
		header[11] = "way of payment";
			
		String sql20 = "SELECT KLANTNR , BESTELLINGNR , DATUMBETALING, PERCENTAGE , BEDRAG, CFMUNT , CFOMSBETVW FROM betaalconditie WHERE CFBOEKINGSNUMMER IS null order by DATUMBETALING";
		Statement st20 = connection.createStatement();
		ResultSet rs20 = st20.executeQuery(sql20);
		int line = 0;
		while(rs20.next()){
			String levernaam = "";
			String serienummer = "";
			String verkoopprijs = "";
			String contractDate = "";
			String orderdate = "";
			String dataKoniecMontazu = "";
			
			String sql30 = "select LEVERNAAM, SERIENUMMER, VERKOOPPRIJS, LEVERDATUM_BEVESTIGD, BESTELDATUM from verkoop where KLANTNR = '" + rs20.getString("KLANTNR")+ "' and BESTELLINGNR = '" + rs20.getString("BESTELLINGNR") + "'";
			Statement st30 = connection.createStatement();
			ResultSet rs30 = st30.executeQuery(sql30);
			while(rs30.next()){
				levernaam 		= rs30.getString("LEVERNAAM");
				serienummer 	= rs30.getString("SERIENUMMER");
				verkoopprijs 	= rs30.getString("VERKOOPPRIJS");
				contractDate	= rs30.getString("LEVERDATUM_BEVESTIGD");
				orderdate		= rs30.getString("BESTELDATUM");
				
			}
			rs30.close();
			st30.close();
			
			String sql40 = "select NrMaszyny , DataKoniecMontazu from calendar where NrMaszyny like '%"+ serienummer +"'";
			Statement st40 = connection.createStatement();
			ResultSet rs40 = st40.executeQuery(sql40);
			 
			Boolean project2 = false;
			Boolean project6 = false;
			
			while(rs40.next()){
				 if (rs40.getString("NrMaszyny").substring(0, 1).equals("0") && !project6 && !project2){
					 dataKoniecMontazu = rs40.getString("DataKoniecMontazu");
					 
				 }
				 if (rs40.getString("NrMaszyny").substring(0, 1).equals("6")   && !project2){
					 dataKoniecMontazu = rs40.getString("DataKoniecMontazu");
					 project6 = true;
				 }
				 if (rs40.getString("NrMaszyny").substring(0, 1).equals("2")){
					 dataKoniecMontazu = rs40.getString("DataKoniecMontazu");
					 project2 = true;
				 }
			}
			rs40.close();
			st40.close();
			
//			header[0] = "clientNr/Order";
//			header[1] = "client";
//			header[2] = "serial No";
//			header[3] = "orderdate";
//			header[4] = "contract delivery date";
//			header[5] = "data konca montazu";
//			header[6] = "salesprice";
//			header[7] = "paymentdate";
//			header[8] = "%";
//			header[9] = "amount";
//			header[10] = "valuta";
//			header[11] = "way of payment";
			
			
			data [0][line] = rs20.getString("KLANTNR") + "/" + rs20.getString("BESTELLINGNR"); //klantnr + bestelling
			data [1][line] = levernaam;
			data [2][line] = serienummer;
			data [3][line] = orderdate;
			data [4][line] = contractDate;
			data [5][line]=  dataKoniecMontazu;
			data [6][line] = verkoopprijs;
			data [7][line] = rs20.getString("DATUMBETALING") ;		//	datum for payment			
			data [8][line] = rs20.getString("PERCENTAGE") ;			//  % part of payment
			data [9][line] = rs20.getString("BEDRAG") ;				//  real amount of money
			data [10][line] = rs20.getString("CFMUNT") ;				// valuta
			data [11][line] = rs20.getString("CFOMSBETVW") ;			// way of payment
			line++;
		}
       	rs20.close();
       	st20.close();
       	
       	CSVFileWriter.nowyPlik("cashplan ", ";", header, numberOfLines, data, naglowek);

        
     
}}
	
	
	
