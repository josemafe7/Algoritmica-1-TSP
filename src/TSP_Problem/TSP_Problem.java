/*EPD04-P
 */
package TSP_Problem;

import edi.io.IO;
import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

/**
 *
 * @author Daniel Barciela Rueda
 * @author José Manuel Fernández Labrador
 */

public class TSP_Problem {
    
    public static void main(String[] args) throws Exception{
       menu();

    } 
    
   public static double[][] cargarMatrizFichero(String ruta) throws Exception {
        Scanner entrada = null;
        String linea;
        double[][] matrizCoordenadas = null;

        try {
            File f = new File(ruta); //escribir ruta para que funcione
            //creamos un Scanner para leer el fichero
            entrada = new Scanner(f);
            //mostramos el nombre del fichero
            System.out.println("Archivo: " + f.getName());
            int dimension = 0;

            while (entrada.hasNext()) { //mientras no se llegue al final del fichero
                linea = entrada.nextLine();  //se lee una linea
                if (linea.contains("DIMENSION")) {   //si la linea contiene el texto buscado se muestra por pantalla
                    Scanner p = new Scanner(linea);
                    while (!p.hasNextInt()) {
                        p.next();
                    }
                    dimension = p.nextInt();

                }
                if (linea.contains("NODE_COORD_SECTION")) {   //si la linea contiene el texto buscado se muestra por pantalla
                    double[][] m = new double[dimension][2];
                    for (int i = 0; i < dimension; i++) {
                        int fila = entrada.nextInt();
                        double x = Double.parseDouble(entrada.next());
                        double y = Double.parseDouble(entrada.next());

                        m[fila - 1][0] = x;
                        m[fila - 1][1] = y;
                    }
                    matrizCoordenadas = m;
                }

            }

        } catch (IOException e) {
            System.out.println(e.toString());
        } finally {
            if (entrada != null) {
                entrada.close();
            }
        }
        return matrizCoordenadas;
    }

    public static void imprimirRuta(int[] v) throws Exception {
        System.out.println("Imprimimos la ruta calculada:");
        for (int i = 0; i < v.length; i++) {
            if (i == v.length - 1) {
                System.out.println((v[i] + 1));
            } else {
                System.out.print((v[i] + 1) + " - ");  //+1 Simplemente para que muestre ciudades empezando por el 1
            }
        }
        System.out.println("\n");
    }

    public static void escribeRutaFichero(int[] v) throws Exception {
        System.out.println("Introduzca el nombre del archivo que contendrÃ¡ la ruta mÃ­nima calculada: ");
        String nombre = IO.readLine();
        FileOutputStream os = new FileOutputStream(nombre);
        PrintStream ps = new PrintStream(os);
        for (int i = 0; i < v.length; i++) {
            if (i == v.length - 1) {
                ps.println((v[i] + 1));
            } else {
                ps.print((v[i] + 1) + " - ");  //+1 Simplemente para que muestre ciudades empezando por el 1
            }

        }

    }

