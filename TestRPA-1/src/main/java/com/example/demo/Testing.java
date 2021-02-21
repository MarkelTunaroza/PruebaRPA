/*Autor: Marcelo Tunarosa*/
/*Para: Prueba Ingeniero RPA*/
/*LinkdIn: @Marcelo Tunarosa*/

package com.example.demo;

//Librerias
//Selenum
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;

//Spring
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

//Own
import com.example.demo.interfaceService.IproductoService;
import com.example.demo.modelo.Producto;

//util
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

//Excel
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

//io
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.io.IOException;
import java.io.File;

//Apache Excel
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;

@RestController
public class Testing<E> {

	///////////////////////////////////////////////////////////////////////////////
	// Declaración ed variables globales
	///////////////////////////////////////////////////////////////////////////////

	@Autowired
	private IproductoService service; // Invoca servicio de la base de datos Crud

	// Se crea el objeto para establecer la relacion con el archivo de Excel
	public static XSSFWorkbook workbook = new XSSFWorkbook();

	// Variables de tipo String
	private static String hoja = "Hoja1"; // Hoja de Excel que se va a leer
	private static String nombreExcelLP; // Nombre del Archivo Excel con la lista de Productos
	private static String directorioExcelLP; // Se almacena las ruta donde se encuentra el archivo de Excel
	public static String elemental;
	static String NombreProducto; // Se almacena las ruta donde se encuentra el archivo de Excel

	// Variable tipo Array de almacenamiento de datos
	private static ArrayList<String> nombreProductos = new ArrayList<String>();
	private static ArrayList<Integer> filas = new ArrayList<Integer>();
	private static ArrayList<String> precioML = new ArrayList<String>();
	private static ArrayList<String> PrecioAZ = new ArrayList<String>();

	// Variables de Tipo Int
	public static int contadorExcel = 0;

	// Variables de Tipo Boolean
	public static boolean Existe;
	public static boolean continua;
	public static boolean writeBD = false;
	public static boolean cambioIdioma;
	
	
	///////////////////////////////////////////////////////////////////////////////
	// Clase Principal de servicio donde se ejecuta todo /ObtenerPrecios
	///////////////////////////////////////////////////////////////////////////////

