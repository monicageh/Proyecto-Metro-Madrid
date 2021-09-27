package es.upm.dit.adsw.practica1;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * Clase para representar contenidos de los ficheros de viales que distribuye
 * el ayuntamiento de Madrid
 * 
 * @author mmiguel
 *
 */
public class Callejero {

	private static final int COD_VIA=0;
	private static final int VIA_CLASE=1;
	private static final int VIA_PAR=2;
	private static final int VIA_NOMBRE=3;
	private static final int VIA_NOMBRE_ACENTOS=4;
	private static final int COD_VIA_COMIENZA=5;
	private static final int CLASE_COMIENZA=6;
	private static final int PARTICULA_COMIENZA=7;
	private static final int NOMBRE_COMIENZA=8;
	private static final int NOMBRE_ACENTOS_COMIENZA=9;
	private static final int COD_VIA_TERMINA=10;
	private static final int CLASE_TERMINA=11;
	private static final int PARTICULA_TERMINA=12;
	private static final int NOMBRE_TERMINA=13;
	private static final int NOMBRE_ACENTOS_TERMINA=14;
	protected static final String fichero="VialesVigentes_20201220.csv";
	protected Via[] vias;

	/**
	 * Constructor de callejero a partir de algun tipo de stream que 
	 * incluye las vias del callejero. Ese stream esta soportado con un Scanner. 
	 * El scanner incluye el contenido del callejero y el constructor lee el stream 
	 * que debe estar en formato csv
	 * 
	 * @param viales scanner del que extraemos el contenido del callejero
	 * @param numViales numero de viales que incluye el scanner
	 */
	public Callejero(Scanner viales,int numViales) {
		vias=new Via[numViales];
		String[] vias_csv;
		for (int i=0; i < numViales; i++) {
			String linea=viales.nextLine();
			vias_csv=linea.split(";");
			vias[i]=new Via(Integer.parseInt(vias_csv[COD_VIA]),vias_csv[VIA_CLASE],vias_csv[VIA_PAR],
					vias_csv[VIA_NOMBRE],vias_csv[VIA_NOMBRE_ACENTOS],
					Integer.parseInt(vias_csv[COD_VIA_COMIENZA]),Integer.parseInt(vias_csv[COD_VIA_TERMINA]));
			if ((i == numViales-1 && viales.hasNext()) || (i < numViales-1 && !viales.hasNext()))
				throw new RuntimeException("Formato fichero errorneo");
		}
		viales.close();
		ordenaVias();
		Via viaComienzo = null;
		Via viaTermina = null;
	}
	
	/**
	 * Metodo que ordena las vias en función del código de via.
	 * Debe ser utilizado unicamente para hacer pruebas
	 */

	public void ordenaVias() {
		List <Via> listVias = new ArrayList<>();
		Collections.addAll(listVias, vias);
		ordenaVias(listVias);
		listVias.toArray(vias);
	}
	
	private void ordenaVias(List<Via> listVias) {
		//dividir
		if (listVias.size() < 2)
			return;
		int m = listVias.size() / 2;
		List <Via> izq = new ArrayList<>(listVias.subList(0, m));
		List <Via> der = new ArrayList<>(listVias.subList(m, listVias.size()));
		//ordenar las dos listas
		ordenaVias(izq);
		ordenaVias(der);
		//mezclar las dos listas (izq y der)
		listVias.clear();
		while (izq.size() > 0 && der.size() > 0) {
			Via si = izq.get(0);
			Via sd = der.get(0);
			if(si.getCodigo() < sd.getCodigo())
				listVias.add(izq.remove(0));
			else
				listVias.add(der.remove(0));
		}
		while (izq.size() > 0)
			listVias.add(izq.remove(0));
		while (der.size() > 0)
			listVias.add(der.remove(0));
	}
	
	/**
	 * Imprime en salida estandar todos los viales del callejero
	 */
	public void printViales() {
		for (Via via : vias) {
			via.formatPrint();
			System.out.println();
		}
	}
	
	/**
	 * Devuelve las vias del callejero
	 * @return vias del callejero
	 */
	public Via[] getVias() {
		return vias;
	}
	
	/**
	 * Fija las vias del callejero. Debe estar completamente inicializada
	 * @param vias nuevas vias del callejero
	 */
	public void setVias(Via[] vias) {
		this.vias = vias;
	}
	
	/**
	 * Devuelve el cojunto de vias del callejero ordenadas por nombre
	 * 
	 * @return conjunto de vias ordenadas por nombre 
	 */
	public Via[] ordenaViasPorNombre() {
		bottomUpOrdenaViasPorNombre(vias, new Via [vias.length]);
		return vias;
	}
	
	private void bottomUpOrdenaViasPorNombre(Via[] vias, Via[] viaAux) {
		int n = vias.length;
		for(int w = 1; w < n; w *= 2) {
			for(int i = 0; i < n; i += 2*w){
				int iIzq = i;
				int iDer = Math.min(i + w, n);
				int iFin = Math.min(i + 2*w, n);
				bottomUpMerge(vias, iIzq, iDer, iFin, viaAux);
			}
			System.arraycopy(viaAux, 0, vias, 0, n);
		}
	}

	private void bottomUpMerge(Via[] vias, int iIzq, int iDer, int iFin, Via[] viaAux) {
		int i0 = iIzq;
		int i1 = iDer;
		int dst = iIzq;
		while(i0 < iDer && i1 < iFin) {
			if(vias[i0].getNombre().compareTo(vias[i1].getNombre()) <= 0)
				viaAux[dst++] = vias[i0++];
			else
				viaAux[dst++] = vias[i1++];
		}
		while (i0 < iDer)
			viaAux[dst++] = vias[i0++];
		while (i1 < iFin)
			viaAux[dst++] = vias[i1++];
	}

	public static void main(String[] args) {
		try {
			FileInputStream fi=new FileInputStream(fichero);
			Scanner viales=new Scanner(fi);
			int lineas=0;
			while (viales.hasNext()) {
				lineas++;
				viales.nextLine();
			}
			viales.close();
			fi=new FileInputStream(fichero);
			viales=new Scanner(fi);
			viales.nextLine(); // nos saltamos las cabeceras del fichero
			Callejero c=new Callejero(viales,lineas-1);
			c.printViales();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