    public static double calculaDistancia(double x1, double y1, double x2, double y2) throws Exception {

        return Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));

    }

    public static double[][] calculaMatrizDistancia(double[][] mo) throws Exception {
        double[][] md = new double[mo.length][mo.length];
        for (int i = 0; i < mo.length; i++) {

            for (int j = i; j < mo.length; j++) {
                md[i][j] = calculaDistancia(mo[i][0], mo[i][1], mo[j][0], mo[j][1]);

            }
        }
        return md;

    }
    
    public static int[] fuerzaBruta(double[][] m) throws Exception {
        double disTotal = 0;
        int nCiudades = m.length;
        int[] indicesCiudades = new int[nCiudades];
        for (int i = 0; i < nCiudades; i++) {
            indicesCiudades[i] = i;
        }

        double minimo = 0;
        int[] rutaMinima = new int[nCiudades];

        //RECORRIDO PRIMERA PERMUTACION
        disTotal = distanciaTotalVector(indicesCiudades, m);
        minimo = disTotal;
        rutaMinima = indicesCiudades.clone();

        //RECORRIDO TODAS PERMUTACIONES
        while (nextPermutation(indicesCiudades) && indicesCiudades[0] == 0) {

            disTotal = 0;

            //RECORREMOS MATRIZ DISTANCIA CON LA PERMUTACION
            disTotal = distanciaTotalVector(indicesCiudades, m);

            //GUARDAMOS SIEMPRE LA RUTA MAS CORTA
            if (disTotal < minimo) {
                rutaMinima = indicesCiudades.clone();

                minimo = disTotal;
            }

        }

        return rutaMinima;
    }

    public static boolean nextPermutation(int[] array) throws Exception {

        int i = array.length - 1;
        while (i > 0 && array[i - 1] >= array[i]) {
            i--;
        }

        if (i <= 0) {
            return false;
        }

        int j = array.length - 1;
        while (array[j] <= array[i - 1]) {
            j--;
        }

        int temp = array[i - 1];
        array[i - 1] = array[j];
        array[j] = temp;

        j = array.length - 1;
        while (i < j) {
            temp = array[i];
            array[i] = array[j];
            array[j] = temp;
            i++;
            j--;
        }
        return true;
    }

    public static void runExperimentFuerzaBruta(String ruta, int ntimes) throws Exception {
        double[][] matrizCoordenadas = cargarMatrizFichero(ruta);
        double[][] matrizDistancia = calculaMatrizDistancia(matrizCoordenadas);

        int[] v = null;

        long start = System.currentTimeMillis();
        for (int i = 0; i < ntimes; i++) {
            v = fuerzaBruta(matrizDistancia);
        }
        long stop = System.currentTimeMillis();

        long tiempo = (stop - start) / ntimes;

        System.out.println("\nTiempo de ejecucion FUERZA BRUTA: " + tiempo + " (ms)");
        System.out.println("\nDistancias totales FUERZA BRUTA: " + distanciaTotalVector(v, matrizDistancia) + " \n");
        System.out.println("RUTA POR FUERZA BRUTA:");
        imprimirRuta(v);
    }
    
    public static int[] busquedaAleatoriaActualizando(double[][] m, int parada) throws Exception {
        int[] rutaMinima = new int[m.length];
        int[] indicesCiudades = new int[m.length];
        for (int i = 0; i < m.length; i++) {
            indicesCiudades[i] = i;
        }

        int[] rutaAleatoria = generaRutaAleatoria(indicesCiudades);
        double disTotal = distanciaTotalVector(rutaAleatoria, m);
        rutaMinima = rutaAleatoria.clone();

        for (int j = 0; j < parada; j++) {
            rutaAleatoria = generaRutaAleatoria(indicesCiudades);
            if (disTotal > distanciaTotalVector(rutaAleatoria, m)) {
                disTotal = distanciaTotalVector(rutaAleatoria, m);
                rutaMinima = rutaAleatoria.clone();
            }
        }

        return rutaMinima;

    }
    
    public static int[] busquedaAleatoriaSinActualizar(double[][] m, int parada) throws Exception {
        int[] rutaMinima = new int[m.length];
        int[] indicesCiudades = new int[m.length];
        for (int i = 0; i < m.length; i++) {
            indicesCiudades[i] = i;
        }

        int[] rutaAleatoria = generaRutaAleatoria(indicesCiudades);
        double disTotal = distanciaTotalVector(rutaAleatoria, m);
        rutaMinima = rutaAleatoria.clone();
        int contador = 0;

        while (contador < parada) {
            rutaAleatoria = generaRutaAleatoria(indicesCiudades);
            if (disTotal > distanciaTotalVector(rutaAleatoria, m)) {
                disTotal = distanciaTotalVector(rutaAleatoria, m);
                rutaMinima = rutaAleatoria.clone();
                contador = 0;
            } else {
                contador++;
            }

        }

        return rutaMinima;

    }
    
    public static int[] generaRutaAleatoria(int[] v) throws Exception {
        int[] k = new int[v.length];
        k = v.clone();
        int num = k.length;
        int[] seleccion = new int[v.length];
        int tam = k.length;
        int c = 0;
        for (int i = 0; i < num; i++) {
            int aleatorio = (int) Math.floor(Math.random() * (tam));
            seleccion[c] = k[aleatorio];
            c++;
            tam = eliminar(k, k[aleatorio], tam);

        }
        return seleccion;
    }

    public static int eliminar(int[] v, int elemento, int tam) throws Exception {
        for (int i = 0; i < tam; i++) {
            if (v[i] == elemento) {
                for (int j = i; j < tam - 1; j++) {
                    v[j] = v[j + 1];
                }
                v[tam - 1] = 0;
            }
        }
        return tam - 1;
    }

    public static double distanciaTotalVector(int[] v, double[][] m) throws Exception {
        double res = 0;
        for (int i = 0; i < v.length; i++) { //Si se pone -1 no calcularia la vuelta a la primera ciudad, en este caso la calculo
            if (i == v.length - 1) // Esto es porque queremos calcular la distancia del ultimo al primero.
            {
                res = res + m[v[0]][v[i]];
            } else if (v[i] < v[i + 1]) {
                res = res + m[v[i]][v[i + 1]];
            } else if (v[i] > v[i + 1]) {
                res = res + m[v[i + 1]][v[i]];
            }
        }

        return res;
    }
    
    
    
    public static void runExperimentBusquedaAleatoriaActualizando(String ruta, int[] n, int[] ntimes) throws Exception {
        double[][] matrizCoordenadas = cargarMatrizFichero(ruta);
        double[][] matrizDistancia = calculaMatrizDistancia(matrizCoordenadas);

        double[] tIt = new double[n.length];
        double[] dIt = new double[n.length];
        int[] v = null;

        for (int i = 0; i < n.length; i++) {
            int d = n[i];

            long start = System.currentTimeMillis();
            for (int j = 0; j < ntimes[i]; j++) {
                v = busquedaAleatoriaActualizando(matrizDistancia, d);
            }
            long stop = System.currentTimeMillis();

            tIt[i] = ((double) (stop - start)) / ((double) ntimes[i]);
            dIt[i] = distanciaTotalVector(v, matrizDistancia);
        }

        System.out.println("\nTiempo de ejecucion BUSQUEDA ALEATORIA ACTUALIZANDO: " + Arrays.toString(tIt) + " (ms) ");
        System.out.println("\nDistancias totales BUSQUEDA ALEATORIA ACTUALIZANDO: " + Arrays.toString(dIt) + " \n");
        System.out.println("RUTA POR BUSQUEDA ALEATORIA ACTUALIZANDO:");
        imprimirRuta(v);
    }

    public static void runExperimentBusquedaAleatoriaSinActualizar(String ruta, int[] n, int[] ntimes) throws Exception {
        double[][] matrizCoordenadas = cargarMatrizFichero(ruta);
        double[][] matrizDistancia = calculaMatrizDistancia(matrizCoordenadas);

        double[] tIt = new double[n.length];
        double[] dIt = new double[n.length];
        int[] v = null;

        for (int i = 0; i < n.length; i++) {
            int d = n[i];

            long start = System.currentTimeMillis();
            for (int j = 0; j < ntimes[i]; j++) {
                v = busquedaAleatoriaSinActualizar(matrizDistancia, d);
            }
            long stop = System.currentTimeMillis();

            tIt[i] = ((double) (stop - start)) / ((double) ntimes[i]);
            dIt[i] = distanciaTotalVector(v, matrizDistancia);
        }

        System.out.println("\nTiempo de ejecucion BUSQUEDA ALEATORIA SIN ACTUALIZAR: " + Arrays.toString(tIt) + " (ms) ");
        System.out.println("\nDistancias totales BUSQUEDA ALEATORIA SIN ACTUALIZAR: " + Arrays.toString(dIt) + " \n");
        System.out.println("RUTA POR BUSQUEDA ALEATORIA SIN ACTUALIZAR:");
        imprimirRuta(v);

    }
    
    public static void menu() throws Exception {
        int i = 0;
        String[] ruta = {"data/berlin52.tsp", "data/kroA100.tsp", "data/kroA150.tsp", "data/kroA200.tsp", "data/a280.tsp", "data/vm1084.tsp", "data/vm1748.tsp", "data/usa13509.tsp"};
        do {
            System.out.println("¿Cómo desea resolver el problema TSP?:\n"
                    + "\t1.- Por Fuerza Bruta \n"
                    + "\t2.- Por Busqueda Aleatoria Actualizando\n"
                    + "\t3.- Por Busqueda Aleatoria Sin Actualizar\n");
            i = (int) IO.readNumber();
            System.out.println("\n");
        } while (i < 1 || i > 3);

        if (i == 1) {
            for (int k = 0; k < ruta.length; k++) {
                runExperimentFuerzaBruta(ruta[k], 10);
            }
        } else if (i == 2) {
            for (int k = 0; k < ruta.length; k++) {
                runExperimentBusquedaAleatoriaActualizando(ruta[k], new int[]{10, 20, 30, 50, 60}, new int[]{60, 50, 30, 20, 10});
            }
        } else if (i == 3) {
            for (int k = 0; k < ruta.length; k++) {
                runExperimentBusquedaAleatoriaSinActualizar(ruta[k], new int[]{10, 20, 30, 50, 60}, new int[]{60, 50, 30, 20, 10});
            }
        } 
    }
}