	@GetMapping("/ObtenerPrecios") // Mapeo para invocar el servicio
	public void testingGoogle() throws IOException, InterruptedException {
		
		//variables locales de control para el while
		continua = true; //al cambiar a false sale del while una vez se termine de ejecutar y vuelva a la condición inicial
		cambioIdioma = false;//Control de la variable y proceso de cambio deidioma y region en la pñagina de Amazon


		//Inicia el ciclo para obtener los precios de Mercado Liobre y Amazon de Manera Secuencial
		while (continua == true) {

			System.out.print("-------------" + "\n");
			System.out.print("Inicio Ejecucion" + "\n");
			System.out.print("-------------" + "\n");

			DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
			Date date = new Date();
			System.out.println("-->Inicio Hora de ejecución: " + dateFormat.format(date));

			// Lee la carpeta donde se encuentra el directorio de trabajo y lo ubica
			String directorioRaiz = System.getProperty("user.dir"); // Se llama la propiedad para extraer la ruta donde
																	// se ejecuta el proyecto
			System.out.println("-->Directorio de trabajo = " + directorioRaiz);

			// Se ubica la carpeta donde se encuentra el archivo Excel ruta:
			// \scr\PruebaRPA\Productos
			String directorioListaProductos = directorioRaiz + "\\" + "src\\PruebaRPA\\Productos";

			// Se invoca la clase para localizar el Excel de Lista De Productos, leer los
			// daots y guardarlos en el array
			archivos(directorioListaProductos);
			directorioExcelLP = directorioListaProductos + "\\" + nombreExcelLP; // Se crea la ruta para el hallar el
																					// directorio del Excel
			System.out.println("Directorio de Ubicación de Hoja Excel: Lista_Productos.xlsx = " + directorioExcelLP);

			System.out.print("-------------" + "\n");
			System.out.print("Leer Excel" + "\n");
			System.out.print("-------------" + "\n");

			// Se llama la clase para leer los datos del Excel y almacenarlos
			leerExcel();

			// Obtener informacion del Array con los datos extraidos del Array
			int tamanioAP = nombreProductos.size(); // Se extrae el tamaño total del Array con los productos leidos
			System.out.println("Tamanio Array Productos extraidos del Excel: " + tamanioAP); //

			System.out.print("-------------" + "\n");
			System.out.print("Consulta Precios Mercado Libre" + "\n");
			System.out.print("-------------" + "\n");

			// Clase para consultar los precios con el Driver Selenium Para Mercado Libre,
			// donde se guardan los precios de manera ordenada
			consultarPrecioML(nombreProductos);

			System.out.print("-------------" + "\n");
			System.out.print("Consulta Precios Amazon" + "\n");
			System.out.print("-------------" + "\n");

			// Clase para consultar los precios con el Driver Selenium Para Mercado Libre,
			// donde se guardan los precios de manera ordenada
			consultarPrecioAZ(nombreProductos);

			// Vector de Productos
			Producto VectorProductos[] = new Producto[tamanioAP];
			for (int cont = 0; cont < tamanioAP; cont++) {
				VectorProductos[cont] = new Producto();
			}

			System.out.print("-------------" + "\n");
			System.out.print("Registro de Precios en Excel y Base de Datos MySql" + "\n");
			System.out.print("-------------" + "\n");

			// Se recorre los Arrays con los precios ya guardados para ser escritos en Excel
			// de manera ordenada
			for (int i = 0; i < tamanioAP; i++) {

				String PrecioMLExcel = precioML.get(i); // Variable para guardar de manera temporal el Precio del
														// Producto consultado en Mercado libre
				
				String PrecioAZExcel = PrecioAZ.get(i); // Variable para guardar de manera temporal el Precio del
														// Producto consultado en Amazon
				
				String NombreProductoBD = nombreProductos.get(i); 	// Variable para guardar de manera temporal el Precio
																	// del Producto consultado en Amazon
				
				int filaExcel = filas.get(i); // Se establece el valor de la fila de Excel

				// Clase para escribir el valor de los producto consultados en Mercado Libre y
				// Amazon en la fila correspondiente para cada producto en Excel
				escribirExcel(filaExcel, PrecioMLExcel, PrecioAZExcel);

				// Se recorren los objetos Productos y se les asigna los valores
				// para guardarlos en la base de datos
				VectorProductos[i].setNameproduct(NombreProductoBD); // nombre del producto
				VectorProductos[i].setPrecioamazon(PrecioAZExcel);  // precio Pagina Amazon
				VectorProductos[i].setPreciomercadolibre(PrecioMLExcel); // Precio página Mercado Libre
			}

			Thread.sleep(1000); // Tiempo de esperar para no saturar el servicio de Base de datos
			
			//Con los Objetos Producto ya seteteados con sus datos procedemos a insertarlos en la base de datos
			for (int j = 0; j < tamanioAP; j++) {
				Thread.sleep(200); // para que cada 60 segundos se ejecute
				addProducto(VectorProductos[j]);
				Thread.sleep(200); // para que cada 60 segundos se ejecute
			}

			System.out.print("-------------" + "\n");
			System.out.print("Tiempo Fin Y Tiempo Proxima Ejecucion" + "\n");
			System.out.print("-------------" + "\n");

			Calendar fecha = Calendar.getInstance();
			System.out.println("Fecha y hora de ejecución terminada: "
					+ String.format("%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS", fecha));

			fecha.add(Calendar.SECOND, 60);
			System.out.println("Fecha y hora de la proxima ejecución: "
					+ String.format("%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS", fecha));

			System.out.print("-------------" + "\n");
			System.out.print("Fin ejecucion" + "\n");
			System.out.print("-------------" + "\n");
			if(continua == true) {
			System.out.print("-------------" + "\n");
			System.out.print("Inicio Espera 60 segundos" + "\n");
			System.out.print("-------------" + "\n");
				Thread.sleep(60000); // para que cada 60 segundos se ejecute
			}

			//Se borra el contenido de cada Array creado para una nueva iteracción
			nombreProductos.clear();
			filas.clear();
			PrecioAZ.clear();
			precioML.clear();
			
		} //cierra while

		// Una vez sale del While resetea los arrays, quedando listo para una nueva incvocacion
		continua = false;
		nombreProductos.clear();
		filas.clear();
		PrecioAZ.clear();
		precioML.clear();
		System.out.println("Sale del While"); //
		
		Calendar fechaFin = Calendar.getInstance();
		System.out.print("-------------" + "\n");
		System.out.print("Proceso Obtener Precio : Finalizado" + "\n");
		System.out.print("-------------" + "\n");
		System.out.println("-->Fecha y hora de Finalización de Proceso Obtener Precio - terminado: "
				+ String.format("%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS", fechaFin));
	}

