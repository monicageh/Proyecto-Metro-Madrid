package es.upm.dit.adsw.practica1;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

import org.junit.Before;

public class PruebaCallejero {

	private Callejero c1;
	private Via[] vias;
	
	@Before
	public void SetUp () {
		try {
			FileInputStream fi=new FileInputStream("VialesVigentes_20201220.csv");
			Scanner viales=new Scanner(fi);
			int lineas=0;
			while (viales.hasNext()) {
			    lineas++;
			    viales.nextLine();
			}
			viales.close();
			fi=new FileInputStream("VialesVigentes_20201220.csv");
			viales=new Scanner(fi);
			viales.nextLine(); 
			Callejero c=new Callejero(viales,lineas-1);
			c.printViales();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