	///////////////////////////////////////////////////////////////////////////////
	// Metodo para detener el servicio de consulta de precios
	///////////////////////////////////////////////////////////////////////////////

	@GetMapping("/stop") // Mapeo para detener el servicio de consulta de precios
	public void detener() {
		continua = false;
		System.out.print("-------------" + "\n");
		System.out.print("Atención: Se detendrá una vez termine de consultar los precios para mabas paginas | Puede iniciarlo de nuevo con http://localhost:8080/ObtenerPrecios " + "\n");
		System.out.print("-------------" + "\n");

	}

	///////////////////////////////////////////////////////////////////////////////
	// Clase para consultar los archivos y verificar que existan en el directorio
	///////////////////////////////////////////////////////////////////////////////
	
	public static void archivos(String directorioRaiz) {

		File carpeta = new File(directorioRaiz); // Nueva objeto carpeta
		int contadorArchivos = 0;// Contador para asociarlo a la cantidad de archivos dentro de la carpeta a
									// analizar

		// verificar que la carpeta exista

		if (carpeta.exists()) {
			File[] ficheros = carpeta.listFiles(); // Listar archivos en carpeta raiz
			for (File f : ficheros) {
				// Se verifica que el archivo sea xlsx
				if (ficheros[contadorArchivos].getName().endsWith("Lista_Productos.xlsx")) {
					nombreExcelLP = f.getName(); // se asocia a la variable de Excel el nombre del archivo encontrado
													// con el nombre Lista_productos
					System.out.println("El archivo seleccionado es:" + f.getName());
				}
				contadorArchivos++;
			}
		} else {
			System.out.println("¡Atención: La Carpeta y/o Archivo buscada/o No Existe!"); // Error asociado, por la no
																							// existencia del archivo
																							// Excel
		}
	}

	///////////////////////////////////////////////////////////////////////////////
	// Clase para leer datos del Excel
	///////////////////////////////////////////////////////////////////////////////

	public void leerExcel() throws IOException {

		// Variables Extraidas de las celdas
		String NombreProductoExtraido = "";
		int NumeroCelda;

		// Variables basicas para interactuar en el Excel
		int fila = 0;
		int columna = 0;
		int contadorTamanio = 0;

		// Leer archivo Excel
		try (InputStream inp = new FileInputStream(directorioExcelLP)) {

			// Objeto para establecer contacto con el Excel
			workbook = new XSSFWorkbook(inp);

			// Obtenemos la Hoja que vamos a leer
			XSSFSheet sheet = workbook.getSheet(hoja);

			// Iterar a través de cada fila una por una
			Iterator<Row> rowIterator = sheet.iterator();
			Row row1;

			// Crea una nueva fila dentro de la hoja y devuelve el valor
			XSSFRow row;
			XSSFCell celda1; // Celda para Nombre del producto
			XSSFCell celda2; // Celda para Valor producto en Mercado Libre
			XSSFCell celda3; // Celda para Valor Producto Amazon

			// Se registra en la consola los datos leídos para el control
			System.out.print("-------------" + "\n");
			System.out.print("Datos Leidos de Excel" + "\n");
			System.out.print("-------------" + "\n");

			// Se recorren las filas diligenciadas del Excel

			while (rowIterator.hasNext()) {

				System.out.print("----" + "\n");
				System.out.print(
						"La Fila del archivo Excel <<lista de Productos>> analizada es: " + contadorExcel + "\n");
				System.out.print("----" + "\n");

				// Itera el indicador de fila en excel
				row1 = rowIterator.next();

				// Se obtiene la fila para la hoja 0
				row = sheet.getRow(fila);

				System.out.print("----" + "\n");

				// se obtiene las celdas por fila
				Iterator<Cell> cellIterator = row1.cellIterator();
				Cell cell1;

				// se recorre cada celda
				while (cellIterator.hasNext()) {

					// se obtiene la celda en específico y se la imprime
					if (columna == 0) {

						// Se lee la celda 1 - Nombre del Producto (se crea la excepción para no leer el
						// titulo de la tabla)
						if (fila != 0) {

							celda1 = row.getCell(0); // Celda ubicada en la columna 0 del excel, es decir en la "A"
							celda1.setCellType(CellType.STRING); // La celda se convierte a tipo String
							String FinalProducto = celda1.getStringCellValue(); //// Se obtiene el valor de la celda
							System.out.print("El producto extraido del Excel es: " + celda1.getStringCellValue()
									+ " , Para la fila" + fila);
							nombreProductos.add(contadorTamanio, FinalProducto); // Se agregan los productos en el
																					// arraylist de manera dinamica
							filas.add(contadorTamanio, fila); // Se crea la indexación de filas en el Arraylist de filas
							FinalProducto = ""; // Se resetea la variable de FinalProducto
							contadorTamanio++;
						}
						cell1 = cellIterator.next(); // Continua con la siguiente celda
						columna++;// Columna aumenta paera salir del IF
						System.out.print("La Columna analizada es: " + columna + "\n");
					} else {
						break; // Salir del IF
					}
				}
				System.out.println();
				columna = 0; // Se resetea la variable Columna para ingresar al if
				fila++;
				System.out.print("La Fila de Excel que fue analizada es: " + contadorExcel + "\n");
				System.out.print("-------" + "\n");
				contadorExcel++;
			}
			contadorTamanio = 0;
			contadorExcel = 0;
			fila = 0;
			System.out.print("Reset Contador - Lectura Excel [ok]: " + contadorExcel + "\n");
			try (OutputStream fileOut = new FileOutputStream(directorioExcelLP)) {
				workbook.write(fileOut);
			}
		} catch (IOException e) {
			e.getMessage();
		}

	}

	///////////////////////////////////////////////////////////////////////////////
	// Clase para Escribir datos del Excel
	///////////////////////////////////////////////////////////////////////////////

	// Constructor para escribir en el Excel
	public void escribirExcel(int fila, String valorProductoML, String valorProductoAZ) throws IOException {

		// Variables basicas para interactuar en el Excel
		int filaExcel = fila;

		// Variables establecidas para extraer el campo del constructor y manejarlo
		// localmente
		String valorProductoMCLB = valorProductoML; // Valor del Producto extraido de mercado Libre y Listo para ser
													// guardado
		String valorProductoAMAZ = valorProductoAZ; // Valor del Producto extraido de mercado Libre y Listo para ser
													// guardado

		// Leer archivo Excel
		try (InputStream inp = new FileInputStream(directorioExcelLP)) {

			// Objeto para establecer contacto con el Excel
			workbook = new XSSFWorkbook(inp);

			// Obtenemos la Hoja que vamos a leer
			XSSFSheet sheet = workbook.getSheet(hoja);

			// Iterar a través de cada fila una por una
			Iterator<Row> rowIterator = sheet.iterator();
			Row row1;

			// Crea una nueva fila dentro de la hoja y devuelve el valor
			XSSFRow row;
			XSSFCell celda1; // Nombre del producto
			XSSFCell celda2; // Valor producto en Mercado Libre
			XSSFCell celda3; // Valor Producto Amazon

			// Se registra en la consola los datos leídos para el control
			System.out.print("-------------" + "\n");
			System.out.print("Datos Leidos de Excel" + "\n");
			System.out.print("-------------" + "\n");

			// Se recorren las filas diligenciadas del Excel

			// Fila extraida del array para ubicarse en el documento de Excel
			row = sheet.getRow(fila);

			celda1 = row.getCell(1); // Celda de Producto de Mercado Libre
			celda2 = row.getCell(2); // Celda de Producto de Amazon

			celda1.setCellType(CellType.STRING); // Se establece formato de String a la celda
			celda2.setCellType(CellType.STRING); // Se establece formato de String a la celda

			celda1.setCellValue(valorProductoMCLB); // Se setea el valor que se encuentra en la variable temporal para
													// ML
			celda2.setCellValue(valorProductoAMAZ); // Se setea el valor que se encuentra en la variable temporal para
													// ML

			// Se resetean las variables temporales o de la clase
			filaExcel = 0;
			valorProductoMCLB = "";
			valorProductoAMAZ = "";

			System.out.print("Reset Contador - Escritura Excel [ok]: " + contadorExcel + "\n");

			// Se cierra el archivo Excel y se libera el mismo
			try (OutputStream fileOut = new FileOutputStream(directorioExcelLP)) {
				workbook.write(fileOut);
			}
			//Captura de Excepcion del guardado del archivo
			} catch (IOException e) { 
				e.getMessage();
			}

	}

	///////////////////////////////////////////////////////////////////////////////
	// Clase para Consultar precio en Mercado Libre
	///////////////////////////////////////////////////////////////////////////////

	public void consultarPrecioML(ArrayList<String> nombreProductos2) throws InterruptedException {

		ArrayList<String> productos = new ArrayList<String>(); // Array local para replicar los datos del Array Global con os nombres de los productos
		productos = (ArrayList<String>) nombreProductos2; // Intancia del Array de tipo string con los nombres de la variable local
		System.setProperty("webdriver.gecko.driver", ".\\src\\FirefoxDriver\\geckodriver.exe"); // Localizacion del driver Selenium para firefox
		WebDriver driver; // Se crea el objeto tipo WedDriver para interactuar con el navegador
		driver = new FirefoxDriver(); // Inicializacion del navegador
		driver.manage().window().maximize(); // Se maximiza la ventana del navegador
		int tamanioAP = productos.size(); // Obtiene el tamano del array con los nombres de los productos
		System.out.println("Tamanio Array Productos - Mercado Libre Consulta: " + tamanioAP);
		driver.get("https://www.mercadolibre.com.co/"); // el navegador se dirige a mercadolibre.com
		
		// Extracción de Precios para cada producto del Array y almacenamiento en el Array local - respecto al producto de mercado Libre
		for (int i = 0; i < tamanioAP; i++) {
			int indice = i+1;
			int Fin = tamanioAP;
			System.out.println("Producto a consultar en Mercado libre (co): " + nombreProductos.get(i) + " ("+indice+"/"+Fin+") ");
			String productoConsulta = nombreProductos.get(i); // Manejo de variable local asociando el nombre del producto
			System.out.println();

			// Inicializacion de localizadores de los elementos que contienen el precio (mismo elemento) con XPATH diferente
			By registerPageLocatorML = By.xpath(
					"//*[@class='ui-search-layout__item'][1]//*[@class='ui-search-price ui-search-price--size-medium ui-search-item__group__element']//*[@class='ui-search-price__second-line'][1]//*[@class='price-tag-fraction'][1]");
			By registerPageLocatorML2 = By.xpath(
					"//*[@id='root-app']/div/div/section/ol/li[1]/div/div/div[2]/div[3]/div[1]/div[1]/div/div/span[1]/span[2]");
			
			
			try {

				// Para Mercado Libre

				WebElement searchbox = driver.findElement(By.className("nav-search-input")); //Se localiza el elemento de busqueda
				driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS); // Se da una espera mientras carga la pagina
				searchbox.clear(); // Se limpia el contenido de la caja de busqueda
				searchbox.sendKeys(productoConsulta); // Se escribe dentro del campo el Producto a consultar
				searchbox.submit(); // Simula el enter
				searchbox.submit(); // verificacion del enter para forzar a buscar el producto
				
				
				// Tiempo de espera para carga de todos los elementos
				driver.manage().timeouts().pageLoadTimeout(2, TimeUnit.SECONDS); 
				driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
				
				// En caso de que algun PopUp de Cookies o de recomendacion bloquee el campo de interacción se verifica y se cierra
				Boolean existeElemento = driver.findElements(By.xpath("//*[@class='andes-tooltip-button-close']"))
						.size() != 0; //PopUp de Recomendaciones de Localizacion
				
				// Se valida si existe el elemento y de existir se cierra
				if (existeElemento == true) {
					WebElement poUp = driver.findElement(By.xpath("//*[@class='andes-tooltip-button-close']"));
					poUp.click();
				}
				// Se valida si existe el elemento y de existir se cierra
				Boolean existeElemento2 = driver.findElements(By.xpath("//*[@id='cookieDisclaimerButton']"))
						.size() != 0;
				
				if (existeElemento2 == true) { // Elemento Cookies
					WebElement cookies = driver.findElement(By.xpath("//*[@id='cookieDisclaimerButton']"));
					cookies.click();
				}
				
				//Espera de un segundo
				Thread.sleep(1000);
				
				// Una vez cerrados los PopUps procedemos a buscar el precio del primer producto que 
				// aparece en la pagina
				Boolean precio1 = driver.findElements(registerPageLocatorML).size() != 0; // valida si el elemnto existe, si no retorna cero
				Boolean precio2 = driver.findElements(registerPageLocatorML2).size() != 0; // valida si el elemnto existe, si no retorna cero

				if (precio1 == true) { // Si existe el primer elemento lo busca, lo asigna y extrae el texto del precio
					WebElement busquedaML2 = driver.findElement(registerPageLocatorML);
					busquedaML2.click();
					System.out.println("EL valor del producto ("+indice+"/"+Fin+"): " + productoConsulta + "en Mercado libre es: "
							+ busquedaML2.getText());
					precioML.add(i, busquedaML2.getText());
				} else if (precio2 == true) { // si existe el segundo elemnto con el indicador de XPATH lo busca, lo asigna y extrae el texto del precio
					WebElement busquedaML2 = driver.findElement(registerPageLocatorML2);
					busquedaML2.click();
					precioML.add(i, busquedaML2.getText()); // se agrega al Array de precios de mercado libre en orden, para luego asociarlos con las filas
					System.out.println("EL valor del producto ("+indice+"/"+Fin+"): " + productoConsulta + "en Mercado libre es: "
							+ busquedaML2.getText());
				} else { // De no encontrar ninguno de los elemntos mencionados, coloca el precio como cero
					System.out.println("No se ha encontrado");
					precioML.add(i, "0");
				}

			} finally {

				productoConsulta = ""; // reseteo de la variable local
			}

		}

		driver.quit(); // Se Cierra el Navegador

	}

	///////////////////////////////////////////////////////////////////////////////
	// Clase para Consultar precio en Amazon
	///////////////////////////////////////////////////////////////////////////////
	public void consultarPrecioAZ(ArrayList<String> nombreProductos2) throws InterruptedException {

		ArrayList<String> productos = new ArrayList<String>();  // Array local para replicar los datos del Array Global con os nombres de los productos
		System.setProperty("webdriver.gecko.driver", ".\\src\\FirefoxDriver\\geckodriver.exe"); // Localizacion del driver Selenium para firefox
		WebDriver driver; // Se crea el objeto tipo WedDriver para interactuar con el navegador
		driver = new FirefoxDriver(); // Inicializacion del navegador
		driver.manage().window().maximize(); // Se maximiza la ventana del navegador

		// Valida que la primera vez que se invoque la clase realice el cambio de idioma y formato de moneda en el sitio
		// realiza el cambio dentro de la pagina y la deja lista para consultar
		if (cambioIdioma == false) { 
			driver.get("https://www.amazon.com/-/es/"); // el navegador se dirige a amazon.com
			
			// Simula un mouse con el efecto hover en un boton sin hacer clic, esto aplica para desplegables de bandera de USA
			Actions action = new Actions(driver);
			WebElement we = driver.findElement(By.xpath("//*[@class='icp-nav-link-inner'][1]"));
			action.moveToElement(we).moveToElement(driver.findElement(By.xpath("//*[@class='icp-nav-link-inner'][1]")))
					.click().build().perform(); 
			
			// Tiempo de espera para carga de todos los elementos
			driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS);
			driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			
			// Simula un mouse con el efecto hover en un boton sin hacer clic, esto aplica seleccionar la opción "Cambiar formato" del deplegable anterior
			WebElement boton = driver
					.findElement(By.xpath("/html/body/div[1]/div[4]/div/div/form/div[3]/div/p/span[2]/span/span"));
			action.moveToElement(boton)
					.moveToElement(driver.findElement(
							By.xpath("/html/body/div[1]/div[4]/div/div/form/div[3]/div/p/span[2]/span/span")))
					.click().build().perform();
			
			// Tiempo de espera para carga de todos los elementos
			driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS); // estaba en 10
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			
			// Se digige al Desplegable dpara cambiar la moneda COP (opcion dos de top-Bottom)
			WebElement copMoney = driver.findElement(By.xpath("//*[@id=\"icp-sc-dropdown_2\"]"));
			action.moveToElement(copMoney).moveToElement(driver.findElement(By.xpath("//*[@id=\"icp-sc-dropdown_2\"]")))
					.click().build().perform();
			
			// Simula un scroll para ontener un plano del boton guardar cambios
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("window.scrollBy(0,450)", "");

			// Tiempo de espera para carga de todos los elementos
			driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS); // estaba en 10
			driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

			// Ejecuta clic en el boton Guardar cambios y que se registre la moneda COP
			WebElement guardarCambios = driver.findElement(By.xpath("//*[@id=\"icp-btn-save\"]"));
			action.moveToElement(guardarCambios)
					.moveToElement(driver.findElement(By.xpath("//*[@id=\"icp-btn-save\"]"))).click().build().perform();
			guardarCambios.click();

			// Se cambia la variable para no pasar de nuevo y cambiar el formato por US una vez se ha invocado esta clase por primera vez
			cambioIdioma = true;

		}
		
		// Scroll para volver al inicio de la pagina y buscar el searchbox de Amazon
		JavascriptExecutor js1 = (JavascriptExecutor) driver;
		js1.executeScript("window.scrollBy(0,0)", "");

		// Tiempo de espera para carga de todos los elementos
		driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS); // estaba en 10
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

		productos = (ArrayList<String>) nombreProductos2; // Intancia del Array de tipo string con los nombres de la variable local
		int tamanioAP = productos.size(); // Obtiene el tamano del array con los nombres de los productos
		System.out.println("Tamanio Array Productos - Amazon: " + tamanioAP);
		
		// Extracción de Precios para cada producto del Array y almacenamiento en el Array local - respecto al producto de Amazon
		for (int i = 0; i < tamanioAP; i++) {
			int indice = i+1;
			int fin = tamanioAP;
			String productoConsulta = nombreProductos.get(i);  // Manejo de variable local asociando el nombre del producto

			Thread.sleep(1000); // Tiempo de espera para la carga de los elementos de la pagina Amazon
			WebElement searchboxAmazon = driver.findElement(By.xpath("//*[@id='twotabsearchtextbox']")); //Se localiza el elemento de busqueda

			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS); // Tiempo de espera para carga de todos los elementos

			searchboxAmazon.clear();  // Se limpia el contenido de la caja de busqueda
			System.out.println("El producto a buscar en Amazon es: " + productoConsulta + " ("+indice+"/"+fin+") " );
			searchboxAmazon.sendKeys(productoConsulta); // Se escribe dentro del campo el Producto a consultar
			searchboxAmazon.submit(); // Simula el enter
 
			Thread.sleep(2000); // Tiempo de espera para la carga de los elementos de la pagina Amazon
			
			// Tiempo de espera para carga de todos los elementos
			driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

			// Si el primer enlace es de un patrocinador el contenido es diferente (hablando de XPATH)			
			boolean salePat = false; // Variable que controla el precio encontrado para los patrocinadores
			boolean entraPrecioNormal = false; // En caso de no existir un contenido de Patrocinador debe entrar al contenido con productos no patrocinados

			for (int m = 1; m < 3; m++) { // Hay máximo tres div de Patrocinadores antes de encontrar un producto sin patrocinio
				
				// Contruccion del XPATH dinamico para recorrer los DIVS de Patrocinador
				String numeroDPAtrocinador = String.valueOf(m);
				String rutaAZComPat = "/html/body/div[1]/div[2]/div[1]/div[2]/div/span[3]/div[2]/div["
						+ numeroDPAtrocinador
						+ "]/div/span/div/div/div/div/div[2]/div[2]/div/div[2]/div[1]/div/div[1]/div[1]/div/div/a/span/span[2]/span[2]";
				
				By registerPageLocatorAZPat = By.xpath(rutaAZComPat); // asigna variable de localizacion

				driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS); // Espera para cargue de componentes
				driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS); // espera implicita para reforzar el
																					// cargue de documentos

				Thread.sleep(1000); // Hilo de 1 segundo para asegurarse de el cargue de documentos

				Boolean existePrecioPatro = driver.findElements(registerPageLocatorAZPat).size() != 0; // valida la
																										// existencia
																										// del elemento
				
				// El primer elemento que encuentre es el que registra para tener el primero que aparece en la pagina sea patrocinado o no
				if (salePat == false) { // Si el valor es cero del elemento comprueba la validacion
					if (existePrecioPatro == true) { // si existe lo registre y escribe en el array de precios de amazon
						WebElement busquedaAZPat = driver.findElement(registerPageLocatorAZPat); // Busca el elemnto del localizador Patrocinador
						System.out.println("EL valor del producto ("+indice+"/"+fin+"): " + productoConsulta
								+ " En Amazon es (precio patrocinador): " + busquedaAZPat.getText() + " ");
						PrecioAZ.add(i, busquedaAZPat.getText()); // Agrega el precio del producto en el array de precios amazon
						salePat = true; // sale del if para no evaluar la misma condicion y tomar el primer precio
						entraPrecioNormal = false; // Cancela la opcion de consultar los DIVS con productos sin patrocinio
					} else {
						System.out.println("No se ha encontrado en div de patrocinador: " + m); // En caso de no existir el elemento lo notifica
						entraPrecioNormal = true; // Permite que se verifique los que no tienen patrocinador
					}
				}

			}
			
			// Estra si no existe un producto de patrocinador
			if (entraPrecioNormal == true) {
				boolean sale = false; // Variable que controla cuando no consultar mas productos una vez encuentra un precio

				// Se establecen 11 productos para buscas (top-Bottom) div de Patrocinadores antes de encontrar un producto sin patrocinio
				for (int k = 1; k < 11; k++) {
					
					// Contruccion del XPATH dinamico para recorrer los DIVS de Patrocinador
					String numeroDiv = String.valueOf(k);
					// Productos no patrocinados
					String rutaAZCom = "/html/body/div[1]/div[2]/div[1]/div[2]/div/span[3]/div[2]/div[" + numeroDiv
							+ "]/div/span/div/div/div[2]/div[2]/div/div[2]/div[1]/div/div[1]/div/div/div/a/span/span[2]/span[2]"; // ruta
																																	// construida
					By registerPageLocatorAZ = By.xpath(rutaAZCom); // asigna variable de localizacion
					driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS); // Espera para cargue de
																						// componentes
					driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS); // espera implicita para reforzar
																						// el cargue de documentos

					Thread.sleep(1000); // Hilo de 1 segundo para asegurarse de el cargue de documentos

					Boolean existePrecio = driver.findElements(registerPageLocatorAZ).size() != 0; // valida la
																									// existencia del
																									// elemento

					if (sale == false) { // Si el valor es cero del elemento comprueba la validacion
						if (existePrecio == true) { // si existe lo registre y escribe en el array de precios de amazon
							WebElement busquedaML = driver.findElement(registerPageLocatorAZ); // Busca el elemnto del localizador Patrocinador
							System.out.println("EL valor del producto " + productoConsulta
									+ " En Amazon es (Publicacion No patrocinada): " + busquedaML.getText() + " ");
							PrecioAZ.add(i, busquedaML.getText()); // Agrega el precio del producto en el array de precios amazon
							sale = true; // sale del if para no evaluar la misma condicion y tomar el primer precio
						} else {
							System.out.println("No se ha encontrado en div sin patrocinador: " + k); // En caso de no existir el elemento lo notifica
							// Alguno de los 11 DIVS debe tener precio por eso no se coloca una variable de salida del bucle
						}
					}

				}
			}
		}

		driver.quit(); // Cierra la ventana abierta por el Navegador

		// Boleano para que desde la segunda iteración no cambie el formato e idioma y
		// continue con la busqueda en español y formato COP
		cambioIdioma = false;

	}

	// Add new student
	@GetMapping("/add") // Mapeo para invocar el servicio
	public boolean addProducto(Producto p) {
		return save(p);
	}

	///////////////////////////////////////////////////////////////////////////////
	// Clase para Escribir en la Base de datos
	///////////////////////////////////////////////////////////////////////////////

	public boolean save(Producto p) {
		// Se invoca la clase para guardar los datos en la BD
		service.save(p);
		// Retorna Valor
		return true;

	}

	///////////////////////////////////////////////////////////////////////////////
	// Clase para Borrar la base de datos - se debe modificar el
	/////////////////////////////////////////////////////////////////////////////// application.properties
	/////////////////////////////////////////////////////////////////////////////// (donde
	/////////////////////////////////////////////////////////////////////////////// dice
	/////////////////////////////////////////////////////////////////////////////// none
	/////////////////////////////////////////////////////////////////////////////// por
	/////////////////////////////////////////////////////////////////////////////// create)
	///////////////////////////////////////////////////////////////////////////////
	@GetMapping("/borrar")
	public void borrar() {
		Producto producto1 = new Producto();
		producto1.setNameproduct("Test"); 
		producto1.setPrecioamazon("Test");
		producto1.setPreciomercadolibre("Test");
		save(producto1);
	}
}
